package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.RecyclerItemClickListenerAndDetector;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

public class UserListOfAllOnLightsActivity extends AppCompatActivity implements UserApplianceAdapter.AdapterCallback
{

    private List<RoomAppliance> appliances = new ArrayList<>();
    private RecyclerView rvLight;
    private RecyclerView.Adapter adapterLight;
    DatabaseReference localRef;
    Context context;
    private SimpleArcDialog mDialog;
    private int xOffset;
    private int yOffset;
    private TextView msgTV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_of_all_on_lights);
        localRef = GlobalApplication.firebaseRef;
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        context=UserListOfAllOnLightsActivity.this;
        msgTV = (TextView) findViewById(R.id.msg_tv);

        mDialog = new SimpleArcDialog(this);
        ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
        mDialog.setConfiguration(configuration);
        mDialog.setCancelable(false);
        if(!isFinishing())
        mDialog.show();
        setUpLightsUI();

            /*rvLight.addOnItemTouchListener(new RecyclerItemClickListenerAndDetector(context, new RecyclerItemClickListenerAndDetector.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(UserListOfAllOnLightsActivity.this, "Click Position : "+ position + "**"
                            + view.getScrollX() + " && " + view.getScrollY(), Toast.LENGTH_SHORT).show();
                }
            }));*/





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


    private void setUpLightsUI()
    {
        try
        {

            rvLight = (RecyclerView) findViewById(R.id.recycler_view_user_all_lights);
            /*if(rvLight!=null) {
                rvLight.setHasFixedSize(true);
            }*/
            RecyclerView.LayoutManager lmLight = new GridLayoutManager(this, 2);
            rvLight.addItemDecoration(new DividerItemDecoration(UserListOfAllOnLightsActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
            rvLight.setLayoutManager(lmLight);
            rvLight.setNestedScrollingEnabled(false);

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    appliances = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {/// room
                        //Getting each room
                        for (DataSnapshot type : roomSnapshot.getChildren()) {/// slave

                            for(DataSnapshot slave : type.getChildren()) { //  appliance

                                if (slave.getRef().toString().indexOf("appliance") != 0) {
                                    for (DataSnapshot applianc : slave.getChildren()) {///   id list

                                        RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                        if ("Light".equals(roomAppliance.getApplianceType())) {
                                            if (!(roomAppliance.getApplianceName().equals("NA"))) {
                                                appliances.add(roomAppliance);
                                            }
                                        }
                                    }
                                    if (!isFinishing())
                                        mDialog.dismiss();
                                    break;

                                }
                            }
                        }
                    }

                    if(appliances.size() > 0){
                        adapterLight = new UserApplianceAdapter(appliances,context, Color.parseColor("#4021272b"), "UserListOfAllOnLightsActivity");
                        rvLight.setAdapter(adapterLight);
                        msgTV.setVisibility(View.GONE);

                    }else {
                        msgTV.setVisibility(View.VISIBLE);

                    }

                    rvLight.setNestedScrollingEnabled(false);
                    rvLight.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            if (newState == 0) {
                                GlobalApplication.isOnLightsClickable = true;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            int state = recyclerView.getScrollState();
                            if (state == 0)
                            {
                                GlobalApplication.isOnLightsClickable  = true;

                            } else {

                                GlobalApplication.isOnLightsClickable  = false;

                            }
                        }
                    });

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
        getMenuInflater().inflate(R.menu.menu_user_list_of_all_on_lights, menu);
        Menu actionMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/

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

            rvLight.getLayoutManager().scrollToPosition(GlobalApplication.clickPosition);
        }


    }

}
