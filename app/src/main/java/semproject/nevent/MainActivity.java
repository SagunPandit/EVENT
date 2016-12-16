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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    final String STRING_TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void register(View view){
        Intent i= new Intent(this, SignUp.class);
        startActivity(i);
    }

    public void signin(View view){
        final EditText username= (EditText) findViewById(R.id.username_main);
        final EditText password= (EditText) findViewById(R.id.password_main);

        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        String username= jsonObject.getString("username");
                        String email= jsonObject.getString("email");
                        Intent intent= new Intent(MainActivity.this,HomePage.class);
                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        startActivity(intent);
                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
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

        if((username.getText().toString().isEmpty()|| password.getText().toString().isEmpty())){
            String toastMesg = "Please enter username and password to login";
            Toast toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
        else{
            LoginRequest loginRequest=new LoginRequest(username.getText().toString(),password.getText().toString(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginRequest);
        }

    }
}
