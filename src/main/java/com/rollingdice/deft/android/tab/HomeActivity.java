package com.rollingdice.deft.android.tab;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.rollingdice.deft.android.tab.P2P.WiFiDirectActivity;
import com.rollingdice.deft.android.tab.Voice.HoundVoiceSearchExampleActivity;
import com.rollingdice.deft.android.tab.Voice.StartActivity;
import com.rollingdice.deft.android.tab.datahelper.ShortcutDataHelper;
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.GlobalOff;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.rollingdice.deft.android.tab.model.Shortcut;
import com.rollingdice.deft.android.tab.model.GateControllerDetails;
import com.rollingdice.deft.android.tab.user.SelectRemoteActivity;
import com.rollingdice.deft.android.tab.user.ShortcutActivity;
import com.rollingdice.deft.android.tab.user.UserListAllLockActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllModeActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllOnCurtainActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllOnDevicesActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllOnFansActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllOnLightsActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllOnSocketsActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllRoomsActivity;
import com.rollingdice.deft.android.tab.user.UserListOfAllGateControllers;
import com.rollingdice.deft.android.tab.user.UserListofEnergyActivity;
import com.rollingdice.deft.android.tab.user.UserSchedularActivity;
import com.rollingdice.deft.android.tab.user.UserSensorActivity;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("ConstantConditions")
public class HomeActivity extends AppCompatActivity
{

    public static List<CurtainDetails> curtainList;
    public static List<GateControllerDetails> gateControllerList;
    public Integer serviceReset;
    private TextView tvDevices;
    private TextView tvLights;
    private TextView tvFans;
    private TextView tvSockets;
    private TextView tvRooms;
    private TextView tvCurtain;
    private TextView tvScene;
    private TextView tvAppliance;
    private String totalLightCount, totalFanCount, totalSocketCount, totalRoomCount,totalgateControllerCount;
    private String onLightCount, onFanCount, onSocketCount, onRoomCount;
    private RelativeLayout weatherRelativeLayout;
    HorizontalScrollView scrollView;
    private boolean doubleBackToExitPressedOnce = false;
    private DatabaseReference localRef;
    public List<RoomDetails> roomList = new ArrayList<>();
    public List<RoomAppliance> roomApplianceList = new ArrayList<>();
    Toolbar toolbar;
    FragmentManager manager;
    Button globalOff;
    ImageView deftImage,smallLogo;

    ProgressDialog progressDialog=null;

    int flag;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String onDevicesCount;
    private String sensorCount;
    private String curtainCount;
    private String gateControllerCount;
    private String modeCount;
    private int lockCount;
    DatabaseReference globalOffRef;
    SimpleArcDialog mDialog;
    private GlobalOff dataGlobalOff;
    private Menu actionMenu;
    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
                mDialog.show();

            localRef = GlobalApplication.firebaseRef;
            rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            scrollView = (HorizontalScrollView) findViewById(R.id.subHorizantalScrollView);
            DatabaseReference adminRef=localRef.child("Admin");
            Map<String,Object> adminDetails=new HashMap<>();
            adminDetails.put("adminId","rollingdicedeft");
            adminDetails.put("password","123456");
            adminRef.setValue(adminDetails);

