package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MainActivity extends AppCompatActivity {
    final String STRING_TAG = "MainActivity";
    public static String PreferenceFile = "neventpreff" ;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
        String username= sharedpreferences.getString("username",NULL);
        String password= sharedpreferences.getString("password",NULL);
        if(username.isEmpty()|| password.isEmpty()){
            Intent intent= new Intent(MainActivity.this,SignIn.class);
            startActivity(intent);
            finish();
        }
        else{
            listenerFunction(username,password);
        }
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

                        Intent intent= new Intent(MainActivity.this,HomePage.class);
                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        startActivity(intent);
                        finish();
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
        LoginRequest loginRequest=new LoginRequest(username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(loginRequest);
    }
}
