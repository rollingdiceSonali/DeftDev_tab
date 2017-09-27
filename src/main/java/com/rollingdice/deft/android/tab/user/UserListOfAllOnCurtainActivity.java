package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
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
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 12/31/2015.
 */
public class UserListOfAllOnCurtainActivity extends Activity implements UserCurtainAdapter.AdapterCallback
{
    private List<CurtainDetails> curatins = new ArrayList<>();
    private RecyclerView rvCurtains;
    private RecyclerView.Adapter adapterCurtain;
    DatabaseReference localRef;
    private Menu actionMenu;
    private SimpleArcDialog mDialog;
    private Context context;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_user_list_of_all_on_curtains);
            localRef = GlobalApplication.firebaseRef;
            RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
            context=UserListOfAllOnCurtainActivity.this;
            msgTv = (TextView) findViewById(R.id.msg_tv);
            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if(!isFinishing())
            mDialog.show();

            setUpCurtainsUI();

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

    private void setUpCurtainsUI()
    {
        try {

            rvCurtains = (RecyclerView) findViewById(R.id.recycler_view_user_all_curtains);
            RecyclerView.LayoutManager lmCurtain = new GridLayoutManager(this, 2);
            rvCurtains.addItemDecoration(new DividerItemDecoration(UserListOfAllOnCurtainActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
            rvCurtains.setLayoutManager(lmCurtain);

            DatabaseReference curtainDetails = localRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails");

            curtainDetails.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    curatins = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                        //Getting each room
                        for (DataSnapshot type : roomSnapshot.getChildren()) {
                            if (type.getRef().toString().indexOf("curtainDetails") != 0) {
                                for (DataSnapshot curtain : type.getChildren()) {
                                    CurtainDetails curtaindetails = curtain.getValue(CurtainDetails.class);
                                    curatins.add(curtaindetails);
                                }
                                break;
                            }

                        }

                    }
                    if(curatins.size() > 0 ){

                        msgTv.setVisibility(View.GONE);
                        adapterCurtain = new UserCurtainAdapter(curatins,context, Color.parseColor("#4021272b"));
                        rvCurtains.setAdapter(adapterCurtain);

                    }else {

                        msgTv.setVisibility(View.VISIBLE);
                    }

                    rvCurtains.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                        {
                            if (newState == 0) {
                                GlobalApplication.isCurtainClickable = true;
                            }

                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                        {
                            int state = recyclerView.getScrollState();
                            if (state == 0) {
                                GlobalApplication.isCurtainClickable = true;
                            } else {

                                GlobalApplication.isCurtainClickable = false;
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
                    mDialog.dismiss();
                    // progressDialog.dismiss();
                }
            },3000);  // 3000 milliseconds

        }

    }
}
