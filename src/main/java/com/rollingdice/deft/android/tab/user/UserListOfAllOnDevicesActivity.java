package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;


public class UserListOfAllOnDevicesActivity extends AppCompatActivity implements UserApplianceAdapter.AdapterCallback {
    private List<RoomAppliance> appliances = new ArrayList<>();
    private RecyclerView rvAppliance;
    private RecyclerView.Adapter adapterAppliance;
    Context context;

    DatabaseReference localRef;
    private SimpleArcDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_list_of_all_on_devices);
            localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            context = UserListOfAllOnDevicesActivity.this;

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
            mDialog.show();
            setUpApplianceUI();
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

    private void setUpApplianceUI()
    {
        try {


            rvAppliance = (RecyclerView) findViewById(R.id.recycler_view_user_all_appliance);
            RecyclerView.LayoutManager lmAppliance = new GridLayoutManager(this, 2);
            rvAppliance.addItemDecoration(new DividerItemDecoration(UserListOfAllOnDevicesActivity.this, DividerItemDecoration.VERTICAL_LIST));
            rvAppliance.setLayoutManager(lmAppliance);


            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");


            roomDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    appliances = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        //Getting each room
                        for (DataSnapshot type : roomSnapshot.getChildren()) {

                            for(DataSnapshot slave : type.getChildren()) {

                                if (slave.getRef().toString().contains("appliance") && !(slave.getRef().toString().contains("sensors"))) {
                                    for (DataSnapshot applianc : slave.getChildren()) {

                                        RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                        if (roomAppliance.isState()) {
                                            if (!(roomAppliance.getApplianceName().equals("NA"))) {
                                                appliances.add(roomAppliance);
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    adapterAppliance = new UserApplianceAdapter(appliances,context, Color.parseColor("#4021272b"), "UserListOfAllOnDevicesActivity");

                    rvAppliance.setAdapter(adapterAppliance);
                    rvAppliance.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                        {
                            if (newState == 0) {
                                GlobalApplication.isOnDevicesClickable = true;
                            }

                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                        {
                            int state = recyclerView.getScrollState();
                            if (state == 0) {
                                GlobalApplication.isOnDevicesClickable = true;
                            } else {

                                GlobalApplication.isOnDevicesClickable = false;
                            }

                        }
                    });
                    if(!isFinishing())
                    mDialog.dismiss();


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

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list_of_all_on_devices, menu);
        Menu actionMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

*/
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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    if(!isFinishing())
                    mDialog.dismiss();

                }
            },1000);
        }

    }
}
