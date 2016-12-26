package semproject.nevent;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aayush on 12/25/2016.
 */
public class UploadRequest extends StringRequest
{
    final String STRING_TAG= "UploadRequest";
    private static final String REGISTER_REQUEST_URL = "http://avashadhikari.com.np/Upload.php";
    private Map<String, String> params; //maps key to value dont have fixed size any number of values can be stored.

    public UploadRequest(String event_name,String location,String date,String username, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        Log.e(STRING_TAG,event_name);
        Log.e(STRING_TAG,location);
        Log.e(STRING_TAG,date);
        params = new HashMap<>();
        params.put("event_name", event_name);
        params.put("location",location);
        params.put("date", date);
        params.put("username",username);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
