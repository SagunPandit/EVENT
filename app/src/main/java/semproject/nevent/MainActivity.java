package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    final String STRING_TAG = "MainActivity";
    public static String PreferenceFile = "neventpreff" ;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(STRING_TAG,"Check");
        if(checkConnection(this)){
            sharedpreferences = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
            String username= sharedpreferences.getString("username","");
            String password= sharedpreferences.getString("password","");
            if(username.isEmpty()|| password.isEmpty()){
                Log.e(STRING_TAG,"splash");
                Intent intent= new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
                finish();
            }
            else{
                listenerFunction(username,password);
            }
        }
        /*if(hasInternetAccess(this)){
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }*/
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

   /* public boolean checkInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public boolean hasInternetAccess(Context context) {
        if (checkInternet(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(STRING_TAG, "Error checking internet connection", e);
            }
        } else {
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
        return false;
    }*/

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
