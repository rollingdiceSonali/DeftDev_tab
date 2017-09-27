package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.datahelper.CurtainDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rolling Dice on 12/30/2015.
 */
public class CurtainDetailActivity extends Activity
{
    EditText etCurtainName;
    EditText etCurtainId;
    String updateRoomId;
    String roomName;
    String curtainId;
    String curtainName;
    boolean curtainUpdateMode;
    String customerId;
    DatabaseReference localRef;
    String slaveId;
    Spinner etCurtainType;
    public String[] CURTAIN_TYPE= {"Rolling", "AC", "DC"};

    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.curtain_details);

        localRef = GlobalApplication.firebaseRef;

        try {

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                updateRoomId = extras.getString("ROOM_ID");
                slaveId = extras.getString("SLAVE_ID");
                roomName = extras.getString("ROOM_NAME");
                curtainId = extras.getString("CURTAIN_ID");
                curtainName = extras.getString("CURTAIN_NAME");
                customerId = extras.getString("CUSTOMER_ID");
                curtainUpdateMode = extras.getBoolean("CURTAIN_UPDATE_MODE");

            }

            etCurtainName = (EditText) findViewById(R.id.et_curtain_name);
            etCurtainId = (EditText) findViewById(R.id.et_curtain_id);

            etCurtainType = (Spinner) findViewById(R.id.et_curtain_type);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CURTAIN_TYPE);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            etCurtainType.setAdapter(spinnerArrayAdapter);

            etCurtainId.setText(curtainId);
            etCurtainName.setText(curtainName);

            Button btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Button btnSaveCurtain = (Button) findViewById(R.id.btn_save);
            btnSaveCurtain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurtainDataHelper.addCurtain(etCurtainId.getText().toString(), etCurtainName.getText().toString(), updateRoomId, roomName, customerId, 0);
                    GlobalApplication.CURTAIN_ADD_MODE = true;
                    GlobalApplication.CURTAIN_ID = etCurtainId.getText().toString();
                    finish();

                    DatabaseReference curtainRef = localRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails")
                            .child(updateRoomId).child("curtainDetails").child(etCurtainId.getText().toString());

                    Map<String, Object> curtainDetails = new HashMap<>();
                    curtainDetails.put("curtainName", etCurtainName.getText().toString());
                    curtainDetails.put("curtainId", etCurtainId.getText().toString());
                    curtainDetails.put("curtainLevel", 0);
                    curtainDetails.put("curtainType", etCurtainType.getSelectedItem().toString());
                    curtainDetails.put("curtainRoomName", roomName);
                    curtainDetails.put("roomId", updateRoomId);
                    curtainDetails.put("slaveId", slaveId);
                    curtainDetails.put("toggle",0);
                    curtainRef.updateChildren(curtainDetails);

                    if (!curtainUpdateMode) {
                        DatabaseReference curtainCountDetails = localRef.child("curtain").child(Customer.getCustomer().customerId).child("curtainCountDetails").child("curtainCount");
                        updateCount(curtainCountDetails, 1);
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

    private void updateCount(DatabaseReference curtainCountDetails, final  int count)
    {
        try {
            curtainCountDetails.runTransaction(new Transaction.Handler() {
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
