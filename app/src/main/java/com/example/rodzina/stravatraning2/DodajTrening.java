package com.example.rodzina.stravatraning2;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import com.sweetzpot.stravazpot.activity.api.ActivityAPI;
import com.sweetzpot.stravazpot.activity.model.Activity;
import com.sweetzpot.stravazpot.activity.model.ActivityType;
import com.sweetzpot.stravazpot.common.model.Distance;
import com.sweetzpot.stravazpot.common.model.Time;

import static android.R.id.message;
import static com.example.rodzina.stravatraning2.R.attr.title;



public class DodajTrening extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String m_trening_nazwa = "";
    private String m_trening_opis = "";
    private String m_trenign_czas = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public TreningDataBase baza;
    private OnFragmentInteractionListener mListener;
    final ArrayList<String> lista_treningow = new ArrayList<String>();
    NetworkInfo netInfo;
    ConnectivityManager cm;
    private boolean flag = false;
    public DodajTrening() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DodajTrening.
     */
    // TODO: Rename and change types and number of parameters
    public static DodajTrening newInstance(String param1, String param2) {
        DodajTrening fragment = new DodajTrening();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myInflatedView = inflater.inflate(R.layout.fragment_lista_treningow, container, false);


        final ListView listView = (ListView) myInflatedView.findViewById(R.id.list);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, lista_treningow);

        listView.setClickable(true);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String selected = String.valueOf(parent.getItemAtPosition(position));
                        final Trening trening = (Trening) parent.getItemAtPosition(position);
                        //Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                        final String[] items = {"Wyślij", "Edytuj"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Akcja");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (items[item].equals("Wyślij")) {
                                    if(!CheckConnection()){
                                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                                                getActivity());
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
                                                        //MainActivity.this.finish();
                                                    }
                                                });
                                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        new Save().execute(trening);
                                    }
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
                                            new Save().execute(trening);
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

                                //url
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
        );

        final TreningAdapterList adapter2 = new TreningAdapterList(getActivity(),R.layout.row_layout);
        listView.setAdapter(adapter2);
        try {
            /*Environment.getDataDirectory();
            String root2 = Environment.getExternalStorageDirectory().toString();
            //String root2 =  Environment.getDataDirectory().toString();
            //Log.e("TEST",root2);
            File myDir2 = new File(root2 + "/treningi");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(myDir2+"/lista_treningow.txt"));
            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("trening");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Trening temp_trening = new Trening(new Date(),getValue("nazwa", element2),Integer.parseInt(getValue("czas", element2)),getValue("opis", element2));
                    adapter2.add(temp_trening);
                }
            }*/
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
    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            this.dialog.setMessage("Wysyłam");
            this.dialog.show();
        }
    }
    public boolean CheckConnection(){
        cm =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }
}
