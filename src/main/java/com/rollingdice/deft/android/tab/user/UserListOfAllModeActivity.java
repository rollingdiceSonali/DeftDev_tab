package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.datahelper.ModeDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.modeDetails;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 1/28/2016.
 */
public class UserListOfAllModeActivity extends Activity implements UserModeAdapter.AdapterCallback
{
    private List<modeDetails> modeDetailsList;
    private RecyclerView rvModes;
    private RecyclerView.Adapter adapterMode;
    DatabaseReference localRef;
    private ArrayList<RoomAppliance> appliances;
    private Context context;

    SimpleArcDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            localRef = GlobalApplication.firebaseRef;
            getAllLights();
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_list_of_all_mode);
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            context=UserListOfAllModeActivity.this;
            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
            mDialog.show();
            setUpModeUI();

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

    private void setUpModeUI()
    {
        try {

            rvModes = (RecyclerView) findViewById(R.id.recycler_view_user_all_moods);
            RecyclerView.LayoutManager lmMode = new GridLayoutManager(this, 4);
            rvModes.addItemDecoration(new DividerItemDecoration(UserListOfAllModeActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
            rvModes.setLayoutManager(lmMode);

            DatabaseReference modeRef = localRef.child("mode").child(Customer.getCustomer().customerId).child("modeDetails");
            modeRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot)

                {
                    modeDetailsList = new ArrayList<>();
                    for (DataSnapshot modeSnapsot : dataSnapshot.getChildren()) {
                        modeDetails tempModeDetails = modeSnapsot.getValue(modeDetails.class);
                        modeDetailsList.add(tempModeDetails);
                    }
                    adapterMode = new UserModeAdapter(context, appliances, modeDetailsList, Color.parseColor("#4021272b"));
                    rvModes.setAdapter(adapterMode);
                    rvModes.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                        {
                            if (newState == 0) {
                                GlobalApplication.isMoodClickable= true;
                            }

                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                        {
                            int state = recyclerView.getScrollState();
                            if (state == 0)
                            {
                                GlobalApplication.isMoodClickable = true;

                            } else {

                                GlobalApplication.isMoodClickable  = false;

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

    private void getAllLights() {
        try {
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
                                        if(roomAppliance != null && roomAppliance.getId() != null) {
                                            appliances.add(roomAppliance);
                                        }
                                    }

                                    ///  1 sep break comment
                                    //break;
                                }
                            }
                        }
                    }
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
    public boolean onContextItemSelected(MenuItem item)
    {
        try {
            if (item.getTitle() == "Delete Mode")
            {

                final modeDetails details = modeDetailsList.get(item.getGroupId());
                ModeDataHelper.deleteMode(details.getMoodName());
                DatabaseReference modeRef = localRef.child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(details.getModeId());
                modeRef.setValue(null);
                DatabaseReference modeCountDetails = localRef.child("mode").child(Customer.getCustomer().customerId).child("modeCountDetails").child("modeCount");
                updateCount(modeCountDetails, -1);
                if(modeDetailsList.size()!=0) {
                    rvModes.setAdapter(adapterMode);
                    Toast.makeText(this, "Delete Mode" + item, Toast.LENGTH_SHORT).show();
                }else
                {
                    finish();
                }

            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
        return super.onContextItemSelected(item);
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
