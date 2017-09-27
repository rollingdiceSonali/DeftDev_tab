package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.RecyclerItemClickListenerAndDetector;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RemoteDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deft on 17/05/2017.
 */
public class SelectRemoteActivity extends Activity {

    SimpleArcDialog waitDialog;

    private List<RemoteDetails> remoteDetailsArrayList;
    private RecyclerView rvRemoteModel;
    private RecyclerView.Adapter userRemoteModelAdapter;
    private DatabaseReference localRef = null;
    private Context context;
    private RecyclerView.ItemAnimator itemAnimator;
    TextView text;




    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.selectmodel);
            context = this.getApplicationContext();
                        localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            context=SelectRemoteActivity.this;
            waitDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            waitDialog.setConfiguration(configuration);
            waitDialog.setCancelable(false);
            text = (TextView) findViewById(R.id.warningText);
            if(!isFinishing())
                waitDialog.show();
            setUpRemoteModelUI();

        }catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer() != null
                    && Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }

        }
    }

    private void setUpRemoteModelUI() {
        rvRemoteModel = (RecyclerView) findViewById(R.id.recycler_view_select_model);
        RecyclerView.LayoutManager lmRoom = new LinearLayoutManager(context);
        rvRemoteModel.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));
        rvRemoteModel.setLayoutManager(lmRoom);


        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                && Customer.getCustomer().customerId != null)
        {

            final DatabaseReference remoteDetailsRef = localRef.child("remote").child(Customer.getCustomer().customerId);

            remoteDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    remoteDetailsArrayList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getRef().toString().contains("command")) {
                            String id = snapshot.child("Ids").getValue(String.class);
                            String roomId = snapshot.child("roomId").getValue(String.class);
                            String roomName = snapshot.child("roomName").getValue(String.class);
                            String IRId = snapshot.child("IRId").getValue(String.class);
                            String remoteId = snapshot.child("remoteId").getValue(String.class);
                            String brand = snapshot.child("brand").getValue(String.class);
                            String remoteType = snapshot.child("remoteType").getValue(String.class);
                            RemoteDetails remoteDetails = new RemoteDetails(id,roomId, roomName, IRId,remoteType, remoteId, brand);
                            remoteDetailsArrayList.add(remoteDetails);
                        }
                    }
                    if (remoteDetailsArrayList.size() != 0)
                    {
                        text.setVisibility(View.INVISIBLE);
                        if(waitDialog!=null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                        userRemoteModelAdapter = new UserRemoteModelAdapter(remoteDetailsArrayList,context);
                        rvRemoteModel.setAdapter(userRemoteModelAdapter);
                        rvRemoteModel.addOnItemTouchListener(new RecyclerItemClickListenerAndDetector(context,
                                new RecyclerItemClickListenerAndDetector.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position)
                                    {
                                        Intent intent=new Intent(context, NewUserRemoteActivity.class);
                                        intent.putExtra("RemoteDetails",remoteDetailsArrayList.get(position));
                                        intent.putExtra("RoomName",remoteDetailsArrayList.get(position).getRoomName());
                                        intent.putExtra("RoomId",remoteDetailsArrayList.get(position).getRoomId());
                                        intent.putExtra("Brand",remoteDetailsArrayList.get(position).getBrand());
                                        intent.putExtra("RemoteId",remoteDetailsArrayList.get(position).getRemoteId());
                                        intent.putExtra("RemoteType", remoteDetailsArrayList.get(position).getRemoteType());
                                        startActivity(intent);




                                       /* ApplianceListFragment f = new ApplianceListFragment();
                                        TabFragment fragment = new TabFragment();
                                        Bundle args = new Bundle();
                                        args.putString(GlobalApplication.SELECTED_ROOM_ID, roomList.get(position).getRoomId());
                                        f.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.root_layout, f, ROOM_FRAGMENT);
                                        fragmentTransaction.addToBackStack(fragmentTransaction.toString());
                                        fragmentTransaction.commit();
                                        *//*intent.putExtra(GlobalApplication.SELECTED_ROOM_ID, roomList.get(position).getRoomId());
                                        startActivity(intent);*/
                                    }
                                })
                        );

                    }
                    else
                    {
                        if(waitDialog!=null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                        text.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "No Remote Model Are Added.Please Contact Us", Toast.LENGTH_SHORT).show();

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
