package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.HomeActivity;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.LockDetails;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.List;

import in.arjsna.passcodeview.PassCodeView;

/**
 * Created by Rolling Dice on 6/10/2016.
 */
public class UserListAllLockActivity extends Activity implements  UserLockAdapter.AdapterCallback
{
    DatabaseReference localRef;
    RecyclerView rvLocks;
    private RecyclerView.Adapter adapterLock;
    private List<LockDetails> lockDetailsList;
    SimpleArcDialog mDialog;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
        localRef= GlobalApplication.firebaseRef;
            context=UserListAllLockActivity.this;

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_list_of_all_locks);
        mDialog = new SimpleArcDialog(this);
        ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
        mDialog.setConfiguration(configuration);
        mDialog.setCancelable(false);
        setUpLockUI();

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

    private void setUpLockUI()
    {
        if(!isFinishing())
        mDialog.show();
        rvLocks= (RecyclerView) findViewById(R.id.recycler_view_user_all_locks);
        RecyclerView.LayoutManager lmLock = new GridLayoutManager(this, 4);
        rvLocks.addItemDecoration(new DividerItemDecoration(UserListAllLockActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
        rvLocks.setLayoutManager(lmLock);

        DatabaseReference lockRef = localRef.child("Locks").child(Customer.getCustomer().customerId).child("lockDetails");
        lockRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                lockDetailsList= new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String lockName=snapshot.child("lockName").getValue(String.class);
                    String lockId=snapshot.child("lockId").getValue(String.class);
                    boolean state=snapshot.child("lockState").getValue(Boolean.class);
                    Integer toggle=snapshot.child("lockToggle").getValue(Integer.class);
                    LockDetails lock=new LockDetails(lockName,lockId,state,toggle);
                    lockDetailsList.add(lock);

                }

                adapterLock = new UserLockAdapter(context,lockDetailsList, Color.parseColor("#4021272b"));
                rvLocks.setAdapter(adapterLock);
                rvLocks.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                    {
                        int state = recyclerView.getScrollState();
                        if (state == 0)
                        {
                            GlobalApplication.isLockClickable = true;

                        } else {

                            GlobalApplication.isLockClickable  = false;

                        }


                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                    {
                        if (newState == 0) {
                            GlobalApplication.isLockClickable= true;
                        }


                    }
                });
                if(!isFinishing() && mDialog.isShowing())
                mDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {

            }
        });
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

                }
            },1000);
        }

    }





}

