package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.RemoteDataHelper;
import com.rollingdice.deft.android.tab.datahelper.RemoteKeysDataHelper;
import com.rollingdice.deft.android.tab.model.BrandDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RemoteKeyDetails;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rolling Dice on 7/28/2016.
 */
public class RemoteConfigDetailsActivity extends AppCompatActivity {
    SimpleArcDialog mDialog;
    List<RoomDetails> roomList = GlobalApplication.roomList;
    private String RoomId;
    private String RoomName;
    private ArrayList<RoomAppliance> appliances = new ArrayList<>();
    private ArrayList<BrandDetails> brandDetailsArrayList = new ArrayList<>();
    private String brandName;
    private String brandId;

    private ValueEventListener remoteValueEventListene = null;
    private String applianceId, applianceName, applianceType;
    private String IRId;
    AlertDialog.Builder roomListAlert, builder, brandAlert, remoteBuilder, recordBuilder, remoteConfig;
    AlertDialog roomDialog, applianceDialog, brandDialog, remoteDialog;
    RoomAppliance selected_appliance;
    String message;
    String remoteId;
    private LayoutInflater inflater;
    private DatabaseReference remoteKeyReference = null;
    private Context context;
    private List<RemoteKeyDetails> remoteKeyDetailsArrayList = new ArrayList<RemoteKeyDetails>();
    private String newRemoteKey, newRemoteTag, key;
    private boolean remoteNodeFlag = false;
    int pos = -1;
    private TextView idTv;

