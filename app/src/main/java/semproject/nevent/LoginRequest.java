package semproject.nevent;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 12/16/2016.
 */

public class LoginRequest extends StringRequest {
    final String STRING_TAG= "LoginRequest";
    private static final String LOGIN_REQUEST_URL = "http://avashadhikari.com.np/Login.php";
    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        Log.e(STRING_TAG,"Inside");
        Log.e(STRING_TAG,username);
        Log.e(STRING_TAG,password);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
