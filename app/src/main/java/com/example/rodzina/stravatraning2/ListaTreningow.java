package com.example.rodzina.stravatraning2;



import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sweetzpot.stravazpot.activity.api.ActivityAPI;
import com.sweetzpot.stravazpot.activity.model.Activity;
import com.sweetzpot.stravazpot.activity.model.ActivityType;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginButton;
import com.sweetzpot.stravazpot.common.model.Distance;
import com.sweetzpot.stravazpot.common.model.Time;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;




import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.rodzina.stravatraning2.MainActivity.config_global;



/**
 * A simple {@link Fragment} subclass.
 */
public class ListaTreningow extends Fragment {
    public TreningDataBase baza;
    public TextView tv1;
    String[] mobileArray ={};
    final ArrayList<String> lista_treningow = new ArrayList<String>();
    final ArrayList<Trening> lista_trenigow_class = new ArrayList<>();
    public TreningAdapterList adapter2 = null;
    public ListaTreningow() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myInflatedView = inflater.inflate(R.layout.fragment_lista_treningow, container, false);


        final ListView listView = (ListView) myInflatedView.findViewById(R.id.list);
        listView.setClickable(true);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String selected = String.valueOf(parent.getItemAtPosition(position));
                        final Trening trening = (Trening) parent.getItemAtPosition(position);
                        //Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                        final String[] items = {"Edytuj", "Dodaj Nowy","Usuń"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Akcja");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                if (items[item].equals("Dodaj Nowy")) {

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.dialog_box, null);
                                    dialogBuilder.setView(dialogView);

                                    final EditText trening_nazwa = (EditText) dialogView.findViewById(R.id.text_dialog_nazwa);
                                    trening_nazwa.setText(trening.getNazwa());
                                    final EditText trening_opis = (EditText) dialogView.findViewById(R.id.text_dialog_opis);
                                    trening_opis.setText(trening.getOpis());
                                    final EditText trening_czas = (EditText) dialogView.findViewById(R.id.text_dialog_czas);
                                    trening_czas.setText(String.valueOf(trening.getCzas()));
                                    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            baza.insertData(trening_nazwa.getText().toString(),
                                                    trening_opis.getText().toString(),
                                                    Integer.parseInt(trening_czas.getText().toString())
                                                    );
                                            adapter2 = new TreningAdapterList(getActivity(),R.layout.row_layout);
                                            LoadTraning();
                                            listView.setAdapter(adapter2);

                                        }

                                    });
                                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //pass
                                        }
                                    });
                                    AlertDialog b = dialogBuilder.create();
                                    b.show();
                                }
                                if (items[item].equals("Edytuj")) {
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.dialog_box, null);
                                    dialogBuilder.setView(dialogView);

                                    final EditText trening_nazwa = (EditText) dialogView.findViewById(R.id.text_dialog_nazwa);
                                    trening_nazwa.setText(trening.getNazwa());
                                    final EditText trening_opis = (EditText) dialogView.findViewById(R.id.text_dialog_opis);
                                    trening_opis.setText(trening.getOpis());
                                    final EditText trening_czas = (EditText) dialogView.findViewById(R.id.text_dialog_czas);
                                    trening_czas.setText(String.valueOf(trening.getCzas()));
                                    //dialogBuilder.setTitle("Custom dialog");
                                    //dialogBuilder.setMessage("Enter text below");
                                    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            trening.setNazwa(trening_nazwa.getText().toString());
                                            trening.setOpis(trening_opis.getText().toString());
                                            trening.setCzas(Integer.parseInt(trening_czas.getText().toString()));
                                            baza.UpdateData(String.valueOf(trening.getId()),trening_nazwa.getText().toString(),
                                                    trening_opis.getText().toString(),
                                                    Integer.parseInt(trening_czas.getText().toString())
                                                    );
                                            adapter2 = new TreningAdapterList(getActivity(),R.layout.row_layout);
                                            LoadTraning();
                                            listView.setAdapter(adapter2);
                                        }
                                    });
                                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //pass
                                        }
                                    });
                                    AlertDialog b = dialogBuilder.create();
                                    b.show();
                                }
                                if (items[item].equals("Usuń")) {
                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                                            getActivity());
                                    alertDialogBuilder.setTitle("Usuwanie treningu");
                                    alertDialogBuilder
                                            .setMessage("Jesteś pewny?")
                                            .setCancelable(false)
                                            .setPositiveButton("Tak",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                            baza.DeleteData(String.valueOf(trening.getId()));
                                            adapter2 = new TreningAdapterList(getActivity(),R.layout.row_layout);
                                             LoadTraning();
                                              listView.setAdapter(adapter2);
                                                }
                                            })
                                            .setNegativeButton("Nie",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {

                                                }
                                            });

                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                }
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
        );

        adapter2 = new TreningAdapterList(getActivity(),R.layout.row_layout);
        listView.setAdapter(adapter2);

        try {
            ///////////
            baza = new TreningDataBase(getActivity());
            Cursor res = baza.getAllData();
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                //0 = id
                buffer.append("Id :"+res.getString(0)+"\n");
                buffer.append("Nazwa :"+res.getString(1)+"\n");
                buffer.append("Opis :"+res.getString(2)+"\n");
                buffer.append("Czas :"+res.getString(3)+"\n");
                Trening temp_trening = new Trening(Integer.parseInt(res.getString(0)),new Date(),res.getString(1),Integer.parseInt(res.getString(3)),res.getString(2));
                adapter2.add(temp_trening);
            }
            ///////////
            adapter2.notifyDataSetChanged();
        } catch (Exception e) {e.printStackTrace();}


        return myInflatedView;
    }
    private void LoadTraning(){
        try {
            baza = new TreningDataBase(getActivity());
            Cursor res = baza.getAllData();
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                //0 = id
                buffer.append("Id :"+res.getString(0)+"\n");
                buffer.append("Nazwa :"+res.getString(1)+"\n");
                buffer.append("Opis :"+res.getString(2)+"\n");
                buffer.append("Czas :"+res.getString(3)+"\n");
                Trening temp_trening = new Trening(Integer.parseInt(res.getString(0)),new Date(),res.getString(1),Integer.parseInt(res.getString(3)),res.getString(2));
                adapter2.add(temp_trening);
            }
            ///////////
            adapter2.notifyDataSetChanged();

        } catch (Exception e) {e.printStackTrace();}

    }
    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
    public static class Server {
        public String getName() { return "foo"; }
        public Integer getPort() { return 12345; }
    }
    public class Save extends AsyncTask<Trening,Void,String> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected String doInBackground(Trening... params) {
            Date dataFrom = new Date();
            //dataFrom.setTime(01/01/2016);
            ActivityAPI activityAPI = new ActivityAPI(MainActivity.getConfig());
            Activity activity = activityAPI.createActivity(params[0].getNazwa())
                    .ofType(ActivityType.RIDE)
                    .startingOn(params[0].getData())
                    .withElapsedTime(Time.seconds(params[0].getCzas()*60))
                    .withDescription(params[0].getOpis())
                    .withDistance(Distance.meters(0))
                    .isPrivate(true)
                    .withTrainer(true)
                    .withCommute(false)
                    .execute();

            Activity activityUP = activityAPI.updateActivity(activity.getID())

                    .changeName(activity.getName())
                    .changeType(ActivityType.RIDE)
                    .changePrivate(true)
                    .changeCommute(false)
                    .changeTrainer(true)
                    .changeGearID(activity.getGearID())
                    .execute();
            return "ok";

        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
    }
}
