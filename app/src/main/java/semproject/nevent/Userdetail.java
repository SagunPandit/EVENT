package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static semproject.nevent.MainActivity.PreferenceFile;

/**
 * Created by User on 1/29/2017.
 */

public class Userdetail extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="Userdetail";
    SharedPreferences sharedpreferences;
    private RecyclerView mRecyclerView;
    String username;
    EventRecyclerView eventRecyclerView = new EventRecyclerView();
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

        Button userevents=(Button) rootView.findViewById(R.id.userevents);
        userevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(display){
                    if(checkConnection(getContext())){
                        userListener(username);
                    }
                    display=false;
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

    public void retreiveFromDatabase(EventRecyclerView eventRecyclerView,RecyclerView mRecyclerView,Context context){
        Log.e(STRING_TAG,"database");
        if(checkConnection(context)){
            for (int i=0;i < eventList.size();i++)
            {
                Log.i("Value of element "+i,eventList.get(i));
                eventRecyclerView.initializeData(eventId.get(i),eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),username,viewcount.get(i),context);
                RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(context, eventRecyclerView.getItem());
                mRecyclerView.setAdapter(mAdapter);
            }
        }

    }

    public void userListener(String username){
        Log.e(STRING_TAG,"insideListiner");
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Log.e(STRING_TAG,"insideSuccess");
                        JSONArray jsonArray = jsonObject.getJSONArray("event_name");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("location_name");
                        JSONArray jsonArray3 = jsonObject.getJSONArray("event_date");
                        JSONArray jsonArray4 = jsonObject.getJSONArray("event_category");
                        JSONArray jsonArray5 = jsonObject.getJSONArray("event_id");
                        JSONArray jsonArray7 = jsonObject.getJSONArray("viewcount");
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            Log.e(STRING_TAG,Integer.toString(len));
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
                                viewcount.add((Integer) jsonArray7.get(i));
                            }

                            retreiveFromDatabase(eventRecyclerView, mRecyclerView, getContext());
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
            RecyclerRequest recyclerRequest=new RecyclerRequest(username,"own",responseListener);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(recyclerRequest);
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
