package com.rollingdice.deft.android.tab.user;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;

import java.util.Calendar;

/**
 * Created by Rolling Dice on 2/23/2016.
 */
public class MyAlarmManager
{
    AlarmManager alarmManager;
    private DatabaseReference localRef;


    public MyAlarmManager()
    {
        alarmManager = (AlarmManager) GlobalApplication.context.getSystemService(Context.ALARM_SERVICE);
        localRef= GlobalApplication.firebaseRef;

    }

    public void SetAlarm(final Calendar calendar, String scene_Id,String s_roomId, String s_applianceId, String s_state, int pendind_intent_id, String applianceType, int level,String slaveId)
    {
        try
        {
            Intent intent = new Intent(UserSchedularActivity.appcontext, SceneBroadCastReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putString("roomId", s_roomId);
            bundle.putString("applianceId", s_applianceId);
            bundle.putString("state", s_state);
            bundle.putSerializable("calendar", calendar);
            bundle.putInt("pendind_intent_id", pendind_intent_id);
            bundle.putString("applianceType",applianceType);
            bundle.putInt("curtainLevel",level);
            bundle.putString("scene_Id",scene_Id);
            bundle.putString("slaveId",slaveId);
            intent.putExtras(bundle);

            long interaval = DateUtils.MINUTE_IN_MILLIS;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(UserSchedularActivity.appcontext, pendind_intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interaval, pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }

    public void CancelAlarm(String scene_id,String s_roomId,String s_applianceId,int pendingIntentId,String slaveId)
    {
        try {
            Intent cancelIntent = new Intent(UserSchedularActivity.appcontext, SceneBroadCastReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putString("roomId", s_roomId);
            bundle.putString("applianceId", s_applianceId);
            bundle.putString("scene_Id",scene_id);
            bundle.putString("slaveId",slaveId);

            cancelIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(UserSchedularActivity.appcontext, pendingIntentId, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);


        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }
}
