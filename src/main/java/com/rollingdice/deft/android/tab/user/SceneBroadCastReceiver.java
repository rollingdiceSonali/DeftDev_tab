package com.rollingdice.deft.android.tab.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.GateControllerDetails;
import com.rollingdice.deft.android.tab.model.SensorDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Rolling Dice on 1/15/2016.
 */
public class SceneBroadCastReceiver extends BroadcastReceiver
{
    DatabaseReference localRef,applianceRef,toggleRef;


    private RoomAppliance roomAppliance=null;
    private CurtainDetails curtain;
    private GateControllerDetails gateControllerDetails;
    private DatabaseReference notificationRef ;
    private boolean flag;
    private SensorDetail sensorDetail;
    private ArrayList<SensorDetail> sensor;
    String action;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {

            localRef = GlobalApplication.firebaseRef;
            List<RoomAppliance> appliances = GlobalApplication.applianceList;
            List<CurtainDetails> curtainList =GlobalApplication.curtainList;


            List<SensorDetail> sensorList = GlobalApplication.sensorDetailsList;

            List<GateControllerDetails> gateControllerList =GlobalApplication.gateControllerList;
            final String roomId = intent.getStringExtra("roomId");
            final String applianceId = intent.getStringExtra("applianceId");
            final String state = intent.getStringExtra("state");
            final String slaveId = intent.getStringExtra("slaveId");
            final int pendind_intent_id=intent.getIntExtra("pendind_intent_id", 0);
            final Calendar calendar= (Calendar) intent.getSerializableExtra("calendar");
            final String applianceType=intent.getStringExtra("applianceType");
            final int level=intent.getIntExtra("curtainLevel", 0);
            final  String scene_Id=intent.getStringExtra("scene_Id");
            final Calendar today=Calendar.getInstance();
            today.set(Calendar.SECOND,0);
            today.set(Calendar.MILLISECOND,0);

            action = intent.getAction();


            final MyAlarmManager alarmManager=new MyAlarmManager();

            switch (applianceType) {
                case "Appliances":

                    for (int i = 0; i < appliances.size(); i++) {
                        if (applianceId.equals(appliances.get(i).getId()) && roomId.equals(appliances.get(i).getRoomId()) && slaveId.equals(appliances.get(i).getSlaveId())) {
                            roomAppliance = appliances.get(i);
                        }
                    }
                    break;
                case "Curtains":

                    for (int j = 0; j < curtainList.size(); j++) {
                        if (applianceId.equals(curtainList.get(j).getCurtainId())
                                && roomId.equals(curtainList.get(j).getRoomId())) {
                            curtain = curtainList.get(j);
                        }
                    }
                    break;

                case "Sensor":
                    for (int j = 0; j < sensorList.size(); j++) {
                        if (applianceId.equals(sensorList.get(j).getId())
                                && roomId.equals(sensorList.get(j).getRoomId())) {
                            sensorDetail = sensorList.get(j);
                        }
                    }



                default:
                    for (int k = 0; k < gateControllerList.size(); k++) {
                        if (applianceId.equals(gateControllerList.get(k).getGateControllerId())) {
                            gateControllerDetails = gateControllerList.get(k);
                        }
                    }
                    break;
            }

                if (calendar.get(Calendar.DAY_OF_WEEK) == today.get(Calendar.DAY_OF_WEEK)) {
                    int k = calendar.compareTo(today);
                    if (k == 0) {
                        switch (applianceType) {
                            case "Appliances":
                                if (roomAppliance.isState() != Boolean.parseBoolean(state))
                                {
                                    UpdateFirebase();

                                    if (UserSchedularActivity.Repeating) {
                                        calendar.add(Calendar.DAY_OF_YEAR, 7);
                                        alarmManager.SetAlarm(calendar, scene_Id, roomId, applianceId, state, pendind_intent_id, applianceType, level,slaveId);
                                    }

                                } else {
                                    String msg = "Your Appliance is already " + (Boolean.parseBoolean(state) ? "on" : "off ");
                                    Toast.makeText(UserSchedularActivity.appcontext, msg, Toast.LENGTH_LONG).show();

                                }
                                break;
                            case "Curtains":
                                UpdateFirebaseForCurtain(level);
                               /* if (curtain.isToggle() != (Boolean.parseBoolean(state) ? 0 : 1)) {
                                    UpdateFirebaseForCurtain(level);
                                }*/

                                break;

                            case "Sensor":
                                UpdateFirebaseForSensor();
                                break;


                            default:
                                if (gateControllerDetails.isToggle() != (Boolean.parseBoolean(state) ? 1 : 0)) {
                                    updateFirebaseForgateController();
                                } else {
                                    String msg = "Your Appliance is already " + (Boolean.parseBoolean(state) ? "on" : "off ");
                                    Toast.makeText(UserSchedularActivity.appcontext, msg, Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    } else {
                        calendar.add(Calendar.DAY_OF_YEAR, 7);
                        alarmManager.SetAlarm(calendar, scene_Id, roomId, applianceId, state, pendind_intent_id, applianceType, level,slaveId);
                    }

                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                    alarmManager.SetAlarm(calendar, scene_Id, roomId, applianceId, state, pendind_intent_id, applianceType, level,slaveId);
                }



        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }

    private void updateFirebaseForgateController()
    {

            Toast.makeText(UserSchedularActivity.appcontext, "updateFirebaseForgateController", Toast.LENGTH_SHORT).show();


    }

    private void UpdateFirebaseForCurtain(final int level)
    {
        try
        {
            if(checkWiFiConnection()) {

                DatabaseReference curtainRef1 = GlobalApplication.firebaseRef
                        .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtain.getRoomId()).child("curtainDetails")
                        .child(curtain.getCurtainId()).child("curtainLevel");

                curtainRef1.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if (mutableData.getValue() == null) {
                            mutableData.setValue(level);
                        } else {
                            mutableData.setValue(level);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });


                DatabaseReference curtainRef = GlobalApplication.firebaseRef
                        .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtain.getRoomId()).child("curtainDetails")
                        .child(curtain.getCurtainId()).child("toggle");
                curtainRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        currentData.setValue(1);
                        return Transaction.success(currentData);
                        //we can also abort by calling Transaction.abort()
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                        Toast.makeText(UserSchedularActivity.appcontext, "Done", Toast.LENGTH_LONG).show();
                        getCurtainList();

                    }
                });
            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);

        }



    }

    private void getCurtainList() 
    {
        try {
            DatabaseReference curtainDetails = localRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails");

            curtainDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<CurtainDetails> curtainList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                        //Getting each room
                        for (DataSnapshot type : roomSnapshot.getChildren()) {
                            if (type.getRef().toString().indexOf("curtainDetails") != 0) {
                                for (DataSnapshot curtain : type.getChildren()) {
                                    CurtainDetails curtaindetails = curtain.getValue(CurtainDetails.class);
                                    curtainList.add(curtaindetails);

                                    GlobalApplication.curtainList = curtainList;
                                }
                                break;
                            }

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    System.out.println("The read failed: " + DatabaseError.getMessage());

                }
            });
        }catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }


    private void UpdateFirebase()
    {


            try
            {
                if(checkWiFiConnection())
                {

                    DatabaseReference applianceRef = GlobalApplication.firebaseRef
                            .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                            .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("toggle");


                    applianceRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            currentData.setValue(1);
                            return Transaction.success(currentData);
                            //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                            getAppliabceList();


                        }
                    });
                }

            }catch(Exception e)
            {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
    }

    private boolean checkWiFiConnection()
    {
        try
        {
            Toast.makeText(UserSchedularActivity.appcontext, "Schedular Service Starts", Toast.LENGTH_SHORT).show();
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) GlobalApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null
                    && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

        return false;
    }





    private  void UpdateFirebaseForSensor(){

        try
        {
            if(checkWiFiConnection())
            {

                DatabaseReference applianceRef = localRef
                        .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(sensorDetail.getRoomId())
                        .child("sensors").child(sensorDetail.getSlaveId()).child(sensorDetail.getId()).child("toggle");


                applianceRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        currentData.setValue(1);
                        return Transaction.success(currentData);
                        //we can also abort by calling Transaction.abort()
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                        getSensorList();


                    }
                });
            }

        }catch(Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }



    }

    private void getSensorList()
    {

        final DatabaseReference applianceDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

        applianceDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    if (!roomSnapshot.getRef().toString().contains("globalOff") ) {
                        //Getting each room
                            /*if(roomSnapshot.getRef())*/
                        String roomId = roomSnapshot.child("roomId").getValue(String.class);
                        String roomName = roomSnapshot.child("roomName").getValue(String.class);
                        if (roomId != null) {

                            sensor = new ArrayList<SensorDetail>();

                            for (DataSnapshot type : roomSnapshot.getChildren()) {

                                if(type.getRef().getKey().equals("sensors")){

                                    for (DataSnapshot sensors : type.getChildren()) {

                                        for(DataSnapshot sId : sensors.getChildren()){
                                            SensorDetail sensorDetail = sId.getValue(SensorDetail.class);
                                            sensor.add(sensorDetail);
                                        }
                                    }
                                      /*  SensorDetail sensor1 = type.getValue(SensorDetail.class);
                                         sensor.add(sensor1);*/
                                }

                            }
                        }

                    }
                }
                GlobalApplication.sensorDetailsList = sensor;
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                System.out.println("The read failed: " + DatabaseError.getMessage());
            }
        });

    }

    private void getAppliabceList()
    {
        try
        {
        DatabaseReference roomDetails =localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

        roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<RoomAppliance> applianceList = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    //Getting each room
                    String roomId = roomSnapshot.child("roomId").getValue(String.class);
                    if (roomId != null) {
                        for (DataSnapshot type : roomSnapshot.getChildren()) {

                            for(DataSnapshot slave : type.getChildren()) {

                                if (slave.getRef().toString().contains("appliance")) {
                                    for (DataSnapshot applianc : slave.getChildren()) {

                                        RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                        applianceList.add(roomAppliance);

                                        GlobalApplication.applianceList = applianceList;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                System.out.println("The read failed: " + DatabaseError.getMessage());
            }
        });
    }catch (Exception e)
    {
        DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                .child(String.valueOf(System.currentTimeMillis()));
        String currentStatus = e.getMessage();
        errorRef.setValue(currentStatus);
    }
    }
}


