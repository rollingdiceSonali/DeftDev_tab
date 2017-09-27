package com.rollingdice.deft.android.tab;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.user.UserSchedularActivity;

/**
 * Created by Rolling Dice on 1/8/2016.
 */
public class NotificationBroadCastReceiver extends BroadcastReceiver
{
    DatabaseReference localRef = GlobalApplication.firebaseRef;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
            String OK_ACTION = "com.rollingdice.homeautomatin.tab.user.UserListOfAllScenesActivity.OK_ACTION";
            String CANCEL_ACTION = "com.rollingdice.homeautomatin.tab.user.UserListOfAllScenesActivity.CANCEL_ACTION";

            Bundle extras = intent.getExtras();
            int notify_Id = extras.getInt("NOTIFICATION_ID");
            final String sceneID = extras.getString("sceneId");
            final String sceneName = extras.getString("sceneName");

            Toast.makeText(UserSchedularActivity.appcontext, "" + notify_Id, Toast.LENGTH_SHORT).show();

            Toast.makeText(UserSchedularActivity.appcontext, "in OnReceive", Toast.LENGTH_SHORT).show();

            String action = intent.getAction();


            Toast.makeText(UserSchedularActivity.appcontext, "" + action, Toast.LENGTH_SHORT).show();


            if (OK_ACTION.equals(action))
            {
                NotificationManager manager = (NotificationManager) UserSchedularActivity.appcontext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notify_Id);
                if(sceneID!=null) {

                    DatabaseReference deActive = localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails").child(sceneID).child("isActivated");
                    deActive.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            mutableData.setValue(0);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                            Toast.makeText(UserSchedularActivity.appcontext, sceneName + " Scene is DeActivated", Toast.LENGTH_SHORT).show();

                        }
                    });

                    Toast.makeText(UserSchedularActivity.appcontext, "OK", Toast.LENGTH_LONG).show();
                }


            } else if (CANCEL_ACTION.equals(action)) {


                NotificationManager manager = (NotificationManager) UserSchedularActivity.appcontext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notify_Id);
                Toast.makeText(UserSchedularActivity.appcontext, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }


}


