package semproject.nevent;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 3/9/2017.
 */

public class ParticipantRequest extends StringRequest {
    final String STRING_TAG= "RecyclerRequest";
    private static final String REGISTER_REQUEST_URL = "http://avashadhikari.com.np/Extract.php";
    private Map<String, String> params;//maps key to value dont have fixed size any number of values can be stored.

    public ParticipantRequest(String username, String eventid, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data

        params = new HashMap<>();
        params.put("username",username);
        params.put("eventId",eventid);
        params.put("check","participant");

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
