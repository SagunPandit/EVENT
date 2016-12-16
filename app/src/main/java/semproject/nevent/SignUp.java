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
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        /* int success = jsonResponse.getInt("success");
                           Log.e(STRING_TAG,Integer.toString(success));
                           switch (success){
                            case 0:
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                                break;
                            case 1:
                                Log.e(STRING_TAG, "Success");
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                toastMesg = "This email is already registered";
                                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                v = (TextView) toast.getView().findViewById(android.R.id.message);
                                if (v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                                break;
                            case 3:
                                toastMesg = "This username is already taken.";
                                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                v = (TextView) toast.getView().findViewById(android.R.id.message);
                                if (v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                                break;
                            default:
                                break;
                        }*/
                        if (success) {
                            Log.e(STRING_TAG, "Success");
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
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
            RequestQueue queue = Volley.newRequestQueue(SignUp.this);
            queue.add(registerRequest);
        }

    }
}