            DatabaseReference service =localRef.child("ServiceStart").child(Customer.getCustomer().customerId);
            service.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    serviceReset=dataSnapshot.child("service").getValue(Integer.class);
                    if(serviceReset.equals(1))
                    {
                        if(! isServiceRunning("com.rollingdice.deft.android.tab.SocketCommunicationService")){

                            Toast.makeText(HomeActivity.this, "Service Alredy Started", Toast.LENGTH_SHORT).show();
                            startService(new Intent(GlobalApplication.context, SocketCommunicationService.class));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            localRef.child("rooms").child(Customer.getCustomer().customerId).
                    child("roomdetails").child("globalOff").child("globalOffState")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){
                        if(!dataSnapshot.getValue(boolean.class)){


                            globalOff.setText("GOOD BYE");

                        }else {
                            globalOff.setText("WELCOME");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            globalOff= (Button) findViewById(R.id.button_global_off);

            globalOff.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if(globalOff.getText().toString().equals("WELCOME")){

                        globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).
                                child("roomdetails").child("globalOff").child("toggle");

                        globalOffRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                currentData.setValue(1);
                                return Transaction.success(currentData);
                                //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.

                                globalOff.setText("GOOD BYE");
                            }
                        });

                    }else {


                        DatabaseReference globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).
                                child("roomdetails").child("globalOff").child("toggle");
                        globalOffRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                currentData.setValue(1);
                                return Transaction.success(currentData);
                                //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.
                                globalOff.setText("WELCOME");
                            }
                        });



                    }


            /*        globalOffRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot != null){

                                dataGlobalOff = dataSnapshot.getValue(GlobalOff.class);
                                if(!dataGlobalOff.isGlobalOffState()){

                                    globalOff.setText("GOOD BYE");

                                    globalOffRef.child("toggle").runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData currentData) {
                                            currentData.setValue(1);
                                            return Transaction.success(currentData);
                                            //we can also abort by calling Transaction.abort()
                                        }

                                        @Override
                                        public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                            //This method will be called once with the results of the transaction.
                                        }
                                    });


                                }else {
                                    globalOff.setText("WELCOME");
                                    globalOffRef.child("toggle").runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData currentData) {
                                            currentData.setValue(1);
                                            return Transaction.success(currentData);
                                            //we can also abort by calling Transaction.abort()
                                        }

                                        @Override
                                        public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                            //This method will be called once with the results of the transaction.
                                        }
                                    });
                                }


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/



/*
                    if(globalOff.getText().toString().equals("GOOD BYE"))
                    {
                        globalOff.setText("WELCOME");
                        DatabaseReference globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).
                                child("roomdetails").child("globalOff").child("toggle");
                        globalOffRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                currentData.setValue(1);
                                return Transaction.success(currentData);
                                //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.
                            }
                        });
                    }
                    else
                    {
                        globalOff.setText("GOOD BYE");
                        DatabaseReference globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).
                                child("roomdetails").child("globalOff").child("toggle");
                        globalOffRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                currentData.setValue(1);
                                return Transaction.success(currentData);
                                //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.

                            }

                        });
                    }*/
                }
            });



            CardView cvRooms = (CardView) findViewById(R.id.card_view_rooms);
            CardView cvDevices = (CardView) findViewById(R.id.card_view_devices);
            CardView  cvCurrentlyOnApplinces = (CardView) findViewById(R.id.card_view_applinaces);
            CardView  cvEnergy = (CardView) findViewById(R.id.card_view_energy);
            CardView cvLights = (CardView) findViewById(R.id.card_view_lights);
            CardView cvFans = (CardView) findViewById(R.id.card_view_fans);
            CardView cvSockets = (CardView) findViewById(R.id.card_view_sockets);
            CardView  cvCurtain = (CardView) findViewById(R.id.card_view_curtains);
            CardView  cvMoods = (CardView) findViewById(R.id.card_view_modes);
            CardView  cvSensors = (CardView) findViewById(R.id.card_sensors);
            CardView   cvgateController= (CardView) findViewById(R.id.card_view_GATE_CONTROLLER);
            CardView  cvLocks= (CardView) findViewById(R.id.card_locks);
            final Button camera= (Button) findViewById(R.id.btnCamera);
            final Button voice = (Button) findViewById(R.id.btnVoice);


            tvDevices = (TextView) findViewById(R.id.tv_devices);
            tvLights = (TextView) findViewById(R.id.tv_lights);
            tvFans = (TextView) findViewById(R.id.tv_fans);
            tvSockets = (TextView) findViewById(R.id.tv_sockets);
            tvRooms = (TextView) findViewById(R.id.tv_rooms);
            TextView tvgateController = (TextView) findViewById(R.id.tv_sprinkler);
            tvCurtain = (TextView) findViewById(R.id.tv_curtain);

            tvAppliance = (TextView) findViewById(R.id.tv_applinces);
            deftImage= (ImageView) findViewById(R.id.imageView);
            smallLogo= (ImageView) findViewById(R.id.small_logo);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if(toolbar!=null) {
                toolbar.inflateMenu(R.menu.menu_home);
            }


            camera.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final PackageManager pm = getPackageManager();
                    //get a list of installed apps.
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                    /*for (ApplicationInfo packageInfo : packages) {
                        Log.d("TAG", "Installed package :" + packageInfo.packageName);
                        Log.d("TAG ","Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                    }*/
                    PackageManager pm1 = GlobalApplication.context.getPackageManager();
                    Intent appStartIntent = pm1.getLaunchIntentForPackage("object.smartbell.client");
                    if (null != appStartIntent)
                    {
                        GlobalApplication.context.startActivity(appStartIntent);
                    }

                }
            });

            voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, HoundVoiceSearchExampleActivity.class));
                }
            });



            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    int id = item.getItemId();
                    String title= (String) item.getTitle();

                    if (id == R.id.shortcut_action) {
                        List<Shortcut> shortcutList = ShortcutDataHelper.getAllShortcuts();
                        if (shortcutList.size() != 0) {

                            startActivity(new Intent(HomeActivity.this, ShortcutActivity.class));
                        } else {
                            Toast.makeText(HomeActivity.this, "No Shortcuts were Created. ", Toast.LENGTH_SHORT).show();
                        }


                        return true;
                    }
                    if(id==R.id.moods_action)
                    {
                        startActivity(new Intent(HomeActivity.this, NewModeDetailsActivity.class));
                    }
                    if(id==R.id.lock_action)
                    {
                        startActivity(new Intent(HomeActivity.this,LockDetailsActivity.class));
                    }
                    if(id==R.id.remote_action)
                    {
                        startActivity(new Intent(HomeActivity.this,RemoteConfigDetailsActivity.class));
                    }

                    if(id==R.id.remote_operation)
                    {
                        startActivity(new Intent(HomeActivity.this,SelectRemoteActivity.class));
                    }

                    if (id == R.id.action_settings) {
                        startActivity(new Intent(HomeActivity.this, RoomListActivity.class));

                        return true;
                    }
                    if (id == R.id.action_notification) {
                        startActivity(new Intent(HomeActivity.this, NotificationListActivity.class));

                        return true;

                    }

                    if (id == R.id.action_set_alarm) {
                        startActivity(new Intent(HomeActivity.this, UserSchedularActivity.class));
                    }

                    if (id == R.id.action_help) {
                        manager = getSupportFragmentManager();
                        UserHelpActivity userHelpActivity = new UserHelpActivity();
                        userHelpActivity.show(manager, "Tag");


                        //startActivity(new Intent(HomeActivity.this, UserHelpActivity.class));

                    }

                    if (id == R.id.action_start_service) {

                        if(! isServiceRunning("com.rollingdice.deft.android.tab.SocketCommunicationService")){
                            startService(new Intent(GlobalApplication.context, SocketCommunicationService.class));
                            Intent intent = new Intent();
                            intent.setAction("ServiceStartBroadcast");
                            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                            intent.setClass(GlobalApplication.context,ConnectionChangeReceiver.class);
                            sendBroadcast(intent);
                        }else {
                            Toast.makeText(HomeActivity.this, "Service Alredy Started", Toast.LENGTH_SHORT).show();
                        }

                    }

                    if (id == R.id.action_start_offline_service) {
                        startActivity(new Intent(HomeActivity.this, WiFiDirectActivity.class));
                    }

                  /*  if (id == R.id.action_stop_service) {
                        if (check()) {
                            stopService(new Intent(GlobalApplication.context, SocketCommunicationService.class));
                            Intent intent = new Intent();
                            intent.setAction("ServiceStopBroadcast");
                            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                            intent.setClass(GlobalApplication.context,ConnectionChangeReceiver.class);
                            sendBroadcast(intent);
                        }
                    }
*/

                    //Notification Settings

                    if (id == R.id.notificationSettings_schedularNotification) {
                        if (item.isChecked()) {
                            item.setChecked(false);
                            GlobalApplication.isSchedularNotificationSubscribe = false;
                            DatabaseReference schedularNotification = localRef.child("notification").child("schedular").child(Customer.getCustomer().customerId)
                                    .child("isSchedularNotificationSubscribe");
                            schedularNotification.setValue(false);
                            Toast.makeText(HomeActivity.this, "Schedular Notification DeActivated", Toast.LENGTH_SHORT).show();
                        } else {
                            item.setChecked(true);
                            GlobalApplication.isSchedularNotificationSubscribe = true;
                            DatabaseReference schedularNotification = localRef.child("notification").child("schedular").child(Customer.getCustomer().customerId)
                                    .child("isSchedularNotificationSubscribe");
                            schedularNotification.setValue(true);
                            Toast.makeText(HomeActivity.this, " Schedular Notification Activated", Toast.LENGTH_SHORT).show();
                        }

                    }

                    if (id == R.id.notificationSettings_energyNotification)
                    {
                        if (item.isChecked()) {
                            item.setChecked(false);
                            GlobalApplication.isEnergyNotificationSubscribe = false;
                            DatabaseReference energyNotification = localRef.child("notification").child("energy").child(Customer.getCustomer().customerId)
                                    .child("isEnergyNotificationSubscribe");
                            energyNotification.setValue(false);

                            Toast.makeText(HomeActivity.this, "Energy Notification DeActivated", Toast.LENGTH_SHORT).show();


                        } else {
                            item.setChecked(true);
                            GlobalApplication.isEnergyNotificationSubscribe = true;
                            DatabaseReference energyNotification = localRef.child("notification").child("energy").child(Customer.getCustomer().customerId)
                                    .child("isEnergyNotificationSubscribe");
                            energyNotification.setValue(true);

                            Toast.makeText(HomeActivity.this, "Energy Notification Activated", Toast.LENGTH_SHORT).show();


                        }


                    }

                    if(id==R.id.lock)
                    {
                        if (item.isChecked()) {
                            item.setChecked(false);
                            GlobalApplication.isLockNotificationSubscribe = false;
                            DatabaseReference lockNotification = localRef.child("notification").child("lock").child(Customer.getCustomer().customerId)
                                    .child("isLockNotificationSubscribe");
                            lockNotification.setValue(false);

                            Toast.makeText(HomeActivity.this, "Lock Notification DeActivated", Toast.LENGTH_SHORT).show();
                        } else {
                            item.setChecked(true);
                            GlobalApplication.isLockNotificationSubscribe = true;
                            DatabaseReference energyNotification = localRef.child("notification").child("lock").child(Customer.getCustomer().customerId)
                                    .child("isLockNotificationSubscribe");
                            energyNotification.setValue(true);

                            Toast.makeText(HomeActivity.this, "Lock Notification Activated", Toast.LENGTH_SHORT).show();


                        }
                    }


                    if(id==R.id.notificationSettings_motionDetectioNotification)
                    {
                        if(item.isChecked())
                        {
                            item.setChecked(false);
                            GlobalApplication.isMotionDetectionSubscribe = false;
                            DatabaseReference motionNotificaiton = localRef.child("notification").child("motionDetection").child(Customer.getCustomer().customerId)
                                    .child("isMotionDetectionSubscribe");
                            motionNotificaiton.setValue(false);
                            Toast.makeText(HomeActivity.this, "Motion Notification DeActivated", Toast.LENGTH_SHORT).show();


                        }
                        else
                        {
                            item.setChecked(true);
                            GlobalApplication.isMotionDetectionSubscribe = true;
                            DatabaseReference motionNotificaiton  = localRef.child("notification").child("motionDetection").child(Customer.getCustomer().customerId)
                                    .child("isMotionDetectionSubscribe");

                            motionNotificaiton.setValue(true);
                            Toast.makeText(HomeActivity.this, "Motion Notification Activated", Toast.LENGTH_SHORT).show();

                        }
                    }


                      /*   if (id == R.id.notificationSettings_errorNotification) {
                        if (item.isChecked()) {
                            item.setChecked(false);
                            GlobalApplication.isErrorNotificationSubscribe = false;
                            DatabaseReference errorNotification = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                                    .child("isErrorNotificationSubscribe");

                            errorNotification.setValue(false);
                            Toast.makeText(HomeActivity.this, "Error Notification DeActivated", Toast.LENGTH_SHORT).show();
                        } else {

                            item.setChecked(true);
                            GlobalApplication.isErrorNotificationSubscribe = true;
                            DatabaseReference errorNotification = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                                    .child("isErrorNotificationSubscribe");

                            errorNotification.setValue(true);
                            Toast.makeText(HomeActivity.this, " Error Notification Activated", Toast.LENGTH_SHORT).show();

                        }

                    }
*/

                    return false;
                }
            });


            cvRooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, UserListOfAllRoomsActivity.class));
                }
            });

            cvDevices.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if(!onDevicesCount.equals("0"))
                    {

                        startActivity(new Intent(HomeActivity.this, UserListOfAllOnDevicesActivity.class));
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "Currently No Appliances Are ON", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cvCurrentlyOnApplinces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    flag = scrollView.getVisibility();

                    if (flag == View.GONE)
                    {
                        scrollView.setVisibility(View.VISIBLE);
                        globalOff.setVisibility(View.GONE);
                        deftImage.setVisibility(View.GONE);
                        smallLogo.setVisibility(View.VISIBLE);
                        camera.setVisibility(View.GONE);
                        voice.setVisibility(View.GONE);

                        //  weatherRelativeLayout.setVisibility(View.GONE);
                    } else {
                        scrollView.setVisibility(View.GONE);
                        globalOff.setVisibility(View.VISIBLE);
                        deftImage.setVisibility(View.VISIBLE);
                        smallLogo.setVisibility(View.GONE);
                        camera.setVisibility(View.VISIBLE);
                        voice.setVisibility(View.VISIBLE);
                        //weatherRelativeLayout.setVisibility(View.VISIBLE);
                    }
                }

            });

            cvgateController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(!gateControllerCount.equals("0"))
                    {
                        startActivity(new Intent(HomeActivity.this, UserListOfAllGateControllers.class));
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "NO GateController Were Added.Please Contact Customer HelpLine", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            cvEnergy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, UserListofEnergyActivity.class));

                }
            });


            cvLights = (CardView) findViewById(R.id.card_view_lights);
            cvLights.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(!totalLightCount.equals("0")) {
                        startActivity(new Intent(HomeActivity.this, UserListOfAllOnLightsActivity.class));
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "No Lights Were Added.Please Contact Customer HelpLine", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cvFans = (CardView) findViewById(R.id.card_view_fans);
            cvFans.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(!totalFanCount.equals("0"))
                    {
                        startActivity(new Intent(HomeActivity.this, UserListOfAllOnFansActivity.class));
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "No Fans were Added.Please Contact Customer HelpLine ", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            cvSockets = (CardView) findViewById(R.id.card_view_sockets);
            cvSockets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(!totalSocketCount.equals("0"))
                        startActivity(new Intent(HomeActivity.this, UserListOfAllOnSocketsActivity.class));
                    else
                    {
                        Toast.makeText(HomeActivity.this, "No Socket were Added.Please Contact Customer Care", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            cvCurtain = (CardView) findViewById(R.id.card_view_curtains);
            cvCurtain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(!curtainCount.equals("0")) {
                        startActivity(new Intent(HomeActivity.this, UserListOfAllOnCurtainActivity.class));
                    }else
                    {
                        Toast.makeText(HomeActivity.this, "No Curtains Were Added. Please Contact Customer HelpLine", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cvMoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(!modeCount.equals("0")) {
                        startActivity(new Intent(HomeActivity.this, UserListOfAllModeActivity.class));
                    }else
                    {
                        Toast.makeText(HomeActivity.this, "No Moods Were Added. Please Contact Customer HelpLine", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            cvSensors.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!sensorCount.equals("0"))
                    {
                        startActivity(new Intent(HomeActivity.this, UserSensorActivity.class));

                    } else {
                        Toast.makeText(HomeActivity.this, "No sensor were added.Please contact CustomerHelpline", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cvLocks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //startActivity(new Intent(HomeActivity.this,UserListAllLockActivity.class));

                    if(lockCount!=0)
                    {
                        startActivity(new Intent(HomeActivity.this,UserListAllLockActivity.class));
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "No Locks were added.Please contact CustomerHelpline", Toast.LENGTH_SHORT).show();

                    }

                }
            });


            setRoomNameList();
            setRoomApplianceList();
            setUpRoomUI();
            setUpDevicesUI();
            setUpLightsUI();
            setUpFansUI();
            setUpSocketsUI();
            setUpCurtainUI();
            setUpSensorUI();
            setUpgateControllerUI();
            setUpModeUI();
            setUpLockUI();
            setInitialSchedular();



            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

            // progressDialog.dismiss();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    if(!isFinishing())
                        mDialog.dismiss();
                }
            },7000);
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


        if(!check()) {
            startService(new Intent(HomeActivity.this, SchedularSevice.class));
        }
        else
        {
            Toast.makeText(HomeActivity.this, "Schedular Service is running", Toast.LENGTH_LONG).show();
        }

    }


    public static boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) GlobalApplication.context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        this.actionMenu=menu;
        MenuItem schedularNotificationItem=menu.findItem(R.id.notificationSettings_schedularNotification);
        schedularNotificationItem.setChecked(GlobalApplication.isSchedularNotificationSubscribe);

      /*  MenuItem errorNotificationItem=menu.findItem(R.id.notificationSettings_errorNotification);
        errorNotificationItem.setChecked(GlobalApplication.isErrorNotificationSubscribe);
*/
        MenuItem energyNotificationItem=menu.findItem(R.id.notificationSettings_energyNotification);
        energyNotificationItem.setChecked(GlobalApplication.isEnergyNotificationSubscribe);

        MenuItem lockNotificationItem=menu.findItem(R.id.lock);
        lockNotificationItem.setChecked(GlobalApplication.isLockNotificationSubscribe);

        MenuItem motionDetectionNotification=menu.findItem(R.id.notificationSettings_motionDetectioNotification);
        motionDetectionNotification.setChecked(GlobalApplication.isMotionDetectionSubscribe);
        cloudAndTabConnectionListioner();


        return true;
    }

    private void cloudAndTabConnectionListioner() {
        try {
            actionMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.disconnected));
            if(Customer.getCustomer().customerId !=null) {
                DatabaseReference lastOnline = localRef.child("servicestate").child(Customer.getCustomer().customerId);
                lastOnline.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (null != snapshot.getValue()) {

                            if(mDialog != null && !isFinishing()) mDialog.dismiss();
                            disableEnableView(snapshot.getValue(Boolean.class), rootLayout);

                            if (!snapshot.getValue(Boolean.class)) {
                                //noinspection deprecation
                                actionMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.disconnected));
                            } else {
                                //noinspection deprecation
                                actionMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.connected));
                            }
                        } else {
                            int i = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                    }
                });
            }
        } catch (Exception e)
        {
            if(Customer.getCustomer().customerId !=null) {

                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    public static void disableEnableView(boolean enable, ViewGroup vg) {

        DatabaseReference ref = GlobalApplication.firebaseRef;
        try {

            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                child.setEnabled(enable);
                if (child instanceof ViewGroup) {
                    disableEnableView(enable, (ViewGroup) child);
                }
            }
        } catch (Exception e)
        {
            if(Customer.getCustomer().customerId !=null) {
                DatabaseReference errorRef = ref.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }


    private void setInitialSchedular()
    {
        DatabaseReference first=localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails").child("00");
        Map<String, Object> sceneDetails1 = new HashMap<>();
        sceneDetails1.put("sceneId","NA");
        sceneDetails1.put("sceneName","Create New Scene");
        sceneDetails1.put("startTime", "NA");
        sceneDetails1.put("endTime", "NA");
        sceneDetails1.put("isActivated",0);
        sceneDetails1.put("isAlarmSet",false);
        sceneDetails1.put("listOfDays", "NA");
        sceneDetails1.put("isRepeating",false);
        first.updateChildren(sceneDetails1);

        DatabaseReference sceneConfigRef1 =localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails").child("00")
                .child("sceneConfigDetails").child("00");
        Map<String, Object> sceneConfigDetails1 = new HashMap<>();
        sceneConfigDetails1.put("appStartTime", "NA");
        sceneConfigDetails1.put("appEndTime","NA" );
        sceneConfigDetails1.put("applianceId", "NA");
        sceneConfigDetails1.put("applianceName", "NA");
        sceneConfigDetails1.put("day", "NA");
        sceneConfigDetails1.put("roomId","NA");
        sceneConfigDetails1.put("offPendingIntentId", 0);
        sceneConfigDetails1.put("onPendingIntentId",0);
        sceneConfigDetails1.put("applianceType", "NA");
        sceneConfigDetails1.put("curtainLevel", 0);
        sceneConfigRef1.updateChildren(sceneConfigDetails1);



    }

    private void setUpLockUI()
    {
        DatabaseReference lockDetails=localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockCount");
        lockDetails.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot !=null && dataSnapshot.getValue()!=null)
                {

                    lockCount=dataSnapshot.getValue(Integer.class);
                }
                else
                {
                    lockCount=0;
                }


            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {

            }
        });

    }


    public boolean check()
    {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (("com.rollingdice.deft.android.tab.SchedularService")
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    private void setRoomApplianceList()
    {
        try {
            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    roomApplianceList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        //Getting each room
                        String roomId = roomSnapshot.child("roomId").getValue(String.class);
                        if (roomId != null) {
                            for (DataSnapshot type : roomSnapshot.getChildren()) {
                                for(DataSnapshot slave : type.getChildren()) {
                                    if (slave.getRef().toString().contains("appliance")) {
                                        for (DataSnapshot applianc : slave.getChildren()) {

                                            RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                            roomApplianceList.add(roomAppliance);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    tvAppliance.setText(String.valueOf(roomApplianceList.size()));
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

    private void setRoomNameList()
    {
        try {

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    roomList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
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
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private void setUpRoomUI()
    {
        try {

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/roomCount");

            roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        tvRooms.setText(snapshot.getValue().toString());
                    } else {
                        tvRooms.setText("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

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

    private void setUpDevicesUI()
    {
        try {

            DatabaseReference allOnDevices = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/on/all");

            allOnDevices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot != null && snapshot.getValue() != null) {
                        onDevicesCount = snapshot.getValue().toString();
                        tvDevices.setText("" + snapshot.getValue() + "\n" + "Devices On");
                    } else {
                        onDevicesCount = "0";
                        tvDevices.setText("0 \n" + "Devices On");
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
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

    private void setUpLightsUI()
    {
        try {

            DatabaseReference allLightDevices = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount").child("lightCount");

            allLightDevices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null)
                    {
                        totalLightCount = "0";
                    } else {
                        totalLightCount = snapshot.getValue().toString();
                    }
                    tvLights.setText("" + onLightCount + "/" + totalLightCount);
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                }
            });
        }
        catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

        DatabaseReference lightDevices = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/on/light");
        try {

            lightDevices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        onLightCount = snapshot.getValue().toString();
                    } else {
                        onLightCount = "0";
                    }
                    tvLights.setText("" + onLightCount + "/" + totalLightCount);

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {


                }
            });
            Log.i("LIGHT", "onLightCount " + onLightCount);
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private void setUpFansUI()
    {
        try {
            DatabaseReference allFanDevices = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/fanCount");

            allFanDevices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        totalFanCount = "0";
                    } else {
                        totalFanCount = snapshot.getValue().toString();
                    }

                    tvFans.setText("" + onFanCount + "/" + totalFanCount);
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                }
            });
        }
        catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


        DatabaseReference fanDevices = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/on/fan");
        try
        {
            fanDevices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        onFanCount = snapshot.getValue().toString();
                    } else {
                        onFanCount = "0";
                    }
                    tvFans.setText("" + onFanCount + "/" + totalFanCount);
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
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

    private void setUpSocketsUI()
    {
        try {

            DatabaseReference allSocketDevices = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/socketCount");

            allSocketDevices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        totalSocketCount = "0";
                    } else {
                        totalSocketCount = snapshot.getValue().toString();
                    }

                    tvSockets.setText("" + onSocketCount + "/" + totalSocketCount);
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                }
            });
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


        DatabaseReference onSockets = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/on/socket");
        try {

            onSockets.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        onSocketCount = snapshot.getValue().toString();
                    } else {
                        onSocketCount = "0";
                    }
                    tvSockets.setText("" + onSocketCount + "/" + totalSocketCount);
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
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
    private void setUpCurtainUI()
    {
        try
        {
            DatabaseReference curtainsRef=localRef.child("curtain").child(Customer.getCustomer().customerId).child("curtainCountDetails");
            curtainsRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot detailsSnapshot : dataSnapshot.getChildren()) {
                        if (detailsSnapshot != null && detailsSnapshot.getValue() != null) {
                            curtainCount = detailsSnapshot.getValue().toString();
                            tvCurtain.setText(curtainCount);
                        } else {
                            curtainCount = "0";
                            tvCurtain.setText(curtainCount);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

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
    private void setUpSensorUI()
    {
        try {
            DatabaseReference sensorRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetailsCount/sensorCount");
            sensorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        sensorCount = dataSnapshot.getValue().toString();
                    } else {
                        sensorCount = "0";

                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

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
    private void setUpgateControllerUI()
    {
        try {


            DatabaseReference waterRef = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerCountDetails")
                    .child("waterSprinklerCount");
            waterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        gateControllerCount = dataSnapshot.getValue().toString();
                    } else {
                        gateControllerCount = "0";
                    }

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

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
    private void setUpModeUI()
    {
        try {
            DatabaseReference modeRef = localRef.child("mode").child(Customer.getCustomer().customerId).child("modeCountDetails").child("modeCount");
            modeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                    {
                        modeCount = dataSnapshot.getValue().toString();

                    } else
                    {
                        modeCount = "0";
                    }

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

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








    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.shortcut_action)
        {
            List<Shortcut>shortcutList= ShortcutDataHelper.getAllShortcuts();
            if(shortcutList.size()!=0)
            {

                startActivity(new Intent(HomeActivity.this, ShortcutActivity.class));
            }
            else
            {
                Toast.makeText(HomeActivity.this, "No Shortcuts were Created. ", Toast.LENGTH_SHORT).show();
            }

            finish();
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(HomeActivity.this, RoomListActivity.class));
            finish();
            return true;
        }

        if (id == R.id.action_start_service) {

            if(! isServiceRunning("com.rollingdice.deft.android.tab.SocketCommunicationService")){
                Toast.makeText(HomeActivity.this, "Service Alredy Started", Toast.LENGTH_SHORT).show();
                startService(new Intent(getApplicationContext(), SocketCommunicationService.class));
                finish();
            }

        }
       /* if (id == R.id.action_stop_service) {
            stopService(new Intent(getApplicationContext(), SocketCommunicationService.class));
            finish();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRoomUI();
        setUpDevicesUI();
        setUpLightsUI();
        setUpFansUI();
        setUpSocketsUI();
        setUpCurtainUI();
        setUpSensorUI();
        setUpgateControllerUI();
        setUpModeUI();
        setUpLockUI();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /*  @Override
      public void onStart() {
          super.onStart();

          // ATTENTION: This was auto-generated to implement the App Indexing API.
          // See https://g.co/AppIndexing/AndroidStudio for more information.
          client.connect();
          Action viewAction = Action.newAction(
                  Action.TYPE_VIEW, // TODO: choose an action type.
                  "Home Page", // TODO: Define a title for the content shown.
                  // TODO: If you have web page content that matches this app activity's content,
                  // make sure this auto-generated web page URL is correct.
                  // Otherwise, set the URL to null.
                  Uri.parse("http://host/path"),
                  // TODO: Make sure this auto-generated app deep link URI is correct.
                  Uri.parse("android-app://com.rollingdice.deft.android.tab/http/host/path")
          );
          AppIndex.AppIndexApi.start(client, viewAction);
      }

      @Override
      public void onStop() {
          super.onStop();

          // ATTENTION: This was auto-generated to implement the App Indexing API.
          // See https://g.co/AppIndexing/AndroidStudio for more information.
          Action viewAction = Action.newAction(
                  Action.TYPE_VIEW, // TODO: choose an action type.
                  "Home Page", // TODO: Define a title for the content shown.
                  // TODO: If you have web page content that matches this app activity's content,
                  // make sure this auto-generated web page URL is correct.
                  // Otherwise, set the URL to null.
                  Uri.parse("http://host/path"),
                  // TODO: Make sure this auto-generated app deep link URI is correct.
                  Uri.parse("android-app://com.rollingdice.deft.android.tab/http/host/path")
          );
          AppIndex.AppIndexApi.end(client, viewAction);
          client.disconnect();
      }
  */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(mDialog!=null && mDialog.isShowing() && !isFinishing())
        {
            mDialog.dismiss();
        }
    }

}
