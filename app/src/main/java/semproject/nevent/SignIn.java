package semproject.nevent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static semproject.nevent.MainActivity.PreferenceFile;

public class SignIn extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener  {
    final String STRING_TAG = "SignIn";
    SharedPreferences sharedpreferences;
    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void register(View view){
        Intent i= new Intent(this, SignUp.class);
        startActivity(i);
    }

    public void signin(View view){
        if(checkConnection(this)) {
            username = (EditText) findViewById(R.id.username_main);
            password = (EditText) findViewById(R.id.password_main);

            if ((username.getText().toString().isEmpty() || password.getText().toString().isEmpty())) {
                String toastMesg = "Please enter username and password to login";
                Toast toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            } else {
                listenerFunction(username.getText().toString(), password.getText().toString());
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

    public void listenerFunction(String username, String password){
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    Log.e(STRING_TAG,jsonObject.getString("inside"));
                    boolean success = jsonObject.getBoolean("success");
                    Log.e(STRING_TAG,jsonObject.getString("inside"));
                    if(success){
                        String username= jsonObject.getString("username");
                        String email= jsonObject.getString("email");
                        String password = jsonObject.getString("password");

                        //using sharedpreference to store username and password
                        sharedpreferences = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply();

                        Intent intent= new Intent(SignIn.this,HomePage.class);
                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(SignIn.this);
                        builder.setMessage("Login Failed")
                                .setNegativeButton("Retry",null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest=new LoginRequest(username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SignIn.this);
        queue.add(loginRequest);
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

