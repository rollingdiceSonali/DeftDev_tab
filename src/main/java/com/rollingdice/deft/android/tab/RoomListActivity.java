package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.RoomDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Room;

import java.util.ArrayList;


public class RoomListActivity extends Activity {

    private ArrayList<Room> myDataset = new ArrayList<>();
    private int i = 1;
    private DatabaseReference localRef;
    AlertDialog ad;
    private Context context;
    String adminId,password;

    private Button slaveConfigBtn,masterConfigBtn,sensorConfigBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        localRef=GlobalApplication.firebaseRef;
        try
        {
            context = this;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            DatabaseReference adminRef=localRef.child("Admin");
            adminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    adminId=dataSnapshot.child("adminId").getValue(String.class);
                    password=dataSnapshot.child("password").getValue(String.class);



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            showAuthenticationDialog();
            setContentView(R.layout.activity_room_list);

            sensorConfigBtn = (Button) findViewById(R.id.btn_config_sensor);
            sensorConfigBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(context,ConfigrationActivity.class).setAction("Sensor Config"));

                }
            });

            masterConfigBtn = (Button)findViewById(R.id.btn_config_master);
            masterConfigBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,ConfigrationActivity.class).setAction("Master Config"));
                }
            });

            slaveConfigBtn = (Button)findViewById(R.id.btn_configure_slave);
            slaveConfigBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,ConfigrationActivity.class).setAction("Slave Config"));
                    //finish();
                }
            });

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_room);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            for (Room room : RoomDataHelper.getAllRooms()) {
                myDataset.add(room);
            }

            RecyclerView.Adapter mAdapter = new RoomAdapter(myDataset);
            mRecyclerView.setAdapter(mAdapter);



            findViewById(R.id.iv_add_room).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("RoomListActivity", "add_room_clicked" + i);

                    startActivity(new Intent(RoomListActivity.this, RoomDetailsActivity.class));
                    finish();
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
    public void onDestroy(){
        super.onDestroy();
        if ( ad!=null && ad.isShowing() )
        {
            ad.dismiss();
        }
    }

    private void showAuthenticationDialog()
    {
        try
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view=inflater.inflate(R.layout.admin_auth_dialog, null);
            final EditText editTextUserName= (EditText) view.findViewById(R.id.username);
            final EditText editTextpassword=(EditText) view.findViewById(R.id.password);

            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Signin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            if( password!=null )
                            {
                                if ( adminId!=null && password!=null && adminId.equals(editTextUserName.getText().toString())&&password.equals(editTextpassword.getText().toString())) {
                                    Toast.makeText(RoomListActivity.this, "Signing Successful :)", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RoomListActivity.this, "Admin Registration is Invalid", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RoomListActivity.this, HomeActivity.class));
                                    finish();
                                }
                            }


                            // sign in the user ...


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(RoomListActivity.this, HomeActivity.class));
                            finish();
                            Toast.makeText(RoomListActivity.this, "Authentication Dialog", Toast.LENGTH_SHORT).show();
                        }
                    });

            builder.setCancelable(false);
            builder.create();
            ad=builder.show();
            // builder.show();
            Toast.makeText(RoomListActivity.this, "Authentication Dialog", Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomeActivity.class));
    }
}
