package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
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
import com.rollingdice.deft.android.tab.model.GateController;
import com.rollingdice.deft.android.tab.model.GateControllerDetails;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.rollingdice.deft.android.tab.model.WaterSprinklerDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 3/23/2016.
 */
public class UserListOfAllGateControllers extends Activity implements UserGateControllerAdapter.SpinklerAdapterCallback
{
    private ArrayList<WaterSprinklerDetails> gateControllerDetailsArrayList;
    private RecyclerView rvgateController;
    private RecyclerView.Adapter adptergateController;
    DatabaseReference localRef;
    private Menu actionMenu;
    static Context context;
    private SimpleArcDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_user_list_of_all_gate_controller);
            context=UserListOfAllGateControllers.this;
            localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
            mDialog.show();
            setUpgateControllerUI();

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

    private void setUpgateControllerUI()
    {
        try {

            rvgateController = (RecyclerView) findViewById(R.id.recycler_view_user_all_GATE_CONTROLLER);
            RecyclerView.LayoutManager lmwater = new GridLayoutManager(this, 2);
            rvgateController.addItemDecoration(new DividerItemDecoration(UserListOfAllGateControllers.this, DividerItemDecoration.HORIZONTAL_LIST));
            rvgateController.setLayoutManager(lmwater);

            final DatabaseReference waterSpinklerDetails = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerDetails");

            waterSpinklerDetails.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    gateControllerDetailsArrayList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : dataSnapshot.getChildren())
                    {
                        gateControllerDetailsArrayList.add(roomSnapshot.getValue(WaterSprinklerDetails.class));
                    }
                    adptergateController = new UserGateControllerAdapter(context,gateControllerDetailsArrayList);
                    rvgateController.setAdapter(adptergateController);
                    rvgateController.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                        {
                            if (newState == 0) {
                                GlobalApplication.isgateControllerClickable = true;
                            }

                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                        {
                            int state = recyclerView.getScrollState();
                            if (state == 0)
                            {
                                GlobalApplication.isgateControllerClickable = true;

                            } else {

                                GlobalApplication.isgateControllerClickable  = false;

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

    @Override
    public void onMethodCallback(boolean flag) {
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

            rvgateController.getLayoutManager().scrollToPosition(GlobalApplication.clickPosition);
        }

    }
}
