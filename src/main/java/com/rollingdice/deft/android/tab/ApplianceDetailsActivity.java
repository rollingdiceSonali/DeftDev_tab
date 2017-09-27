package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.ApplianceDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.State;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class ApplianceDetailsActivity extends Activity
{

    private String[] applianceTypeArray = GlobalApplication.APPLIANCE_TYPE;
    private Spinner spinnerApplianceType;
    private EditText etApplianceName;
    private EditText etApplianceId;
    private CheckBox cbApplianceDimmable;
    private String updateRoomId;
    private String applianceId;
    private String applianceName;
    private String slaveId;
    private boolean applianceUpdateMode;
    private int applianceTypeId;
    private String customerId;
    private String roomName ;
    private DatabaseReference localRef,energylog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_appliance_details);
            localRef = GlobalApplication.firebaseRef;
            Bundle extras = getIntent().getExtras();
            if (extras != null)
            {
                applianceTypeId = extras.getInt("APPLIANCE_TYPE_ID");
                updateRoomId = extras.getString("ROOM_ID");
                applianceId = extras.getString("APPLIANCE_ID");
                applianceName = extras.getString("APPLIANCE_NAME");
                slaveId = extras.getString("SLAVE_ID");
                customerId = extras.getString("CUSTOMER_ID");
                applianceUpdateMode = extras.getBoolean("APPLIANCE_UPDATE_MODE");
            }

            etApplianceName = (EditText) findViewById(R.id.et_appliance_name);
            etApplianceId = (EditText) findViewById(R.id.et_appliance_id);
            cbApplianceDimmable = (CheckBox) findViewById(R.id.cb_appliance_dimmable);

            spinnerApplianceType = (Spinner) findViewById(R.id.spinner_appliance_type);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, applianceTypeArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerApplianceType.setAdapter(spinnerArrayAdapter);

            etApplianceId.setText(applianceId);
            etApplianceName.setText(applianceName);
            spinnerApplianceType.setSelection(applianceTypeId);
            Button btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

            Button btnSaveAppliance = (Button) findViewById(R.id.btn_save);
            btnSaveAppliance.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ApplianceDataHelper.addAppliance((int) spinnerApplianceType.getSelectedItemId(),
                            slaveId,
                            etApplianceName.getText().toString(),
                            etApplianceId.getText().toString(),
                            State.STATE_OFF, cbApplianceDimmable.isChecked(),
                            0,
                            updateRoomId);

                    /* Fire Base Code Start*/
                    if (!applianceUpdateMode)
                    {

                        // Increase slave controller Count
                        DatabaseReference roomSlaveController = localRef.child("rooms").child(Customer.getCustomer().customerId).
                                child("roomController")
                                .child(updateRoomId);
                        Map<String, Object> roomSlvCntrlr = new HashMap<>();
                        roomSlvCntrlr.put(slaveId, Boolean.valueOf(true));
                        roomSlaveController.updateChildren(roomSlvCntrlr);
                        DatabaseReference roomSlaveControllerCnt = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomControllerCnt")
                                .child(updateRoomId).child(slaveId);
                        updateCount(roomSlaveControllerCnt, 1);

                        //Increase appilcation Count
                        DatabaseReference roomApplianceCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                .child("roomdetailsCount")
                                .child("applianceCount");
                        updateCount(roomApplianceCountRef, 1);

                        //Increase Count of Specific devices
                        switch (applianceTypeArray[(int) spinnerApplianceType.getSelectedItemId()]) {
                            case "Light":
                                DatabaseReference lightCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("lightCount");
                                updateCount(lightCountRef, 1);
                                break;
                            case "Fan":
                                DatabaseReference fanCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("fanCount");
                                updateCount(fanCountRef, 1);
                                break;
                            case "Sockets":
                                DatabaseReference socketCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountRef, 1);
                                break;
                            case "TV":
                                DatabaseReference socketCountTvRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountTvRef, 1);
                                break;
                            case "Refrigerator":
                                DatabaseReference socketCountFridgeRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountFridgeRef, 1);
                                break;
                            case "Washing Machine":
                                DatabaseReference socketCountWMRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountWMRef, 1);
                                break;
                            case "Geyser":
                                DatabaseReference socketCountGeyserRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountGeyserRef, 1);
                                break;
                            case "Music System":
                                DatabaseReference socketCountMSRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountMSRef, 1);
                                break;
                            case "AC":
                                DatabaseReference socketCountACRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");
                                updateCount(socketCountACRef, 1);
                                break;
                            default:
                                DatabaseReference defaultCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("others");
                                updateCount(defaultCountRef, 1);
                                break;
                        }

                    }
                    else
                    {



                        //Change count if the type changes
                        if (spinnerApplianceType.getSelectedItemId() != applianceTypeId) {
                            if ("Light".equals(applianceTypeArray[(int) spinnerApplianceType.getSelectedItemId()])) {
                                DatabaseReference lightCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("lightCount");

                                updateCount(lightCountRef, 1);

                            } else if ("Fan".equals(applianceTypeArray[(int) spinnerApplianceType.getSelectedItemId()])) {
                                DatabaseReference fanCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("fanCount");

                                updateCount(fanCountRef, 1);
                            } else if ("Sockets".equals(applianceTypeArray[(int) spinnerApplianceType.getSelectedItemId()])) {
                                DatabaseReference socketCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");

                                updateCount(socketCountRef, 1);
                            } else {
                                DatabaseReference defaultCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("others");

                                updateCount(defaultCountRef, 1);
                            }

                            if ("Light".equals(applianceTypeArray[applianceTypeId])) {
                                DatabaseReference lightCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("lightCount");

                                updateCount(lightCountRef, -1);

                            } else if ("Fan".equals(applianceTypeArray[applianceTypeId])) {
                                DatabaseReference fanCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("fanCount");

                                updateCount(fanCountRef, -1);
                            } else if ("Sockets".equals(applianceTypeArray[applianceTypeId])) {
                                DatabaseReference socketCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("socketCount");

                                updateCount(socketCountRef, -1);
                            } else {
                                DatabaseReference defaultCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount").child("others");

                                updateCount(defaultCountRef, -1);
                            }
                        }

                    }
                //Save Room in roomdetails url
                    DatabaseReference roomAppLianceRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails")
                        .child(updateRoomId).child(slaveId).child("appliance").child(etApplianceId.getText().toString());

                    DatabaseReference roomNameUrl = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails")
                        .child(updateRoomId).child("roomName");


                roomNameUrl.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // do some stuff once
                        roomName = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                    }
                });



                Map<String, Object> applianceDetails = new HashMap<>();
                applianceDetails.put("applianceType", applianceTypeArray[(int) spinnerApplianceType.getSelectedItemId()]);
                applianceDetails.put("applianceTypeId", String.valueOf(spinnerApplianceType.getSelectedItemId()));
                applianceDetails.put("applianceName", etApplianceName.getText().toString());
                applianceDetails.put("state", Boolean.valueOf(false));
                applianceDetails.put("dimmable", Boolean.valueOf(cbApplianceDimmable.isChecked()));
                applianceDetails.put("roomId", updateRoomId);
                applianceDetails.put("roomName", roomName);
                applianceDetails.put("id", etApplianceId.getText().toString());
                applianceDetails.put("toggle", 0);
                applianceDetails.put("dimableToggle", 0);
                applianceDetails.put("processing",Boolean.valueOf(false));
                applianceDetails.put("energy", "0");
                if (cbApplianceDimmable.isChecked()) {

                    applianceDetails.put("dimableValue", Integer.valueOf(5));
                } else {
                    applianceDetails.put("dimableValue", null);
                }

                applianceDetails.put("slaveId", slaveId);
                roomAppLianceRef.updateChildren(applianceDetails);
                /*Fire Base Code End*/
                Log.i("APPLIANCE  :  ", "SAVED");
                GlobalApplication.APPLIANCE_ADD_MODE = true;
                GlobalApplication.APPLIANCE_ID = etApplianceId.getText().toString();
                GlobalApplication.ROOM_ID = updateRoomId;
                finish();
            }
        });

        long date = System.currentTimeMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy-HH:mm:ss");

        //SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date);

        String energy = "0";

        energylog = localRef.child("energylog").child(Customer.getCustomer().customerId)
                .child(updateRoomId).child(slaveId).child(applianceId).child(formattedDate);


        Map<String, Object> energylogDetails = new HashMap<>();
        energylogDetails.put("energyDateObject", formattedDate);
        energylogDetails.put("energyDateCount", 0);
        energylogDetails.put("state", "0");
        energylogDetails.put("energy", energy);
        energylogDetails.put("level", "0");

        energylog.updateChildren(energylogDetails);
        }
        catch(Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }


    /*public String getApplicanceIDForFirebase(String slaveId, String roomId,String applianceID)
    {
        String id = "";

        switch(slaveId) {
            case "1":
                id = applianceID;
                    break;
            case "2":
                int slaveOneAppCount = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(roomId,"1").size();
                int appID = Integer.valueOf(applianceID) + 1;
                int count = slaveOneAppCount + appID;
                id = String.valueOf(count);
                break;
            case "3":
                int slaveOneThreeAppCount = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(roomId,"1").size();
                int slaveTwoThreeAppCount = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(roomId,"2").size();
                int appliID = Integer.valueOf(applianceID);
                int countThree = slaveOneThreeAppCount + slaveTwoThreeAppCount + appliID;
                id = String.valueOf(countThree);
                break;
        }

        return id;
    }*/


    public void updateCount(DatabaseReference firebaseRef , final int count)
    {
        try
        {
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
                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                    //This method will be called once with the results of the transaction.
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
