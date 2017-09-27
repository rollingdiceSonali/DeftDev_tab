package com.rollingdice.deft.android.tab;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.EnergyDataHelper;
import com.rollingdice.deft.android.tab.datahelper.EnergyInfoDataHelper;
import com.rollingdice.deft.android.tab.datahelper.RoomDataHelper;
import com.rollingdice.deft.android.tab.datahelper.SceneDataHelper;
import com.rollingdice.deft.android.tab.datahelper.SensorDataHelper;
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.EnergyData;
import com.rollingdice.deft.android.tab.model.EnergyInfo;
import com.rollingdice.deft.android.tab.model.GlobalOff;
import com.rollingdice.deft.android.tab.model.LockDetails;
import com.rollingdice.deft.android.tab.model.Room;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.rollingdice.deft.android.tab.model.Scene;
import com.rollingdice.deft.android.tab.model.SceneDetails;
import com.rollingdice.deft.android.tab.model.SensorTab;
import com.rollingdice.deft.android.tab.model.WaterSprinklerDetails;
import com.rollingdice.deft.android.tab.model.modeDetails;
import com.rollingdice.deft.android.tab.user.MyAlarmManager;
import com.rollingdice.deft.android.tab.user.UserListofEnergyActivity;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketCommunicationService extends Service {
    DatabaseReference localRef = GlobalApplication.firebaseRef;
    public String masterControllerIDVal;
    CommunicationHandler commThread;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    Uri soundUri;
    ScheduledThreadPoolExecutor executor_;

    LastConnectedTimeStamp lastConnectedTimeStamp;
    static boolean toBeDestroyed = false;

    List<EnergyData> roomWiswEnergyConsumed = new ArrayList<>();

    PowerManager mgr;
    PowerManager.WakeLock wakeLock;
    ValueEventListener remoteRefEventListner;
    boolean moodCheck;


    ArrayList<ArrayList<String>> slaveWiseOnApplianceList = new ArrayList<ArrayList<String>>(4);
    ArrayList<ArrayList<String>> slaveWiseOffApplianceList = new ArrayList<ArrayList<String>>(4);
    // String[][] slaveWiseOffApplianceList = new String[4][5];

    DatabaseReference slaveConfig = localRef.child("SlaveConfigCommand").child(Customer.getCustomer().customerId);
    DatabaseReference remoteRef = localRef.child("remote").child(Customer.getCustomer().customerId);
    /* final DatabaseReference sceneDetailsRef = localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails");
     ValueEventListener sceneDetailsEventListner;*/
    final DatabaseReference socketConnection = localRef.child("SocketConnection").child(Customer.getCustomer().customerId)
            .child("IsAlive");
    ValueEventListener socketConnectionEventListner;
    final DatabaseReference serviceReset = localRef.child("serviceReset").child(Customer.getCustomer().customerId).child("serviceResetFlag");
    ValueEventListener serviceResetEventListner;
    DatabaseReference curtainDetails = localRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails");
    ValueEventListener curtainDetailsEventListner;
    DatabaseReference modeRef = localRef.child("mode").child(Customer.getCustomer().customerId).child("modeDetails");
    ValueEventListener modeRefEventListner;
    ValueEventListener spinklerRefEventListner;
    DatabaseReference roomsDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
    ValueEventListener roomsDetailsEventListner;

    DatabaseReference lockDetails = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockDetails");
    ValueEventListener lockEventListener;

    private List<LockDetails> lockDetailsList;


    int port;
    String serverHostname = "";
    private ArrayList<Double> applianceWiseEnergyConsumed;
    private ArrayList<String> applianceNameList;
    List<RoomAppliance> appliancesList;
    String applianceWiseEnergy;
    private String applianceWiseName;

    public CommunicationHandler getCommThread() {
        return commThread;
    }

    public void setCommThread(CommunicationHandler commThread) {
        this.commThread = commThread;
    }

    private final IBinder binder = new SocketCommunicationServiceBinder();

    public SocketCommunicationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
        //Log.i(" onStartCommand :", Customer.getCustomer().customerId);

        Customer customer = Customer.getCustomer();
        if (customer != null) {
            serverHostname = customer.ipAddress;
            port = customer.port;
        } else {
            if (GlobalApplication.ipAddress != null || !GlobalApplication.ipAddress.equals("")) {
                if (serverHostname == null || serverHostname.equals("")) {
                    serverHostname = GlobalApplication.ipAddress;
                }
            } else {

                serverHostname = "192.168.0.111";
            }
                /*Toast.makeText(getApplicationContext(),"IP Address not configured. " +
                        "Please check your Router Connection.", Toast.LENGTH_SHORT ).show();
            }*/

            if (GlobalApplication.port != 0) {
                port = GlobalApplication.port;
            } else {
                port = 2000;
            }
        }


      /*  if(GlobalApplication.ipAddress != null || !GlobalApplication.ipAddress.equals("")) {
            if (serverHostname == null || serverHostname.equals("")) {
                serverHostname = GlobalApplication.ipAddress;
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"IP Address not configured. " +
                    "Please check your Router Connection.", Toast.LENGTH_SHORT ).show();
        }

        if(GlobalApplication.port != 0)
        {
            port =GlobalApplication.port;
        }
        else
        {
            port = 2000;
        }*/

        getMasterControllerId();
        CommunicationHandler comm = initSocketConnection();
        try {

            Thread.sleep(500);

            fireBaseTabConnectionState();

            Thread.sleep(5000);

            pingPongExercise();
            CheckSocketConnection();
            //acquirewakeLock();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Start Communcation Handeler Thread //
        /*CommuncicationHandelerChecker communcicationHandelerChecker = new CommuncicationHandelerChecker(this);
        Thread communicationChecker = new Thread(communcicationHandelerChecker);
        communicationChecker.start();*/
        return Service.START_STICKY;
    }

    public void getMasterControllerId() {
        localRef.child("masterController").child(Customer.getCustomer().customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                masterControllerIDVal = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void pingPongExercise() {
        try {
            executor_ = new ScheduledThreadPoolExecutor(1);
            executor_.scheduleWithFixedDelay(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     try {
                                                         DatabaseReference socketConnection = localRef.child("SocketConnection").child(Customer.getCustomer().customerId)
                                                                 .child("IsAlive");

                                                         socketConnection.setValue(System.currentTimeMillis());

                                                     } catch (Exception e) {
                                                         Toast.makeText(SocketCommunicationService.this, Arrays.toString(e.getStackTrace()), Toast.LENGTH_LONG).show();
                                                     }
                                                 }
                                             }

                    , 0L, 28, TimeUnit.SECONDS);
        } catch (Exception e) {
            Toast.makeText(SocketCommunicationService.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public class SocketCommunicationServiceBinder extends Binder {

        SocketCommunicationService getSocketCommunicationService() {

            return SocketCommunicationService.this;
        }
    }

    private void fireBaseTabConnectionState() {
        try {

            final DatabaseReference myConnectionsRef = localRef.child("userOnlineDetails").child(Customer.getCustomer().customerId).child("lastOnline");
            lastConnectedTimeStamp = new LastConnectedTimeStamp(myConnectionsRef);
            Thread lastConnThread = new Thread(lastConnectedTimeStamp);
            lastConnThread.start();
            globalOffStateChangeOnline();
            roomsApplianceChangeOnline();
            modeChangeOnline();
            curtainOnline();
            //ServiceStateChangeOnline();
            remoteStateChangeOnline();
            serviceResetOnline();
            lockStateChaneOnline();
            energyInfoChangeOnline();

            waterSpinklerChangeOnline();
            slaveConfigration();
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


    private void waterSpinklerChangeOnline() {

        DatabaseReference waterSprinklerDetails = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerDetails");
        try {
            spinklerRefEventListner = waterSprinklerDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot SpinklerSnapshot : dataSnapshot.getChildren()) {
                            if (SpinklerSnapshot.exists()) {
                                WaterSprinklerDetails tempModeDetails = SpinklerSnapshot.getValue(WaterSprinklerDetails.class);
                                if (tempModeDetails.isToggle() == 1) {

                                    String state = tempModeDetails.isState() ? "1" : "2";
                                    String payLoad = "S" + masterControllerIDVal + tempModeDetails.getWaterSprinklerId() + state + "0F";


                                    /*String[] allOnApplianceArray = tempModeDetails.getApplianceId().split(",");

                                    String[] allOffApplianceArray = tempModeDetails.getOffAppliances().split(",");
                                    String allOffAppliance = "";
                                    String allOnAppliance = "";

                                    for (String s : allOnApplianceArray) {
                                        allOnAppliance = allOnAppliance + s;
                                    }
                                    for (String s : allOffApplianceArray) {
                                        allOffAppliance = allOffAppliance + s;
                                    }

                                    String state = tempModeDetails.isState() ? "b" : "a";*/

                                    String response = sendMessage(payLoad);
                                    if (response != null && response.endsWith("1F")) {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(!tempModeDetails.isState());

                                        DatabaseReference applianceRef = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerDetails")
                                                .child(tempModeDetails.waterSprinklerId);
                                        applianceRef.setValue(tempModeDetails);

                                        //MaintainState(allOnApplianceArray,allOffApplianceArray,tempModeDetails);


                                    } else if (response != null && response.endsWith("2F")) {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(tempModeDetails.isState());
                                        DatabaseReference applianceRef = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerDetails")
                                                .child(tempModeDetails.waterSprinklerId);
                                        applianceRef.setValue(tempModeDetails);
                                        DatabaseReference notifyReference = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId).child(String.valueOf(System.currentTimeMillis()));

                                        String currentStatus = "The Slave for this is turned Off \n thus unable to process your request";
                                        notifyReference.setValue(currentStatus);
                                        sendNotification(currentStatus, "Information");

                                    } else if (response != null && response.endsWith("0F")) {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(tempModeDetails.isState());
                                        DatabaseReference applianceRef = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerDetails")
                                                .child(tempModeDetails.waterSprinklerId);
                                        applianceRef.setValue(tempModeDetails);

                                        DatabaseReference notifyReference = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId).child(String.valueOf(System.currentTimeMillis()));
                                        String currentStatus = "The Master Controller could not process your request due to mismatch of message.\n Please contact our helpline";
                                        notifyReference.setValue(currentStatus);
                                        sendNotification(currentStatus, "Information");

                                    } else {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(tempModeDetails.isState());

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
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


    }

    private void slaveConfigration() {

        slaveConfig.child("Command").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {

                    if (dataSnapshot.getValue(String.class) != null) {

                        String response = sendMessage(dataSnapshot.getValue(String.class));
                        slaveConfig.child("Command").setValue("");
                        slaveConfig.child("Response").setValue(response);

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void energyInfoChangeOnline() {
        DatabaseReference energyRef = localRef.child("Energy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("consumed");
        energyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.getKey().toString().equals("enegyInfo")) {

                    boolean roomWiseConsumed = dataSnapshot.child("roomWiseConsumed").getValue(Boolean.class);
                    boolean applinceWiseConsumed = dataSnapshot.child("applinceWiseConsumed").getValue(Boolean.class);
                    if (roomWiseConsumed) {

                        Date currentdate = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss");
                        String formattedCurrentDate = dateFormat.format(currentdate);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        Date beforeDate = cal.getTime();

                        String formattedBeforeDate = dateFormat.format(beforeDate);


                        //getRoomWiseEnergyConsumed(formattedBeforeDate, formattedCurrentDate);


                        // Toast.makeText(SocketCommunicationService.this, "RoomWise", Toast.LENGTH_SHORT).show();

                    } else if (applinceWiseConsumed) {
                        // Toast.makeText(SocketCommunicationService.this, "ApplianceWise", Toast.LENGTH_SHORT).show();
                        Date currentdate = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss");
                        String formattedCurrentDate = dateFormat.format(currentdate);


                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        Date beforeDate = cal.getTime();

                        String formattedBeforeDate = dateFormat.format(beforeDate);

                        applianceWiseEnergy = "";
                        applianceWiseName = "";
                        getApplianceWiseEnergyConsumed(formattedBeforeDate, formattedCurrentDate);

                        DatabaseReference energyCalc = localRef.child("Energy").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("consumed").child("energyData");

                        Map<String, Object> energyData = new HashMap<>();
                        energyData.put("Energy", applianceWiseEnergy);
                        energyData.put("Appliance", applianceWiseName);
                        energyCalc.setValue(energyData);


                        DatabaseReference ref = localRef.child("Energy").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("consumed").child("applinceWiseConsumed");
                        ref.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                mutableData.setValue(false);

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getApplianceWiseEnergyConsumed(String fromDate, String toDate) {
        applianceWiseEnergyConsumed = new ArrayList<>();
        applianceNameList = new ArrayList<>();
        appliancesList = GlobalApplication.applianceList;

        if (appliancesList.size() != 0 && appliancesList != null) {


            for (int i = 0; i < appliancesList.size(); i++) {
                RoomAppliance roomAppliance = appliancesList.get(i);
                String roomId = roomAppliance.getRoomId();
                String applinaceId = roomAppliance.getId();
                String app = roomAppliance.getApplianceName();
                applianceNameList.add(app);
                List<EnergyData> applinceWiseList = EnergyDataHelper.getAllApplinaceWiseData(applinaceId, roomId);
                double finalEnergyCount = 0;
                if (applinceWiseList.size() != 0 && applinceWiseList != null) {

                    for (int j = 0; j < applinceWiseList.size(); j++) {
                        EnergyData energyData = applinceWiseList.get(j);
                        boolean state = energyData.state;
                        String onTime = energyData.datetime;
                        int energy = energyData.energy;
                        if (state) {
                            if (applinceWiseList.size() != 1) {
                                for (int k = j + 1; k < applinceWiseList.size(); k++) {

                                    EnergyData data = applinceWiseList.get(j + 1);
                                    boolean s = data.state;
                                    String offTime = data.datetime;
                                    int e = data.energy;
                                    String applianceName = data.applianceName;
                                    if (!s) {
                                        if (applianceNameList.size() != 0 && !applianceNameList.contains(applianceName)) {
                                            applianceNameList.add(applianceName);

                                        } else if (applianceNameList.size() == 0) {
                                            applianceNameList.add(applianceName);
                                        }
                                        if (ValidateDateForRoom(fromDate, toDate, offTime)) {

                                            double difference = getDifference(onTime, offTime);

                                            double finalEnergy = (e * difference);
                                            finalEnergyCount = finalEnergyCount + finalEnergy;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Date Mismatch", Toast.LENGTH_SHORT);
                                        }

                                        break;
                                    }

                                }

                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Data is not Available", Toast.LENGTH_SHORT).show();
                }
                if (finalEnergyCount < 0) {
                    finalEnergyCount = 0;
                }
                applianceWiseEnergyConsumed.add(finalEnergyCount);
            }

            for (int i = 0; i < applianceWiseEnergyConsumed.size(); i++) {
                applianceWiseEnergy = applianceWiseEnergy + String.valueOf(applianceWiseEnergyConsumed.get(i)) + " : ";
            }
        }
        if (applianceNameList.size() == applianceWiseEnergyConsumed.size() && applianceNameList != null && applianceWiseEnergyConsumed != null) {

            for (int i = 0; i < applianceNameList.size(); i++) {
                applianceWiseName = applianceWiseName + String.valueOf(applianceNameList.get(i)) + " : ";
            }

        } else {
            Toast.makeText(getApplicationContext(), "Improper Data", Toast.LENGTH_SHORT).show();
        }

    }

    private double getDifference(String onTime, String offTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss");
        double hour = 0.0000;
        try {
            Calendar cal = Calendar.getInstance();
            Date on = sdf.parse(onTime);
            cal.setTime(on);
            long time = cal.getTimeInMillis();

            Calendar cal1 = Calendar.getInstance();
            Date off = sdf.parse(offTime);
            cal1.setTime(off);
            long time1 = cal1.getTimeInMillis();
            long diff = time1 - time;
            if (diff > 5000) {
              /*  long diffHours = diff / (60 * 60 * 1000) % 24;*/
                hour = diff / (60 * 60 * 1000) % 24;
            } else {
                hour = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return hour;
    }

    private boolean ValidateDateForRoom(String fromDate, String toDate, String currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy"); //edit here
        boolean result = false;
        try {
            Date from = sdf.parse(fromDate.substring(0, fromDate.length() - 9));
            Date to = sdf.parse(toDate.substring(0, toDate.length() - 9));
            Date current = sdf.parse(currentDate.substring(0, currentDate.length() - 9));

            int r = current.compareTo(to);
            int r1 = current.compareTo(from);

            if (r1 == -1 || r == 1) {
                result = false;
            } else {
                result = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }


    private void lockStateChaneOnline() {

        lockEventListener = lockDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (commThread != null) {
                    lockDetailsList = new ArrayList<>();
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String lockName = snapshot.child("lockName").getValue(String.class);
                            String lockId = snapshot.child("lockId").getValue(String.class);
                            boolean lockState = snapshot.child("lockState").getValue(Boolean.class);
                            Integer lockToggle = snapshot.child("lockToggle").getValue(Integer.class);
                            LockDetails lock = new LockDetails(lockName, lockId, lockState, lockToggle);
                            if (lock.isLockToggle() == 1) {
                                String payLoad = "L" + masterControllerIDVal + lock.getLockId() + "2" + "0F";
                                String response = sendMessage(payLoad);

                                // Toast.makeText(SocketCommunicationService.this, "Respon Pabs :" + response, Toast.LENGTH_SHORT).show();
                                //It is correctly done
                                if (response != null && response.endsWith("1F")) {
                                    lock.setLockToggle(0);
                                    DatabaseReference lockRef = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockDetails").child(lockId);
                                    lockRef.setValue(lock);

                                    DatabaseReference notifyReference = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId).child(String.valueOf(System.currentTimeMillis()));

                                    String currentStatus = "Door is Unlock";
                                    notifyReference.setValue(currentStatus);
                                    sendNotification(currentStatus, "Information");

                                } else if (response != null && response.endsWith("2F")) {
                                    lock.setLockToggle(0);
                                    DatabaseReference lockRef = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockDetails").child(lockId);
                                    lockRef.setValue(lock);


                                    DatabaseReference notifyReference = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId).child(String.valueOf(System.currentTimeMillis()));

                                    String currentStatus = "The Slave for this room is turned Off \n thus unable to process your request";
                                    notifyReference.setValue(currentStatus);
                                    sendNotification(currentStatus, "Information");

                                    //sendNotification();

                                } else if (response != null && response.endsWith("0F")) {
                                    lock.setLockToggle(0);
                                    DatabaseReference lockRef = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockDetails").child(lockId);
                                    lockRef.setValue(lock);

                                    DatabaseReference notifyReference = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId).child(String.valueOf(System.currentTimeMillis()));
                                    String currentStatus = "The Master Controller could not process your request due to mismatch of message.\n Please contact our helpline";
                                    notifyReference.setValue(currentStatus);
                                    sendNotification(currentStatus, "Information");

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class LastConnectedTimeStamp implements Runnable {
        DatabaseReference lastOnline;
        private boolean toStart = true;

        public LastConnectedTimeStamp(DatabaseReference lastOnline) {
            this.lastOnline = lastOnline;
        }

        public boolean isToStart() {
            return toStart;
        }

        public void setToStart(boolean toStart) {
            this.toStart = toStart;
        }

        @Override
        public void run() {
            while (toStart) {
                if (isInternetAvailable()) {
                    lastOnline.setValue(ServerValue.TIMESTAMP);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    private void remoteStateChangeOnline() {
        try {
            remoteRefEventListner = remoteRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (commThread != null) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                                if (snapShot.exists() && snapShot.getChildrenCount() > 0) {
                                    String remoteId = snapShot.getKey().toString();
                                    for (DataSnapshot id : snapShot.getChildren()) {

                                        if (id.getRef().toString().contains("command")) {

                                            String s = id.child("message").getValue(String.class);
                                            Integer toggle = id.child("toggle").getValue(Integer.class);
                                            String responce = null;
                                            if (toggle == 1 && s != null && !s.equals("message")) {
                                                responce = sendMessage(s);
                                                DatabaseReference messageRef = localRef.child("remote")
                                                        .child(Customer.getCustomer().customerId).child(remoteId)
                                                        .child("command").child("toggle");
                                                messageRef.setValue(0);
                                                if (responce != null && responce.endsWith("1")) {
                                                    Toast.makeText(SocketCommunicationService.this, "IR Successfull", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


    private void CheckSocketConnection() {
        try {
            socketConnectionEventListner = socketConnection.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if (commThread != null) {

                            String response = sendMessage("z");

                            if (response != null && !response.equals("") && !response.equals("z")) {
                                if (response.equals("1")) {
                                    Toast.makeText(getApplicationContext(), "System is Online", Toast.LENGTH_SHORT).show();
                                }
                                //else if (response.startsWith("K") && response.endsWith("A"))
                                else if (response.startsWith("K") && response.endsWith("A")) {
                                    // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                    String[] responseArrayWhole = response.split("A");
                                    for (String arrayIndividual : responseArrayWhole) {
                                        arrayIndividual = arrayIndividual.substring(1);
                                        String slave = arrayIndividual.substring(0, 3);
                                        arrayIndividual = arrayIndividual.substring(3);
                                        String[] responseArray = arrayIndividual.split(":");
                                        for (String temp : responseArray) {
                                            if (temp.length() == 2) {
                                                Character appliance = temp.charAt(0);
                                                Character state = temp.charAt(1);
                                                if (state.equals('a') || state.equals('b') || state.equals('c') || state.equals('d') || state.equals('e')) {
                                                    UpdateDataDimmable(slave, appliance, state);
                                                } else if (state.equals('1') || state.equals('2')) {
                                                    UpdateDataNotDimmable(slave, appliance, state);
                                                }
                                            }
                                        }
                                    }
                                } else if (response.startsWith("T") && response.endsWith("A")) {
                                    String[] responseArrayWhole = response.split("A");
                                    for (String arrayIndividual : responseArrayWhole) {
                                        if (arrayIndividual.startsWith("K")) {
                                            arrayIndividual = arrayIndividual.substring(1);
                                            String slave = arrayIndividual.substring(0, 2);
                                            arrayIndividual = arrayIndividual.substring(3);
                                            String[] responseArray = arrayIndividual.split(":");
                                            for (String temp : responseArray) {
                                                if (temp.length() == 2) {
                                                    Character appliance = temp.charAt(0);
                                                    Character state = temp.charAt(1);
                                                    if (state.equals('a') || state.equals('b') || state.equals('c') || state.equals('d') || state.equals('e')) {
                                                        UpdateDataDimmable(slave, appliance, state);
                                                    } else if (state.equals('1') || state.equals('2')) {
                                                        UpdateDataNotDimmable(slave, appliance, state);
                                                    }
                                                }
                                            }

                                        } else if (arrayIndividual.startsWith("T")) {
                                            arrayIndividual = arrayIndividual.substring(1);
                                            String roomId = arrayIndividual.substring(0, 2);
                                            String time = arrayIndividual.substring(2);

                                            DatabaseReference errorRef = localRef.child("notification").child("motionDetection").child(Customer.getCustomer().customerId)
                                                    .child(String.valueOf(System.currentTimeMillis()));

                                          /*  String currentStatus = "Motion detected at Room No:" + RoomDataHelper.getRoomDetaildFromId(roomId).roomName + " at " +
                                                    time + " HRS";*/

                                            if (roomId != null) {


                                                String currentStatus = "Motion detected in " + SensorDataHelper.getRoomNameFromSensorId(roomId) + "Room  at " +
                                                        time + " HRS";

                                                errorRef.setValue(currentStatus);
                                                sendNotification("Motion detected in " + SensorDataHelper.getRoomNameFromSensorId(roomId) + "Room at " +
                                                        time + " HRS", "motionDetection");
                                                Toast.makeText(getApplicationContext(), "Motion detected in " + SensorDataHelper.getRoomNameFromSensorId(roomId) +
                                                        "Room at " + time + " HRS", Toast.LENGTH_SHORT).show();

                                                DatabaseReference occupancyRef = localRef.child("occupancy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(roomId);
                                                occupancyRef.setValue(String.valueOf(System.currentTimeMillis()));


                                            }

                                        }

                                    }

                                } else if (response.startsWith("L") && response.endsWith("A")) {
                                    String[] responseArrayWhole = response.split("A");
                                    for (String arrayIndividual : responseArrayWhole) {
                                        if (arrayIndividual.startsWith("K")) {
                                            arrayIndividual = arrayIndividual.substring(1);
                                            String slave = arrayIndividual.substring(0, 2);
                                            arrayIndividual = arrayIndividual.substring(3);
                                            String[] responseArray = arrayIndividual.split(":");
                                            for (String temp : responseArray) {
                                                if (temp.length() == 2) {
                                                    Character appliance = temp.charAt(0);
                                                    Character state = temp.charAt(1);
                                                    if (state.equals('a') || state.equals('b') || state.equals('c') || state.equals('d') || state.equals('e')) {
                                                        UpdateDataDimmable(slave, appliance, state);
                                                    } else if (state.equals('1') || state.equals('2')) {
                                                        UpdateDataNotDimmable(slave, appliance, state);
                                                    }
                                                }
                                            }

                                        } else if (arrayIndividual.startsWith("L")) {
                                            arrayIndividual = arrayIndividual.substring(1);
                                            Character lockID = arrayIndividual.charAt(0);
                                            Character batteryType = arrayIndividual.charAt(1);
                                            Character status = arrayIndividual.charAt(2);

                                            if (batteryType.equals('0')) {
                                                if (status.equals('1')) {
                                                    DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                            child(Customer.getCustomer().customerId)
                                                            .child(String.valueOf(System.currentTimeMillis()));
                                                    String currentStatus = "The Door is Locked!!!";
                                                    errorRef.setValue(currentStatus);
                                                    sendNotification(currentStatus, "lock");
                                                } else if (status.equals('2')) {
                                                    DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                            child(Customer.getCustomer().customerId)
                                                            .child(String.valueOf(System.currentTimeMillis()));
                                                    String currentStatus = "The Door is Unlocked!!!";
                                                    errorRef.setValue(currentStatus);
                                                    sendNotification(currentStatus, "lock");
                                                } else if (status.equals('3')) {
                                                    DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                            child(Customer.getCustomer().customerId)
                                                            .child(String.valueOf(System.currentTimeMillis()));
                                                    String currentStatus = "The Door was Closed but now its Open";
                                                    errorRef.setValue(currentStatus);
                                                    sendNotification(currentStatus, "lock");

                                                } else if (status.equals('4')) {
                                                    DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                            child(Customer.getCustomer().customerId)
                                                            .child(String.valueOf(System.currentTimeMillis()));
                                                    String currentStatus = "The Door was Closed and still now its Closed";
                                                    errorRef.setValue(currentStatus);
                                                    sendNotification(currentStatus, "lock");
                                                } else if (status.equals('5')) {
                                                    DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                            child(Customer.getCustomer().customerId)
                                                            .child(String.valueOf(System.currentTimeMillis()));
                                                    String currentStatus = "The Door was Open and still now its Open";
                                                    errorRef.setValue(currentStatus);
                                                    sendNotification(currentStatus, "lock");
                                                } else if (status.equals('6')) {
                                                    DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                            child(Customer.getCustomer().customerId)
                                                            .child(String.valueOf(System.currentTimeMillis()));
                                                    String currentStatus = "The Door was Open but now its Closed";
                                                    errorRef.setValue(currentStatus);
                                                    sendNotification(currentStatus, "lock");
                                                }
                                            } else if (batteryType.equals('1')) {
                                                DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                        child(Customer.getCustomer().customerId)
                                                        .child(String.valueOf(System.currentTimeMillis()));
                                                String currentStatus = "The Lock " + lockID + " Battery is running Low!!!";
                                                errorRef.setValue(currentStatus);
                                                sendNotification(currentStatus, "lock");
                                            } else if (batteryType.equals('2')) {
                                                DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                        child(Customer.getCustomer().customerId)
                                                        .child(String.valueOf(System.currentTimeMillis()));
                                                String currentStatus = "The Lock " + lockID + " is Full. Unplug the charger!!!";
                                                errorRef.setValue(currentStatus);
                                                sendNotification(currentStatus, "lock");
                                            } else if (batteryType.equals('3')) {
                                                DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                        child(Customer.getCustomer().customerId)
                                                        .child(String.valueOf(System.currentTimeMillis()));
                                                String currentStatus = "The Lock " + lockID + " charger is Connected.";
                                                errorRef.setValue(currentStatus);
                                                sendNotification(currentStatus, "lock");
                                            } else if (batteryType.equals('4')) {
                                                DatabaseReference errorRef = localRef.child("notification").child("lock").
                                                        child(Customer.getCustomer().customerId)
                                                        .child(String.valueOf(System.currentTimeMillis()));
                                                String currentStatus = "The Lock " + lockID + " charger is Disconnected.";
                                                errorRef.setValue(currentStatus);
                                                sendNotification(currentStatus, "lock");
                                            }
                                        }

                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Response Hulk: " + response, Toast.LENGTH_SHORT).show();
                                    toBeDestroyed = true;
                                    onDestroy();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Response Spider: " + response, Toast.LENGTH_SHORT).show();
                                toBeDestroyed = true;
                                onDestroy();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private void UpdateDataNotDimmable(String slave, Character appliance, Character state) {
        try {
            String roomId = "";
            String slaveId = "";
            final Boolean appliance_State;

            roomId = slave.substring(0, 2);
            slaveId = slave.substring(2);

            switch (state) {
                case '1':
                    appliance_State = false;
                    break;
                case '2':
                    appliance_State = true;
                    break;
                default:
                    appliance_State = false;
                    break;
            }

            final DatabaseReference _applianceRef = localRef
                    .child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetails")
                    .child(roomId).child(slaveId).child("appliance").child(appliance.toString());

            _applianceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                            && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {

                        RoomAppliance roomAppliance = dataSnapshot.getValue(RoomAppliance.class);
                        if (roomAppliance != null) {
                            roomAppliance.setState(appliance_State);

                            _applianceRef.setValue(roomAppliance);

                            /*roomAppliance = SetEnergyValue(roomAppliance, roomAppliance.getEnergy());
                            sendEnergyNotification(roomAppliance);*/

                            DatabaseReference allOnDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/all");
                            if (roomAppliance.isState()) {
                                updateCount(allOnDevices, 1);
                            } else {
                                updateCount(allOnDevices, -1);
                            }
                            if ("Light".equals(roomAppliance.getApplianceType())) {
                                DatabaseReference lightDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/light");

                                if (roomAppliance.isState()) {
                                    updateCount(lightDevices, 1);
                                } else {
                                    updateCount(lightDevices, -1);
                                }

                            } else if ("Fan".equals(roomAppliance.getApplianceType())) {
                                DatabaseReference fanDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/fan");

                                if (roomAppliance.isState()) {
                                    updateCount(fanDevices, 1);
                                } else {
                                    updateCount(fanDevices, -1);
                                }

                            } else if (!"Fan".equals(roomAppliance.getApplianceType()) &&
                                    !"Light".equals(roomAppliance.getApplianceType())) {
                                DatabaseReference onSockets = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/socket");

                                if (roomAppliance.isState()) {
                                    updateCount(onSockets, 1);
                                } else {
                                    updateCount(onSockets, -1);
                                }

                            } else {
                                DatabaseReference onOthers = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/others");

                                if (roomAppliance.isState()) {
                                    updateCount(onOthers, 1);
                                } else {
                                    updateCount(onOthers, -1);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
            sendNotification(currentStatus, "error");
        }

    }


    public static String toHex(String arg) throws UnsupportedEncodingException {
        //Change encoding according to your need
        return String.format("%04x", new BigInteger(1, arg.getBytes("UTF8")));
    }


    private void UpdateDataDimmable(String slave, Character appliance, Character state) {
        try {
            String roomId = "";
            String slaveId = "";
            final Character intensity;

            String hexValue = toHex(slave.toString());

            roomId = slave.substring(0, 1);
            slaveId = slave.substring(2);

            switch (state) {
                case 'a':
                    intensity = '1';
                    break;
                case 'b':
                    intensity = '2';
                    break;
                case 'c':
                    intensity = '3';
                    break;
                case 'd':
                    intensity = '4';
                    break;
                case 'e':
                    intensity = '5';
                    break;
                default:
                    intensity = '5';
                    break;
            }

            final DatabaseReference _applianceRef = localRef
                    .child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetails").child(roomId)
                    .child(slaveId).child("appliance").child(appliance.toString());

            _applianceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        RoomAppliance roomAppliance = dataSnapshot.getValue(RoomAppliance.class);
                        roomAppliance.setState(true);
                        roomAppliance.setDimableValue(Integer.parseInt(intensity.toString()));
                        _applianceRef.setValue(roomAppliance);
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
            sendNotification(currentStatus, "error");
        }
    }

    private void serviceResetOnline() {
        try {

            serviceResetEventListner = serviceReset.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (commThread != null && dataSnapshot.exists()) {
                        Boolean reset = dataSnapshot.getValue(Boolean.class);
                        if (reset) {
                            toBeDestroyed = true;
                            onDestroy();
                            serviceReset.setValue(false);
                        }
                    } else {
                        toBeDestroyed = true;
                        onDestroy();
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


    private void curtainOnline() {
        try {
            curtainDetailsEventListner = curtainDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (commThread != null && snapshot.exists()) {
                        for (DataSnapshot detailsSnapshot : snapshot.getChildren()) {
                            if (detailsSnapshot.exists()) {
                                for (DataSnapshot temp : detailsSnapshot.getChildren()) {
                                    if (temp.exists()) {

                                        for (DataSnapshot type : temp.getChildren()) {
                                            if (type.exists()) {

                                                CurtainDetails curtain = type.getValue(CurtainDetails.class);
                                                if (curtain.isToggle() == 1) {
                                        /*String payLoad = "M" + masterControllerIDVal + curtain.getRoomId() +
                                                curtain.getSlaveId();*/
                                                    String payLoad = "M" + masterControllerIDVal + curtain.getCurtainId();
                                                    String level = String.valueOf(curtain.getcurtainLevel());
                                                    payLoad = payLoad + level + "0F";
                                                    String response = sendMessage(payLoad);

                                                    curtain.setToggle(0);
                                                    DatabaseReference curtainRef = localRef
                                                            .child("curtain").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetails").child(curtain.getRoomId())
                                                            .child("curtainDetails").child(curtain.getCurtainId());

                                                    curtainRef.setValue(curtain);
                                                    if (response != null) {

                                                        if (response.endsWith("1F")) {
                                                            // Toast.makeText(getApplicationContext(), "Curtain Success", Toast.LENGTH_LONG).show();

                                                        } else if (response.endsWith("2F")) {
                                                            //Toast.makeText(getApplicationContext(), "Curtain Not Success", Toast.LENGTH_LONG).show();


                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Network Failure", Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        //  Toast.makeText(getApplicationContext(), "Fatal Error", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }
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
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


    }

    private void modeChangeOnline() {

        moodCheck = false;
        try {
            modeRefEventListner = modeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot modeSnapshot : dataSnapshot.getChildren()) {
                            if (modeSnapshot.exists()) {
                                final modeDetails tempModeDetails = modeSnapshot.getValue(modeDetails.class);
                                if (tempModeDetails.isToggle() == 1) {



                                    final Thread t1 = new Thread() {
                                        @Override
                                        public void run() {
                                            try {


                                                getSlaveWiseAppliances(tempModeDetails.getApplianceId(), tempModeDetails.getOffAppliances());

                                                for (int i = 0; i < slaveWiseOnApplianceList.size() || i < slaveWiseOffApplianceList.size(); i++) {


                                                    String payLoad = "E" + masterControllerIDVal + tempModeDetails.getRoomId() +
                                                            (i + 1);
                                                    String allOffAppliance = "";
                                                    String allOnAppliance = "";

                                                    String state = tempModeDetails.isState() ? "b" : "a";

                                                    for (int j = 0; j < slaveWiseOnApplianceList.get(i).size(); j++) {
                                                        allOnAppliance = allOnAppliance + slaveWiseOnApplianceList.get(i).get(j);
                                                    }


                                                    for (int j = 0; j < slaveWiseOffApplianceList.get(i).size(); j++) {
                                                        allOffAppliance = allOffAppliance + slaveWiseOffApplianceList.get(i).get(j);
                                                    }

                                                    StringBuilder offApplianceBuilder = new StringBuilder(allOffAppliance);


                                                    if(!tempModeDetails.isState()){
                                                        int k = 0;
                                                        while (k < offApplianceBuilder.length()) {

                                                            int index = allOnAppliance.indexOf(offApplianceBuilder.charAt(k));
                                                            if (index != -1) {

                                                                offApplianceBuilder.replace(k, k + 1, "");

                                                            }
                                                            k++;

                                                        }

                                                    }

                                                    allOffAppliance = offApplianceBuilder.toString();

                                                    if (!allOnAppliance.equals("") || !allOffAppliance.equals("")) {

                                                        payLoad = payLoad + allOnAppliance + state + allOffAppliance + "0F";

                                                        String response = sendMessage(payLoad);

                                        /*    try {
                                                Thread.sleep(600);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }*/

                                                        //}

                                                        if (response != null && response.endsWith("1F")) {

                                                            if (state.equals("a")) {
                                                                changeApplianceState(i, allOnAppliance, allOffAppliance, tempModeDetails.getRoomId());
                                                            } else {
                                                                changeApplianceState(i, allOffAppliance, allOnAppliance, tempModeDetails.getRoomId());
                                                            }


                                                            // tempModeDetails.setToggle(0);
                                                            moodCheck = true;
                                                            //  tempModeDetails.setState(!tempModeDetails.isState());
                                                /*DatabaseReference applianceRef = localRef
                                                        .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                                applianceRef.setValue(tempModeDetails);*/

                                                        } else if (response != null && response.endsWith("2F")) {
                                              /*  tempModeDetails.setToggle(0);
                                                // tempModeDetails.setState(tempModeDetails.isState());
                                                DatabaseReference applianceRef = localRef
                                                        .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                                applianceRef.setValue(tempModeDetails);*/
                                                        } else if (response != null && response.endsWith("0F")) {
                                               /* tempModeDetails.setToggle(0);
                                                //tempModeDetails.setState(tempModeDetails.isState());
                                                DatabaseReference applianceRef = localRef
                                                        .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                                applianceRef.setValue(tempModeDetails);*/
                                                        } else {
                                             /*   tempModeDetails.setToggle(0);
                                                //   tempModeDetails.setState(!tempModeDetails.isState());
                                                DatabaseReference applianceRef = localRef
                                                        .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                                applianceRef.setValue(tempModeDetails);*/
                                                        }

                                                    }
                                                }

                                                tempModeDetails.setToggle(0);
                                                if (moodCheck) {

                                                    moodCheck = false;
                                                    tempModeDetails.setState(!tempModeDetails.isState());
                                                }

                                                DatabaseReference applianceRef = localRef
                                                        .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                                applianceRef.setValue(tempModeDetails);


                                                String xx = tempModeDetails.getOffAppliances();
                                                String yy = tempModeDetails.getApplianceId();

                                                String iRCommand = "";
                                                if (tempModeDetails.isState()) {
                                                    iRCommand = tempModeDetails.getMoodONIRCommand();
                                                } else {
                                                    iRCommand = tempModeDetails.getMoodOFFIRCommand();
                                                }

                                                final String[] allIRCommand = iRCommand.split(",");
                                                runIRCommands(allIRCommand);


                                            } finally {

                                            }
                                        }
                                    };
                                    t1.start();







                                }

                                /*            String payLoad = "E" + masterControllerIDVal + tempModeDetails.getRoomId() +
                                            tempModeDetails.getSlaveId();
                                    String[] allOnApplianceArray = tempModeDetails.getApplianceId().split(",");

                                    String[] allOffApplianceArray = tempModeDetails.getOffAppliances().split(",");
                                    String allOffAppliance = "";
                                    String allOnAppliance = "";

                                    for (String s : allOnApplianceArray) {
                                        allOnAppliance = allOnAppliance + s;
                                    }
                                    for (String s : allOffApplianceArray) {
                                        allOffAppliance = allOffAppliance + s;
                                    }

                                    String state = tempModeDetails.isState() ? "b" : "a";
                                    payLoad = payLoad + allOnAppliance + state + allOffAppliance + "0F";
                                    String response = sendMessage(payLoad);
                                    if (response != null && response.endsWith("1F")) {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(!tempModeDetails.isState());
                                        DatabaseReference applianceRef = localRef
                                                .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                        applianceRef.setValue(tempModeDetails);

                                        //MaintainState(allOnApplianceArray,allOffApplianceArray,tempModeDetails);


                                    } else if (response != null && response.endsWith("2F")) {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(tempModeDetails.isState());
                                        DatabaseReference applianceRef = localRef
                                                .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                        applianceRef.setValue(tempModeDetails);
                                    } else if (response != null && response.endsWith("0F")) {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(tempModeDetails.isState());
                                        DatabaseReference applianceRef = localRef
                                                .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                        applianceRef.setValue(tempModeDetails);
                                    } else {
                                        tempModeDetails.setToggle(0);
                                        tempModeDetails.setState(!tempModeDetails.isState());
                                        DatabaseReference applianceRef = localRef
                                                .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(tempModeDetails.getModeId());
                                        applianceRef.setValue(tempModeDetails);
                                    }*/
                                //}
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


    public void changeApplianceState(int slaveId, String onApplianceList,String offApplianceList, String roomID) {

        DatabaseReference dbRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").
                child(roomID).child("" + (slaveId + 1));

        for (int a = 0; a < offApplianceList.length(); a++) {
            if (offApplianceList.charAt(a) != ' ')
                dbRef.child("appliance").child(""+offApplianceList.charAt(a)).child("state").setValue(false);
        }

        for (int a = 0; a < onApplianceList.length(); a++) {
            if (onApplianceList.charAt(a) != ' ')
                dbRef.child("appliance").child(""+onApplianceList.charAt(a)).child("state").setValue(true);
        }


    }


    int i = 0;

    public void runIRCommands(final String[] allIRCommand) {

        i = 0;


        for (; i < allIRCommand.length; i++) {
            if (allIRCommand != null && !allIRCommand.equals("")) {

                sendMessage(allIRCommand[i]);
                // handler.postDelayed(this, 200);

            }
        }
        /*try {
            final Handler handler = new Handler();

            final Runnable r = new Runnable() {
                @Override
                public void run() {

                    for (; i < allIRCommand.length; i++) {
                        if (allIRCommand != null && !allIRCommand.equals("")) {

                            sendMessage(allIRCommand[i]);
                           // handler.postDelayed(this, 200);

                        }
                    }
                    return;
                }
            };
            handler.post(r);

        } catch (Exception e) {

        }
*/
    }


    public void getSlaveWiseAppliances(String applianceList, String OffApplianceList) {


        slaveWiseOnApplianceList = new ArrayList<ArrayList<String>>(4);

        slaveWiseOffApplianceList = new ArrayList<ArrayList<String>>(4);
        //slaveWiseDimValue = new ArrayList<ArrayList<Character>>(4);

        for (int j = 0; j < 4; j++) {

            ArrayList<String> arr = new ArrayList<>(5);
            ArrayList<String> arr1 = new ArrayList<>(5);
            slaveWiseOnApplianceList.add(arr);
            slaveWiseOffApplianceList.add(arr1);

        }

        String[] allOnApplianceArray = applianceList.split(",");
        String[] allOffApplianceArray = OffApplianceList.split(",");

        for (int i = 0; i < allOffApplianceArray.length; i++) {

            if (allOffApplianceArray[i].length() == 2) {

                slaveWiseOffApplianceList.get(Character.getNumericValue(allOffApplianceArray[i].charAt(0)) - 1).add("" + allOffApplianceArray[i].charAt(1));

            }
        }

        for (int i = 0; i < allOnApplianceArray.length; i++) {

            if (allOnApplianceArray[i].length() == 2) {

                slaveWiseOnApplianceList.get(Character.getNumericValue(allOnApplianceArray[i].charAt(0)) - 1).add("" + allOnApplianceArray[i].charAt(1));

            }
        }


    }


    /*public void getSlaveWiseAppliances(String applianceList, String OffApplianceList) {


        slaveWiseOnApplianceList = new ArrayList<ArrayList<String>>(4);

        slaveWiseOffApplianceList = new ArrayList<ArrayList<String>>(4);
        //slaveWiseDimValue = new ArrayList<ArrayList<Character>>(4);

        for (int j = 0; j < 4; j++) {

            ArrayList<String> arr = new ArrayList<>(5);
            slaveWiseOnApplianceList.add(arr);
            slaveWiseOffApplianceList.add(arr);

        }

        String[] allOnApplianceArray = applianceList.split(",");
        String[] allOffApplianceArray = OffApplianceList.split(",");

        for (int i = 0; i < allOffApplianceArray.length; i++) {

            if (allOffApplianceArray[i].length() == 2) {

                slaveWiseOffApplianceList.get(Character.getNumericValue(allOffApplianceArray[i].charAt(0)) - 1).add("" + allOffApplianceArray[i].charAt(1));

            }
        }

        for (int i = 0; i < allOnApplianceArray.length; i++) {

            if (allOnApplianceArray[i].length() == 2) {

                slaveWiseOnApplianceList.get(Character.getNumericValue(allOnApplianceArray[i].charAt(0)) - 1).add("" + allOnApplianceArray[i].charAt(1));

            }
        }




    }*/


    private void roomsApplianceChangeOnline() {
        roomsDetailsEventListner = roomsDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (commThread != null && dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                        for (DataSnapshot type : dataSnapshot1.getChildren()) {
                            if (type.exists()) {

                                for (DataSnapshot slave : type.getChildren()) {

                                    if (slave.exists() && slave.getRef().toString().contains("appliance")) {
                                        for (DataSnapshot applianc : slave.getChildren()) {
                                            if (applianc.exists()) {
                                                RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                                if (roomAppliance.isToggle() == 1) {
                                                    DatabaseReference applianceRef = localRef
                                                            .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                                            .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId());
                                                    //Send Message to controller
                                                    String payLoad = "C" + masterControllerIDVal + roomAppliance.getRoomId() +
                                                            roomAppliance.getSlaveId() + roomAppliance.getId();
                                                    String state = roomAppliance.isState() ? "1" : "2";
                                                    String dimable = roomAppliance.isDimmable() ? "1" : "0";
                                                    String dimabbleValue = roomAppliance.isDimmable() ? String.
                                                            valueOf(roomAppliance.getDimableValue()) : "0";
                                                    payLoad = payLoad + state + dimable + dimabbleValue + "00000F";

                                                    boolean State = roomAppliance.isState();
                                                    int toggle = roomAppliance.isToggle();
                                                    String energy = roomAppliance.getEnergy();
                                                    String name = roomAppliance.getApplianceName();
                                                    boolean dim = roomAppliance.isDimmable();


                                                    // Toast.makeText(getApplicationContext(), payLoad, Toast.LENGTH_LONG).show();

                                                    Log.i("MESSAGE : ", payLoad);

                                                    String response = sendMessage(payLoad);

                                                    //  Toast.makeText(SocketCommunicationService.this, "Respon Pabs :" + response, Toast.LENGTH_SHORT).show();
                                                    //It is correctly done
                                                    if (response != null && response.endsWith("1F")) {

                                                        roomAppliance.setToggle(0);
                                                        roomAppliance.setState(!roomAppliance.isState());
                                                        if (response.length() > 6) {
                                                            roomAppliance.setEnergy(response.substring(response.length() - 6, response.length() - 2));
                                                            SetEnergyValue(roomAppliance);
                                                            sendEnergyNotification(roomAppliance);
                                                        }

                                                        applianceRef.setValue(roomAppliance);

                                                        DatabaseReference allOnDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/all");
                                                        if (roomAppliance.isState()) {
                                                            updateCount(allOnDevices, 1);
                                                        } else {
                                                            updateCount(allOnDevices, -1);
                                                        }
                                                        if ("Light".equals(roomAppliance.getApplianceType())) {
                                                            DatabaseReference lightDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/light");

                                                            if (roomAppliance.isState()) {
                                                                updateCount(lightDevices, 1);
                                                            } else {
                                                                updateCount(lightDevices, -1);
                                                            }

                                                        } else if ("Fan".equals(roomAppliance.getApplianceType())) {
                                                            DatabaseReference fanDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/fan");

                                                            if (roomAppliance.isState()) {
                                                                updateCount(fanDevices, 1);
                                                            } else {
                                                                updateCount(fanDevices, -1);
                                                            }

                                                        } else if (!"Fan".equals(roomAppliance.getApplianceType()) &&
                                                                !"Light".equals(roomAppliance.getApplianceType())) {
                                                            DatabaseReference onSockets = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/socket");

                                                            if (roomAppliance.isState()) {
                                                                updateCount(onSockets, 1);
                                                            } else {
                                                                updateCount(onSockets, -1);
                                                            }

                                                        } else {
                                                            DatabaseReference onOthers = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/others");

                                                            if (roomAppliance.isState()) {
                                                                updateCount(onOthers, 1);
                                                            } else {
                                                                updateCount(onOthers, -1);
                                                            }

                                                        }

                                                    } else if (response != null && response.endsWith("2F")) {


                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Slave for this room is turned Off \n thus unable to process your request";
                                                        errorRef.setValue(currentStatus);
                                                        sendNotification(currentStatus, "Information");

                                        /*}*/
                                                        //Dont change state just reset toggle
                                                        roomAppliance.setToggle(0);
                                                        roomAppliance.setState(roomAppliance.isState());

                                                        applianceRef.setValue(roomAppliance);

                                                        Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                    } else if (response != null && response.endsWith("0F")) {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));
                                                        String currentStatus = "The Master Controller could not process your request due to mismatch of message.\n Please contact our helpline";
                                                        errorRef.setValue(currentStatus);                                           //
                                                        sendNotification(currentStatus, "Information");

                                                        Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                        roomAppliance.setToggle(0);
                                                        roomAppliance.setState(roomAppliance.isState());
                                                        applianceRef.setValue(roomAppliance);

                                                        Toast.makeText(getApplicationContext(), "Response Hulk: " + response, Toast.LENGTH_SHORT).show();
                                                        toBeDestroyed = true;
                                                        onDestroy();

                                                    } else {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));
                                                        String currentStatus = "The Master is not responding.\n Please restart the Master. \n If issue still persists please contact our helpline";
                                                        errorRef.setValue(currentStatus);
                                                        sendNotification(currentStatus, "Information");

                                                        Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                        roomAppliance.setToggle(0);
                                                        roomAppliance.setState(roomAppliance.isState());
                                                        applianceRef.setValue(roomAppliance);

                                                        Toast.makeText(getApplicationContext(), "Response Hulk: " + response, Toast.LENGTH_SHORT).show();
                                                        toBeDestroyed = true;
                                                        onDestroy();
                                                    }
                                                }


                                                //TODO Dimmable
                                                if (roomAppliance.isDimableToggle() == 1) {
                                                    //Send Message

                                                    String payLoad = "C" + masterControllerIDVal + roomAppliance.getRoomId() +
                                                            roomAppliance.getSlaveId() + roomAppliance.getId();
                                                    String state = "2"; //roomAppliance.isState() ? "1" :
                                                    String dimable = roomAppliance.isDimmable() ? "1" : "0";
                                                    String dimabbleValue = roomAppliance.isDimmable() ? String.
                                                            valueOf(roomAppliance.getDimableValue()) : "0";
                                                    payLoad = payLoad + state + dimable + dimabbleValue + "00000F";

                                                    Log.i("MESSAGE : ", payLoad);

                                                    String response = sendMessage(payLoad);

                                                    // Toast.makeText(getApplicationContext(), "Dimable " + payLoad, Toast.LENGTH_SHORT).show();

                                                    DatabaseReference dimableRef = localRef
                                                            .child("rooms").child(Customer.getCustomer().customerId).
                                                                    child("roomdetails").child(roomAppliance.getRoomId())
                                                            .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableToggle");
                                                    if (response != null && response.endsWith("1F")) {

                                                        roomAppliance.setDimableToggle(0);
                                                        //roomAppliance.setState(!roomAppliance.isState());

                                                        dimableRef.setValue(0);

                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                                                .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId());
                                                        if (response.length() > 6) {

                                                            roomAppliance.setEnergy(response.substring(response.length() - 6, response.length() - 2));
                                                            SetEnergyValue(roomAppliance);
                                                            sendEnergyNotification(roomAppliance);
                                                        }

                                                        applianceRef.setValue(roomAppliance);


                                                    } else if (response != null && response.endsWith("2F")) {

                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Slave for this room is turned Off thus unable to process your request";
                                                        errorRef.setValue(currentStatus);

                                        /*}*/
                                                        //Dont change state just reset toggle
                                                        roomAppliance.setDimableToggle(0);
                                                        //roomAppliance.setState(roomAppliance.isState());

                                                        dimableRef.setValue("false");

                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                                                .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId());

                                                        applianceRef.setValue(roomAppliance);
                                                    } else if (response != null && response.endsWith("0F")) {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Master Controller could not process your resquest due to mismatch of message. Please contact our helpline";
                                                        errorRef.setValue(currentStatus);

                                                        Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                        roomAppliance.setDimableToggle(0);
                                                        //roomAppliance.setState(roomAppliance.isState());

                                                        dimableRef.setValue("false");

                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                                                .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId());

                                                        applianceRef.setValue(roomAppliance);

                                                        // Toast.makeText(getApplicationContext(), "Response Hulk: " + response, Toast.LENGTH_SHORT).show();
                                                        toBeDestroyed = true;
                                                        onDestroy();
                                                    } else {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Master is not responding.\n Please restart the Master. \n If issue still persists please contact our helpline";
                                                        errorRef.setValue(currentStatus);

                                                        //  Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                        roomAppliance.setDimableToggle(0);
                                                        //roomAppliance.setState(roomAppliance.isState());

                                                        dimableRef.setValue("false");

                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                                                .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId());

                                                        applianceRef.setValue(roomAppliance);

                                                        Toast.makeText(getApplicationContext(), "Response Hulk: " + response, Toast.LENGTH_SHORT).show();
                                                        toBeDestroyed = true;
                                                        onDestroy();
                                                    }
                                                }
                                            }
                                        }

                                    } else if (slave.getRef().toString().contains("sensors") && !slave.getRef().toString().contains("appliance")) {
                                        for (DataSnapshot sensors : slave.getChildren()) {

                                            if (sensors.exists()) {

                                                SensorTab sensor = sensors.getValue(SensorTab.class);
                                                if (sensor.getId() != null && sensor.isToggle() == 1) {


                                                    //START OF CHANGE

                                                    sensor.setToggle(0);
                                                    String sensor_id = "0" + String.valueOf(sensor.getSensorTypeId() - 1);

                                                    DatabaseReference _sensorRef = localRef
                                                            .child("rooms")
                                                            .child(Customer.getCustomer().customerId)
                                                            .child("roomdetails").child(sensor.getRoomId())
                                                            .child("sensors").child(sensor.getSlaveId()).child(sensor_id);

                                                    _sensorRef.setValue(sensor);

                                                    //END OF CHANGE


                                                    ////   Sir code
                                              /*  String payLoad = "G" + masterControllerIDVal + "0" + String.valueOf(Integer.valueOf(sensor.getSlaveId())-1) + "2"
                                                        + String.valueOf((Integer.valueOf(sensor.getId()) % 4)+1);*/

                                                    String payLoad = "G" + masterControllerIDVal + sensor.getId() + "2"
                                                            + String.valueOf((Integer.valueOf(sensor.getSensorTypeId())));
                                                    String state = sensor.isState() ? "1" : "2";

                                                    payLoad = payLoad + state + "0F";

                                                    //Toast.makeText(getApplicationContext(), payLoad, Toast.LENGTH_LONG).show();

                                                    Log.i("MESSAGE : ", payLoad);
                                                    String response = sendMessage(payLoad);

                                                    if (response != null && response.endsWith("1F")) {
                                                        sensor.setToggle(0);
                                                        sensor.setState(!sensor.isState());

                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms")
                                                                .child(Customer.getCustomer().customerId)
                                                                .child("roomdetails").child(sensor.getRoomId())
                                                                .child("sensors").child(sensor.getSlaveId()).child(sensor_id);

                                                        applianceRef.setValue(sensor);
                                                    } else if (response != null && response.endsWith("2F")) {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Slave for this room is turned Off thus unable to process your request";


                                                        errorRef.setValue(currentStatus);

                                        /*}*/
                                                        sensor.setToggle(0);
                                                        sensor.setState(sensor.isState());

                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms")
                                                                .child(Customer.getCustomer().customerId)
                                                                .child("roomdetails").child(sensor.getRoomId())
                                                                .child("sensors").child(sensor.getSlaveId()).child(sensor_id);

                                                        applianceRef.setValue(sensor);
                                                    } else if (response != null && response.endsWith("0F")) {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Master Controller could not process your resquest due to mismatch of message. Please contact our helpline";
                                                        errorRef.setValue(currentStatus);

                                                        Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                        sensor.setToggle(0);
                                                        sensor.setState(sensor.isState());
                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms")
                                                                .child(Customer.getCustomer().customerId)
                                                                .child("roomdetails").child(sensor.getRoomId())
                                                                .child("sensors").child(sensor.getSlaveId()).child(sensor_id);

                                                        applianceRef.setValue(sensor);
                                                    } else {
                                                        DatabaseReference errorRef = localRef.child("notification").child("Information").child(Customer.getCustomer().customerId)
                                                                .child(String.valueOf(System.currentTimeMillis()));

                                                        String currentStatus = "The Master is not responding.\n Please restart the Master. \n If issue still persists please contact our helpline";
                                                        errorRef.setValue(currentStatus);

                                                        Toast.makeText(SocketCommunicationService.this, currentStatus, Toast.LENGTH_LONG).show();
                                                        sensor.setToggle(0);
                                                        sensor.setState(sensor.isState());
                                                        DatabaseReference applianceRef = localRef
                                                                .child("rooms")
                                                                .child(Customer.getCustomer().customerId)
                                                                .child("roomdetails").child(sensor.getRoomId())
                                                                .child("sensors").child(sensor.getSlaveId()).child(sensor_id);

                                                        applianceRef.setValue(sensor);

                                                        Toast.makeText(getApplicationContext(), "Response Hulk: " + response, Toast.LENGTH_SHORT).show();
                                                        toBeDestroyed = true;
                                                        onDestroy();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void globalOffStateChangeOnline() {
        try {
            roomsDetailsEventListner = roomsDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (commThread != null && snapshot.exists()) {

                        for (DataSnapshot roomSnapshot : snapshot.getChildren()) {

                            if (roomSnapshot.exists()) {
                                if (roomSnapshot.getRef().toString().contains("globalOff")) {
                                    GlobalOff toGlobalOff = roomSnapshot.getValue(GlobalOff.class);
                                    if (toGlobalOff.isToggle() == 1) {
                                        if (!toGlobalOff.isGlobalOffState()) {
                                            String globalOffRequest = "B" + masterControllerIDVal + "20F";
                                            String globalOffResponse = sendMessage(globalOffRequest);

                                            if (!(globalOffResponse.equals("") || globalOffResponse.equals(null))) {

                                                if (globalOffResponse.endsWith("1F")) {

                                                    toGlobalOff.setToggle(0);
                                                    toGlobalOff.setGlobalOffState(true);

                                                    DatabaseReference globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("globalOff");
                                                    globalOffRef.setValue(toGlobalOff);

                                                    sendGlobalOffNotification("All Appliances have been switched off");
                                                    Toast.makeText(getApplicationContext(), "All Appliances have been switched off", Toast.LENGTH_LONG).show();


                                                    for (DataSnapshot allRooms : snapshot.getChildren()) {
                                                        if (allRooms.exists()) {
                                                            for (DataSnapshot type : allRooms.getChildren()) {
                                                                if (type.exists()) {
                                                                    for (DataSnapshot slave : type.getChildren()) {
                                                                        if (slave.exists()) {
                                                                            if (slave.getRef().toString().contains("appliance")) {
                                                                                for (DataSnapshot applianc : slave.getChildren()) {
                                                                                    if (applianc.exists()) {
                                                                                        RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                                                                        if (roomAppliance.isState()) {
                                                                                            roomAppliance.setState(false);
                                                                                            DatabaseReference applianceRef = localRef
                                                                                                    .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                                                                                    .child(roomAppliance.getSlaveId())
                                                                                                    .child("appliance").child(roomAppliance.getId());
                                                                                            applianceRef.setValue(roomAppliance);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    DatabaseReference allOnDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/all");
                                                    allOnDevices.setValue(0);
                                                    DatabaseReference lightDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/light");
                                                    lightDevices.setValue(0);
                                                    DatabaseReference fanDevices = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/fan");
                                                    fanDevices.setValue(0);
                                                    DatabaseReference onSockets = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("roomdetailsCount/on/socket");
                                                    onSockets.setValue(0);

                                                } else {

                                                    DatabaseReference globalOffRef1 = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("globalOff").child("toggle");
                                                    globalOffRef1.setValue("0");
                                                    Toast.makeText(getApplicationContext(), "All your appliances could not be switched off " +
                                                            "/n due to some tecnical glitch. /n Please try again after sometime.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            String globalOnRequest = "B" + masterControllerIDVal + "10F";
                                            String globalOnResponse = sendMessage(globalOnRequest);

                                            if (!(globalOnResponse.equals("") || globalOnResponse.equals(null))) {

                                                if (globalOnResponse.endsWith("1F")) {

                                                    toGlobalOff.setToggle(0);
                                                    toGlobalOff.setGlobalOffState(false);

                                                    DatabaseReference globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("globalOff");
                                                    globalOffRef.setValue(toGlobalOff);

                                                    sendGlobalOffNotification("Motion Sensors Disarmed.. Welcome Home!!!");

                                                } else {
                                                    DatabaseReference globalOffRef1 = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("globalOff").child("toggle");
                                                    globalOffRef1.setValue("0");
                                                    Toast.makeText(getApplicationContext(), "All your appliances could not be switched off " +
                                                            "/n due to some tecnical glitch. /n Please try again after sometime.", Toast.LENGTH_LONG).show();
                                                }

                                            }


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
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private void sendGlobalOffNotification(String s) {
        try {
            DatabaseReference globalOffNotification = localRef.child("notification")
                    .child("globalOff").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(String.valueOf(System.currentTimeMillis()));
            globalOffNotification.setValue(s);
            builder = new NotificationCompat.Builder(getApplicationContext());
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setContentTitle("GlobalOff Notification");
            builder.setSmallIcon(R.drawable.small_logo1);
            builder.setContentText(s);
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            builder.setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.small_logo1));
            builder.setSound(soundUri);
            builder.setPriority(Notification.PRIORITY_HIGH);
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }

    private void sendEnergyNotification(RoomAppliance appliance) {
        try {
            if (appliance != null && appliance.isState()) {

                String notification = null;
                notification = appliance.getApplianceName() + " Of Room " + appliance.getRoomId() + " is Currently Switched on \n and Consuming " + appliance.getEnergy().concat(" Watts") + " of Energy";


                builder = new NotificationCompat.Builder(getApplicationContext());
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setContentTitle("Energy Notification");
                builder.setSmallIcon(R.drawable.small_logo1);
                builder.setContentText(notification);
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
                builder.setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.small_logo1));
                builder.setSound(soundUri);
                builder.setPriority(Notification.PRIORITY_HIGH);
                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if (GlobalApplication.isEnergyNotificationSubscribe) {
                    notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                }

                DatabaseReference energyNotification = localRef.child("notification")
                        .child("energy").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                energyNotification.setValue(notification);
            } else if (appliance != null && !appliance.isState()) {

                String notification1 = null;
                notification1 = appliance.getApplianceName() + " Of Room " + appliance.getRoomId() + " is Currently Switched off \n and Consuming " + appliance.getEnergy().concat(" Watts") + " of Energy";


                builder = new NotificationCompat.Builder(getApplicationContext());
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setContentTitle("Energy Notification");
                builder.setSmallIcon(R.drawable.small_logo1);
                builder.setContentText(notification1);
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
                builder.setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.small_logo1));
                builder.setSound(soundUri);
                builder.setPriority(Notification.PRIORITY_HIGH);
                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if (GlobalApplication.isEnergyNotificationSubscribe) {
                    notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                }

                DatabaseReference energyOffNotification = localRef.child("notification")
                        .child("energy").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                energyOffNotification.setValue(notification1);
            }
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private void sendNotification(String currentStatus, String type) {
        try {
            builder = new NotificationCompat.Builder(getApplicationContext());
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           /* DatabaseReference notification = localRef.child("notification")
                    .child("motionDetection").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(String.valueOf(System.currentTimeMillis()));
            notification.setValue(currentStatus);*/

            builder.setContentTitle("Notification");
            builder.setSmallIcon(R.drawable.small_logo1);
            builder.setContentText(currentStatus);
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.small_logo1));
            builder.setSound(soundUri);
            builder.setPriority(Notification.PRIORITY_HIGH);
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (type.equals("motionDetection")) {

                if (GlobalApplication.isMotionDetectionSubscribe) {
                    notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                }
            } else if (type.equals("lock")) {
                if (GlobalApplication.isLockNotificationSubscribe) {

                    notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                }

            } else if (type.equals("Information")) {


                notificationManager.notify((int) System.currentTimeMillis(), builder.build());


            }

        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String Status = e.getMessage();
            errorRef.setValue(Status);
        }
    }

    public void updateCount(DatabaseReference firebaseRef, final int count) {
        try {
            firebaseRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        currentData.setValue((Long) currentData.getValue() + count);
                    }
                    return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                }

                @Override
                public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                    //This method will be called once with the results of the transaction.
                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String Status = e.getMessage();
            errorRef.setValue(Status);
        }
    }

    public CommunicationHandler initSocketConnection() {
        try {

            Log.i("initSocketConnection ", "CALLED");
            try {
                if (commThread.getEchoSocket() != null) {
                    commThread.getEchoSocket().close();
                    commThread.getLooper().quit();
                    if (!serverHostname.equals(""))
                        Toast.makeText(getApplicationContext(), "Disconnected from " + serverHostname, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
            if (!serverHostname.equals(null)) {

                commThread = new CommunicationHandler(this, "COMM_Thread", serverHostname, port);
                commThread.setPriority(Thread.MAX_PRIORITY);
                commThread.start();
            }
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String Status = e.getMessage();
            errorRef.setValue(Status);
        }

        return commThread;
    }

    @Override
    public void onDestroy() {
        try {

            if (toBeDestroyed) {
                toBeDestroyed = false;
                wakeLock.release();
                if (!executor_.isTerminated()) executor_.shutdownNow();

                lastConnectedTimeStamp.setToStart(false);
                lastConnectedTimeStamp = null;
                if (commThread != null) {
                    try {
                        commThread.getCommhandler().removeCallbacks(commThread);
                        commThread.getEchoSocket().close();
                        if (commThread.getLooper() != null) {
                            commThread.getLooper().quit();
                        }
                        Thread moribund = commThread;
                        commThread = null;
                        moribund.interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //  if(remoteRef != null && remoteRefEventListner != null) remoteRef.removeEventListener(remoteRefEventListner);
                /*if(sceneDetailsRef != null && sceneDetailsEventListner != null) sceneDetailsRef.removeEventListener(sceneDetailsEventListner);*/
                if (socketConnection != null && socketConnectionEventListner != null)
                    socketConnection.removeEventListener(socketConnectionEventListner);
                if (serviceReset != null && serviceResetEventListner != null)
                    serviceReset.removeEventListener(serviceResetEventListner);
                if (curtainDetails != null && curtainDetailsEventListner != null)
                    curtainDetails.removeEventListener(curtainDetailsEventListner);
                if (modeRef != null && modeRefEventListner != null)
                    modeRef.removeEventListener(modeRefEventListner);
                if (roomsDetails != null && roomsDetailsEventListner != null)
                    roomsDetails.removeEventListener(roomsDetailsEventListner);

                super.onDestroy();
                Intent intent = new Intent();
                intent.setAction("ServiceStopBroadcast");
                //intent.putExtra("pid",getServicePid());
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setClass(this, ConnectionChangeReceiver.class);
                sendBroadcast(intent);
                stopSelf();

                Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();

                DatabaseReference state = localRef.child("servicestate").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                state.setValue(false);
/*

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Attention")
                        .setMessage("Service Stopped\n" +
                                "Please Restart Your WIFI To Continue..")
                        .create();

                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alertDialog.show();
*/


               /* Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                GlobalApplication.context.sendBroadcast(closeDialog);*/


            }
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String Status = e.getMessage();
            errorRef.setValue(Status);
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // Check internet connection and accrding to state change the
        // text of activity by calling method
        if ((networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized String sendMessage(String message) {
        String response = "";
        int counter = 0;
        try {

            if (commThread != null && commThread.getCommhandler() != null) {
                Message messg = commThread.getCommhandler().obtainMessage();
                Bundle bundledata = new Bundle();
                bundledata.putString("msg", message);
                messg.setData(bundledata);
                commThread.setPriority(Thread.MAX_PRIORITY);
                commThread.getResponsListner().put(message, false);
                commThread.getCommhandler().sendMessage(messg);
                try {
                    while (!commThread.getResponsListner().get(message) && counter <= 50) {
                        Thread.sleep(200);
                        counter++;
                    }
                    commThread.getResponsListner().clear();

                    response = commThread.getResponse().get(message);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                Log.i("#APPLIANCE#", message);
            } else {
                Toast.makeText(getApplicationContext(), "No socket created", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            toBeDestroyed = true;
            onDestroy();
        }
        return response;
    }

    private void SetEnergyValue(final RoomAppliance roomAppliance) {


        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss");
        String formattedDate = dateFormat.format(date);
        int energy;
        try {

            energy = Integer.parseInt((roomAppliance.getEnergy()));
        } catch (NumberFormatException e) {
            energy = 0;
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

        EnergyDataHelper.addEnergy(formattedDate, roomAppliance.getSlaveId(),
                energy, roomAppliance.getApplianceName(), roomAppliance.getId(), roomAppliance.isState(),
                roomAppliance.isDimmable(), roomAppliance.getDimableValue(), roomAppliance.getRoomId());


    }
}