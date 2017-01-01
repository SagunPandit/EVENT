package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static semproject.nevent.MainActivity.PreferenceFile;

public class UserDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {
    final String STRING_TAG = "UserDetails";
    SharedPreferences sharedpreferences;
    private RecyclerView mRecyclerView;
    String username;
    EventRecyclerView eventRecyclerView = new EventRecyclerView();
    List<String>eventList=new ArrayList<>();
    List<String>eventLocation=new ArrayList<>();
    List<String>eventDate=new ArrayList<>();
    List<String>eventCategory=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }
    public void viewevents(View view){
        if(checkConnection(this)){
            listenerFunction(username);
        }
        else {
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
    }

    public void logout(View view){
        if(checkConnection(this)){
            sharedpreferences = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent= new Intent(UserDetails.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
        return isConnected;
    }

    public void retreiveFromDatabase(EventRecyclerView eventRecyclerView,RecyclerView mRecyclerView,Context context){
        Log.e(STRING_TAG,"database");
        if(checkConnection(this)){
            for (int i=0;i < eventList.size();i++)
            {
                Log.i("Value of element "+i,eventList.get(i));
                eventRecyclerView.initializeData(eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),username,context);
                RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(context, eventRecyclerView.getItem());
                mRecyclerView.setAdapter(mAdapter);
            }
        }
        else {
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }

    }

    public void listenerFunction(String username){
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
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            Log.e(STRING_TAG,Integer.toString(len));
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

                            retreiveFromDatabase(eventRecyclerView, mRecyclerView, UserDetails.this);
                        }
                        else
                            Log.e(STRING_TAG,"insideNull");

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(UserDetails.this);
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
        if(checkConnection(this)){
            RecyclerRequest recyclerRequest=new RecyclerRequest(username,"own",responseListener);
            RequestQueue queue = Volley.newRequestQueue(UserDetails.this);
            queue.add(recyclerRequest);
        }
        else {
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
    }

}
