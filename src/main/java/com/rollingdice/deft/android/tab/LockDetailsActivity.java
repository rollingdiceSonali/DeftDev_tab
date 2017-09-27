package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.datahelper.LockDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rolling Dice on 6/10/2016.
 */
public class LockDetailsActivity extends Activity
{
    EditText editTextLocalName;
    Button save,cancel;
    EditText txtLocakId;
    DatabaseReference localRef;
    String lockId,lockName;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_details);
        editTextLocalName = (EditText) findViewById(R.id.et_lock_name);
        txtLocakId = (EditText) findViewById(R.id.edt_lock_id);
        editTextLocalName= (EditText) findViewById(R.id.et_lock_name);
        save= (Button) findViewById(R.id.btn_lock_save);
        cancel= (Button) findViewById(R.id.btn_lock_cancel);
        localRef=GlobalApplication.firebaseRef;

        lockId=autogeneratedLockId();


        txtLocakId.setText(lockId);


        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockName= String.valueOf(editTextLocalName.getText());
                LockDataHelper.addLock(lockId,lockName,false,0);
                DatabaseReference lockRef = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockDetails").child(lockId);
                Map<String, Object> lockDetails = new HashMap<>();
                lockDetails.put("lockName",lockName);
                lockDetails.put("lockId", lockId);
                lockDetails.put("lockState", false);
                lockDetails.put("lockToggle",0);
                lockRef.setValue(lockDetails);

                DatabaseReference lockCountRef = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockCount");
                updateCount(lockCountRef, 1);
                finish();
                startActivity(new Intent(LockDetailsActivity.this, HomeActivity.class));


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
                startActivity(new Intent(LockDetailsActivity.this, HomeActivity.class));

            }
        });
    }

    private String autogeneratedLockId()
    {
        int lockCount= LockDataHelper.getAllLocks().size();

        return String.valueOf(lockCount);
    }


    private void updateCount(DatabaseReference modeCountDetails, final int i)
    {
        try {
            modeCountDetails.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        currentData.setValue((Long) currentData.getValue() + i);
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