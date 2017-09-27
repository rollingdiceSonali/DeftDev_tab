package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.activeandroid.ActiveAndroid;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.ShortcutDataHelper;
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.rollingdice.deft.android.tab.model.SensorDetail;
import com.rollingdice.deft.android.tab.model.Shortcut;
import com.rollingdice.deft.android.tab.model.GateControllerDetails;
import com.leo.simplearcloader.SimpleArcDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by koushik on 04/06/15.
 */
public class GlobalApplication extends Application
{
    public static String SELECTED_ROOM_ID = "SELECTED_ROOM_ID";
    static boolean SENSOR_ADD_MODE = false;
    static boolean CURTAIN_ADD_MODE=false;
    static boolean APPLIANCE_ADD_MODE = false;
    static boolean SENSOR_EDIT_MODE = false;
    static int SENSOR_TYPE_ID;
    public static ArrayList<SensorDetail> sensorDetailsList;
    static boolean CURTAIN_EDIT_MODE=false;
    public static boolean GATE_CONTROLLER_ADD_MODE =false ;
    public static boolean GATE_CONTROLLER_EDIT_MODE =false ;
    static boolean APPLIANCE_EDIT_MODE = false;
    static String SENSOR_ID;
    static String APPLIANCE_ID;
    static String CURTAIN_ID;
    static String GATE_CONTROLLER_ID;
    static String CUSTOMER_ID;
    static long UPDATE_SENSOR_POSITION = 0;
    static long UPDATE_APPLIANCE_POSITION = 0;
    static long UPDATE_CURTAIN_POSITION=0;
    public static long UPDATE_GATE_CONTROLLER_POSITION;
    public static int clickPosition = 0;

    public static String ipAddress="";
    public static int port=0;



    public static boolean isActivated;
    public static List<Shortcut> shortcutApplianceList;
    public static String ROOM_ID;
    public static DatabaseReference rootRef ;


    public static boolean isEnergyNotificationSubscribe;
    public static boolean isErrorNotificationSubscribe;
    public static boolean isSchedularNotificationSubscribe;
    public static boolean isLockNotificationSubscribe;
    public static boolean isMotionDetectionSubscribe;

    public static Context context;
    public static boolean isApplianceClickable = true;
    public static boolean isCurtainClickable = true;
    public static boolean isOnDevicesClickable = true;
    public static boolean isOnFansClickable = true;
    public static boolean isOnLightsClickable = true;
    public static boolean isOnSocketsClickable = true;
    public static boolean isShortcutClickable = true;
    public static boolean isgateControllerClickable = true;
    public static boolean isSchedularClickable = true;
    public static boolean isLockClickable = true;
    public static boolean isMoodClickable = true;

    public static List<RoomAppliance> applianceList;
    public static List<CurtainDetails> curtainList;
    public static List<SensorDetail> sensorList;
    public static ArrayList<GateControllerDetails> gateControllerList;
    public static List<RoomDetails> roomList;


    public static DatabaseReference firebaseRef;
    public static String[] ROOM_TYPE= {"Bed Room", "Child Bed Room", "Restroom", "Living Room", "Terrace", "Kitchen", "Dining Room"};
    public static String[] APPLIANCE_TYPE = {"Light","Fan", "TV","Refrigerator", "Washing Machine", "Geyser", "Music System", "Sockets","AC","Water Pump","PROJECTOR", "DVD", "SETTOP BOX","MOOD LIGHT"};

    public static String[] SENSOR_TYPE = {"Temperature", "Light", "Motion","Occupancy"};

    public static String[] AC_REMOTE_KEYS = {"POWER ON","POWER OFF","FAN","MODE","QUITE","SWING","FEEL","PRESET","Temp 18","Temp 20","Temp 22","Temp 24","Temp 26",
            "Temp 28","Temp 30"};
    public static String[] PROJECTOR_REMOTE_KEYS = {"POWER ON","STANDBY","MENU","ENTER","LEFT ARROW","RIGHT ARROW","UP ARROW","DOWN ARROW","INPUT-","VOL-","VOL+",
            "PIC MODE","POINTER","INPUT+","RETURN"};

    public static String[] DVD_REMOTE_KEYS = {"POWER ON","REPLAY/USB","OPEN/CLOSE","SELECT","LEFT ARROW","RIGHT ARROW","UP ARROW","DOWN ARROW","MENU","REVERSE","FORWORD","PLAY",
            "STOP","PREVIOUS","NEXT"};

    // public static String[] MOOD_REMOTE_KEYS = {"ON","OFF","B+","B-","R","G","B","W","FLASH","#3D5AFE",};

    public static String[] SETTOPBOX_REMOTE_KEYS = {"POWER ON","POWER OFF","GUIDE","SELECT","LEFT ARROW","RIGHT ARROW","UP ARROW","DOWN ARROW","HOME","VOL-",
            "VOL+","CH-","CH+","BACK","MUTE"};

