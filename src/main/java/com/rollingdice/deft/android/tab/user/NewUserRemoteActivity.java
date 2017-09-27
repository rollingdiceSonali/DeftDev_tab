package com.rollingdice.deft.android.tab.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.datahelper.RemoteKeysDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RemoteDetails;
import com.rollingdice.deft.android.tab.model.RemoteKeyDetails;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Rolling Dice on 12/2/2016.
 */
public class NewUserRemoteActivity extends Activity {
    RemoteDetails remoteDetails;
    private Context context;

    /// tV bTN
    ImageButton powerOn, powerOff, tv_power_on, tv_power_off, tv_guide, tv_Vol_down, tv_Vol_up, tv_Ch_down, tv_Ch_up, tv_back, tv_mute, tv_home;
    ImageButton tv_left, tv_right, tv_up, tv_down;
    Button tv_select;

    // SETTOP bOX
    ImageButton settop_power_on, settop_power_off, settop_guide, settop_Vol_down, settop_Vol_up, settop_Ch_down,
            settop_Ch_up, settop_back, settop_mute, settop_home;
    Button settop_select;
    ImageButton settop_left, settop_right, settop_up, settop_down;
    TextView sbPowerOnTv, sbPowerOffTv, sbVolpTv, sbVolmTv, sbMuteTv, sbHomeTv, sbGuideTv, sbChpTv, sbChmTv, sbBackTv;


    //  pROJECTOR bUTTON
    ImageButton proj_power_on, proj_power_off, proj_guide, proj_Vol_down, proj_Vol_up, proj_Ch_down, proj_Ch_up, proj_back, proj_mute, proj_home;
    Button proj_select;
    ImageButton proj_left, proj_right, proj_up, proj_down;

    // Dvd Btn
    ImageButton dvd_left, dvd_right, dvd_up, dvd_down;
    ImageButton dvd_power_on, dvd_power_off, dvd_menu, dvd_Vol_down, dvd_Vol_up, dvd_open_close, dvd_play, dvd_stop, dvd_back, dvd_options;
    Button dvd_select;

    // Ac Btn
    private ImageButton fix_btn, preset_btn, comfort_btn, swing_btn, fan_btn, mode_btn, ac_power_On_Btn, ac_power_Off_Btn;
    TextView txt_temprature, remoteType;/*applianceName, brandName,*/
    DiscreteSeekBar discreteSeekBar;
    private TextView powerOnTextView, acPowerOffTV, acSwingTv, acFeelTv, acModeTv, acFanTv, acQuiteTv, acTempTv;




    /// Key change On long Press
    private LayoutInflater inflater;
    private String newRemoteKey, newRemoteTag, key;
    int pos = -1;

    String s, message;
    Object data;
    DatabaseReference localRef, messageRef;

    private Button moodLight_power_on, moodLight_power_off, moodLight_02, moodLight_03, moodLight_04, moodLight_05, moodLight_06,
            moodLight_07, moodLight_08, moodLight_09, moodLight_10, moodLight_11, moodLight_12, moodLight_13, moodLight_14, moodLight_15,
            moodLight_16, moodLight_17, moodLight_18, moodLight_19, moodLight_20, moodLight_21, moodLight_22, moodLight_23;

    private DatabaseReference remoteKeyReference = null;
    private ValueEventListener remoteValueEventListene = null;
    private ArrayList<RemoteKeyDetails> remoteKeyDetailsArrayList = new ArrayList<RemoteKeyDetails>();
    private boolean remoteNodeFlag = false;
    ArrayList<String> keyIndex = new ArrayList<String>();
    private DatabaseReference refLevel5;
    private DatabaseReference refdimmableToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        remoteDetails = (RemoteDetails) getIntent().getSerializableExtra("RemoteDetails");
        refLevel5 = GlobalApplication.firebaseRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("01").child("1").
                child("appliance").child("0").child("dimableValue");
        refdimmableToggle = GlobalApplication.firebaseRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("01").child("1").
                child("appliance").child("0").child("dimableToggle");



