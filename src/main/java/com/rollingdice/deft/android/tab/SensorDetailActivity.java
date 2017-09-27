package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.datahelper.SensorDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.State;


import java.util.HashMap;
import java.util.Map;


public class SensorDetailActivity extends Activity
{
    EditText etSensorName;
    EditText etSensorId;
    CheckBox cbSensorState;
    String updateRoomId;
    String sensorId;
    String sensorName;
    String slaveId;
    int sensorTypeId;
    boolean sensorUpdateMode;
    String customerId;
    private Context context;
    private Spinner spinnerSensorType;
    private String[] sensorTypeArray = GlobalApplication.SENSOR_TYPE;

    DatabaseReference localRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_details);
        context = this;

        try {
            localRef = GlobalApplication.firebaseRef;
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                updateRoomId = extras.getString("ROOM_ID");
                sensorId = extras.getString("SENSOR_ID");
                sensorName = extras.getString("SENSOR_NAME");
                slaveId = extras.getString("SLAVE_ID");
                customerId = extras.getString("CUSTOMER_ID");
                sensorUpdateMode = extras.getBoolean("SENSOR_UPDATE_MODE");
                sensorTypeId = extras.getInt("SENSOR_TYPE_ID");
            }

            spinnerSensorType = (Spinner) findViewById(R.id.spinner_sensor_type);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sensorTypeArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSensorType.setAdapter(spinnerArrayAdapter);

            etSensorName = (EditText) findViewById(R.id.et_sensor_name);
            etSensorId = (EditText) findViewById(R.id.et_sensor_id);

            etSensorId.setText(sensorId);
            etSensorName.setText(sensorName);
            spinnerSensorType.setSelection(sensorTypeId);




            Button btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Button btnSaveSensor = (Button) findViewById(R.id.btn_save);
            btnSaveSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    if(!SensorDataHelper.isSensorAvailable(updateRoomId,((int) spinnerSensorType.getSelectedItemId()+1),sensorId)){

                        if(etSensorName.getText().toString() != null && !etSensorName.getText().toString().equals("")){

                            SensorDataHelper.addSensor(updateRoomId, slaveId, etSensorName.getText().toString(),
                                    State.STATE_OFF, etSensorId.getText().toString(),((int) spinnerSensorType.getSelectedItemId()+1));

                            GlobalApplication.SENSOR_ADD_MODE = true;
                            GlobalApplication.SENSOR_ID = etSensorId.getText().toString();
                            GlobalApplication.SENSOR_TYPE_ID = ((int) spinnerSensorType.getSelectedItemId()+1);
                            GlobalApplication.ROOM_ID = updateRoomId;
                            finish();


                            //Save or Update room sensor details

                            sensorTypeId = (int) spinnerSensorType.getSelectedItemId();
                            DatabaseReference roomSensorRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails")
                                    .child(updateRoomId).child("sensors").child(slaveId).child(getIdForSensorType(sensorTypeId));

                            Map<String, Object> sensorDetails = new HashMap<>();

                            sensorDetails.put("sensorName", etSensorName.getText().toString());
                            sensorDetails.put("state", false);
                            sensorDetails.put("toggle", 0);
                            sensorDetails.put("slaveId", slaveId);
                            sensorDetails.put("roomId", updateRoomId);
                            sensorDetails.put("id", etSensorId.getText().toString());
                            sensorDetails.put("sensorTypeId",((int) spinnerSensorType.getSelectedItemId()+1));
                            roomSensorRef.updateChildren(sensorDetails);

                            DatabaseReference  occuRef=localRef.child("occupancy").child(Customer.getCustomer().customerId).child(updateRoomId);
                            occuRef.setValue("NA");

                            if (!sensorUpdateMode)
                            {
                                //Slave Controller Saved
                                DatabaseReference roomSlaveController = localRef.child("rooms").child(Customer.getCustomer().customerId).
                                        child("roomController")
                                        .child(updateRoomId);

                                Map<String, Object> roomSlvCntrlr = new HashMap<>();
                                roomSlvCntrlr.put(slaveId, true);

                                roomSlaveController.updateChildren(roomSlvCntrlr);

                                //Slave Controller Count update
                                DatabaseReference roomSlaveControllerCnt = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomControllerCnt")
                                        .child(updateRoomId).child(slaveId);

                                updateCount(roomSlaveControllerCnt, 1);

                                //Sensor Count

                                DatabaseReference roomApplianceCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                        .child("roomdetailsCount")
                                        .child("sensorCount");

                                updateCount(roomApplianceCountRef, 1);
                            }

                        }else {
                            getDialog("Please Enter Sensor Name  !!!").show();
                        }

                    }else {

                        getDialog("Sensor Id  "+etSensorId.getText().toString()+" Already Available "+getSensorTypeId(((int) spinnerSensorType.getSelectedItemId()+1))+" Please Check !! ").show();

                    }

                }
            });
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


    public String getIdForSensorType(int sensorId){
        String Id;
        return ("0" + String.valueOf((sensorId)));

    }


    public String getSensorTypeId(int sensorType)
    {
        String sensorTypeId = "";

        if(sensorType == 1){
            sensorTypeId =  "Temperature";
        }else if(sensorType == 2){
            sensorTypeId =  "Light";
        }else if(sensorType == 3){
            sensorTypeId =  "Motion";
        }else if(sensorType == 4){
            sensorTypeId =  "Occupancy";
        }

        return sensorTypeId;
    }

    public Dialog getDialog(String msg){
        return new AlertDialog.Builder(context)
//Please Check your password and try again
                .setMessage(msg)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public void updateCount(DatabaseReference firebaseRef , final int count)
    {
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
                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                    //This method will be called once with the results of the transaction.
                }
            });
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
