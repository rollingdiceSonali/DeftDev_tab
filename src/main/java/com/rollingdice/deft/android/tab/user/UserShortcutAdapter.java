package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.HomeActivity;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;

import java.util.List;

/**
 * Created by Rolling Dice on 12/19/2015.
 */
public class UserShortcutAdapter extends RecyclerView.Adapter<UserShortcutAdapter.ViewHolder>
{

    private Context context;
    private List<RoomAppliance> appliances;
    private int cardBackgroundColor;
    DatabaseReference localRef;
    AdapterCallback mAdapterCallback;


    public UserShortcutAdapter(List<RoomAppliance> appliances, Context context, int cardBackgroundColor)
    {
        this.appliances = appliances;
        this.context = context;
        this.cardBackgroundColor = cardBackgroundColor;
        localRef = GlobalApplication.firebaseRef;
        this.mAdapterCallback=((UserShortcutAdapter.AdapterCallback)context);

    }


    @Override
    public UserShortcutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_shortcut, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserShortcutAdapter.ViewHolder holder, final int position)
    {
        try {

            final RoomAppliance appliance = appliances.get(position);
            String s = appliances.get(position).getApplianceName();
            holder.cvAppliance.setCardBackgroundColor(cardBackgroundColor);
            holder.tvApplianceName.setText(s);
            holder.tvRoomName.setText(getRoomName(appliances.get(position).getRoomId()));
            if (appliance.isState()) {
                holder.userApplianceToggleBtn.setChecked(true);
            } else {
                holder.userApplianceToggleBtn.setChecked(false);
            }
            holder.userApplianceIcon.setImageResource(getImageResourceId(appliance.getApplianceType()));


            if(appliances.get(position).isToggle() == 1)
            {
                mAdapterCallback.onMethodCallback(true);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        mAdapterCallback.onMethodCallback(false);
                    }
                }, 2000);
            }


            //    holder.userApplianceToggleBtn.setEnabled(true);
            holder.userApplianceToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (GlobalApplication.isShortcutClickable)
                    {
                        mAdapterCallback.onMethodCallback(true);
                        Toast.makeText(context, "onToggle :" + isChecked, Toast.LENGTH_LONG).show();
                        final RoomAppliance roomAppliance = appliances.get(position);
                        if (roomAppliance.isState() && roomAppliance.isDimmable())
                        {
                        for (int p = 0; p < holder.radioGroup.getChildCount(); p++)
                        {
                            holder.radioGroup.getChildAt(p).setEnabled(true);
                        }

                        }

                        DatabaseReference applianceRef = GlobalApplication.firebaseRef
                            .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                            .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("toggle");


                        applianceRef.runTransaction(new Transaction.Handler()
                        {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            currentData.setValue(1);
                            return Transaction.success(currentData);
                            //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                            //This method will be called once with the results of the transaction.
                        }
                        });
                    mAdapterCallback.onMethodCallback(false);
                }
                }
            });

            holder.radioGroup.setVisibility(appliances.get(position).isDimmable() ? View.VISIBLE : View.GONE);
            if (appliances.get(position).isDimmable() && !(appliances.get(position).getDimableValue() == 0)) {
                ((RadioButton) holder.radioGroup.getChildAt(appliance.getDimableValue() - 1)).setChecked(true);
            }
            if (appliances.get(position).isState()) {

                for (int p = 0; p < holder.radioGroup.getChildCount(); p++) {
                    holder.radioGroup.getChildAt(p).setEnabled(true);
                }
            } else {
                for (int p = 0; p < holder.radioGroup.getChildCount(); p++) {
                    holder.radioGroup.getChildAt(p).setEnabled(false);
                }
            }

            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, final int checkedId) {
                    if (GlobalApplication.isShortcutClickable) {

                        //holder.spinner.setVisibility(View.VISIBLE);
                        View selected = group.findViewById(checkedId);
                        RadioButton rd = (RadioButton) selected;
                        final String value = rd.getText().toString();

                        RoomAppliance roomAppliance = appliances.get(position);
                        if (roomAppliance.isState()) {
                            DatabaseReference applianceRef = localRef
                                    .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                    .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableValue");

                            applianceRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData currentData) {
                                    if (currentData.getValue() == null) {
                                        currentData.setValue(value);
                                    } else {
                                        currentData.setValue(value);
                                    }
                                    return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                                }

                                @Override
                                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                    //This method will be called once with the results of the transaction.
                                }
                            });


                            DatabaseReference dimableRef = localRef
                                    .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                                    .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableToggle");
                            dimableRef.setValue("true");
                        }
                    }
                }

            });

            holder.user_shotcut_relative_layout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    int shortcutId = GlobalApplication.shortcutApplianceList.size();
                    menu.add(position, v.getId(), shortcutId, "Delete Shortcut");
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



    private String getRoomName(String roomFinalID)
    {
        String roomName = "";

        for (int i = 0; i< GlobalApplication.roomList.size(); i++)
        {
            if(GlobalApplication.roomList.get(i).getRoomId().equals(roomFinalID))
            {roomName = GlobalApplication.roomList.get(i).getRoomName().toUpperCase();
                break;}
        }

        return roomName;
    }


    @Override
    public int getItemCount()
    {
       return appliances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvApplianceName;
        public TextView tvApplianceType;
        public TextView tvRoomName;
        public RadioGroup radioGroup;
        public RadioButton level1,level2,level3,level4,level5;
        public ImageView userApplianceIcon;
        public SwitchCompactAir userApplianceToggleBtn;
        public CardView cvAppliance;
        public RelativeLayout user_shotcut_relative_layout;


        public ViewHolder(View v)
        {
            super(v);
            cvAppliance = (CardView) v.findViewById(R.id.card_view_appliance);
            userApplianceIcon = (ImageView) v.findViewById(R.id.user_appliance_icon);
            tvApplianceName = (TextView) v.findViewById(R.id.tv_user_appliance_name);
            tvRoomName = (TextView) v.findViewById(R.id.tv_user_room_name);
            userApplianceToggleBtn = (SwitchCompactAir) v.findViewById(R.id.user_appliance_toggle_btn);
            user_shotcut_relative_layout= (RelativeLayout) v.findViewById(R.id.user_shortcut_relative_layout);

            radioGroup= (RadioGroup) v.findViewById(R.id.radio_group);
            // spinner=(ProgressBar)v.findViewById(R.id.progressBar1);
            level1= (RadioButton) v.findViewById(R.id.radio_button_1);
            level2= (RadioButton) v.findViewById(R.id.radio_button_1);
            level3= (RadioButton) v.findViewById(R.id.radio_button_1);
            level4= (RadioButton) v.findViewById(R.id.radio_button_1);
            level5= (RadioButton) v.findViewById(R.id.radio_button_1);

        }
    }



    public int getImageResourceId(String applianceType)
    {
        if(applianceType.equals("Light"))
            return R.drawable.light_off;
        if(applianceType.equals("Fan"))
            return R.drawable.fan_off;
        if(applianceType.equals("TV"))
            return R.drawable.tv_off;
        if(applianceType.equals("Refrigerator"))
            return R.drawable.refrigerator_off;
        if(applianceType.equals("Washing Machine"))
            return R.drawable.washing_machine_off;
        if(applianceType.equals("Geyser"))
            return R.drawable.ic_add_circle;
        if(applianceType.equals("Music System"))
            return R.drawable.music_system_off;
        if(applianceType.equals("Sockets"))
            return R.drawable.sockets_off;

        return R.drawable.ic_add_circle;

    }
    public  interface AdapterCallback
    {
        void onMethodCallback(boolean flag);
    }

}
