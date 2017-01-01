package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import static semproject.nevent.MainActivity.PreferenceFile;

public class UserDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {
    final String STRING_TAG = "UserDetails";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
    }
    public void logout(View view){
        if(checkConnection(this)){
            sharedpreferences = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent= new Intent(UserDetails.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
    }
    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
        return isConnected;
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent= new Intent(this,InternetConnection.class);
            startActivity(intent);
            finish();
        }
    }

}
