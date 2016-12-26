package semproject.nevent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Upload extends AppCompatActivity {


    final String STRING_TAG = "Upload";
    String username;
    EditText event_name, location, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.e(STRING_TAG,username);
        event_name = (EditText) findViewById(R.id.event_name);
        location = (EditText) findViewById(R.id.location);
        date = (EditText) findViewById(R.id.date);

    }

    public void upload(View view)
    {
        String toastMesg;
        Toast toast;
        TextView v;
        Log.e(STRING_TAG,username);
        if(event_name.getText().toString().isEmpty() || location.getText().toString().isEmpty() || date.getText().toString().isEmpty())
        {
            toastMesg = "All fields must be filled.";
            toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
        else
        {
            final String fevent_name = event_name.getText().toString();
            final String flocation = location.getText().toString();
            final String fdate = date.getText().toString();

            Response.Listener<String> responseListener = new Response.Listener<String>()
            {

                @Override
                public void onResponse(String response)
                {
                    String toastMesg;
                    Toast toast;
                    TextView v;
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int success = jsonResponse.getInt("success");
                        Log.e(STRING_TAG,Integer.toString(success));
                        switch (success) {
                            case 0:
                                toastMesg = "Event Name already exists.";
                                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                v = (TextView) toast.getView().findViewById(android.R.id.message);
                                if (v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                                break;
                            case 1:
                                Log.e(STRING_TAG, "Success");
                                Intent intent = new Intent(Upload.this, HomePage.class);
                                startActivity(intent);
                                toastMesg = "Congratulations!! You have successfully created an event.";
                                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_LONG);
                                v = (TextView) toast.getView().findViewById(android.R.id.message);
                                if (v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                                break;

                            default:
                                break;
                        }

                        }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }

                }
            };



            UploadRequest uploadRequest = new UploadRequest(fevent_name, flocation, fdate, username,responseListener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(uploadRequest);//automatically start the string request on the queue

        }
    }
}
