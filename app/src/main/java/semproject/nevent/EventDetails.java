package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class EventDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="EventDetails";
    private static final String SERVER_ADDRESS="http://avashadhikari.com.np/";
    ImageView downloadedimage;
    TextView veventLabel,veventLocation,veventDate,veventOrganizer,veventCategory,veventId,veventDetails,attendingtext, participantnumber;
    Button attendingbutton;
    String eventId, eventLabel, eventLocation, eventDate, eventOrganizer, eventCategory,eventDetails,eventLatitude, eventLongitude, username;
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
        attendingbutton=(Button) findViewById(R.id.going);
        attendingtext=(TextView) findViewById(R.id.attend);
        participantnumber=(TextView) findViewById(R.id.goingnumber);

        Intent intent = getIntent();
        username=intent.getStringExtra("username");
        eventId=intent.getStringExtra("eventId");
        eventLabel=intent.getStringExtra("eventLabel");
        eventLocation=intent.getStringExtra("eventLocation");
        eventDate=intent.getStringExtra("eventDate");
        eventOrganizer=intent.getStringExtra("eventOrganizer");
        eventCategory=intent.getStringExtra("eventCategory");
        listenerFunction();
        checkgoing();
        downloadedimage=(ImageView) findViewById(R.id.detaildownloadedimage);
        new Downloadimage(eventLabel).execute();
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


    public void showlocation(View view)
    {
        Intent i= new Intent(this,ShowLocation.class);
        i.putExtra("eventname",eventLabel);
        i.putExtra("latitude",eventLatitude);
        i.putExtra("longitude",eventLongitude);
        startActivity(i);

    }

    public void attendingevents(View view)
    {
        attendingbutton.setVisibility(View.GONE);
        attendingtext.setVisibility(View.GONE);
        Response.Listener<String> responselistener= new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String number= Integer.toString(jsonObject.getInt("participants"));
                    if(success){
                        participantnumber.setText(number);
                        Log.e(STRING_TAG,"insideSuccess");
                        String toastMesg = "See you there.";
                        Toast toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if (v != null) v.setGravity(Gravity.CENTER);
                        toast.show();

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
            AttendingEventRequest attendingEventRequest=new AttendingEventRequest(username, eventId ,responselistener);
            RequestQueue queue = Volley.newRequestQueue(EventDetails.this);
            queue.add(attendingEventRequest);
        }
    }

    public void checkgoing(){
        Response.Listener<String> responselistener= new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    boolean going= jsonObject.getBoolean("going");
                    String number= Integer.toString(jsonObject.getInt("participants"));
                    if(success){
                        if(going){
                            attendingbutton.setVisibility(View.GONE);
                            attendingtext.setVisibility(View.GONE);
                        }
                        participantnumber.setText(number);

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
            ParticipantRequest participantRequest=new ParticipantRequest(username, eventId ,responselistener);
            RequestQueue queue = Volley.newRequestQueue(EventDetails.this);
            queue.add(participantRequest);
        }
    }
    public void listenerFunction(){
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
                        eventLatitude=jsonObject.getString("event_lat");
                        eventLongitude=jsonObject.getString("event_long");
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


    //For retrieving the image of event.
    private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
    {
        String name;
        public Downloadimage(String name)
        {
            this.name=name;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            if(name.contains(" "))
            {
                name = name.replaceAll(" ", "_");
            }
            String url=SERVER_ADDRESS+"pictures/eventimages/"+name+".JPG";
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
}
