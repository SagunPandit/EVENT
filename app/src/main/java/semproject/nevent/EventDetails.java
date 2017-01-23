package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="EventDetails";

    TextView veventLabel,veventLocation,veventDate,veventOrganizer,veventCategory,veventId,veventDetails;
    String eventId, eventLabel, eventLocation, eventDate, eventOrganizer, eventCategory,eventDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        veventLabel= (TextView) findViewById(R.id.deventLabel);
        veventLocation= (TextView) findViewById(R.id.deventLocation);
        veventDate= (TextView) findViewById(R.id.deventDate);
        veventOrganizer= (TextView) findViewById(R.id.deventOrganizer);
        veventCategory= (TextView) findViewById(R.id.deventCategory);
        veventId= (TextView) findViewById(R.id.deventId);
        veventDetails= (TextView) findViewById(R.id.deventDetails);

        Intent intent = getIntent();
        eventId=intent.getStringExtra("eventId");
        eventLabel=intent.getStringExtra("eventLabel");
        eventLocation=intent.getStringExtra("eventLocation");
        eventDate=intent.getStringExtra("eventDate");
        eventOrganizer=intent.getStringExtra("eventOrganizer");
        eventCategory=intent.getStringExtra("eventCategory");
        listenerFunction(eventId);
        setvalues();

    }

    private void setvalues() {
        veventId.setText(eventId);
        veventLabel.setText(eventLabel);
        veventLocation.setText(eventLocation);
        veventDate.setText(eventDate);
        veventOrganizer.setText(eventOrganizer);
        veventCategory.setText(eventCategory);
    }
    //String eventId,String eventLabel,String eventLocation,String eventDate,String eventOrganizer,String eventCategory



    public void listenerFunction(String eventId){
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
                        eventDetails = jsonObject.getString("event_details");
                        veventDetails.setText(eventDetails);
                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EventDetails.this);
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
            DetailRequest detailRequest=new DetailRequest(eventId,responseListener);
            RequestQueue queue = Volley.newRequestQueue(EventDetails.this);
            queue.add(detailRequest);
        }
    }

    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(this,InternetConnection.class);
            finish();
            startActivity(intent);
        }
        return isConnected;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
        }
        else{
            Intent intent= new Intent(this,InternetConnection.class);
            finish();
            startActivity(intent);
        }
    }
}
