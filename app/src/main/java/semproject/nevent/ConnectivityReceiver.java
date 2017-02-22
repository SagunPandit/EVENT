package semproject.nevent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by User on 1/1/2017.
 */

public class ConnectivityReceiver extends BroadcastReceiver {
    final static String STRING_TAG = "ConnectivityReceiver";

        public static ConnectivityReceiverListener connectivityReceiverListener;

        public ConnectivityReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent arg1) {
            Log.e(STRING_TAG,"onReceive");
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();

            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }
        }

        public static boolean isConnected(Context context) {
            Log.e(STRING_TAG,"isConnected");
            ConnectivityManager
                    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();
        }


        public static boolean hasInternetAccess(Context context) {
            if (isConnected(context)) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                } catch (IOException e) {
                    Log.e(STRING_TAG, "Error checking internet connection", e);
                }
            }
            return false;
        }


        public interface ConnectivityReceiverListener {
            void onNetworkConnectionChanged(boolean isConnected);
        }
}
