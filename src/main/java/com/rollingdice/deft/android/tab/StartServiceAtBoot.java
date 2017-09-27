package com.rollingdice.deft.android.tab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Customer;

/**
 * Created by koushikpal on 03/10/15.
 */
public class StartServiceAtBoot extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        DatabaseReference localRef = GlobalApplication.firebaseRef;
        try {
            context.startService(new Intent(context, SocketCommunicationService.class));

            context.startService(new Intent(context,SchedularSevice.class));
        }catch (Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }
}