    DatabaseReference localRef, messageRef;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_config);
        remoteKeyReference = GlobalApplication.firebaseRef.child("remoteKey").child(Customer.getCustomer().customerId);

       /* remoteKeyReference = localRef.child("remoteKey").child(Customer.getCustomer().customerId);
        remoteValueEventListene = remoteKeyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    keyIndex = new ArrayList<String>();

                    remoteKeyDetailsArrayList = new ArrayList<RemoteKeyDetails>();
                    for (DataSnapshot remoteKeySnapshot : dataSnapshot.getChildren()) {

                        keyIndex.add(remoteKeySnapshot.getKey());
                        remoteKeyDetailsArrayList.add(remoteKeySnapshot.getValue(RemoteKeyDetails.class));
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        context = this;
        inflater = getLayoutInflater();
        localRef = GlobalApplication.firebaseRef;
        mDialog = new SimpleArcDialog(this);
        mDialog.setCancelable(false);
        ArcConfiguration configuration = new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
        mDialog.setConfiguration(configuration);
        mDialog.show();
        try {


            DatabaseReference brandDetails = localRef.child("Brands");
            brandDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    brandDetailsArrayList = new ArrayList<>();
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {

                        String Appliance = snapShot.child("Appliance").getValue(String.class);
                        String Brand = snapShot.child("Brand").getValue(String.class);
                        String Id = snapShot.child("Id").getValue(String.class);
                        BrandDetails brandDetails = new BrandDetails(Appliance, Brand, Id);
                        brandDetailsArrayList.add(brandDetails);

                    }
                    mDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            selectRemoteTypeConfiguration();


            /*DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");


            roomDetails.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot snapshot)
                {
                    roomList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren())
                    {
                        if (!roomSnapshot.getRef().toString().contains("globalOff"))
                        {
                            localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
                            String roomType = roomSnapshot.child("roomType").getValue(String.class);
                            String roomName = roomSnapshot.child("roomName").getValue(String.class);
                            String roomId = roomSnapshot.child("roomId").getValue(String.class);
                            String lastMotionDetected=roomSnapshot.child("lastMotionDetected").getValue(String.class);
                            RoomDetails room = new RoomDetails(roomName, roomType, roomId,lastMotionDetected);
                            roomList.add(room);
                        }

                    }

                    mDialog.dismiss();
                    getRooms();

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    System.out.println("The read failed: " + DatabaseError.getMessage());
                }
            });*/


        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }

    private void selectRemoteTypeConfiguration() {
        final Button btnMode;
        final Button btnIRRemote;

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mode_config_layout, null);

        btnMode = (Button) view.findViewById(R.id.btn_mood);
        btnIRRemote = (Button) view.findViewById(R.id.btn_Remote);

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RemoteConfigDetailsActivity.this, HomeActivity.class));

            }
        });

        btnIRRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRooms();
            }
        });

        remoteConfig = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        remoteConfig.setTitle("Select Remote Config");
        remoteConfig.setView(view);

        remoteDialog = remoteConfig.create();

        Context context = RemoteConfigDetailsActivity.this;

        if (!((Activity) context).isFinishing()) {
            remoteDialog.show();
        } else {
            //  Activity has been finished
        }

    }

    private void getRooms() {
        final RadioGroup radioGroup;
        RadioGroup.LayoutParams rprms;

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.mode_room_layout, null);
        radioGroup = (RadioGroup) view.findViewById(R.id.mode_radio_group);
        //final RadioButton[] radioButtons = new RadioButton[roomList.size()];
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        for (int z = 0; z < roomList.size(); z++) {

            if (roomList.get(z).getRoomId() != null) {
                RadioButton rb = new RadioButton(this);
                rb.setText(roomList.get(z).getRoomName());
                rb.setTextColor(getResources().getColor(R.color.white));
                rb.setPadding(5, 5, 5, 5);
                rb.setButtonDrawable(R.drawable.radio_btn);
                rb.setId(z);

                rprms = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.addView(rb, rprms);
            }


           /* radioButtons[z]=new RadioButton(this);
            String mrn = roomList.get(z).getRoomName();
            radioButtons[z].setText(mrn);
            radioGroup.addView(radioButtons[z]);*/
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RoomId = roomList.get(checkedId).getRoomId();
                RoomName = roomList.get(checkedId).getRoomName();

                getApplianceList(RoomId);
                /*for (int k = 0; k < radioGroup.getChildCount(); k++) {
                    radioGroup.getChildAt(k).setEnabled(false);
                }*/


            }
        });


        roomListAlert = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        roomListAlert.setTitle("Select Room");
        roomListAlert.setView(view);
        roomListAlert.setCancelable(false);


        roomListAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {

                    getAppliances(appliances);
                } else {
                    Toast.makeText(RemoteConfigDetailsActivity.this, "Please Select Room First", Toast.LENGTH_SHORT).show();
                    getRooms();
                }


            }
        });
        roomListAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* for (int k = 0; k < radioGroup.getChildCount(); k++) {
                    radioGroup.getChildAt(k).setEnabled(true);
                }*/
                dialog.dismiss();
                finish();

            }
        });

        roomDialog = roomListAlert.create();

        Context context = RemoteConfigDetailsActivity.this;

        if (!((Activity) context).isFinishing()) {
            roomDialog.show();
        } else {
            //  Activity has been finished
        }


        Toast.makeText(RemoteConfigDetailsActivity.this, "getRoomName", Toast.LENGTH_SHORT).show();


    }

    private void getApplianceList(final String RoomId) {
        appliances = new ArrayList<RoomAppliance>();
        mDialog.show();

        for (int i = 0; i < GlobalApplication.applianceList.size(); i++) {
            if (GlobalApplication.applianceList.get(i).getRoomId().equals(RoomId) &&
                    (GlobalApplication.applianceList.get(i).getApplianceType().equals("TV") ||
                            GlobalApplication.applianceList.get(i).getApplianceType().equals("SETTOP BOX") ||
                            GlobalApplication.applianceList.get(i).getApplianceType().equals("AC") ||
                            GlobalApplication.applianceList.get(i).getApplianceType().equals("PROJECTOR") ||
                            GlobalApplication.applianceList.get(i).getApplianceType().equals("DVD") ||
                            GlobalApplication.applianceList.get(i).getApplianceType().equals("MOOD LIGHT"))) {
                appliances.add(GlobalApplication.applianceList.get(i));
            }
        }
        mDialog.dismiss();
    }

    private void getAppliances(List<RoomAppliance> appliancesList) {

        Toast.makeText(RemoteConfigDetailsActivity.this, "getApplinces", Toast.LENGTH_SHORT).show();
        int i = appliancesList.size();

        CharSequence[] item = new CharSequence[i];
        boolean[] seleted = {false, false, false, false, false, false, false, false, false};
        for (int g = 0; g < appliancesList.size(); g++) {
            RoomAppliance app = appliancesList.get(g);
            String name = app.getApplianceName();
            item[g] = name;
        }



        if(item.length == 0){
            builder = new AlertDialog.Builder(RemoteConfigDetailsActivity.this, R.style.DialogSlideAnim);

            builder.setTitle("Select Appliances");
            builder.setMessage("No Remote ReletedAppliance Avalaible");
            builder.setCancelable(false);
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    getRooms();
                }
            });


        }else {

            builder = new AlertDialog.Builder(RemoteConfigDetailsActivity.this, R.style.DialogSlideAnim);

            builder.setTitle("Select Appliances");
            builder.setCancelable(false);

            builder.setMultiChoiceItems(item, seleted, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        selected_appliance = appliances.get(which);
                        applianceId = selected_appliance.getId();
                        applianceName = selected_appliance.getApplianceName();
                        applianceType = selected_appliance.getApplianceType();
                        dialog.dismiss();
                        getApplianceBrand();

                    }
                    Toast.makeText(RemoteConfigDetailsActivity.this, "" + which, Toast.LENGTH_SHORT).show();

                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                    startActivity(new Intent(RemoteConfigDetailsActivity.this, HomeActivity.class));
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    getRooms();
                }
            });


        }

        applianceDialog = builder.create();
        applianceDialog.show();


    }

    private void getIRId() {
        CharSequence[] item = new CharSequence[10];
        boolean[] selected = {false, false, false, false, false, false, false, false, false, false, false};


        for (int g = 0; g < 10; g++) {
            item[g] = new DecimalFormat("00").format(g);
        }

        builder = new AlertDialog.Builder(RemoteConfigDetailsActivity.this, R.style.DialogSlideAnim);
        builder.setTitle("Select IR Id");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(item, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    IRId = new DecimalFormat("00").format(which);
                }
                Toast.makeText(RemoteConfigDetailsActivity.this, "" + which, Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                remoteId = generatedAutoRemoteID();

                if (RemoteDataHelper.isRemoteAlreadyAvailable(IRId, remoteId) == null) {

                    addRemoteToDb();
                    openConfigView();
                } else {
                    Toast.makeText(RemoteConfigDetailsActivity.this, "Remote Id Already Available in IR Select Another", Toast.LENGTH_SHORT).show();
                    getIRId();

                }


            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getApplianceBrand();


            }
        });

        applianceDialog = builder.create();
        applianceDialog.show();
    }

    private void openConfigView() {

        remoteBuilder = new AlertDialog.Builder(RemoteConfigDetailsActivity.this, R.style.DialogSlideAnim);
        remoteBuilder.setTitle("Configure Buttons");
        remoteBuilder.setCancelable(false);

        if (selected_appliance.getApplianceType().equals("MOOD LIGHT")) {
            moodLightConfig();

        } else if (selected_appliance.getApplianceType().equals("SETTOP BOX")) {

            settopBoxConfig();

        } else if (selected_appliance.getApplianceType().equals("TV")) {
            tvConfig();

        } else if (selected_appliance.getApplianceType().equals("AC")) {
            acConfig();

        } else if (selected_appliance.getApplianceType().equals("PROJECTOR")) {

            projectorConfig();

        } else if (selected_appliance.getApplianceType().equals("DVD")) {

            dvdConfig();
        }
    }

    private void getApplianceBrand() {
        final RadioGroup radioGroup;
        RadioGroup.LayoutParams rprms;

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.mode_room_layout, null);
        radioGroup = (RadioGroup) view.findViewById(R.id.mode_radio_group);

        for (int z = 0; z < brandDetailsArrayList.size(); z++) {

            RadioButton rb = new RadioButton(this);
            rb.setText(brandDetailsArrayList.get(z).getBrand());
            rb.setId(z);
            rb.setTextColor(getResources().getColor(R.color.white));
            rb.setPadding(5, 5, 5, 5);
            rb.setButtonDrawable(R.drawable.radio_btn);

            rprms = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioGroup.addView(rb, rprms);

        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                brandName = brandDetailsArrayList.get(checkedId).getBrand();
                brandId = brandDetailsArrayList.get(checkedId).getId();

            }
        });

        brandAlert = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        brandAlert.setTitle("Select Brand");
        brandAlert.setView(view);
        brandAlert.setCancelable(false);


        brandAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getIRId();

            }
        });
        brandAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getAppliances(appliances);
            }
        });
        brandDialog = brandAlert.create();
        brandDialog.show();

    }

    private String generatedAutoRemoteID() {
        int addedRemote = RemoteDataHelper.getAllRemotesByRoomId(IRId).size();
        return new DecimalFormat("00").format(addedRemote);
    }

    private String generatedAutoRemote() {
        int addedRemote = RemoteDataHelper.getAllRemotes().size();
        return new DecimalFormat("00").format(addedRemote);
    }


    private void moodLightConfig() {


        final FrameLayout frameView = new FrameLayout(this);
        remoteBuilder.setView(frameView);
        remoteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

              //  addRemoteToDb();
                dialog.dismiss();
                finish();

            }
        });
        remoteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRemoteFromDb();
                dialog.dismiss();
                finish();
            }
        });


        final AlertDialog alertDialog = remoteBuilder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.rgb_led_strip_remote_layout1, frameView);
        alertDialog.show();


        idTv = (TextView) dialoglayout.findViewById(R.id.remote_IR_id);
        idTv.setText("IR ID   :" + IRId + "    Remote ID  :" + remoteId);

        //Power Button

        /*final Button settop_confirm = (Button) dialoglayout.findViewById(R.id.settop_btn_ok);
        settop_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        final Button moodLight_power_on = (Button) dialoglayout.findViewById(R.id.id_01);
        moodLight_power_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_power_on.getTag().toString()).show();

            }
        });


        //Power OFF id 01
        final Button moodLight_power_off = (Button) dialoglayout.findViewById(R.id.id_02);
        moodLight_power_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_power_off.getTag().toString()).show();
            }
        });


        //Power id 02
        final Button moodLight_02 = (Button) dialoglayout.findViewById(R.id.id_03);
        moodLight_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_02.getTag().toString()).show();
            }
        });


        //Power id 03
        final Button moodLight_03 = (Button) dialoglayout.findViewById(R.id.id_04);
        moodLight_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_03.getTag().toString()).show();
            }
        });


        //Power id 04
        final Button moodLight_04 = (Button) dialoglayout.findViewById(R.id.id_05);
        moodLight_04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_04.getTag().toString()).show();
            }
        });


        //Power id 05
        final Button moodLight_05 = (Button) dialoglayout.findViewById(R.id.id_06);
        moodLight_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_05.getTag().toString()).show();

            }
        });


        //Power id 06
        final Button moodLight_06 = (Button) dialoglayout.findViewById(R.id.id_07);
        moodLight_06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_06.getTag().toString()).show();

            }
        });


        //Power id 07
        final Button moodLight_07 = (Button) dialoglayout.findViewById(R.id.id_08);
        moodLight_07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_07.getTag().toString()).show();

            }
        });


        //Power id 08
        final Button moodLight_08 = (Button) dialoglayout.findViewById(R.id.id_09);
        moodLight_08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_08.getTag().toString()).show();

            }
        });


        //Power id 09
        final Button moodLight_09 = (Button) dialoglayout.findViewById(R.id.id_10);
        moodLight_09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_09.getTag().toString()).show();

            }
        });


        //Power id 10
        final Button moodLight_10 = (Button) dialoglayout.findViewById(R.id.id_11);
        moodLight_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_10.getTag().toString()).show();

            }
        });


        //Power id 11
        final Button moodLight_11 = (Button) dialoglayout.findViewById(R.id.id_12);
        moodLight_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_11.getTag().toString()).show();

            }
        });


        //Power id 12
        final Button moodLight_12 = (Button) dialoglayout.findViewById(R.id.id_13);
        moodLight_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_12.getTag().toString()).show();

            }
        });


        //Power id 13
        final Button moodLight_13 = (Button) dialoglayout.findViewById(R.id.id_14);
        moodLight_13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_13.getTag().toString()).show();
            }
        });


        //Power id 14
        final Button moodLight_14 = (Button) dialoglayout.findViewById(R.id.id_15);
        moodLight_14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_14.getTag().toString()).show();

            }
        });


        //Power id 15
        final Button moodLight_15 = (Button) dialoglayout.findViewById(R.id.id_16);
        moodLight_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_15.getTag().toString()).show();

            }
        });


        //Power id 16
        final Button moodLight_16 = (Button) dialoglayout.findViewById(R.id.id_17);
        moodLight_16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_16.getTag().toString()).show();
            }
        });


        //Power id 17
        final Button moodLight_17 = (Button) dialoglayout.findViewById(R.id.id_18);
        moodLight_17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_17.getTag().toString()).show();

            }
        });


        //Power id 18
        final Button moodLight_18 = (Button) dialoglayout.findViewById(R.id.id_19);
        moodLight_18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_18.getTag().toString()).show();
            }
        });


        //Power id 19
        final Button moodLight_19 = (Button) dialoglayout.findViewById(R.id.id_20);
        moodLight_19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_19.getTag().toString()).show();
            }
        });


        //Power id 20
        final Button moodLight_20 = (Button) dialoglayout.findViewById(R.id.id_21);
        moodLight_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_20.getTag().toString()).show();
            }
        });


        //Power id 21
        final Button moodLight_21 = (Button) dialoglayout.findViewById(R.id.id_22);
        moodLight_21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_21.getTag().toString()).show();

            }
        });


        //Power id 22
        final Button moodLight_22 = (Button) dialoglayout.findViewById(R.id.id_23);
        moodLight_22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_22.getTag().toString()).show();

            }
        });


        //Power id 23
        final Button moodLight_23 = (Button) dialoglayout.findViewById(R.id.id_24);
        moodLight_23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,moodLight_23.getTag().toString()).show();
            }
        });


    }



    private void tvConfig() {

        final FrameLayout frameView = new FrameLayout(this);
        remoteBuilder.setView(frameView);
        remoteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

              //  addRemoteToDb();
                dialog.dismiss();
                finish();

            }
        });
        remoteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRemoteFromDb();
                dialog.dismiss();
                finish();
            }
        });

        final AlertDialog alertDialog = remoteBuilder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_remotebuilder_tv, frameView);
        alertDialog.show();

        idTv = (TextView) dialoglayout.findViewById(R.id.remote_IR_id);
        idTv.setText("IR ID   :" + IRId + "    Remote ID  :" + remoteId);

        //TV Button
        final Button tv = (Button) dialoglayout.findViewById(R.id.tv_power_on);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, tv.getText().toString(), tv.getTag().toString(),
                        tv);
                recordAlert.show();
            }
        });

        //Mode Button
        final Button home = (Button) dialoglayout.findViewById(R.id.home_btn_btn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, home.getText().toString(), home.getTag().toString(),
                        home);
                recordAlert.show();
            }
        });

        //Guide Button
        final Button guide = (Button) dialoglayout.findViewById(R.id.power_off);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, guide.getText().toString(), guide.getTag().toString(),
                        guide);
                recordAlert.show();
            }
        });

        //power_btn_btn Button
        final Button power = (Button) dialoglayout.findViewById(R.id.proj_open_btn);
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, power.getText().toString(), power.getTag().toString(),
                        power);
                recordAlert.show();
            }
        });

        //select_btn Button
        final Button select = (Button) dialoglayout.findViewById(R.id.select_btn);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, select.getText().toString(), select.getTag().toString(),
                        select);
                recordAlert.show();
            }
        });

        //left_arrow_btn Button
        final ImageButton left = (ImageButton) dialoglayout.findViewById(R.id.left_arrow_btn);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId, left.getTag().toString()).show();
            }
        });


        //Right Button
        final ImageButton right = (ImageButton) dialoglayout.findViewById(R.id.right_arrow_btn);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId, right.getTag().toString()).show();
            }
        });

        //up_arrow_btn Button
        final ImageButton up = (ImageButton) dialoglayout.findViewById(R.id.up_arrow_btn);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,up.getTag().toString()).show();
            }
        });

        //down_arrow_btn Button
        final ImageButton down = (ImageButton) dialoglayout.findViewById(R.id.down_arrow_btn);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,down.getTag().toString()).show();
            }
        });

        //volume_minus_btn Button
        final Button volumeMinus = (Button) dialoglayout.findViewById(R.id.volume_minus_btn);
        volumeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, volumeMinus.getText().toString(), volumeMinus.getTag().toString(),
                        volumeMinus);
                recordAlert.show();
            }
        });

        //volume_plus_btn Button
        final Button volumePlus = (Button) dialoglayout.findViewById(R.id.volume_plus_btn);
        volumePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, volumePlus.getText().toString(), volumePlus.getTag().toString(),
                        volumePlus);
                recordAlert.show();
            }
        });

        //ch_minus_btn Button
        final Button chMinus = (Button) dialoglayout.findViewById(R.id.ch_minus_btn);
        chMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, chMinus.getText().toString(), chMinus.getTag().toString(),
                        chMinus);
                recordAlert.show();
            }
        });

        //ch_plus_btn Button
        final Button chPlus = (Button) dialoglayout.findViewById(R.id.ch_plus_btn);
        chPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, chPlus.getText().toString(), chPlus.getTag().toString(),
                        chPlus);
                recordAlert.show();
            }
        });

        //back_btn Button
        final Button back = (Button) dialoglayout.findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, back.getText().toString(), back.getTag().toString(),
                        back);
                recordAlert.show();
            }
        });

        //mute_btn Button
        final Button mute_btn = (Button) dialoglayout.findViewById(R.id.mute_btn);
        mute_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, mute_btn.getText().toString(), mute_btn.getTag().toString(),
                        mute_btn);
                recordAlert.show();
            }
        });

       /* //Confirm Button
        final Button confirm = (Button) dialoglayout.findViewById(R.id.btn_ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });*/

    }



    private void acConfig() {


        final FrameLayout frameView = new FrameLayout(this);
        remoteBuilder.setView(frameView);
        remoteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //addRemoteToDb();
                dialog.dismiss();
                finish();

            }
        });
        remoteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRemoteFromDb();
                dialog.dismiss();
                finish();
            }
        });

        final AlertDialog alertDialog = remoteBuilder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_remotebuilder_ac, frameView);

        alertDialog.show();

        idTv = (TextView) dialoglayout.findViewById(R.id.remote_IR_id);
        idTv.setText("IR ID   :" + IRId + "    Remote ID  :" + remoteId);
        //Power Button
        final Button power = (Button) dialoglayout.findViewById(R.id.power_btn);
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, power.getText().toString(), power.getTag().toString(),
                        power);
                recordAlert.show();

            }
        });

        //Mode Button
        final Button mode = (Button) dialoglayout.findViewById(R.id.mode_btn_btn);
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, mode.getText().toString(), mode.getTag().toString(),
                        mode);
                recordAlert.show();
            }
        });

        //Fan Speed Button
        final Button fanSpeed = (Button) dialoglayout.findViewById(R.id.Fan_Speed_btn_btn);
        fanSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, fanSpeed.getText().toString(), fanSpeed.getTag().toString(),
                        fanSpeed);
                recordAlert.show();
            }
        });

        //Temp 18 Button
        final Button temp18 = (Button) dialoglayout.findViewById(R.id.temp_18_btn);
        temp18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, temp18.getText().toString(), temp18.getTag().toString(),
                        temp18);
                recordAlert.show();
            }
        });

        //Temp 19 Button
        final Button temp19 = (Button) dialoglayout.findViewById(R.id.temp_19_btn);
        temp19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, temp19.getText().toString(), temp19.getTag().toString(),
                        temp19);
                recordAlert.show();
            }
        });

        //Temp 20 Button
        final Button temp20 = (Button) dialoglayout.findViewById(R.id.temp_20_btn);
        temp20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, temp20.getText().toString(), temp20.getTag().toString(),
                        temp20);
                recordAlert.show();
            }
        });


        //Temp 21 Button
        final Button temp21 = (Button) dialoglayout.findViewById(R.id.temp_21_btn);
        temp21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, temp21.getText().toString(), temp21.getTag().toString(),
                        temp21);
                recordAlert.show();
            }
        });

        //Temp 22 Button
        final Button temp22 = (Button) dialoglayout.findViewById(R.id.temp_22_btn);
        temp22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, temp22.getText().toString(), temp22.getTag().toString(),
                        temp22);
                recordAlert.show();
            }
        });

        //Temp 23 Button
        final Button temp23 = (Button) dialoglayout.findViewById(R.id.temp_23_btn);
        temp23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,temp23.getTag().toString()).show();
            }
        });

        //Temp 24 Button
        final Button temp24 = (Button) dialoglayout.findViewById(R.id.temp_24_btn);
        temp24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,temp24.getTag().toString()).show();
            }
        });

        //Temp 25 Button
        final Button temp25 = (Button) dialoglayout.findViewById(R.id.temp_25_btn);
        temp25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,temp25.getTag().toString()).show();
            }
        });

        //Temp 26 Button
        final Button temp26 = (Button) dialoglayout.findViewById(R.id.temp_26_btn);
        temp26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,temp26.getTag().toString()).show();

            }
        });

        //Temp 27 Button
        final Button temp27 = (Button) dialoglayout.findViewById(R.id.temp_27_btn);
        temp27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,temp27.getTag().toString()).show();
            }
        });

        //Temp 28 Button
        final Button temp28 = (Button) dialoglayout.findViewById(R.id.temp_28_btn);
        temp28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,temp28.getTag().toString()).show();
            }
        });

        //Temp 29 Button
        final Button temp29 = (Button) dialoglayout.findViewById(R.id.temp_29_btn);
        temp29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,temp29.getTag().toString()).show();

            }
        });

        //Confirm Button
        /*final Button confirm = (Button)dialoglayout.findViewById(R.id.btn_ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });*/

    }



    private void settopBoxConfig() {

        final FrameLayout frameView = new FrameLayout(this);
        remoteBuilder.setView(frameView);
        remoteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //addRemoteToDb();
                dialog.dismiss();
                finish();

            }
        });
        remoteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRemoteFromDb();
                dialog.dismiss();
                finish();
            }
        });

        final AlertDialog alertDialog = remoteBuilder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_remotebuilder_settop, frameView);
        alertDialog.show();

        idTv = (TextView) dialoglayout.findViewById(R.id.remote_IR_id);
        idTv.setText("IR ID   :" + IRId + "    Remote ID  :" + remoteId);

        //Power Button
        final Button settop_power_on = (Button) dialoglayout.findViewById(R.id.settop_power_on);
        settop_power_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_power_on.getText().toString(), settop_power_on.getTag().toString(),
                        settop_power_on);
                recordAlert.show();

            }
        });

        //Home Button
        final Button settop_power_off = (Button) dialoglayout.findViewById(R.id.settop_power_off);
        settop_power_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_power_off.getText().toString(), settop_power_off.getTag().toString(),
                        settop_power_off);
                recordAlert.show();

            }
        });

        //Guide Button
        final Button settop_guide_btn = (Button) dialoglayout.findViewById(R.id.settop_guide_btn);
        settop_guide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_guide_btn.getText().toString(), settop_guide_btn.getTag().toString(),
                        settop_guide_btn);
                recordAlert.show();
            }
        });

        //settop_home_btn_btn Button
        final Button settop_home_btn_btn = (Button) dialoglayout.findViewById(R.id.settop_home_btn_btn);
        settop_home_btn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_home_btn_btn.getText().toString(), settop_home_btn_btn.getTag().toString(),
                        settop_home_btn_btn);
                recordAlert.show();
            }
        });

        //select_btn Button
        final Button settop_select_btn = (Button) dialoglayout.findViewById(R.id.settop_select_btn);
        settop_select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_select_btn.getText().toString(), settop_select_btn.getTag().toString(),
                        settop_select_btn);
                recordAlert.show();
            }
        });

        //left_arrow_btn Button
        final ImageButton settop_left = (ImageButton) dialoglayout.findViewById(R.id.settop_left_arrow_btn);
        settop_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getDialogDialogToPlayAndRecord(IRId,remoteId,settop_left.getTag().toString()).show();
            }
        });


        //Right Button
        final ImageButton settop_right = (ImageButton) dialoglayout.findViewById(R.id.settop_right_arrow_btn);
        settop_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,settop_right.getTag().toString()).show();

            }
        });

        //up_arrow_btn Button
        final ImageButton settop_up = (ImageButton) dialoglayout.findViewById(R.id.settop_up_arrow_btn);
        settop_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,settop_up.getTag().toString()).show();
            }
        });

        //down_arrow_btn Button
        final ImageButton settop_down = (ImageButton) dialoglayout.findViewById(R.id.settop_down_arrow_btn);
        settop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,settop_down.getTag().toString()).show();
            }
        });

        //volume_minus_btn Button
        final Button settop_volumeMinus = (Button) dialoglayout.findViewById(R.id.settop_volume_minus_btn);
        settop_volumeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_volumeMinus.getText().toString(), settop_volumeMinus.getTag().toString(),
                        settop_volumeMinus);
                recordAlert.show();
            }
        });

        //volume_plus_btn Button
        final Button settop_volumePlus = (Button) dialoglayout.findViewById(R.id.settop_volume_plus_btn);
        settop_volumePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_volumePlus.getText().toString(), settop_volumePlus.getTag().toString(),
                        settop_volumePlus);
                recordAlert.show();
            }
        });

        //ch_minus_btn Button
        final Button settop_chMinus = (Button) dialoglayout.findViewById(R.id.settop_ch_minus_btn);
        settop_chMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_chMinus.getText().toString(), settop_chMinus.getTag().toString(),
                        settop_chMinus);
                recordAlert.show();
            }
        });

        //ch_plus_btn Button
        final Button settop_chPlus = (Button) dialoglayout.findViewById(R.id.settop_ch_plus_btn);
        settop_chPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_chPlus.getText().toString(), settop_chPlus.getTag().toString(),
                        settop_chPlus);
                recordAlert.show();
            }
        });

        //back_btn Button
        final Button settop_back = (Button) dialoglayout.findViewById(R.id.settop_back_btn);
        settop_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_back.getText().toString(), settop_back.getTag().toString(),
                        settop_back);
                recordAlert.show();
            }
        });

        //mute_btn Button
        final Button settop_mute_btn = (Button) dialoglayout.findViewById(R.id.settop_mute_btn);
        settop_mute_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, settop_mute_btn.getText().toString(), settop_mute_btn.getTag().toString(),
                        settop_mute_btn);
                recordAlert.show();
            }
        });

     /*   //Confirm Button
        final Button settop_confirm = (Button) dialoglayout.findViewById(R.id.settop_btn_ok);
        settop_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
*/

    }



    private void dvdConfig() {

        final FrameLayout frameView = new FrameLayout(this);
        remoteBuilder.setView(frameView);
        remoteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

               // addRemoteToDb();
                dialog.dismiss();
                finish();

            }
        });
        remoteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRemoteFromDb();
                dialog.dismiss();
                finish();
            }
        });


        final AlertDialog alertDialog = remoteBuilder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_remotebuilder_dvd, frameView);
        alertDialog.show();


        idTv = (TextView) dialoglayout.findViewById(R.id.remote_IR_id);
        idTv.setText("IR ID   :" + IRId + "    Remote ID  :" + remoteId);
        //Power On Button
        final Button dvdpoweron = (Button) dialoglayout.findViewById(R.id.dvd_power_on);
        dvdpoweron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, dvdpoweron.getText().toString(), dvdpoweron.getTag().toString(),
                        dvdpoweron);
                recordAlert.show();
            }
        });

        //Power Off Button
        final Button PowerOff = (Button) dialoglayout.findViewById(R.id.dvd_power_off);
        PowerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, PowerOff.getText().toString(), PowerOff.getTag().toString(),
                        PowerOff);
                recordAlert.show();
            }
        });

        //Open Button
        final Button Open = (Button) dialoglayout.findViewById(R.id.dvd_open_btn);
        Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, Open.getText().toString(), Open.getTag().toString(),
                        Open);
                recordAlert.show();
            }
        });

        //Menu Button
        final Button Menu = (Button) dialoglayout.findViewById(R.id.dvd_menu_btn_btn);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, Menu.getText().toString(), Menu.getTag().toString(),
                        Menu);
                recordAlert.show();
            }
        });

        //Play Button
        final Button PLay = (Button) dialoglayout.findViewById(R.id.dvd_play_btn);
        PLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, PLay.getText().toString(), PLay.getTag().toString(),
                        PLay);
                recordAlert.show();
            }
        });

        //left_arrow_btn Button
        final ImageButton left = (ImageButton) dialoglayout.findViewById(R.id.dvd_left_arrow_btn);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,left.getTag().toString()).show();

            }
        });


        //Right Button
        final ImageButton right = (ImageButton) dialoglayout.findViewById(R.id.dvd_right_arrow_btn);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,right.getTag().toString()).show();

            }
        });

        //up_arrow_btn Button
        final ImageButton up = (ImageButton) dialoglayout.findViewById(R.id.dvd_up_arrow_btn);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialogDialogToPlayAndRecord(IRId,remoteId,up.getTag().toString()).show();

            }
        });

        //down_arrow_btn Button
        final ImageButton down = (ImageButton) dialoglayout.findViewById(R.id.dvd_down_arrow_btn);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,down.getTag().toString()).show();
            }
        });

        //volume_minus_btn Button
        final Button volumeMinus = (Button) dialoglayout.findViewById(R.id.dvd_volume_minus_btn);
        volumeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, volumeMinus.getText().toString(), volumeMinus.getTag().toString(),
                        volumeMinus);
                recordAlert.show();
            }
        });

        //volume_plus_btn Button
        final Button volumePlus = (Button) dialoglayout.findViewById(R.id.dvd_volume_plus_btn);
        volumePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, volumePlus.getText().toString(), volumePlus.getTag().toString(),
                        volumePlus);
                recordAlert.show();
            }
        });

        //Options Button
        final Button Options = (Button) dialoglayout.findViewById(R.id.dvd_options_btn);
        Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, Options.getText().toString(), Options.getTag().toString(),
                        Options);
                recordAlert.show();
            }
        });

        //OK Button
        final Button OK = (Button) dialoglayout.findViewById(R.id.dvd_select_btn);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, OK.getText().toString(), OK.getTag().toString(),
                        OK);
                recordAlert.show();
            }
        });

        //Stop Button
        final Button stop = (Button) dialoglayout.findViewById(R.id.dvd_stop_btn);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, stop.getText().toString(), stop.getTag().toString(),
                        stop);
                recordAlert.show();
            }
        });

        //mute_btn Button
        final Button back = (Button) dialoglayout.findViewById(R.id.dvd_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, back.getText().toString(), back.getTag().toString(),
                        back);
                recordAlert.show();
            }
        });

       /* //Confirm Button
        final Button confirm = (Button) dialoglayout.findViewById(R.id.btn_ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });*/

    }



    private void projectorConfig() {


        final FrameLayout frameView = new FrameLayout(this);
        remoteBuilder.setView(frameView);
        remoteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

               // addRemoteToDb();
                dialog.dismiss();
                finish();

            }
        });
        remoteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                deleteRemoteFromDb();
                dialog.dismiss();
                finish();
            }
        });


        final AlertDialog alertDialog = remoteBuilder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_remotebuilder_projector, frameView);
        alertDialog.show();
        idTv = (TextView) dialoglayout.findViewById(R.id.remote_IR_id);
        idTv.setText("IR ID   :" + IRId + "    Remote ID  :" + remoteId);

        //Power On Button
        final Button projector = (Button) dialoglayout.findViewById(R.id.proj_power_on);
        projector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, projector.getText().toString(), projector.getTag().toString(),
                        projector);
                recordAlert.show();

            }
        });

        //Power Off Button
        final Button PowerOff = (Button) dialoglayout.findViewById(R.id.proj_power_off);
        PowerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, PowerOff.getText().toString(), PowerOff.getTag().toString(),
                        PowerOff);
                recordAlert.show();
            }
        });

        //Menu Button
        final Button Menu = (Button) dialoglayout.findViewById(R.id.proj_menu_btn);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, Menu.getText().toString(), Menu.getTag().toString(),
                        Menu);
                recordAlert.show();
            }
        });

        //power_btn_btn Button
        final Button Mode = (Button) dialoglayout.findViewById(R.id.proj_mode_btn);
        Mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, Mode.getText().toString(), Mode.getTag().toString(),
                        Mode);
                recordAlert.show();
            }
        });

        //Source Button
        final Button Source = (Button) dialoglayout.findViewById(R.id.proj_source_btn);
        Source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, Source.getText().toString(), Source.getTag().toString(),
                        Source);
                recordAlert.show();
            }
        });

        //left_arrow_btn Button
        final ImageButton left = (ImageButton) dialoglayout.findViewById(R.id.proj_left_arrow_btn);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,left.getTag().toString()).show();
            }
        });


        //Right Button
        final ImageButton right = (ImageButton) dialoglayout.findViewById(R.id.proj_right_arrow_btn);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,right.getTag().toString()).show();

            }
        });

        //up_arrow_btn Button
        final ImageButton up = (ImageButton) dialoglayout.findViewById(R.id.proj_up_arrow_btn);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,up.getTag().toString()).show();
            }
        });

        //down_arrow_btn Button
        final ImageButton down = (ImageButton) dialoglayout.findViewById(R.id.proj_down_arrow_btn);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialogDialogToPlayAndRecord(IRId,remoteId,down.getTag().toString()).show();
            }
        });

        //volume_minus_btn Button
        final Button volumeMinus = (Button) dialoglayout.findViewById(R.id.proj_volume_minus_btn);
        volumeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, volumeMinus.getText().toString(), volumeMinus.getTag().toString(),
                        volumeMinus);
                recordAlert.show();
            }
        });

        //volume_plus_btn Button
        final Button volumePlus = (Button) dialoglayout.findViewById(R.id.proj_volume_plus_btn);
        volumePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, volumePlus.getText().toString(), volumePlus.getTag().toString(),
                        volumePlus);
                recordAlert.show();
            }
        });

        //ch_minus_btn Button
        final Button zoomMinus = (Button) dialoglayout.findViewById(R.id.proj_zoom_minus_btn);
        zoomMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, zoomMinus.getText().toString(), zoomMinus.getTag().toString(),
                        zoomMinus);
                recordAlert.show();
            }
        });

        //ch_plus_btn Button
        final Button zoomPlus = (Button) dialoglayout.findViewById(R.id.proj_zoom_plus_btn);
        zoomPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, zoomPlus.getText().toString(), zoomPlus.getTag().toString(),
                        zoomPlus);
                recordAlert.show();
            }
        });

        //back_btn Button
        final Button pgup = (Button) dialoglayout.findViewById(R.id.proj_page_up_btn);
        pgup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, pgup.getText().toString(), pgup.getTag().toString(),
                        pgup);
                recordAlert.show();
            }
        });

        //mute_btn Button
        final Button pgdn = (Button) dialoglayout.findViewById(R.id.proj_page_down_btn);
        pgdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog recordAlert = getRemoteDialog(IRId, remoteId, pgdn.getText().toString(), pgdn.getTag().toString(),
                        pgdn);
                recordAlert.show();
            }
        });

        //Confirm Button
      /*  final Button confirm = (Button) dialoglayout.findViewById(R.id.btn_ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });*/

    }

    private void deleteRemoteFromDb(){


        Toast.makeText(RemoteConfigDetailsActivity.this, "" + brandName, Toast.LENGTH_SHORT).show();

        String Id = generatedAutoRemote();
        int id  = Integer.parseInt(Id);
        id -=1;
        Id = new DecimalFormat("00").format(id);


        DatabaseReference remoteRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(Id);
        remoteRef.removeValue();

       /* DatabaseReference messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(Id).child("command");
        Map<String, Object> messageDetails = new HashMap<>();
        messageDetails.put("message", "message");
        messageDetails.put("toggle", 0);
        messageRef.updateChildren(messageDetails);*/

        RemoteDataHelper.deleteRemote(remoteId, IRId);

    }

    private void addRemoteToDb() {



        Toast.makeText(RemoteConfigDetailsActivity.this, "" + brandName, Toast.LENGTH_SHORT).show();

        String Id = generatedAutoRemote();

        DatabaseReference remoteRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(Id);
        Map<String, Object> details = new HashMap<>();
        details.put("Ids", Id);
        details.put("IRId", IRId);
        details.put("roomId", RoomId);
        details.put("roomName", RoomName);
        details.put("brand", brandName);
        details.put("remoteId", remoteId);
        details.put("remoteType", applianceType);
        remoteRef.setValue(details);

        DatabaseReference messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(Id).child("command");
        Map<String, Object> messageDetails = new HashMap<>();
        messageDetails.put("message", "message");
        messageDetails.put("toggle", 0);
        messageRef.updateChildren(messageDetails);

        RemoteDataHelper.addRemote(remoteId, brandName, IRId, RoomId);
    }



    private AlertDialog getDialogDialogToPlayAndRecord(final String IRId, final String remoteId, final String tag) {

        AlertDialog.Builder recordBuilder = new AlertDialog.Builder(RemoteConfigDetailsActivity.this, R.style.DialogSlideAnim);
        recordBuilder.setTitle("Record");
        recordBuilder.setMessage("Press on Record and then press Confirm");

        recordBuilder.setPositiveButton("Record", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                message = "I012" + IRId + "0" + remoteId + tag + "0" + "0F";
                writeCommand(message);
                dialog.dismiss();
            }
        });

        recordBuilder.setNegativeButton("Play", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                message = "I012" + IRId + "1" + remoteId + tag + "0" + "0F";

                writeCommand(message);
                dialog.dismiss();
            }
        });


        AlertDialog recordAlert = recordBuilder.create();
        return recordAlert;


    }


    public void writeCommand(String message){

        int addedRemote = RemoteDataHelper.getAllRemotes().size();
        addedRemote = addedRemote -1;

        String id = new DecimalFormat("00").format(addedRemote);

        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                && Customer.getCustomer().customerId != null) {

            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(id)
                    .child("command").child("message");
            messageRef.setValue(message);
        }

        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                && Customer.getCustomer().customerId != null) {


            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                    .child(id).child("command").child("toggle");
            messageRef.setValue(1);
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (remoteDialog != null && remoteDialog.isShowing()) {
            remoteDialog.dismiss();
        }


        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (roomDialog != null && roomDialog.isShowing()) {
            roomDialog.dismiss();
        }

        if (applianceDialog != null && applianceDialog.isShowing()) {
            applianceDialog.dismiss();
        }
        if (brandDialog != null && brandDialog.isShowing()) {
            brandDialog.dismiss();
        }

    }


    public AlertDialog getRemoteDialog(final String irId, final String remoteId, final String remoteKey, final String remoteKeyTag, final Button powerOnTextView) {


        final EditText remoteIdEt, remoteKeyEt, newRemoteKeyEt, remoteKeyTagEt, irIDEt;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogSlideAnim);
        // Get the layout inflater
        View view = inflater.inflate(R.layout.change_remote_key_layout, null);


        remoteIdEt = (EditText) view.findViewById(R.id.remoteId);
        remoteKeyEt = (EditText) view.findViewById(R.id.key);
        newRemoteKeyEt = (EditText) view.findViewById(R.id.new_key);
        remoteKeyTagEt = (EditText) view.findViewById(R.id.tag);
        irIDEt = (EditText) view.findViewById(R.id.irId);
        newRemoteKeyEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRemoteKeyEt.setFocusableInTouchMode(true);
                newRemoteKeyEt.requestFocus();
                final InputMethodManager inputMethodManager = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(newRemoteKeyEt, InputMethodManager.SHOW_IMPLICIT);

            }
        });


        remoteIdEt.setHint("Remote Id : " + remoteId);
        irIDEt.setHint("IR Id : " + irId);

        if (RemoteKeysDataHelper.getRemoteKey(irId, remoteId, remoteKeyTag) != null) {
            key = RemoteKeysDataHelper.getRemoteKey(irId, remoteId, remoteKeyTag).getOldKey();
            key = "";
        }

        if (key != null && !key.equals("")) {
            remoteKeyEt.setHint("Remote Key : " + remoteKey);

        } else {
            remoteKeyEt.setHint("Remote Key : " + remoteKey);
        }

        remoteKeyTagEt.setHint("Key Number : " + remoteKeyTag);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("UPdate Remote Key");
        builder.setView(view)
                // Add action buttons
                .setNeutralButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        newRemoteKey = newRemoteKeyEt.getText().toString();
                        if (newRemoteKey != null && !newRemoteKey.equals("")) {
                            powerOnTextView.setText(newRemoteKey);
                            RemoteKeyDetails remoteKeyDetails = new RemoteKeyDetails();

                            remoteKeyDetails.setRemoteId(remoteId);
                            remoteKeyDetails.setIrId(irId);
                            remoteKeyDetails.setNewKey(newRemoteKey);
                            remoteKeyDetails.setOldTag(remoteKeyTag);

                            if (key != null && !key.equals("")) {
                                remoteKeyDetails.setOldKey(remoteKey);
                                key = "";
                            } else {
                                remoteKeyDetails.setOldKey(remoteKey);
                            }

                            remoteKeyDetailsArrayList = new ArrayList<RemoteKeyDetails>();
                            remoteKeyDetailsArrayList = RemoteKeysDataHelper.getRemoteKeys(irId, remoteId);

                            for (int i = 0; i < remoteKeyDetailsArrayList.size(); i++) {
                                String oldTag = remoteKeyDetailsArrayList.get(i).getOldTag();
                                String IrId = remoteKeyDetailsArrayList.get(i).getIrId();
                                String Id = remoteKeyDetailsArrayList.get(i).getRemoteId();
                                if (remoteKeyDetailsArrayList.get(i).getIrId().equals(irId) && remoteKeyDetailsArrayList.get(i).getRemoteId().equals(remoteId)
                                        && remoteKeyDetailsArrayList.get(i).getOldTag().equals(remoteKeyTag)) {

                                    pos = i;
                                    remoteNodeFlag = true;
                                    break;


                                }
                            }
                            if (remoteNodeFlag == true) {

                                remoteKeyReference.child("" + pos).child("newKey").setValue(newRemoteKey);
                                remoteKeyReference.child("" + pos).child("oldKey").setValue(remoteKey);

                                RemoteKeyDetails keyDetailsObj = new RemoteKeyDetails();
                                keyDetailsObj.setIrId(irId);
                                keyDetailsObj.setRemoteId(remoteId);
                                keyDetailsObj.setOldTag(remoteKeyTag);
                                keyDetailsObj.setNewKey(newRemoteKey);
                                keyDetailsObj.setOldKey(remoteKey);

                                RemoteKeysDataHelper.updateRemoteKey(keyDetailsObj);
//

                            } else {


                                RemoteKeyDetails keyDetailsObj = new RemoteKeyDetails();
                                keyDetailsObj.setIrId(irId);
                                keyDetailsObj.setRemoteId(remoteId);
                                keyDetailsObj.setOldTag(remoteKeyTag);
                                keyDetailsObj.setNewKey(newRemoteKey);
                                keyDetailsObj.setOldKey(remoteKey);

                                remoteKeyReference.child("" + (remoteKeyDetailsArrayList.size())).setValue(keyDetailsObj);
                                RemoteKeysDataHelper.addRemoteKey(remoteKeyDetails);
                                remoteNodeFlag = false;


                            }


                        }

                    }
                })
                .setPositiveButton("Record", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        message = "I012" + IRId + "0" + remoteId + remoteKeyTag + "0" + "0F";
                        writeCommand(message);

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                          /*  messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(generatedAutoRemote())
                                    .child("command").child("message");
                            messageRef.setValue(message);*/
                        }

                     /*   if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(generatedAutoRemote()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }*/

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Play", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        message = "I012" + IRId + "1" + remoteId + remoteKeyTag + "0" + "0F";

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            writeCommand(message);

                       /*     messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(generatedAutoRemote())
                                    .child("command").child("message");*/
                         //   messageRef.setValue(message);
                        }

                   /*     if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(generatedAutoRemote()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
*/
                        dialog.dismiss();
                    }
                });
        return builder.create();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}