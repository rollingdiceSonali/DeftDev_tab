package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.RecyclerItemClickListenerAndDetector;
import com.rollingdice.deft.android.tab.RoomListActivity;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

public class UserListOfAllRoomsActivity extends AppCompatActivity implements UserApplianceAdapter.AdapterCallback {

    private List<RoomDetails> roomList = new ArrayList<>();
    private RecyclerView rvRoom;
    private RecyclerView.Adapter adapterRoom;
    DatabaseReference localRef;
    RecyclerView.ItemAnimator itemAnimator;
    SimpleArcDialog mDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {


            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_room_list);

            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);

            context = UserListOfAllRoomsActivity.this;

            localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(500);
            itemAnimator.setRemoveDuration(500);


            setUpRoomsUI();
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

    private void setUpRoomsUI()
    {
        try
        {
            if(!isFinishing())
            mDialog.show();

            rvRoom = (RecyclerView) findViewById(R.id.recycler_view_user_all_rooms);
            if(rvRoom!=null) {
                rvRoom.setHasFixedSize(true);
            }

            LinearLayoutManager lmRoom = new GridLayoutManager(this, 2);
            rvRoom.setLayoutManager(lmRoom);

            DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");


            roomDetails.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot snapshot)
                {
                    roomList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren())
                    {
                        if (!roomSnapshot.getRef().toString().contains("globalOff")) {
                            String roomType = roomSnapshot.child("roomType").getValue(String.class);
                            String roomName = roomSnapshot.child("roomName").getValue(String.class);
                            String roomId = roomSnapshot.child("roomId").getValue(String.class);
                            String lastMotionDetected=roomSnapshot.child("lastMotionDetected").getValue(String.class);
                            RoomDetails room = new RoomDetails(roomName, roomType, roomId);
                            roomList.add(room);
                        }

                    }
                    adapterRoom = new UserRoomAdapter(roomList, context, Color.parseColor("#4021272b"));
                    rvRoom.setAdapter(adapterRoom);
                    if(!isFinishing())
                    mDialog.dismiss();


                    final Intent intent = new Intent(UserListOfAllRoomsActivity.this, UserApplianceAndSensorActivity.class);

                    rvRoom.addOnItemTouchListener(new RecyclerItemClickListenerAndDetector(UserListOfAllRoomsActivity.this,
                                    new RecyclerItemClickListenerAndDetector.OnItemClickListener()
                                    {
                                        @Override
                                        public void onItemClick(View view, int position)
                                        {

                                            intent.putExtra(GlobalApplication.SELECTED_ROOM_ID, roomList.get(position).getRoomId());
                                            intent.putExtra("roomType",roomList.get(position).getRoomType());

                                            //overridePendingTransition(R.anim.slide_up_dialog, R.anim.slide_out_down);
                                            startActivity(intent);

                                        }
                                    })
                    );

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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_room_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(UserListOfAllRoomsActivity.this, RoomListActivity.class));
            finish();
            return true;
        }

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
        }

    }
}
