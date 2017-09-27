package com.rollingdice.deft.android.tab;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.List;

/**
 * Created by deft on 25/10/2016.
 */
public class InternetDetector extends BroadcastReceiver {
    public InternetDetector() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isVisible = isActivityVisible(context);

        try {
            if (isVisible == true) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                // Check internet connection and accrding to state change the
                // text of activity by calling method
                if ((networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                    Toast.makeText(context, "Internet Available", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isActivityVisible(Context context)
    {
        boolean isVisible = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            if(procInfos.get(i).processName.equals("com.rollingdice.deft.android.tab"))
            {
               isVisible = true;
            }
        }
        return isVisible;
    }
}
