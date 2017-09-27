package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.datahelper.ShortcutDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

public class UserApplianceAndSensorActivity extends AppCompatActivity implements UserApplianceAdapter.AdapterCallback {
    private List<RoomAppliance> appliances = new ArrayList<>();
    //private List<SensorDetail> sensorList = new ArrayList<>();

    private RecyclerView rvAppliance;// rvSensor ;
    private RecyclerView.Adapter adapterAppliance;//adapterSensor,
    private RecyclerView.LayoutManager lmSensor;
    private String selectedRoomId = "";
    DatabaseReference localRef;
    private Context context;


    private SimpleArcDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);

            context = UserApplianceAndSensorActivity.this;
            setContentView(R.layout.activity_user_appliance_and_sensor);
            localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            Intent intent = getIntent();
            selectedRoomId = intent.getStringExtra(GlobalApplication.SELECTED_ROOM_ID);
            String selectedRoomType = intent.getStringExtra("roomType");
            if (selectedRoomType != null) {
                if (rootLayout != null) {
                    rootLayout.setBackgroundResource(getImageResourceId(selectedRoomType));
                }
            }

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration = new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing()) {
                mDialog.show();
            }

            setUpApplianceUI();
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private int getImageResourceId(String selectedRoomType) {
        if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[0]))
            return R.drawable.background_bedroom;
        else if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[1]))
            return R.drawable.background_childroom;
        else if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[2]))
            return R.drawable.background_bathrrom;
        else if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[3]))
            return R.drawable.background_living_room;
        else if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[4]))
            return R.drawable.background_terrace;
        else if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[5]))
            return R.drawable.background_kitchen;
        else if (selectedRoomType.equals(GlobalApplication.ROOM_TYPE[6]))
            return R.drawable.background_dining;
        else
            return R.drawable.back;
    }

    private void setUpApplianceUI() {
        try {
            rvAppliance = (RecyclerView) findViewById(R.id.recycler_view_user_appliance);
            RecyclerView.LayoutManager lmAppliance = new GridLayoutManager(this, 2);
            rvAppliance.addItemDecoration(new DividerItemDecoration(UserApplianceAndSensorActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
            rvAppliance.setLayoutManager(lmAppliance);

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    appliances = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        //Getting each room
                        String roomId = roomSnapshot.child("roomId").getValue(String.class);
                        if (roomId != null && roomId.equals(selectedRoomId)) {
                            for (DataSnapshot type : roomSnapshot.getChildren())
                            {

                                for(DataSnapshot slave : type.getChildren()) {

                                    if (slave.getRef().toString().contains("appliance")) {
                                        for (DataSnapshot applianc : slave.getChildren()) {
                                            RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                            if (!(roomAppliance.getApplianceName().equals("NA"))) {
                                                appliances.add(roomAppliance);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //}
                    adapterAppliance = new UserApplianceAdapter(appliances, context, Color.parseColor("#4021272b"),"UserApplianceAndSensorActivity");
                    rvAppliance.setAdapter(adapterAppliance);
                    rvAppliance.setNestedScrollingEnabled(false);
                    rvAppliance.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            if (newState == 0) {
                                GlobalApplication.isApplianceClickable = true;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                        {
                            int state = recyclerView.getScrollState();
                            if (state == 0) {
                                GlobalApplication.isApplianceClickable = true;
                            } else {

                                GlobalApplication.isApplianceClickable = false;
                            }
                        }
                    });
                    try {
                        if (mDialog != null && !isFinishing()) {
                            mDialog.dismiss();
                        }

                    } catch (Exception  e) {
                        DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                                .child(String.valueOf(System.currentTimeMillis()));
                        String currentStatus = e.getMessage();
                        errorRef.setValue(currentStatus);

                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    System.out.println("The read failed: " + DatabaseError.getMessage());
                }
            });
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_appliance_and_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Create Shortcut") {

            final RoomAppliance appliance = appliances.get(item.getGroupId());

            ShortcutDataHelper.addShortcut(String.valueOf(item.getOrder()), appliance.getRoomId(), appliance.getSlaveId(), appliance.getId(), appliance.isState(), appliance.getApplianceName(), appliance.getApplianceType(), appliance.getApplianceTypeId(), appliance.isDimmable(), appliance.getDimableValue());

            //GlobalApplication.shortcutApplianceList= ShortcutDataHelper.getAllShortcuts();
            Toast.makeText(this, "Create ShortCut" + item, Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this,ShortcutActivity.class));
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onMethodCallback(boolean flag)
    {
        if (flag)
        {
            if(!isFinishing()) {

                // progressDialog.show();
                mDialog.show();
            }
            //progressDialog = ProgressDialog.show(this, "", "Processing...");
        } else {

            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    if(!isFinishing()) {
                        mDialog.dismiss();
                    }
                    // progressDialog.dismiss();
                }
            }, 1000);  // 3000 milliseconds

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
}
