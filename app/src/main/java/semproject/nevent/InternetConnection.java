package semproject.nevent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InternetConnection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection);
    }

    public void retry(View view){
        Intent intent= new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
    }
}