        localRef = GlobalApplication.firebaseRef;
        // remoteKeyReference =  FirebaseDatabase.getInstance().getReference("remoteKey");
        remoteKeyReference = localRef.child("remoteKey").child(Customer.getCustomer().customerId);

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
        });


        context = this;
        inflater = getLayoutInflater();

        if (remoteDetails.getRemoteType().equals("AC")) {
            setContentView(R.layout.activity_user_remote_ac1);
            //brandName = (TextView) findViewById(R.id.brandName);
            txt_temprature = (TextView) findViewById(R.id.txt_temprature);
            ac_power_On_Btn = (ImageButton) findViewById(R.id.btn_power_on);
            ac_power_Off_Btn = (ImageButton) findViewById(R.id.btn_power_off);
            discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.discrete_seek_bar);
            //   applianceName = (TextView) findViewById(R.id.applinaceName);
            fix_btn = (ImageButton) findViewById(R.id.fix_btn);
            preset_btn = (ImageButton) findViewById(R.id.preset_btn);
            comfort_btn = (ImageButton) findViewById(R.id.comfort_btn);
            swing_btn = (ImageButton) findViewById(R.id.swing_btn);
            fan_btn = (ImageButton) findViewById(R.id.fan_btn);
            mode_btn = (ImageButton) findViewById(R.id.mode_btn);


            setAcRemoteKey();


        } else if (remoteDetails.getRemoteType().equals("SETTOP BOX")) {
            setContentView(R.layout.activity_user_remote_settop1);
            settop_power_on = (ImageButton) findViewById(R.id.settop_power_on);
            settop_power_off = (ImageButton) findViewById(R.id.settop_power_off);
            settop_guide = (ImageButton) findViewById(R.id.settop_guide_btn);
            settop_select = (Button) findViewById(R.id.settop_select_btn);
            settop_left = (ImageButton) findViewById(R.id.settop_left_arrow_btn);
            settop_right = (ImageButton) findViewById(R.id.settop_right_arrow_btn);
            settop_up = (ImageButton) findViewById(R.id.settop_up_arrow_btn);
            settop_down = (ImageButton) findViewById(R.id.settop_down_arrow_btn);
            settop_Vol_down = (ImageButton) findViewById(R.id.settop_valume_minus_btn);
            settop_Vol_up = (ImageButton) findViewById(R.id.settop_valume_plus_btn);
            settop_Ch_down = (ImageButton) findViewById(R.id.settop_ch_minus_btn);
            settop_Ch_up = (ImageButton) findViewById(R.id.settop_ch_plus_btn);
            settop_back = (ImageButton) findViewById(R.id.settop_back_btn);
            settop_mute = (ImageButton) findViewById(R.id.settop_mute_btn);
            settop_home = (ImageButton) findViewById(R.id.settop_home_btn);


            /// tEXTvIEW

            sbPowerOnTv = (TextView) findViewById(R.id.btn_power_on_text);
            sbBackTv = (TextView) findViewById(R.id.settop_back_btn_text);
            sbPowerOffTv = (TextView) findViewById(R.id.settop_power_off_text);
            sbVolpTv = (TextView) findViewById(R.id.settop_valume_plus_btn_text);
            sbVolmTv = (TextView) findViewById(R.id.settop_valume_minus_btn_text);
            sbMuteTv = (TextView) findViewById(R.id.settop_mute_btn_text);
            sbHomeTv = (TextView) findViewById(R.id.settop_home_btn_text);
            sbGuideTv = (TextView) findViewById(R.id.settop_guide_btn_text);
            sbChpTv = (TextView) findViewById(R.id.settop_ch_plus_btn_text);
            sbChmTv = (TextView) findViewById(R.id.settop_ch_minus_btn_text);

            setSettopBoxKey();


        } else if (remoteDetails.getRemoteType().equals("TV")) {


            setContentView(R.layout.activity_user_remote_tv1);
            tv_power_on = (ImageButton) findViewById(R.id.tv_power_on);
            tv_power_off = (ImageButton) findViewById(R.id.tv_power_off);
            tv_guide = (ImageButton) findViewById(R.id.tv_guide_btn);
            tv_select = (Button) findViewById(R.id.select_btn);
            tv_left = (ImageButton) findViewById(R.id.left_arrow_btn);
            tv_right = (ImageButton) findViewById(R.id.right_arrow_btn);
            tv_up = (ImageButton) findViewById(R.id.up_arrow_btn);
            tv_down = (ImageButton) findViewById(R.id.down_arrow_btn);
            tv_Vol_down = (ImageButton) findViewById(R.id.valume_minus_btn);
            tv_Vol_up = (ImageButton) findViewById(R.id.valume_plus_btn);
            tv_Ch_down = (ImageButton) findViewById(R.id.ch_minus_btn);
            tv_Ch_up = (ImageButton) findViewById(R.id.ch_plus_btn);
            tv_back = (ImageButton) findViewById(R.id.back_btn);
            tv_mute = (ImageButton) findViewById(R.id.mute_btn);
            tv_home = (ImageButton) findViewById(R.id.home_btn);


            sbPowerOnTv = (TextView) findViewById(R.id.btn_power_on_text);
            sbBackTv = (TextView) findViewById(R.id.home_btn_text);
            sbPowerOffTv = (TextView) findViewById(R.id.tv_power_off_text);
            sbVolpTv = (TextView) findViewById(R.id.valume_plus_btn_text);
            sbVolmTv = (TextView) findViewById(R.id.valume_minus_btn_text);
            sbMuteTv = (TextView) findViewById(R.id.mute_btn_text);
            sbHomeTv = (TextView) findViewById(R.id.tv_guide_btn_text);
            sbGuideTv = (TextView) findViewById(R.id.ch_plus_btn_text);
            sbChpTv = (TextView) findViewById(R.id.ch_minus_btn_text);
            sbChmTv = (TextView) findViewById(R.id.back_btn_text);

            setSettopBoxKey();


        } else if (remoteDetails.getRemoteType().equals("PROJECTOR")) {
            setContentView(R.layout.activity_user_remote_proj1);
            proj_power_on = (ImageButton) findViewById(R.id.proj_power_on);
            proj_power_off = (ImageButton) findViewById(R.id.proj_power_off);
            proj_guide = (ImageButton) findViewById(R.id.proj_menu_btn);
            proj_select = (Button) findViewById(R.id.proj_mode_btn);
            proj_left = (ImageButton) findViewById(R.id.proj_left_arrow_btn);
            proj_right = (ImageButton) findViewById(R.id.proj_right_arrow_btn);
            proj_up = (ImageButton) findViewById(R.id.proj_up_arrow_btn);
            proj_down = (ImageButton) findViewById(R.id.dvd_down_arrow_btn);
            proj_Vol_down = (ImageButton) findViewById(R.id.proj_valume_minus_btn);
            proj_Vol_up = (ImageButton) findViewById(R.id.proj_valume_plus_btn);
            proj_Ch_down = (ImageButton) findViewById(R.id.proj_zoom_minus_btn);
            proj_Ch_up = (ImageButton) findViewById(R.id.proj_zoom_plus_btn);
            proj_back = (ImageButton) findViewById(R.id.proj_page_up_btn);
            proj_mute = (ImageButton) findViewById(R.id.proj_page_down_btn);
            proj_home = (ImageButton) findViewById(R.id.proj_source_btn);


            sbPowerOnTv = (TextView) findViewById(R.id.btn_power_on_text);
            sbBackTv = (TextView) findViewById(R.id.proj_power_off_text);
            sbPowerOffTv = (TextView) findViewById(R.id.proj_valume_plus_btn_text);
            sbVolpTv = (TextView) findViewById(R.id.proj_valume_minus_btn_text);
            sbVolmTv = (TextView) findViewById(R.id.proj_menu_btn_text);
            sbMuteTv = (TextView) findViewById(R.id.proj_zoom_minus_btn_text);
            sbHomeTv = (TextView) findViewById(R.id.proj_zoom_plus_btn_text);
            sbGuideTv = (TextView) findViewById(R.id.proj_page_up_btn_text);
            sbChpTv = (TextView) findViewById(R.id.proj_source_btn_text);
            sbChmTv = (TextView) findViewById(R.id.proj_page_down_btn_text);

            setSettopBoxKey();


        } else if (remoteDetails.getRemoteType().equals("DVD")) {
            setContentView(R.layout.activity_user_remote_dvd1);
            dvd_power_on = (ImageButton) findViewById(R.id.dvd_power_on);
            dvd_power_off = (ImageButton) findViewById(R.id.dvd_power_off);
            dvd_menu = (ImageButton) findViewById(R.id.dvd_menu_btn);
            dvd_select = (Button) findViewById(R.id.dvd_select_btn);
            dvd_left = (ImageButton) findViewById(R.id.dvd_left_arrow_btn);
            dvd_right = (ImageButton) findViewById(R.id.dvd_right_arrow_btn);
            dvd_up = (ImageButton) findViewById(R.id.dvd_up_arrow_btn);
            dvd_down = (ImageButton) findViewById(R.id.dvd_down_arrow_btn);
            dvd_Vol_down = (ImageButton) findViewById(R.id.dvd_valume_minus_btn);
            dvd_Vol_up = (ImageButton) findViewById(R.id.dvd_valume_plus_btn);
            dvd_open_close = (ImageButton) findViewById(R.id.dvd_open_btn);
            dvd_play = (ImageButton) findViewById(R.id.dvd_play_btn);
            dvd_stop = (ImageButton) findViewById(R.id.dvd_stop_btn);
            dvd_back = (ImageButton) findViewById(R.id.dvd_back_btn);
            dvd_options = (ImageButton) findViewById(R.id.dvd_options_btn);


            sbPowerOnTv = (TextView) findViewById(R.id.btn_power_on_text);
            sbBackTv = (TextView) findViewById(R.id.dvd_power_off_text);
            sbPowerOffTv = (TextView) findViewById(R.id.dvd_valume_plus_btn_text);
            sbVolpTv = (TextView) findViewById(R.id.dvd_valume_minus_btn_text);

            sbVolmTv = (TextView) findViewById(R.id.dvd_menu_btn_text);

            sbMuteTv = (TextView) findViewById(R.id.dvd_play_btn_text);
            sbHomeTv = (TextView) findViewById(R.id.dvd_stop_btn_text);
            sbGuideTv = (TextView) findViewById(R.id.dvd_options_btn_text);
            sbChpTv = (TextView) findViewById(R.id.dvd_back_btn_text);
            sbChmTv = (TextView) findViewById(R.id.dvd_open_btn_text);

            setSettopBoxKey();


        } else if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
            setContentView(R.layout.rgb_led_strip_remote_layout);

            moodLight_power_on = (Button) findViewById(R.id.id_01);
            moodLight_power_off = (Button) findViewById(R.id.id_02);
            moodLight_02 = (Button) findViewById(R.id.id_03);
            moodLight_03 = (Button) findViewById(R.id.id_04);
            moodLight_04 = (Button) findViewById(R.id.id_05);
            moodLight_05 = (Button) findViewById(R.id.id_06);
            moodLight_06 = (Button) findViewById(R.id.id_07);
            moodLight_07 = (Button) findViewById(R.id.id_08);
            moodLight_08 = (Button) findViewById(R.id.id_09);
            moodLight_09 = (Button) findViewById(R.id.id_10);
            moodLight_10 = (Button) findViewById(R.id.id_11);
            moodLight_11 = (Button) findViewById(R.id.id_12);
            moodLight_12 = (Button) findViewById(R.id.id_13);
            moodLight_13 = (Button) findViewById(R.id.id_14);
            moodLight_14 = (Button) findViewById(R.id.id_15);
            moodLight_15 = (Button) findViewById(R.id.id_16);
            moodLight_16 = (Button) findViewById(R.id.id_17);
            moodLight_17 = (Button) findViewById(R.id.id_18);
            moodLight_18 = (Button) findViewById(R.id.id_19);
            moodLight_19 = (Button) findViewById(R.id.id_20);
            moodLight_20 = (Button) findViewById(R.id.id_21);
            moodLight_21 = (Button) findViewById(R.id.id_22);
            moodLight_22 = (Button) findViewById(R.id.id_23);
            moodLight_23 = (Button) findViewById(R.id.id_24);

        }


        try {

            if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {


                moodLight_power_on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_power_on.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_power_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_power_off.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_02.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_02.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_03.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_03.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_04.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_04.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_05.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_05.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_06.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_06.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_07.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_07.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_08.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_08.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_09.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_09.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_10.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_11.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_12.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });

                moodLight_13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_13.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_14.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_15.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_16.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_17.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_17.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_18.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_18.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_19.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_19.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_20.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_21.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_21.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });

                moodLight_22.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_22.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                moodLight_23.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {vibrationOnClickOfBtn();


                        if (remoteDetails.getRemoteType().equals("MOOD LIGHT")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + moodLight_23.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


            } else if (remoteDetails.getRemoteType().equals("AC")) {
                discreteSeekBar.setMin(18);
                discreteSeekBar.setMax(30);
                //For Air Conditioner

                setLongPressAcKey();

                ac_power_On_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + ac_power_On_Btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });

                ac_power_Off_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + ac_power_Off_Btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                fan_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + fan_btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                mode_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + mode_btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                comfort_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + comfort_btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                swing_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + swing_btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                fix_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + fix_btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


                preset_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        if (remoteDetails.getRemoteType().equals("AC")) {
                            message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + preset_btn.getTag()
                                    + "0" + "0F";
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }

                    }
                });


              /*  applianceName.setText(remoteDetails.getRemoteType());
                brandName.setText(remoteDetails.getBrand());
                if (applianceName.equals("AC")) {*/

                //}

                discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                    @Override
                    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                        int temp = seekBar.getProgress();
                        setTemperature(remoteDetails, temp);
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {
                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });
            } else if (remoteDetails.getRemoteType().equals("PROJECTOR")) {

               setClickOnProjectorKeys();

            } else if (remoteDetails.getRemoteType().equals("DVD")) {

                setLongPressKey();
                dvd_power_on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_power_on.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_power_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_power_off.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_menu.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_open_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_open_close.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_left.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_right.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_Vol_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_Vol_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_Vol_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_Vol_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });


                dvd_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_select.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });


                dvd_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_play.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_stop.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_back.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                dvd_options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + dvd_options.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });
            } else if (remoteDetails.getRemoteType().equals("TV")) {

                setLongPressKey();

                tv_power_on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_power_on.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_power_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_power_off.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_guide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_guide.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_select.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_left.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getId() + tv_right.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_Vol_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_Vol_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_Vol_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_Vol_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });


                tv_Ch_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_Ch_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });


                tv_Ch_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_Ch_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_back.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_mute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_mute.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                tv_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + tv_home.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });
            } else if (remoteDetails.getRemoteType().equals("SETTOP BOX")) {


                setLongPressKey();

                settop_power_on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_power_on.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_power_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_power_off.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_guide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_guide.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_select.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_left.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_right.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_Vol_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_Vol_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_Vol_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_Vol_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });


                settop_Ch_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_Ch_down.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });


                settop_Ch_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_Ch_up.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_back.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_mute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_mute.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });

                settop_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {vibrationOnClickOfBtn();

                        message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + settop_home.getTag()
                                + "0" + "0F";
                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {

                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                                    .child("command").child("message");
                            messageRef.setValue(message);
                        }

                        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                                && Customer.getCustomer().customerId != null) {


                            messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                                    .child(remoteDetails.getId()).child("command").child("toggle");
                            messageRef.setValue(1);
                        }
                    }
                });
            }


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public AlertDialog getRemoteDialog(final String irId, final String remoteId, final String remoteKey, final String remoteKeyTag, final TextView powerOnTextView) {


        final EditText remoteIdEt, remoteKeyEt, newRemoteKeyEt, remoteKeyTagEt, irIDEt;
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogSlideAnim);
        builder.setCancelable(false);
        // Get the layout inflater
        View view = inflater.inflate(R.layout.change_remote_key_layout, null);


        remoteIdEt = (EditText) view.findViewById(R.id.remoteId);
        remoteKeyEt = (EditText) view.findViewById(R.id.key);
        newRemoteKeyEt = (EditText) view.findViewById(R.id.new_key);
        remoteKeyTagEt = (EditText) view.findViewById(R.id.tag);
        irIDEt = (EditText) view.findViewById(R.id.irId);



        remoteIdEt.setHint("Remote Id : " + remoteId);
        irIDEt.setHint("IR Id : " + irId);

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

        if (RemoteKeysDataHelper.getRemoteKey(irId, remoteId, remoteKeyTag) != null) {
            key = RemoteKeysDataHelper.getRemoteKey(irId, remoteId, remoteKeyTag).getOldKey();
        }


        if (remoteKey != null && !remoteKey.equals("")) {
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
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
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

                            if (remoteKey != null && !remoteKey.equals("")) {
                                remoteKeyDetails.setOldKey(remoteKey);
                                key = "";
                            } else {
                                remoteKeyDetails.setOldKey(remoteKey);
                            }

                            for (int i = 0; i < remoteKeyDetailsArrayList.size(); i++) {
                                String oldTag =  remoteKeyDetailsArrayList.get(i).getOldTag();
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

                                remoteKeyReference.child(""+keyIndex.get(pos)).child("newKey").setValue(newRemoteKey);
//

                            } else {


                                RemoteKeyDetails keyDetailsObj = new RemoteKeyDetails();
                                keyDetailsObj.setIrId(irId);
                                keyDetailsObj.setRemoteId(remoteId);
                                keyDetailsObj.setOldTag(remoteKeyTag);
                                keyDetailsObj.setNewKey(newRemoteKey);
                                keyDetailsObj.setOldKey(remoteKeyDetails.getOldKey());

                                remoteKeyReference.child(""+(remoteKeyDetailsArrayList.size())).setValue(keyDetailsObj);


                            }
                            RemoteKeysDataHelper.addRemoteKey(remoteKeyDetails);

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        key = "";
                        dialog.dismiss();
                    }
                });
        return builder.create();

    }

    private void setTemperature(RemoteDetails remoteDetails, int temp) {

        if (Customer.getCustomer() != null && Customer.getCustomer() != null
                && Customer.getCustomer().customerId != null) {
            String s, message;
            switch (temp) {
                case 18:
                    txt_temprature.setText("18");
                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "08" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;

                case 19:
                    txt_temprature.setText("19");
                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "08" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 20:
                    txt_temprature.setText("20");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "09" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 21:
                    txt_temprature.setText("21");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "09" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 22:
                    txt_temprature.setText("22");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "10" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 23:
                    txt_temprature.setText("23");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "10" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 24:
                    txt_temprature.setText("24");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "11" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 25:
                    txt_temprature.setText("25");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "11" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 26:
                    txt_temprature.setText("26");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "12" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;
                case 27:
                    txt_temprature.setText("27");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "12" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;

                case 28:
                    txt_temprature.setText("28");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "13" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;

                case 29:
                    txt_temprature.setText("29");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "13" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;

                case 30:
                    txt_temprature.setText("30");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "14" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;

                default:
                    txt_temprature.setText("18");

                    message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId()
                            + "08" + "0" + "0F";
                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                    break;

            }
        }

    }


    /*
    * Change Ac Updated Keys (New Key)
    *
    * */
    public void setAcRemoteKey() {

        powerOnTextView = (TextView) findViewById(R.id.btn_power_on_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), powerOnTextView.getTag().toString()) != null) {
            powerOnTextView.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), powerOnTextView.getTag().toString()).getNewKey());
        }


        acPowerOffTV = (TextView) findViewById(R.id.btn_power_off_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acPowerOffTV.getTag().toString()) != null) {
            acPowerOffTV.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acPowerOffTV.getTag().toString()).getNewKey());
        }


        acSwingTv = (TextView) findViewById(R.id.swing_btn_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acSwingTv.getTag().toString()) != null) {
            acSwingTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acSwingTv.getTag().toString()).getNewKey());
        }


        acFeelTv = (TextView) findViewById(R.id.fix_btn_text);

        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acFeelTv.getTag().toString()) != null) {
            acFeelTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acFeelTv.getTag().toString()).getNewKey());
        }


        acModeTv = (TextView) findViewById(R.id.mode_btn_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acModeTv.getTag().toString()) != null) {
            acModeTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acModeTv.getTag().toString()).getNewKey());
        }

        acFanTv = (TextView) findViewById(R.id.fan_btn_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acFanTv.getTag().toString()) != null) {
            acFanTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acFanTv.getTag().toString()).getNewKey());
        }

        acQuiteTv = (TextView) findViewById(R.id.comfort_btn_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acQuiteTv.getTag().toString()) != null) {
            acQuiteTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acQuiteTv.getTag().toString()).getNewKey());
        }

        acTempTv = (TextView) findViewById(R.id.preset_btn_text);
        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acTempTv.getTag().toString()) != null) {
            acTempTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acTempTv.getTag().toString()).getNewKey());
        }

    }


    /*Attach Log Press Listener To Ac Keys (change Ac Key On  Long Press)*/
    public void setLongPressAcKey() {

        ////        acPowerOffTV,acSwingTv,acFeelTv,acModeTv,acFanTv,acQuiteTv,acTempTv
        powerOnTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), powerOnTextView.getText().toString(), powerOnTextView.getTag().toString(), powerOnTextView).show();

                return false;
            }
        });


        acPowerOffTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acPowerOffTV.getText().toString(), acPowerOffTV.getTag().toString(), acPowerOffTV).show();

                return false;
            }
        });


        acSwingTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acSwingTv.getText().toString(), acSwingTv.getTag().toString(), acSwingTv).show();

                return false;
            }
        });


        acFeelTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acFeelTv.getText().toString(), acFeelTv.getTag().toString(), acFeelTv).show();

                return false;
            }
        });


        acModeTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acModeTv.getText().toString(), acModeTv.getTag().toString(), acModeTv).show();

                return false;
            }
        });


        acFanTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acFanTv.getText().toString(), acFanTv.getTag().toString(), acFanTv).show();

                return false;
            }
        });


        acQuiteTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acQuiteTv.getText().toString(), acQuiteTv.getTag().toString(), acQuiteTv).show();

                return false;
            }
        });


        acTempTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), acTempTv.getText().toString(), acTempTv.getTag().toString(), acTempTv).show();

                return false;
            }
        });


    }


    ///////////////////////////////////    setTop Box Key ///////////////////////////////////////////////
    //sbPowerOnTv,sbPowerOffTv,sbVolpTv,sbVolmTv,sbMuteTv,sbHomeTv,sbGuideTv,sbChpTv,sbChmTv,sbBackTv;

    public void setSettopBoxKey() {


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbPowerOnTv.getTag().toString()) != null) {
            sbPowerOnTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbPowerOnTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbPowerOffTv.getTag().toString()) != null) {
            sbPowerOffTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbPowerOffTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbVolpTv.getTag().toString()) != null) {
            sbVolpTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbVolpTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbVolmTv.getTag().toString()) != null) {
            sbVolmTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbVolmTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbMuteTv.getTag().toString()) != null) {
            sbMuteTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbMuteTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbHomeTv.getTag().toString()) != null) {
            sbHomeTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbHomeTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbGuideTv.getTag().toString()) != null) {
            sbGuideTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbGuideTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbChpTv.getTag().toString()) != null) {
            sbChpTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbChpTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbChmTv.getTag().toString()) != null) {
            sbChmTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbChmTv.getTag().toString()).getNewKey());
        }


        if (RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbBackTv.getTag().toString()) != null) {
            sbBackTv.setText(RemoteKeysDataHelper.getRemoteKey(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbBackTv.getTag().toString()).getNewKey());
        }

    }


    /////////////  sETOPbOX,TV,DVD,MOOD,PROJECTOR.
    public void setLongPressKey() {


        sbPowerOnTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbPowerOnTv.getText().toString(), sbPowerOnTv.getTag().toString(), sbPowerOnTv).show();

                return false;
            }
        });


        sbPowerOffTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbPowerOffTv.getText().toString(), sbPowerOffTv.getTag().toString(), sbPowerOffTv).show();

                return false;
            }
        });


        sbVolpTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbVolpTv.getText().toString(), sbVolpTv.getTag().toString(), sbVolpTv).show();

                return false;
            }
        });


        sbVolmTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbVolmTv.getText().toString(), sbVolmTv.getTag().toString(), sbVolmTv).show();

                return false;
            }
        });


        sbMuteTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbMuteTv.getText().toString(), sbMuteTv.getTag().toString(), sbMuteTv).show();

                return false;
            }
        });


        sbHomeTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbHomeTv.getText().toString(), sbHomeTv.getTag().toString(), sbHomeTv).show();

                return false;
            }
        });


        sbChmTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbChmTv.getText().toString(), sbChmTv.getTag().toString(), sbChmTv).show();

                return false;
            }
        });


        sbBackTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbBackTv.getText().toString(), sbBackTv.getTag().toString(), sbBackTv).show();

                return false;
            }
        });

        sbGuideTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbGuideTv.getText().toString(), sbGuideTv.getTag().toString(), sbGuideTv).show();

                return false;
            }
        });


        sbChpTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                getRemoteDialog(remoteDetails.getIRId(), remoteDetails.getRemoteId(), sbChpTv.getText().toString(), sbChpTv.getTag().toString(), sbChpTv).show();

                return false;
            }
        });


    }


    int ss = 0;
    public void projectorONDimming(){



        //refdimmableToggle.setValue(1);
        /* refLevel5.setValue(3);
        refdimmableToggle.setValue(1);


        refLevel5.setValue(2);
        refdimmableToggle.setValue(1);


        refLevel5.setValue(1);
        refdimmableToggle.setValue(1);*/
        ss= 5;
        try{
            final Handler handler = new Handler();

            final Runnable r = new Runnable() {
                @Override
                public void run() {

                    for(; ss > 0 ; ss--){


                        refLevel5.setValue(ss);
                        refdimmableToggle.setValue(1);
                        handler.postDelayed(this,4000);

                    }
                    return;
                }
            };
            handler.post(r);

        }catch (Exception e){

        }

    }



    public void setClickOnProjectorKeys(){

        setLongPressKey();
       /* proj_power_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_power_on.getTag()
                        + "0" + "0F";

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }

                projectorONDimming();

            }
        });*/
        proj_power_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_power_on.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_power_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_power_off.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_guide.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_select.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_left.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_right.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_up.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_down.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_Vol_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_Vol_down.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_Vol_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_Vol_up.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });


        proj_Ch_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_Ch_down.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });


        proj_Ch_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_Ch_up.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_back.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_mute.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });

        proj_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {vibrationOnClickOfBtn();

                message = "I012" + remoteDetails.getIRId() + "1" + remoteDetails.getRemoteId() + proj_home.getTag()
                        + "0" + "0F";
                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {

                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId).child(remoteDetails.getId())
                            .child("command").child("message");
                    messageRef.setValue(message);
                }

                if (Customer.getCustomer() != null && Customer.getCustomer() != null
                        && Customer.getCustomer().customerId != null) {


                    messageRef = localRef.child("remote").child(Customer.getCustomer().customerId)
                            .child(remoteDetails.getId()).child("command").child("toggle");
                    messageRef.setValue(1);
                }
            }
        });


    }


    public void vibrationOnClickOfBtn(){
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }

}
