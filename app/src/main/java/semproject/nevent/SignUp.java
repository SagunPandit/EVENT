package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.R.attr.button;


public class SignUp extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener{
    final String STRING_TAG = "SignUp";
    private static final int RESULT_LOAD_IMAGE=1;
    private static final int MAX_WIDTH=1024;
    private static final int SELECT_PICTURE = 1;
    private static final String SERVER_ADDRESS="http://avashadhikari.com.np/";
    TextView image;
    String encodedImage;
    ImageView imagetoupload;
    EditText username, password, email, confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        image=(TextView) findViewById(R.id.image);
        imagetoupload=(ImageView) findViewById(R.id.imagetoupload);
        username = (EditText) findViewById(R.id.username_register);
        password = (EditText) findViewById(R.id.password_register);
        email = (EditText) findViewById(R.id.email_register);
        confpassword = (EditText) findViewById(R.id.confpassword_register);
        image.setOnClickListener(this);
    }

    //Override For uploading the user image.
      /* Intent pickIntent = new Intent();
          pickIntent.setType("image/*");
          pickIntent.setAction(Intent.ACTION_GET_CONTENT);

          Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

          String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
          Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
          chooserIntent.putExtra
                  (
                          Intent.EXTRA_INITIAL_INTENTS,
                          new Intent[] { takePhotoIntent }
                  );

          startActivityForResult(chooserIntent, SELECT_PICTURE);*/

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
                    final Bitmap imageupload=((BitmapDrawable)imagetoupload.getDrawable()).getBitmap();
                    new UploadImage(imageupload, fusername).execute();


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
                    RegisterRequest registerRequest = new RegisterRequest(fusername, femail, fpassword, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(this);
                    queue.add(registerRequest); //automatically start the string request on the queue
                }
            }
        }


    }

    private class UploadImage extends AsyncTask<Void,Void,Void>
    {
        Bitmap image;
        String username;
        public UploadImage(Bitmap imageupload, String username)
        {
            this.image=imageupload;
            this.username=username;
            Log.e("USername",username);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            int height=image.getHeight();
            int width=image.getWidth();
            int newheight;
            float ratio = -1.0F;

            if(height == width) {
                newheight = MAX_WIDTH ;
            }
            else
            {
                ratio = (float) height/ (float)width;
                newheight = (int) (MAX_WIDTH*ratio);
            }

            Log.d("RAtio", "value" + ratio);

            Bitmap resized = Bitmap.createScaledBitmap(image, MAX_WIDTH, newheight, true);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
            encodedImage=Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> datatosend=new ArrayList<>();
            datatosend.add(new BasicNameValuePair("image",encodedImage));
            datatosend.add(new BasicNameValuePair("name",username));

            HttpParams httpRequestParams=getHttpRequestParams();
            HttpClient client=new DefaultHttpClient(httpRequestParams);
            HttpPost post=new HttpPost(SERVER_ADDRESS + "Userphoto.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(datatosend));
                client.execute(post);

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    private HttpParams getHttpRequestParams()
    {
        HttpParams httpRequestParams=new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
        HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
        return httpRequestParams;
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


