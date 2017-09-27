package com.rollingdice.deft.android.tab;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Customer;

/**
 * Created by sudarshan on 08/02/2016.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    Context thisContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            String action = intent.getAction();
            //int pid = intent.getIntExtra("pid",0);

            thisContext = GlobalApplication.context;
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) thisContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            WifiManager wifi = (WifiManager) thisContext.getSystemService(Context.WIFI_SERVICE);

            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (action.equals("android.intent.action.PACKAGE_FIRST_LAUNCH"))
            {
                Intent service = new Intent(thisContext, SocketCommunicationService.class);
                thisContext.startService(service);
                Intent shedularService=new Intent(thisContext,SchedularSevice.class);
                thisContext.startService(shedularService);

            } else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE"))
            {
                if (activeNetInfo != null
                        && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (!check()) {
                        Toast.makeText(thisContext, "Wifi Connected!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(thisContext, SocketCommunicationService.class);
                        thisContext.startService(i);

                        /*Intent close = new Intent(thisContext, MyActivity.class);
                        close.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        close.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        close.putExtra("close_activity",false);
                        thisContext.startActivity(close);*/
                    }else {
                        Toast.makeText(thisContext, "Service Alredy Started", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (check()) {
                        String currentStatus = "Wifi Disconnected!";

                        try {
                            /*Intent intent1 = new Intent(thisContext, MyActivity.class);
                            intent1.putExtra("Error", currentStatus);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            thisContext.startActivity(intent1);*/

                        } catch (Exception e) {
                            e.getMessage();
                        }

                        Toast.makeText(thisContext, "Wifi Disconnected!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(thisContext, SocketCommunicationService.class);
                        thisContext.stopService(i);
                    }else {
                        Toast.makeText(thisContext, "Service Alredy Started", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (action.equals("ServiceStopBroadcast") && wifi.isWifiEnabled()) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!check()) {
                    Toast.makeText(thisContext, "Trying to Reconnect...", Toast.LENGTH_SHORT).show();
                    Intent tempIntent = new Intent(thisContext, SocketCommunicationService.class);
                    thisContext.startService(tempIntent);
                }
                else
                {
                    Toast.makeText(thisContext, "Stopping Service...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(thisContext, SocketCommunicationService.class);
                    thisContext.stopService(i);
                }
            } else if (action.equals("ServiceStartBroadcast") && wifi.isWifiEnabled()) {
                if (!check()) {
                    Toast.makeText(thisContext, "Starting Service...", Toast.LENGTH_SHORT).show();
                    Intent tempIntent = new Intent(thisContext, SocketCommunicationService.class);
                    thisContext.startService(tempIntent);
                }else {
                    Toast.makeText(thisContext, "Service Alredy Started", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(NullPointerException ne)
        {
            DatabaseReference errorRef = GlobalApplication.firebaseRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = "Deal with this Null Exception:" +  ne.getMessage();
            errorRef.setValue(currentStatus);
        }
        catch(Exception ex)
        {
            DatabaseReference errorRef = GlobalApplication.firebaseRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = "Deal with this:" + ex.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    public boolean check()
    {
        ActivityManager manager = (ActivityManager) thisContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {

            if ("com.rollingdice.deft.android.tab.SocketCommunicationService"
                    .equals(service.service.getClassName()))
            {
                /*int pid = service.pid;
                android.os.Process.killProcess(pid);*/
                return true;
            }
        }
        return false;
    }

    SocketCommunicationService myService;
    boolean isBound = false;

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketCommunicationService.SocketCommunicationServiceBinder binder = (SocketCommunicationService.SocketCommunicationServiceBinder) service;
            myService = binder.getSocketCommunicationService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };
}
