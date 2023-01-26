package com.example.socion.HelpingClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {
    public static String u_name,u_password,u_phone,u_profile,uid;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = networkInfo != null && networkInfo.isConnected();
        return isAvailable;
    }
}


