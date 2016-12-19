package com.example.rodzina.stravatraning2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sweetzpot.stravazpot.activity.api.ActivityAPI;
import com.sweetzpot.stravazpot.activity.model.Activity;
import com.sweetzpot.stravazpot.activity.model.ActivityType;
import com.sweetzpot.stravazpot.athlete.api.AthleteAPI;
import com.sweetzpot.stravazpot.athlete.model.Athlete;
import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginButton;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;
import com.sweetzpot.stravazpot.common.api.StravaConfig;
import com.sweetzpot.stravazpot.common.model.Distance;
import com.sweetzpot.stravazpot.common.model.Time;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt.AUTO;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    private static final int CLIENT_ID = 14696;
    private static final String CLIENT_SECRET = "2682acd7edb462f7ac8673b84e273a68846d22cb";
    protected static StravaConfig config_global;
    public static final StravaConfig getConfig(){
      return  config_global;
    }
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private static final int RQ_LOGIN = 1001;
    NetworkInfo netInfo;
    ConnectivityManager cm;
    private boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            login();
        }
        else{
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                    MainActivity.this);
            alertDialogBuilder.setTitle("Brak połączenia z siecią");
            alertDialogBuilder
                    .setMessage("Przejść do ustawień?")
                    .setCancelable(false)
                    .setPositiveButton("Tak",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Intent intent=new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                            flag = true;
                        }
                    })
                    .setNegativeButton("Nie",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            MainActivity.this.finish();
                        }
                    });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
    private void login() {
        Intent intent = StravaLogin.withContext(this)
                .withClientID(CLIENT_ID)
                .withRedirectURI("http://www.localhost/UploadTraningTemple")
                .withApprovalPrompt(AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        startActivityForResult(intent, RQ_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RQ_LOGIN && resultCode == RESULT_OK && data != null) {
            String code = data.getStringExtra(StravaLoginActivity.RESULT_CODE);
            new GetToken(MainActivity.this).execute(code);
        }
        netInfo = cm.getActiveNetworkInfo();
    }
    public class GetToken extends AsyncTask<String, Void, LoginResult>{
        private ProgressDialog dialog;
        public GetToken(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }
        @Override
        protected void onPostExecute(LoginResult loginResult) {
            StravaConfig config = StravaConfig.withToken(loginResult.getToken())
                    .debug()
                    .build();
            config_global=config;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //
        }

        @Override
        protected LoginResult doInBackground(String... code) {
            AuthenticationConfig config = AuthenticationConfig.create()
                    .debug()
                    .build();
            AuthenticationAPI api = new AuthenticationAPI(config);
            LoginResult result = api.getTokenForApp(AppCredentials.with(CLIENT_ID, CLIENT_SECRET))
                    .withCode(code[0])
                    .execute();
            return result;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Logowanie");
            this.dialog.show();
        }

    }
    @Override
    protected void onResume(){
        super.onResume();
        if (flag) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
