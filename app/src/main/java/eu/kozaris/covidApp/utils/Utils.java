package eu.kozaris.covidApp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//This class Just checks for internet Connection
//IT is used in multiple fragments
public class Utils {

    public static boolean internetCheck(Context context) {
        ConnectivityManager ConnectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }
}
