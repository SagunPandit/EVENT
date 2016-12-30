package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static semproject.nevent.MainActivity.PreferenceFile;

public class UserDetails extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
    }
    public void logout(View view){
        sharedpreferences = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent= new Intent(UserDetails.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