    public static String[] TV_REMOTE_KEYS = {"POWER ON","POWER OFF","GUIDE","SELECT","LEFT ARROW","RIGHT ARROW","UP ARROW","DOWN ARROW","HOME","VOI-",
            "VOL+","CH-","CH+","BACK","MUTE"};


    @Override
    public void onCreate()
    {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseRef= FirebaseDatabase.getInstance().getReference().getRoot();
       firebaseRef.keepSynced(false);
        ActiveAndroid.initialize(this); 
        context=getApplicationContext();
        shortcutApplianceList= ShortcutDataHelper.getAllShortcuts();

        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        isEnergyNotificationSubscribe=false;
        isErrorNotificationSubscribe=false;
        isSchedularNotificationSubscribe=false;
        isLockNotificationSubscribe = false;
        isMotionDetectionSubscribe = true;
        isActivated=true;

        if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
            setRoomNameList();
            setApplianceList();
            setSensorList();
            setGateControllerList();
            setCurtainList();

            DatabaseReference ref=firebaseRef.child("IPAddress").child(Customer.getCustomer().customerId);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    ipAddress=dataSnapshot.child("ipAddres").getValue(String.class);
                    port=dataSnapshot.child("port").getValue(Integer.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    public boolean check()
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {

            if ("com.rollingdice.deft.android.tab.SchedularSevice"
                    .equals(service.service.getClassName()))
            {
                /*int pid = service.pid;
                android.os.Process.killProcess(pid);*/
                return true;
            }
        }
        return false;
    }

      private void setSensorList()
    {
        DatabaseReference roomDetails = firebaseRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

        roomDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {

                GlobalApplication.sensorDetailsList = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    //Getting each room
                    String roomId = roomSnapshot.child("roomId").getValue(String.class);
                    if (roomId != null) {
                        for (DataSnapshot type : roomSnapshot.getChildren()) {

                            for(DataSnapshot slave : type.getChildren()) {

                                if (slave.getRef().toString().contains("sensors") && !(slave.getRef().toString().contains("appliance"))) {
                                    for (DataSnapshot _sensor : slave.getChildren()) {
                                        SensorDetail sensor = _sensor.getValue(SensorDetail.class);
                                        if(sensor.getId() != null) {
                                            GlobalApplication.sensorDetailsList.add(sensor);
                                        }

                                    }
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
    }

    private void setApplianceList()
    {
        try {


            DatabaseReference roomDetails = firebaseRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    applianceList = new ArrayList<>();
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
            DatabaseReference errorRef = firebaseRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }


   /* private void setSensorList(){
        DatabaseReference curtainDetails = firebaseRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails");
    }
*/

    private void setCurtainList()
    {
        DatabaseReference curtainDetails = firebaseRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails");

        curtainDetails.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                curtainList = new ArrayList<>();
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    //Getting each room
                    for (DataSnapshot type : roomSnapshot.getChildren()) {
                        if (type.getRef().toString().indexOf("curtainDetails") != 0) {
                            for (DataSnapshot curtain : type.getChildren()) {
                                CurtainDetails curtaindetails = curtain.getValue(CurtainDetails.class);
                                curtainList .add(curtaindetails);
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

    }


    private void setGateControllerList()
    {

        try {

            final DatabaseReference gateControllerDetails =firebaseRef.child("gateController").child(Customer.getCustomer().customerId).child("gateControllerDetails");

            gateControllerDetails.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    gateControllerList= new ArrayList<>();
                    for (DataSnapshot roomSnapshot : dataSnapshot.getChildren())
                    {
                        String w_Name=roomSnapshot.child("gateControllerName").getValue(String.class);
                        String w_id=roomSnapshot.child("gateControllerId").getValue(String.class);
                        Integer  toggle=roomSnapshot.child("toggle").getValue(Integer.class);
                        Integer  level=roomSnapshot.child("level").getValue(Integer.class);
                        GateControllerDetails wSD=new GateControllerDetails(w_id,w_Name,toggle,level);
                        gateControllerList.add(wSD);
                    }

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    System.out.println("The read failed: " + DatabaseError.getMessage());

                }
            });
        }catch (Exception e)
        {
            DatabaseReference errorRef = firebaseRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }

    private void setRoomNameList()
    {
        try {

            DatabaseReference roomDetails = firebaseRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    roomList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        firebaseRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
                        String roomType = roomSnapshot.child("roomType").getValue(String.class);
                        String roomName = roomSnapshot.child("roomName").getValue(String.class);
                        String roomId = roomSnapshot.child("roomId").getValue(String.class);
                        String lastMotionDetected=roomSnapshot.child("lastMotionDetected").getValue(String.class);
                        RoomDetails room = new RoomDetails(roomName, roomType, roomId);
                        roomList.add(room);
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    System.out.println("The read failed: " + DatabaseError.getMessage());
                }
            });
        }catch (Exception e)
        {
            DatabaseReference errorRef = firebaseRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }
}
