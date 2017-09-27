package com.rollingdice.deft.android.tab;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.SceneDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Scene;
import com.rollingdice.deft.android.tab.model.SceneDetails;
import com.rollingdice.deft.android.tab.user.MyAlarmManager;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Rolling Dice on 11/11/2016.
 */
public class SchedularSevice extends Service
{
    Context thisContext;

    DatabaseReference localRef = GlobalApplication.firebaseRef;
    DatabaseReference sceneDetailsRef;
    ValueEventListener sceneDetailsEventListner;


    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {
            sceneDetailsRef=localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails");

            schedularStateChangeOnline();

        }catch(Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
        return Service.START_STICKY;
    }


    private void schedularStateChangeOnline()
    {
        try {
            final MyAlarmManager myAlarmManager = new MyAlarmManager();
            sceneDetailsEventListner = sceneDetailsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //sceneDetailsList = new ArrayList<SceneDetails>();
                   for (DataSnapshot sceneId : dataSnapshot.getChildren()) {
                            String id = sceneId.getKey();
                            for (DataSnapshot sceneDetails : sceneId.getChildren()) {
                                String sceneName = sceneId.child("sceneName").getValue(String.class);
                                String startTime = sceneId.child("startTime").getValue(String.class);
                                String endTime = sceneId.child("endTime").getValue(String.class);
                                String days = sceneId.child("listOfDays").getValue(String.class);
                                Integer isActivated = sceneId.child("isActivated").getValue(Integer.class);
                                boolean isRepeating = sceneId.child("isRepeating").getValue(Boolean.class);
                                boolean isAlarmSet = sceneId.child("isAlarmSet").getValue(Boolean.class);

                                if (sceneDetails.getRef().toString().contains("sceneConfigDetails")) {
                                    for (DataSnapshot sceneConfigId : sceneDetails.getChildren()) {
                                        String scene_Config_Id = sceneConfigId.getKey();
                                        String appStartTime = sceneConfigId.child("appStartTime").getValue(String.class);
                                        String appEndTime = sceneConfigId.child("appEndTime").getValue(String.class);
                                        String applianceId = sceneConfigId.child("applianceId").getValue(String.class);
                                        String applianceName = sceneConfigId.child("applianceName").getValue(String.class);
                                        String roomId = sceneConfigId.child("roomId").getValue(String.class);
                                        String slaveId = sceneConfigId.child("slaveId").getValue(String.class);
                                        int onPendingIntentId = sceneConfigId.child("onPendingIntentId").getValue(Integer.class);
                                        int offPendingIntentId = sceneConfigId.child("offPendingIntentId").getValue(Integer.class);
                                        String day = sceneConfigId.child("day").getValue(String.class);
                                        String applianceType = sceneConfigId.child("applianceType").getValue(String.class);
                                        int curtainLevel = sceneConfigId.child("curtainLevel").getValue(Integer.class);

                                        SceneDetails scene = new SceneDetails(id, sceneName, startTime, endTime, days, isRepeating, appStartTime, appEndTime, applianceId,
                                                applianceName, roomId, onPendingIntentId, offPendingIntentId, scene_Config_Id, isActivated, isAlarmSet, day, applianceType, curtainLevel);
                                        if (!sceneName.equals("Create New Scene")) {
                                            if (isActivated == 0 && isAlarmSet) {

                                                myAlarmManager.CancelAlarm(id, roomId, applianceId, onPendingIntentId,slaveId);
                                                if (offPendingIntentId != 0) {
                                                    myAlarmManager.CancelAlarm(id, roomId, applianceId, offPendingIntentId,slaveId);

                                                }
                                                DatabaseReference sceneRef = localRef.child("scene").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("sceneDetails").child(id).child("isAlarmSet");
                                                sceneRef.setValue(false);
                                                List<Scene> s = SceneDataHelper.getScene(id);
                                                s.get(0).isActivated = 1;
                                                s.get(0).save();
                                                Toast.makeText(SchedularSevice.this, "Alarm cancalled for " + sceneName, Toast.LENGTH_SHORT).show();
                                            } else if (isActivated == 1 && !isAlarmSet) {
                                                Calendar calendar = setCalender(appStartTime, day);
                                                myAlarmManager.SetAlarm(calendar, id, roomId, applianceId, "true", onPendingIntentId, applianceType, curtainLevel,slaveId);
                                                if (offPendingIntentId != 0) {

                                                    //String app_end_time = sceneDetails.getAppEndTime();
                                                    Calendar calendar1 = setCalender(appEndTime, day);
                                                    myAlarmManager.SetAlarm(calendar1, id, roomId, applianceId, "false", offPendingIntentId, applianceType, curtainLevel,slaveId);
                                                }
                                                DatabaseReference sceneRef = localRef.child("scene").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("sceneDetails").child(id).child("isAlarmSet");
                                                sceneRef.setValue(true);

                                                List<Scene> s = SceneDataHelper.getScene(id);
                                                s.get(0).isActivated = 1;
                                                s.get(0).save();
                                                Toast.makeText(SchedularSevice.this, "Alarm Set for " + sceneName, Toast.LENGTH_SHORT).show();

                                            } else {
                                               // Toast.makeText(getApplicationContext(), "Do nothng for   " + sceneName, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }
                        }

                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }

            });
        }
        catch(Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


    private Calendar setCalender(String time, String day)
    {
        Calendar calendar = Calendar.getInstance();
        try {

            String c_time[] = time.split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(c_time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(c_time[1]));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            switch (day)
            {
                case "Sunday":
                    calendar.set(Calendar.DAY_OF_WEEK, 1);
                    break;
                case "Monday":
                    calendar.set(Calendar.DAY_OF_WEEK, 2);
                    break;
                case "Tuesday":
                    calendar.set(Calendar.DAY_OF_WEEK, 3);
                    break;
                case "Wednsday":
                    calendar.set(Calendar.DAY_OF_WEEK, 4);
                    break;
                case "Thursday":
                    calendar.set(Calendar.DAY_OF_WEEK, 5);
                    break;
                case "Friday":
                    calendar.set(Calendar.DAY_OF_WEEK, 6);
                    break;
                case "Saturday":
                    calendar.set(Calendar.DAY_OF_WEEK, 7);
                    break;
                default:
                    break;
            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


        return calendar;
    }

}
