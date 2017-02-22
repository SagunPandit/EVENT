package semproject.nevent;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 12/14/2016.
 */

public class RegisterRequest extends StringRequest{
    final String STRING_TAG= "RegisterRequest";
    private static final String REGISTER_REQUEST_URL = "http://avashadhikari.com.np/Register.php";
    private Map<String, String> params; //maps key to value dont have fixed size any number of values can be stored.

    public RegisterRequest(String username, String email, String password, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        Log.e(STRING_TAG,"Inside");
        params = new HashMap<>();
        params.put("username", username);
        params.put("email",email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}