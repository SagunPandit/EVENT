package semproject.nevent;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aayush on 3/9/2017.
 */

public class AttendingEventRequest extends StringRequest {
    final String STRING_TAG= "RecyclerRequest";
    private static final String REGISTER_REQUEST_URL = "http://avashadhikari.com.np/Extract.php";
    private Map<String, String> params;//maps key to value dont have fixed size any number of values can be stored.

    public AttendingEventRequest(String username, String eventname, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data

        params = new HashMap<>();
        params.put("username",username);
        params.put("eventname",eventname);
        params.put("check","attending");

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
