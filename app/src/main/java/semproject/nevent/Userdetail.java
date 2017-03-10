package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static semproject.nevent.MainActivity.PreferenceFile;

/**
 * Created by User on 1/29/2017.
 */

public class Userdetail extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="Userdetail";
    private static final String SERVER_ADDRESS="http://avashadhikari.com.np/";
    ImageView downloadedimage;
    TextView user_name;
    SharedPreferences sharedpreferences;
    private RecyclerView mRecyclerView;
    String username;
    List<String> eventList=new ArrayList<>();
    List<String>eventLocation=new ArrayList<>();
    List<String>eventDate=new ArrayList<>();
    List<String>eventCategory=new ArrayList<>();
    List<String>eventId=new ArrayList<>();
    List<Integer>viewcount=new ArrayList<>();
    boolean display=true;

    public Userdetail(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        username = getArguments().getString("username");

        View rootView = inflater.inflate(R.layout.fragment_userdetails, container, false);
        user_name=(TextView) rootView.findViewById(R.id.user_name);
        user_name.setText(username);

        downloadedimage=(ImageView) rootView.findViewById(R.id.profileimage);
        new Downloadimage(username).execute();
        // BEGIN_INCLUDE(initializeRecyclerView)
        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.user_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Button userevents=(Button) rootView.findViewById(R.id.userevents);
        userevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(display){
                    if(checkConnection(getContext())){
                        userListener(true);
                    }
                    userevents.setVisibility(View.GONE);
                    display=false;
                }

            }
        });

        Button goingevents=(Button) rootView.findViewById(R.id.goingevents);
        goingevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection(getContext())){
                    userListener(false);
                }
            }
        });
        Button userlogout=(Button) rootView.findViewById(R.id.userlogout);
        userlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection(getContext())){
                    sharedpreferences = getActivity().getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent= new Intent(getContext(),MainActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }



    public void retreiveFromDatabase(boolean ownEvents){
        Log.e(STRING_TAG,"database");
        Log.e(STRING_TAG, Integer.toString(eventList.size()));
        EventRecyclerView eventRecyclerView = new EventRecyclerView();
        if(checkConnection(getContext())){
            if(ownEvents){
                for (int i=0;i < eventList.size();i++)
                {

                    Log.i("Value of element "+i,eventList.get(i));
                    eventRecyclerView.initializeData(eventId.get(i),eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),username,viewcount.get(i),getContext());
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(getContext(), eventRecyclerView.getItem());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
            else {
                for (int i=0;i < eventList.size();i++)
                {
                    Log.i("Value of element "+i,eventList.get(i));
                    eventRecyclerView.initializeData(eventId.get(i),eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),username,viewcount.get(i),getContext());
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.AllItemAdapter(getContext(), eventRecyclerView.getItem(),username);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

        }

    }

    public void userListener(final boolean ownEvents){
        Log.e(STRING_TAG,"insideListiner");
        eventLocation=new ArrayList<>();
        eventList=new ArrayList<>();
        eventId=new ArrayList<>();
        eventDate=new ArrayList<>();
        eventCategory=new ArrayList<>();
        viewcount=new ArrayList<>();
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success1");
                    if(success){
                        Log.e(STRING_TAG,"insideSuccess");
                        JSONArray jsonArray = jsonObject.getJSONArray("event_name1");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("location_name1");
                        JSONArray jsonArray3 = jsonObject.getJSONArray("event_date1");
                        JSONArray jsonArray4 = jsonObject.getJSONArray("event_category1");
                        JSONArray jsonArray5 = jsonObject.getJSONArray("event_id1");
                        JSONArray jsonArray7 = jsonObject.getJSONArray("viewcount1");

                        int count= jsonObject.getInt("count1");
                        Log.e(STRING_TAG + "count",Integer.toString(count));
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            Log.e(STRING_TAG + "len",Integer.toString(len));
                            //for eventid
                            for (int i=0;i<len;i++){
                                eventId.add(jsonArray5.get(i).toString());
                            }

                            //for eventname
                            for (int i=0;i<len;i++){
                                eventList.add(jsonArray.get(i).toString());
                            }

                            //for eventlocation
                            for (int i=0;i<len;i++){
                                eventLocation.add(jsonArray2.get(i).toString());
                            }

                            //for eventdate
                            for (int i=0;i<len;i++){
                                eventDate.add(jsonArray3.get(i).toString());
                            }

                            //for eventcategory
                            for (int i=0;i<len;i++){
                                eventCategory.add(jsonArray4.get(i).toString());
                            }

                            //for count
                            for (int i=0;i<len;i++){
                                int value=(Integer) jsonArray7.get(i);
                                viewcount.add(value);
                            }
                            retreiveFromDatabase(ownEvents);
                        }
                        else
                            Log.e(STRING_TAG,"insideNull");

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                        builder.setMessage("Connection Failed")
                                .setNegativeButton("Retry",null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        if(checkConnection(getContext())){
            if(ownEvents)
            {   Log.e(STRING_TAG+" lkasdkf",Boolean.toString(ownEvents));
                RecyclerRequest recyclerRequest=new RecyclerRequest(username,"own",responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(recyclerRequest);
            }
            else {
                Log.e(STRING_TAG+" ajf;lkd",Boolean.toString(ownEvents));
                RecyclerRequest recyclerRequest=new RecyclerRequest(username,"getgoing",responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(recyclerRequest);
            }

        }
    }
    //For retrieving the image of user.
    private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
    {
        String name;
        public Downloadimage(String name)
        {
            this.name=name;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url=SERVER_ADDRESS+"pictures/userimages/"+name+".JPG";
            try{
                URLConnection connection=new URL(url).openConnection();
                connection.setConnectTimeout(1000*30);
                connection.setReadTimeout(1000*30);
                return BitmapFactory.decodeStream((InputStream) connection.getContent(),null,null);

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null)
            {
                downloadedimage.setImageBitmap(bitmap);
            }
        }
    }



    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(getContext(),InternetConnection.class);
            getActivity().finish();
            startActivity(intent);
        }
        return isConnected;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(getContext(),MainActivity.class);
            getActivity().finish();
            startActivity(intent);
        }
        else{
            Intent intent= new Intent(getContext(),InternetConnection.class);
            getActivity().finish();
            startActivity(intent);

        }
    }
}
