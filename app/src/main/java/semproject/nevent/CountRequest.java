package semproject.nevent;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 1/23/2017.
 */

public class CountRequest extends StringRequest {
    final String STRING_TAG= "CountRequest";
    private static final String REGISTER_REQUEST_URL = "http://avashadhikari.com.np/Extract.php";
    private Map<String, String> params;//maps key to value dont have fixed size any number of values can be stored.

    public CountRequest(String eventname,Integer viewcount, String check,Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        Log.e(STRING_TAG,eventname);
        Log.e(STRING_TAG,String.valueOf(viewcount));
        params = new HashMap<>();
        params.put("username",eventname);
        params.put("check",check);
        params.put("viewcount",String.valueOf(viewcount));

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}

