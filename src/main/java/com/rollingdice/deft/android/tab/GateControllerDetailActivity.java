package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.datahelper.GateControllerDataHelper;
import com.rollingdice.deft.android.tab.datahelper.WaterSprinklerDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rolling Dice on 3/24/2016.
 */
public class GateControllerDetailActivity extends Activity
{
    EditText etGateControllerName;
    EditText etGateControllerId;

    String sprinklerName;
    String sprinklerId;
    String slaveId;
    String roomId;
    String roomName;
    Integer toogle;
    boolean state;
    boolean gateControllerUpdateMode;

    /*String updateRoomId;
    String gateControllerId;
    String gateControllerName;
    boolean gateControllerUpdateMode;
    String customerId;
*/
    DatabaseReference localRef;

    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gate_controller_details);

        localRef = GlobalApplication.firebaseRef;

        try {

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                sprinklerName = extras.getString("WaterSprinklerName");
                sprinklerId = extras.getString("WaterSprinklerId");
                roomId = extras.getString("roomID");
                roomName = extras.getString("roomName");
                slaveId = extras.getString("slaveId");
                gateControllerUpdateMode = extras.getBoolean("SPRINKLER_UPDATE_MODE");

            }

            etGateControllerName = (EditText) findViewById(R.id.et_spinkler_name);
            etGateControllerId = (EditText) findViewById(R.id.et_spinkler_id);

            etGateControllerId.setText(sprinklerId);


            Button btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Button btnSaveGateController = (Button) findViewById(R.id.btn_save);
            btnSaveGateController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!etGateControllerName.getText().toString().equals("")){

                        WaterSprinklerDataHelper.addWaterSprinkler(sprinklerId, etGateControllerName.getText().toString(),
                                roomId, roomName,slaveId);

                        GlobalApplication.GATE_CONTROLLER_ADD_MODE = true;
                        GlobalApplication.GATE_CONTROLLER_ID = etGateControllerId.getText().toString();
                        finish();

                        DatabaseReference gateControllerRef = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId)
                                .child("waterSprinklerDetails").child(etGateControllerId.getText().toString());

                        Map<String, Object> gateControllerDetails = new HashMap<>();
                        gateControllerDetails.put("waterSprinklerName", etGateControllerName.getText().toString());
                        gateControllerDetails.put("waterSprinklerId", etGateControllerId.getText().toString());
                        gateControllerDetails.put("roomId", roomId);
                        gateControllerDetails.put("roomName",roomName);
                        gateControllerDetails.put("slaveId",slaveId);
                        gateControllerDetails.put("toggle",0);
                        gateControllerDetails.put("state",false);
                        gateControllerRef.updateChildren(gateControllerDetails);

                        if (!gateControllerUpdateMode) {
                            DatabaseReference gateControllerCountDetails = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerCountDetails").child("waterSprinklerCount");
                            updateCount(gateControllerCountDetails, 1);
                        }

                    }else {
                        etGateControllerName.setError("Please Enter Name");
                    }


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

    private void updateCount(DatabaseReference gateControllerCountDetails, final  int count)
    {
        try {
            gateControllerCountDetails.runTransaction(new Transaction.Handler() {
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

