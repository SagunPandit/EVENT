package semproject.nevent;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

import static java.lang.System.exit;


public class SignUp extends AppCompatActivity {
    final String STRING_TAG = "SignUp";
    EditText username, password, email, confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = (EditText) findViewById(R.id.username_register);
        password = (EditText) findViewById(R.id.password_register);
        email = (EditText) findViewById(R.id.email_register);
        confpassword = (EditText) findViewById(R.id.confpassword_register);
    }

    public void signup(View view) {

        if (!(password.getText().toString()).equals(confpassword.getText().toString())) {
            String toastMesg = "Please enter your password correctly.";
            Toast toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
        else {
            final String fusername = username.getText().toString();
            final String femail = email.getText().toString();
            final String fpassword = password.getText().toString();

            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    String toastMesg;
                    Toast toast;
                    TextView v;
                    try {
                        Log.e(STRING_TAG, "Try");
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        Log.e(STRING_TAG, Boolean.toString(success));
                        if (success) {
                            Log.e(STRING_TAG, "Successs "+Boolean.toString(jsonResponse.getBoolean("emailerror")));
                            if(jsonResponse.getBoolean("emailerror")){
                                toastMesg = "Enter valid email format";
                                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                v = (TextView) toast.getView().findViewById(android.R.id.message);
                                if (v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                            }
                            else {
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                startActivity(intent);
                            }

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                            builder.setMessage("Register Failed")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RegisterRequest registerRequest = new RegisterRequest(fusername, femail, fpassword, responseListener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(registerRequest); //automatically start the string request on the queue
        }

    }
}

