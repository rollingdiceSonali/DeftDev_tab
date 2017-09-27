package com.rollingdice.deft.android.tab;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.NotificationDetails;
import com.rollingdice.deft.android.tab.user.DividerItemDecoration;
import com.rollingdice.deft.android.tab.user.UserNotificationAdapter;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Rolling Dice on 3/8/2016.
 */
public class NotificationListActivity extends AppCompatActivity {

    DatabaseReference localRef;
    ProgressDialog progressDialog;
    List<NotificationDetails> notificationList=null;
    String notificationTime,notificationText;
    private RecyclerView rvNotification;
    private RecyclerView.Adapter adapterNotification;
    Button clear;
    List<NotificationDetails> finalList = null;
    SimpleArcDialog mDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_of_all_notification);
        localRef=GlobalApplication.firebaseRef;
        mDialog = new SimpleArcDialog(this);
        ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
        mDialog.setConfiguration(configuration);
      /*  progressDialog= MyCustomProgressDialog.ctor(this);
//        progressDialog.show();*/
        mDialog.setCancelable(false);
        if(!isFinishing())
        mDialog.show();
        finalList=new ArrayList<NotificationDetails>(100);

        setUpNotificationUI();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(mDialog!=null && mDialog.isShowing())
        {
            if(!isFinishing())
            mDialog.dismiss();
        }
    }
    private void setUpNotificationUI()
    {
        rvNotification = (RecyclerView)findViewById(R.id.recycler_view_user_all_notification);
        RecyclerView.LayoutManager lmNotification = new LinearLayoutManager(this);
        rvNotification.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvNotification.setLayoutManager(lmNotification);
        if(!isFinishing())
        mDialog.show();

        DatabaseReference ref=localRef.child("notification").child("energy").child(Customer.getCustomer().customerId).child("isEnergyNotificationSubscribe");
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {



                boolean isEnergySubcribed=dataSnapshot.getValue(Boolean.class);
                if(isEnergySubcribed)
                {
                    getNotification("energy");
                }
                else
                {
                    if(mDialog.isShowing() && !isFinishing()) {
                        mDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref1=localRef.child("notification").child("error").child(Customer.getCustomer().customerId).child("isErrorNotificationSubscribe");
        ref1.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean isEnergySubcribed=dataSnapshot.getValue(Boolean.class);
                if(isEnergySubcribed)
                {
                    getNotification("error");
                }
                else
                {

                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2=localRef.child("notification").child("schedular").child(Customer.getCustomer().customerId).child("isSchedularNotificationSubscribe");
        ref2.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean isEnergySubcribed=dataSnapshot.getValue(Boolean.class);
                if(isEnergySubcribed)
                {
                    getNotification("schedular");

                }
                else
                {
                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference ref3=localRef.child("notification").child("motionDetection").child(Customer.getCustomer().customerId).child("isMotionDetectionSubscribe");
        ref3.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean isSubcribed=dataSnapshot.getValue(Boolean.class);
                if(isSubcribed)
                {
                    getNotification("motionDetection");

                }
                else
                {
                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref4=localRef.child("notification").child("lock").child(Customer.getCustomer().customerId).child("isLockNotificationSubscribe");
        ref4.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean isSubcribed=dataSnapshot.getValue(Boolean.class);
                if(isSubcribed)
                {
                    getNotification("lock");

                }
                else
                {
                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    }

    private void getNotification(String type)
    {
        DatabaseReference notification=localRef.child("notification").child(type);
        notification.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                notificationList = new ArrayList<>(100);

                for (DataSnapshot notificationType : dataSnapshot.getChildren())
                {

                        if (notificationType.getRef().toString().contains(Customer.getCustomer().customerId)) {

                            for (DataSnapshot TimeStampSnapShot : notificationType.getChildren()) {

                                if (!TimeStampSnapShot.getRef().toString().contains("isEnergyNotificationSubscribe") &&
                                        !TimeStampSnapShot.getRef().toString().contains("isErrorNotificationSubscribe")&&
                                        !TimeStampSnapShot.getRef().toString().contains("isSchedularNotificationSubscribe")&&
                                        !TimeStampSnapShot.getRef().toString().contains("isMotionDetectionSubscribe") &&
                                        !TimeStampSnapShot.getRef().toString().contains("isLockNotificationSubscribe"))
                                {
                                    String time = TimeStampSnapShot.getKey();
                                    Timestamp stamp = new Timestamp(Long.parseLong(time));
                                    notificationTime = new Date(stamp.getTime()).toString();
                                    notificationText = TimeStampSnapShot.getValue(String.class);
                                    NotificationDetails notificationDetails = new NotificationDetails(notificationTime, notificationText);
                                    notificationList.add(notificationDetails);

                                }
                            }
                        }

                }

                Collections.sort(notificationList, new Comparator<NotificationDetails>() {
                    @Override
                    public int compare(NotificationDetails lhs, NotificationDetails rhs)
                    {
                        String time=lhs.getNotificationTime();
                        String time1=rhs.getNotificationTime();
                        try {
                            return new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(time).compareTo(new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(time1));
                        } catch (ParseException e) {
                            return 0;
                        }

                    }
                });

                if(notificationList.size()>=100)
                {
                    for (int i = 0; i < 100; i++) {

                        finalList.add(notificationList.get(i));
                    }

                }else {

                    for (int i = 0; i < notificationList.size(); i++) {

                        finalList.add(notificationList.get(i));
                    }
                }



                adapterNotification = new UserNotificationAdapter(NotificationListActivity.this, finalList);
                rvNotification.setAdapter(adapterNotification);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run()
                    {
                        if(!isFinishing())
                        mDialog.dismiss();
                        // progressDialog.dismiss();
                    }
                }, 2000);
            }

            @Override

            public void onCancelled(DatabaseError DatabaseError) {

            }
        });

    }


}
