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
    private static final String REGISTER_REQUEST_URL = "https://eventmanager.000webhostapp.com/Register.php";
    private Map<String, String> params;

    public RegisterRequest(String username, String email, String password, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
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