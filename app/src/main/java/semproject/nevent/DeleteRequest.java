package semproject.nevent;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 12/30/2016.
 */

public class DeleteRequest extends StringRequest {
    final String STRING_TAG= "RecyclerRequest";
    private static final String REGISTER_REQUEST_URL = "http://avashadhikari.com.np/Delete.php";
    private Map<String, String> params; //maps key to value dont have fixed size any number of values can be stored.

    public DeleteRequest(String username,String eventname,String eventdate,String eventcategory, String eventlocation, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        Log.e(STRING_TAG,username);
        params = new HashMap<>();
        params.put("username",username);
        params.put("eventname",eventname);
        params.put("eventdate",eventdate);
        params.put("eventcategory",eventcategory);
        params.put("eventlocation",eventlocation);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
