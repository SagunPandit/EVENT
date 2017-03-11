package semproject.nevent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static semproject.nevent.MainActivity.PreferenceFile;


public class Upload extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    final String STRING_TAG = "Upload";
    String username,category_name;
    private static final int RESULT_LOAD_IMAGE=1;
    public static String PreferenceFiles = "uploadsharedpreference";
    SharedPreferences sharedpreferences;
    private static final int MAX_WIDTH=1080;
    String encodedImage;
    double longitude, latitude;
    private static final String SERVER_ADDRESS="http://avashadhikari.com.np/";
    LatLng latlang = null;
    TextView image;
    EditText event_name, location, details;
    ImageView imagetoupload;
    public static String eventdate="";
    DatePickerFragment datePickerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        sharedpreferences = getSharedPreferences(PreferenceFiles, Context.MODE_PRIVATE);


        event_name = (EditText) findViewById(R.id.event_name);
        location = (EditText) findViewById(R.id.location);
        details=(EditText) findViewById(R.id.details);

        //Storing the data in shared preferences to these variables.
        event_name.setText(sharedpreferences.getString("event_name",""));
        location.setText(sharedpreferences.getString("location",""));
/*        date.setText(sharedpreferences.getString("date",""));*/
        details.setText(sharedpreferences.getString("details",""));

        image=(TextView) findViewById(R.id.image);
        imagetoupload=(ImageView) findViewById(R.id.imagetoupload);

        Intent intent = getIntent();
        if(intent.getStringExtra("username")!=null)
        {
            username = intent.getStringExtra("username");
            Log.e(STRING_TAG,username);
        }
        else
        {
            username=sharedpreferences.getString("username","");
        }

         longitude=intent.getDoubleExtra("longitude",0.00);
         latitude=intent.getDoubleExtra("latitude",0.00);

         image.setOnClickListener(this);





    }

    public void locationbutton(View view)
    {
        //using sharedpreference to store event details.
        sharedpreferences = getSharedPreferences(PreferenceFiles, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username",username);
        editor.putString("event_name", event_name.getText().toString());
        editor.putString("location", location.getText().toString());
        editor.putString("date",eventdate);
        editor.putString("details",details.getText().toString());
        editor.apply();

        Intent intent= new Intent(this,NMapsActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        Intent galleryintent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryintent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            Uri selectedimage=data.getData();
            imagetoupload.setImageURI(selectedimage);
        }
    }

    public void setEventDate(View view){
        datePickerFragment=new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(),"EventDate");
    }

    public void upload(View view) {

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        assert spinner != null;
        category_name = spinner.getSelectedItem().toString();

        String toastMesg;
        Toast toast;
        TextView v;
        Log.e(STRING_TAG, username);

        if (event_name.getText().toString().isEmpty() || location.getText().toString().isEmpty() || details.getText().toString().isEmpty()) {
            toastMesg = "All fields must be filled.";
            toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        } else if (eventdate.isEmpty()) {
            toastMesg = "Pick a event date first";
            toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        } else if (latitude ==0.00 && longitude==0.00)
        {
            toastMesg = "Select a location from map";
            toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        } else if (imagetoupload.getDrawable()==null)
        {
            toastMesg = "Select a photo for your event.";
            toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }

         else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(eventdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (new Date().after(strDate)) {
                Log.i("On_Date", "outofdate");
                toastMesg = "Date picked for event is outdated.";
                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            } else {
                Log.i("On_Date", "DateIsValid");
                final String fevent_name = event_name.getText().toString();
                final String flocation = location.getText().toString();
                final String fdate = eventdate;
                final String fdetails = details.getText().toString();
                final Bitmap imageupload = ((BitmapDrawable) imagetoupload.getDrawable()).getBitmap();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        String toastMesg;
                        Toast toast;
                        TextView v;
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int success = jsonResponse.getInt("success");
                            Log.e(STRING_TAG, Integer.toString(success));
                            switch (success) {
                                case 0:
                                    toastMesg = "Event Name already exists.";
                                    toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                    v = (TextView) toast.getView().findViewById(android.R.id.message);
                                    if (v != null) v.setGravity(Gravity.CENTER);
                                    toast.show();
                                    break;
                                case 1:
                                    Log.e("Event name", fevent_name);
                                    new UploadImage(imageupload, fevent_name).execute();
                                    toastMesg = "Congratulations!! You have successfully created an event.";
                                    toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_LONG);
                                    v = (TextView) toast.getView().findViewById(android.R.id.message);
                                    if (v != null) v.setGravity(Gravity.CENTER);
                                    toast.show();
                                    Intent intent = new Intent(Upload.this, HomePage.class);
                                    intent.putExtra("username", username);
                                    sharedpreferences = getSharedPreferences(PreferenceFiles, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    Log.e("Stringdoublelatitude", Double.toString(latitude));
                                    Log.e("Stringdoublelongitude", Double.toString(longitude));
                                    Log.d("NORMALlatitude", "value" + latitude);
                                    Log.d("NORMALlongitude", "value" + longitude);
                                    startActivity(intent);
                                    finish();

                                    break;

                                default:
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                if (checkConnection(this)) {
                    UploadRequest uploadRequest = new UploadRequest(fevent_name, flocation, fdate, category_name, username, fdetails, latitude, longitude, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(this);
                    queue.add(uploadRequest);//automatically start the string request on the queue
                }
            }
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


//For uploading event image.
    private class UploadImage extends AsyncTask<Void,Void,Void>
    {
        Bitmap image;
        String event_name;
        public UploadImage(Bitmap imageupload, String event_name)
        {
            this.image=imageupload;
            this.event_name=event_name;
            Log.e("Event_name",event_name);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Event Image Uploaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            int height=image.getHeight();
            int width=image.getWidth();
            int newheight;
            float ratio = -1.0F;

            if(height == width) {
                newheight = MAX_WIDTH ;
            }
            else
            {
                ratio = (float) height/ (float)width;
                newheight = (int) (MAX_WIDTH*ratio);
            }

            Log.d("RAtio", "value" + ratio);

            Bitmap resized = Bitmap.createScaledBitmap(image, MAX_WIDTH, newheight, true);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
            encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            if(event_name.contains(" "))
            {
                event_name = event_name.replaceAll(" ", "_");
            }
            ArrayList<NameValuePair> datatosend=new ArrayList<>();
            datatosend.add(new BasicNameValuePair("image",encodedImage));
            datatosend.add(new BasicNameValuePair("name",event_name));

            HttpParams httpRequestParams=getHttpRequestParams();
            HttpClient client=new DefaultHttpClient(httpRequestParams);
            HttpPost post=new HttpPost(SERVER_ADDRESS + "Eventphoto.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(datatosend));
                client.execute(post);

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    private HttpParams getHttpRequestParams()
    {
        HttpParams httpRequestParams=new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
        HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
        return httpRequestParams;
    }

    //For selecting eventdate
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        String logMessage;
        int[] date={0,0,0};
        public DatePickerFragment(){}
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),this, year, month, day);
        }


        public void onDateSet(DatePicker view, int year, int month, int day) {
            date[0]=day;
            date[1]=month;
            date[2]=year;
            // Do something with the date chosen by the user
            eventdate=String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
            Log.i("On_Date_Set", eventdate);
            String currentDate= DateFormat.getDateFormat(getContext()).format(new Date());
            Log.i("Current", currentDate);
        }
    /*
        public int[] giveDate(){
            //Log.isLoggable("GIVE_DATE",date[2]);
            return date;
        }*/
    }

}
