package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
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
import com.rollingdice.deft.android.tab.model.Shortcut;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 12/19/2015.
 */
public class ShortcutActivity extends Activity implements UserShortcutAdapter.AdapterCallback
{

    private LinearLayoutManager lmAppliance;
    private RecyclerView rvShortcut;
    RoomAppliance room;
    Context context;
    LinearLayoutManager lmShortcut;
    List<Shortcut> shortcutList;
    List<RoomAppliance> roomAppliances;
    private DatabaseReference localRef;
    SimpleArcDialog mDialog;
    List<RoomAppliance> appliances;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.user_shortcut_activity);
            localRef= GlobalApplication.firebaseRef;
            context=ShortcutActivity.this;

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
            mDialog.show();

            rvShortcut = (RecyclerView) findViewById(R.id.recycler_view_user_shortcut);
            lmShortcut = new GridLayoutManager(this, 3);
            rvShortcut.addItemDecoration(new DividerItemDecoration(ShortcutActivity.this, DividerItemDecoration.VERTICAL_LIST));
            rvShortcut.setLayoutManager(lmShortcut);
            rvShortcut.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                    if (newState == 0) {
                        GlobalApplication.isShortcutClickable = true;
                    }

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    int state = recyclerView.getScrollState();
                    if (state == 0)
                    {
                        GlobalApplication.isShortcutClickable = true;

                    } else {

                        GlobalApplication.isShortcutClickable  = false;

                    }

                }
            });
            GetFirebaseAppliance();
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

    private void GetFirebaseAppliance()
    {
        DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
        roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                appliances = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    //Getting each room
                    for (DataSnapshot type : roomSnapshot.getChildren()) {

                        for(DataSnapshot slave : type.getChildren()) {

                            if (slave.getRef().toString().contains("appliance")) {
                                for (DataSnapshot applianc : slave.getChildren()) {

                                    RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                    appliances.add(roomAppliance);
                                }
                                break;
                            }
                        }
                    }
                }
                SetAdapter();
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                System.out.println("The read failed: " + DatabaseError.getMessage());
            }
        });
    }

    private void SetAdapter()
    {

        GlobalApplication.shortcutApplianceList = ShortcutDataHelper.getAllShortcuts();
        shortcutList = GlobalApplication.shortcutApplianceList;

        roomAppliances = new ArrayList<>();
        for (int i = 0; i < shortcutList.size(); i++)
        {
            Shortcut shortcut = shortcutList.get(i);
            for (RoomAppliance roomAppliance : appliances)
            {
                if(roomAppliance.getId().equals(shortcut.applianceId)
                        && roomAppliance.getRoomId().equals(shortcut.roomId))
                {
                    roomAppliances.add(roomAppliance);
                }
            }
        }
        if(roomAppliances.size()!=0) {

            RecyclerView.Adapter adapterShortcut = new UserShortcutAdapter(roomAppliances, context, Color.parseColor("#4021272b"));
            rvShortcut.setAdapter(adapterShortcut);
        }else
        {
            Toast.makeText(ShortcutActivity.this, "No Shortcut Created.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        try {
            if (item.getTitle() == "Delete Shortcut") {

                final RoomAppliance appliance = roomAppliances.get(item.getGroupId());

                ShortcutDataHelper.deleteShortcut(appliance.getApplianceName());
                SetAdapter();
                Toast.makeText(this, "DeleteShortCut" + item, Toast.LENGTH_SHORT).show();

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


    @Override
    public void onMethodCallback(boolean flag)
    {
        if(flag)
        {
            mDialog.show();
        }
        else
        {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    mDialog.dismiss();
                    // progressDialog.dismiss();
                }
            },2000);  // 3000 milliseconds

        }

    }
}
