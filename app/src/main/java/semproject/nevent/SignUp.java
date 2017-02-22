package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class SignUp extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener{
    final String STRING_TAG = "SignUp";
    private static final int RESULT_LOAD_IMAGE=1;
    ImageView imagetoupload;
    String encodedImage;
    EditText username, password, email, confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        imagetoupload=(ImageView) findViewById(R.id.imagetoupload);
        username = (EditText) findViewById(R.id.username_register);
        password = (EditText) findViewById(R.id.password_register);
        email = (EditText) findViewById(R.id.email_register);
        confpassword = (EditText) findViewById(R.id.confpassword_register);
        imagetoupload.setOnClickListener(this);






    }


    //Override For uploading the user image.
    @Override
    public void onClick(View v) {
        Intent galleryintent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryintent, RESULT_LOAD_IMAGE);

    }
    //for showing the selected image.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            Uri selectedimage=data.getData();
            imagetoupload.setImageURI(selectedimage);
        }
    }


    public void signup(View view) {
        String toastMesg;
        Toast toast;
        TextView v;
        Bitmap image=((BitmapDrawable) imagetoupload.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        encodedImage=Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        Log.e("Signup image to text",encodedImage);

        if(checkConnection(this)) {
            if (password.getText().toString().isEmpty() || confpassword.getText().toString().isEmpty() || username.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
                toastMesg = "All fields must be filled.";
                toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
            else {
                if (!(password.getText().toString()).equals(confpassword.getText().toString())) {
                    toastMesg = "Please enter your password correctly.";
                    toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                    v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                } else {
                    final String fusername = username.getText().toString();
                    final String femail = email.getText().toString();
                    final String fpassword = password.getText().toString();
                    final String encodedimage = encodedImage;



                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            String toastMesg;
                            Toast toast;
                            TextView v;
                            try {
                                Log.e(STRING_TAG, "Try");
                                JSONObject jsonResponse = new JSONObject(response);

                                int success = jsonResponse.getInt("success");
                                Log.e(STRING_TAG, Integer.toString(success));
                                switch (success) {
                                    case 0:
                                        toastMesg = "Enter valid email format";
                                        toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                        v = (TextView) toast.getView().findViewById(android.R.id.message);
                                        if (v != null) v.setGravity(Gravity.CENTER);
                                        toast.show();
                                        break;
                                    case 1:
                                        Log.e(STRING_TAG, "Success");
                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);
                                        toastMesg = "Congratulations!! You have been registered successfully.";
                                        toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_LONG);
                                        v = (TextView) toast.getView().findViewById(android.R.id.message);
                                        if (v != null) v.setGravity(Gravity.CENTER);
                                        toast.show();
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
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    RegisterRequest registerRequest = new RegisterRequest(fusername, femail, fpassword, encodedimage, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(this);
                    queue.add(registerRequest); //automatically start the string request on the queue
                }
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

