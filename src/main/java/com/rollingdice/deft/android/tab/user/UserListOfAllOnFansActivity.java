package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class UserListOfAllOnFansActivity extends AppCompatActivity implements UserApplianceAdapter.AdapterCallback  {

    private List<RoomAppliance> appliances = new ArrayList<>();
    private RecyclerView rvFans;
    private RecyclerView.Adapter adapterFan;
    DatabaseReference localRef;
    Context context;
    private TextView msgTV;

    private SimpleArcDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_list_of_all_on_fans);
            localRef = GlobalApplication.firebaseRef;
            localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            context=UserListOfAllOnFansActivity.this;

            msgTV = (TextView)findViewById(R.id.msg_tv);

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
            mDialog.show();

            setUpFansUI();
            
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

    private void setUpFansUI()
    {
        try {

            rvFans = (RecyclerView) findViewById(R.id.recycler_view_user_all_fans);
            if(rvFans!=null) {
                rvFans.setHasFixedSize(true);
            }
            RecyclerView.LayoutManager lmFan = new GridLayoutManager(this, 2);
            rvFans.addItemDecoration(new DividerItemDecoration(UserListOfAllOnFansActivity.this, DividerItemDecoration.VERTICAL_LIST));
            rvFans.setLayoutManager(lmFan);

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    appliances = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        //Getting each room
                        for (DataSnapshot type : roomSnapshot.getChildren()) {

                            for(DataSnapshot slave : type.getChildren()) {

                                if (slave.getRef().toString().indexOf("appliance") != 0 && !(slave.getRef().toString().contains("sensors"))) {
                                    for (DataSnapshot applianc : slave.getChildren()) {

                                        RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                        if ("Fan".equals(roomAppliance.getApplianceType())) {
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

                    if(appliances.size() > 0){

                        adapterFan = new UserApplianceAdapter(appliances,context, Color.parseColor("#4021272b")," UserListOfAllOnFansActivity");
                        rvFans.setAdapter(adapterFan);
                        msgTV.setVisibility(View.GONE);
                    }else {
                        msgTV.setVisibility(View.VISIBLE);
                    }

                    rvFans.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                        {
                            if (newState == 0) {
                                GlobalApplication.isOnFansClickable = true;
                            }

                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                        {
                            int state = recyclerView.getScrollState();
                            if (state == 0) {
                                GlobalApplication.isOnFansClickable = true;
                            } else {

                                GlobalApplication.isOnFansClickable = false;
                            }

                        }
                    });

                    if(!isFinishing())
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

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list_of_all_on_fans, menu);
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

            android.os.Handler handler = new android.os.Handler();
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
