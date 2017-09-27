package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.SensorDetail;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 2/3/2016.
 */
public class UserSensorActivity extends AppCompatActivity implements UserSensorAdapter.AdapterCallback {

    private List<SensorDetail> sensorList = new ArrayList<>();

    private RecyclerView rvSensor ;
    private RecyclerView.Adapter adapterSensor;
    DatabaseReference localRef;
    private RelativeLayout rootLayout;
    private String selectedRoomId = "";
    private SimpleArcDialog mDialog;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_sensor);
            localRef = GlobalApplication.firebaseRef;
            context=UserSensorActivity.this;
            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            //rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
            Intent intent = getIntent();
            //selectedRoomId = intent.getStringExtra(GlobalApplication.SELECTED_ROOM_ID);
            setUpSensorUI();
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(mDialog!=null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }

    private void setUpSensorUI()
    {
        try {
            if(!isFinishing())
                mDialog.show();
            rvSensor = (RecyclerView) findViewById(R.id.recycler_view_user_sensor);
            RecyclerView.LayoutManager lmSensor = new GridLayoutManager(this, 4);
            rvSensor.addItemDecoration(new DividerItemDecoration(UserSensorActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
            rvSensor.setLayoutManager(lmSensor);

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot)
                {

                    sensorList = new ArrayList<>();
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
                                                sensorList.add(sensor);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    adapterSensor = new UserSensorAdapter(sensorList,context);
                    rvSensor.setAdapter(adapterSensor);
                    mDialog.dismiss();

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

    @Override
    public void onMethodCallback(boolean flag)
    {
        if(flag)
        {
            if(!isFinishing())
            mDialog.show();
        }
        else
        {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    if(!isFinishing())
                    mDialog.dismiss();
                    // progressDialog.dismiss();
                }
            },2000);  // 3000 milliseconds

        }

    }
}
