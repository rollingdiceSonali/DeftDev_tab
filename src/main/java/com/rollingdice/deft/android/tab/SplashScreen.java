package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import android.Manifest;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.model.Customer;

import com.rollingdice.deft.android.tab.model.EnergyLog;
import com.rollingdice.deft.android.tab.model.EnergyRoom;
import com.rollingdice.deft.android.tab.model.EnergySlave;
import com.rollingdice.deft.android.tab.user.RuntimePermissionsActivity;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplashScreen extends Activity
{


    String noOfRooms;

    DatabaseReference localRef;

    SimpleArcDialog mDialog;
    public FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash_screen);
            localRef = GlobalApplication.firebaseRef;
            mAuth = FirebaseAuth.getInstance();

            if (isCustomerAvailable()) {

                if (FirebaseAuth.getInstance() == null || FirebaseAuth.getInstance().getCurrentUser() == null) {
                    FirebaseAuth.getInstance().signOut();
                    loginTo(Customer.getCustomer().customerEmail, Customer.getCustomer().customerPassword);
                }
                else {
                    //startActivity(new Intent(SplashScreen.this,LandingActivity.class));
                    //startActivity(new Intent(SplashScreen.this, Login.class));
                    //loginTo(Customer.getCustomer().customerEmail, Customer.getCustomer().customerPassword);
                    getRoomCount();
                }
            } else {

                startActivity(new Intent(SplashScreen.this,Login.class));
                //startActivity(new Intent(SplashScreen.this, AddUserDetails.class));
                finish();
            }
        }
        catch(Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }


    private void loginTo(String s, String toString)
    {
        try {
            mAuth.signInWithEmailAndPassword(s, toString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                getRoomCount();
                                //startActivity(new Intent(Login.this, HomeActivity.class));
                                if (mDialog != null && !isFinishing()) {
                                    mDialog.dismiss();
                                }
                                //finish();
                            } else {
                                if (mDialog != null && !isFinishing()) {
                                    mDialog.dismiss();
                                }
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        catch(Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(mDialog!=null && mDialog.isShowing() && !isFinishing())
        {
            mDialog.dismiss();
        }
    }

    private void moveToNextScreen()
    {
        if (isRoomAvailable())
        {
            if(mDialog!=null && mDialog.isShowing())
            {
                mDialog.dismiss();
            }
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
        } else {


            if(mDialog!=null && mDialog.isShowing())
            {
                mDialog.dismiss();
            }
            startActivity(new Intent(SplashScreen.this, RoomDetailsActivity.class));
        }
        finish();
    }

    private boolean isCustomerAvailable()
    {
        return (new Select().from(Customer.class).execute().size() > 0);
    }

    private boolean isRoomAvailable() {
        return (!"0".equals(noOfRooms));
    }

    private void getRoomCount()
    {
        try {
            mDialog = new SimpleArcDialog(this);
            ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
            mDialog.setConfiguration(configuration);
            mDialog.setCancelable(false);
            if (!isFinishing())
                mDialog.show();

            DatabaseReference roomDetails = localRef.child("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("roomdetailsCount/roomCount");

            roomDetails.orderByValue().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        noOfRooms = dataSnapshot.getValue().toString();
                    } else {
                        noOfRooms = "0";
                    }
                    if(!isFinishing())
                        moveToNextScreen();
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError)
                {
                    if(!isFinishing())
                        mDialog.dismiss();
                    moveToNextScreen();
                }
            });



        }

        catch (Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

}