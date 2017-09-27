package com.rollingdice.deft.android.tab.user;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.HomeActivity;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koushik on 09/06/15.
 */
public class UserApplianceAdapter extends RecyclerView.Adapter<UserApplianceAdapter.ViewHolder> {

    private Context context;
    private List<RoomAppliance> appliances;
    private int cardBackgroundColor;
    public ProgressDialog progress;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    Uri soundUri;
    AdapterCallback mAdapterCallback;
    boolean toggled = false;
    private String activity;
    DatabaseReference localRef = GlobalApplication.firebaseRef;

    public UserApplianceAdapter(List<RoomAppliance> appliances, Context context, int cardBackgroundColor, String activityName) {
        this.appliances = appliances;
        this.context = context;
        this.cardBackgroundColor = cardBackgroundColor;
        boolean progressViewVisiblity = false;
        this.mAdapterCallback=((UserApplianceAdapter.AdapterCallback)context);
        this.activity=activityName;

    }

    @Override
    public UserApplianceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_appliance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int notificationId = (int) System.currentTimeMillis();

        final RoomAppliance appliance = appliances.get(position);

        String s = appliances.get(position).getApplianceName();
        Log.i("APPLIANCE_NAME", appliances.get(position).getApplianceName());
        holder.cvAppliance.setCardBackgroundColor(cardBackgroundColor);
        holder.tvApplianceName.setText(s);
        holder.tvApplianceRoomName.setText(getRoomName(appliances.get(position).getRoomId()));
        holder.tvApplianceName.setAllCaps(true);

        if (appliance.isState() && !toggled)

        {
            holder.userApplianceToggleBtn.setChecked(true);
        } else

        {
            holder.userApplianceToggleBtn.setChecked(false);
        }
        holder.userApplianceIcon.setImageResource(getImageResourceId(appliance.getApplianceType()));


            holder.appliancelayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
            {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
                {
                    if(activity.equals("UserApplianceAndSensorActivity"))
                    {

                        int shortcutId = GlobalApplication.shortcutApplianceList.size() + 1;

                        menu.add(position, v.getId(), shortcutId, "Create Shortcut");
                    }
                }
            }

            );


        if(appliances.get(position).isToggle() == 1 && GlobalApplication.isApplianceClickable
                && GlobalApplication.isOnDevicesClickable
                && GlobalApplication.isOnFansClickable && GlobalApplication.isOnLightsClickable
                && GlobalApplication.isOnSocketsClickable)
        {
            mAdapterCallback.onMethodCallback(true);

            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                   mAdapterCallback.onMethodCallback(false);
                }
            }, 4000);
        }

        holder.userApplianceToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                 if (GlobalApplication.isApplianceClickable
                         && GlobalApplication.isOnDevicesClickable
                         && GlobalApplication.isOnFansClickable && GlobalApplication.isOnLightsClickable
                         && GlobalApplication.isOnSocketsClickable) {
                     mAdapterCallback.onMethodCallback(true);

                     Toast.makeText(context, "" + isChecked, Toast.LENGTH_SHORT).show();

                     final RoomAppliance roomAppliance = appliances.get(position);

                     if (roomAppliance.isState() && roomAppliance.isDimmable()) {
                         for (int p = 0; p < holder.radioGroup.getChildCount(); p++) {
                             holder.radioGroup.getChildAt(p).setEnabled(true);
                         }

                     }


                     DatabaseReference applianceRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails")
                             .child(roomAppliance.getRoomId()).child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("toggle");
                     applianceRef.runTransaction(new Transaction.Handler() {
                         @Override
                         public Transaction.Result doTransaction(MutableData currentData) {
                             currentData.setValue(1);
                             toggled = true;
                             return Transaction.success(currentData);
                             //we can also abort by calling Transaction.abort()
                         }

                         @Override
                         public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                             GlobalApplication.clickPosition = position;
                             mAdapterCallback.onMethodCallback(false);
                             getApplianceList();

                         }

                     });
                 }
             }
         }
        );

        holder.radioGroup.setVisibility(appliances.get(position).isDimmable()?View.VISIBLE : View.GONE);
        if (appliances.get(position).isDimmable()&& !(appliances.get(position).getDimableValue()== 0))
        {
            ((RadioButton) holder.radioGroup.getChildAt(appliance.getDimableValue() - 1)).setChecked(true);
        }

        if (appliances.get(position).isState())
        {
            for (int p = 0; p < holder.radioGroup.getChildCount(); p++) {
                holder.radioGroup.getChildAt(p).setEnabled(true);
            }
        } else

        {
            for (int p = 0; p < holder.radioGroup.getChildCount(); p++) {
                holder.radioGroup.getChildAt(p).setEnabled(false);
            }
        }

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

             {
                 @Override
                 public void onCheckedChanged(RadioGroup group, final int checkedId) {
                     if (GlobalApplication.isApplianceClickable && GlobalApplication.isOnDevicesClickable
                             && GlobalApplication.isOnFansClickable && GlobalApplication.isOnLightsClickable
                             && GlobalApplication.isOnSocketsClickable) {
                         View selected = group.findViewById(checkedId);
                         mAdapterCallback.onMethodCallback(true);

                         RadioButton rd = (RadioButton) selected;
                         final int value = Integer.parseInt(rd.getText().toString());

                         RoomAppliance roomAppliance = appliances.get(position);


                         if (roomAppliance.isState()) {
                             DatabaseReference applianceRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails")
                                     .child(roomAppliance.getRoomId()).child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableValue");

                             applianceRef.runTransaction(new Transaction.Handler() {
                                 @Override
                                 public Transaction.Result doTransaction(MutableData currentData) {
                                     if (currentData.getValue() == null) {
                                         currentData.setValue(value);
                                     } else {
                                         currentData.setValue(value);
                                     }
                                     return Transaction.success(currentData);
                                     //we can also abort by calling Transaction.abort()
                                 }

                                 @Override
                                 public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                     //This method will be called once with the results of the transaction.
                                     getApplianceList();
                                     GlobalApplication.clickPosition = position;
                                 }
                             });

                             DatabaseReference dimableRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails")
                                     .child(roomAppliance.getRoomId()).child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableToggle");

                             dimableRef.setValue(1);
                             mAdapterCallback.onMethodCallback(false);
                         }

                     }
                 }
             });


        if (appliances.get(position).isState())
        {
            //holder.tvApplianceEnergy.setVisibility(View.VISIBLE);
            if(appliances.get(position).getEnergy() != null)
            {

                if(appliances.get(position).isDimmable())
                {
                    //String energy = String.valueOf((Integer.parseInt(appliances.get(position).getEnergy()) / 5) * appliances.get(position).getDimableValue());
                    holder.tvApplianceEnergy.setText(appliances.get(position).getEnergy().concat(" Watts"));
                }else {
                    holder.tvApplianceEnergy.setText(appliances.get(position).getEnergy().concat(" Watts"));
                }
            }

        } else {
            //holder.tvApplianceEnergy.setVisibility(View.INVISIBLE);
            holder.tvApplianceEnergy.setText(appliances.get(position).getEnergy().concat(" Watts"));
        }
    }

    private void getApplianceList()
    {
        try
        {
            DatabaseReference roomDetails =localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");

            roomDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot)
                {
                    ArrayList<RoomAppliance> applianceList = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        //Getting each room
                        String roomId = roomSnapshot.child("roomId").getValue(String.class);
                        if (roomId != null) {
                            for (DataSnapshot type : roomSnapshot.getChildren()) {

                                for(DataSnapshot slave : type.getChildren()) {
                                    if (slave.getRef().toString().contains("appliance")) {
                                        for (DataSnapshot applianc : slave.getChildren()) {

                                            RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                            applianceList.add(roomAppliance);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    GlobalApplication.applianceList=applianceList;
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


    private String getRoomName(String roomFinalID)
    {
        String roomName = "";

        try {
                for (int i = 0; i < GlobalApplication.roomList.size(); i++)
                {
                if (GlobalApplication.roomList.get(i).getRoomId().equals(roomFinalID)) {
                    roomName = GlobalApplication.roomList.get(i).getRoomName().toUpperCase();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

        return roomName;
    }


    @Override
    public int getItemCount() {
        return appliances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
           // private ProgressBar spinner;
        public TextView tvApplianceName;
        public TextView tvApplianceRoomName;
        public DiscreteSeekBar discreteSeekBarAppliance;
        public ImageView userApplianceIcon;
        public SwitchCompactAir userApplianceToggleBtn;
        public CardView cvAppliance;
        public RelativeLayout appliancelayout;
        public RadioGroup radioGroup;
        public RadioButton level1,level2,level3,level4,level5;
        public TextView tvApplianceEnergy;

        public ViewHolder(View v)
        {
            super(v);
            cvAppliance = (CardView) v.findViewById(R.id.card_view_appliance);
            userApplianceIcon = (ImageView) v.findViewById(R.id.user_appliance_icon);
            tvApplianceName = (TextView) v.findViewById(R.id.tv_user_appliance_name);
            tvApplianceRoomName = (TextView) v.findViewById(R.id.tv_user_appliance_room_name);
            userApplianceToggleBtn = (SwitchCompactAir) v.findViewById(R.id.user_appliance_toggle_btn);
            appliancelayout= (RelativeLayout) v.findViewById(R.id.user_appliance_layout);
            radioGroup= (RadioGroup) v.findViewById(R.id.radio_group);
            tvApplianceEnergy = (TextView) v.findViewById(R.id.tv_user_appliance_energy);

           // spinner=(ProgressBar)v.findViewById(R.id.progressBar1);
            level1= (RadioButton) v.findViewById(R.id.radio_button_1);
            level2= (RadioButton) v.findViewById(R.id.radio_button_1);
            level3= (RadioButton) v.findViewById(R.id.radio_button_1);
            level4= (RadioButton) v.findViewById(R.id.radio_button_1);
            level5= (RadioButton) v.findViewById(R.id.radio_button_1);
        }
    }

    public void updateCount(DatabaseReference firebaseRef , final int count)
    {
        try {
            firebaseRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        currentData.setValue((Long) currentData.getValue() + count);
                    }
                    return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                    //This method will be called once with the results of the transaction.
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
            return R.drawable.sockets_off;
        if(applianceType.equals("Music System"))
            return R.drawable.music_system_off;
        if(applianceType.equals("Sockets"))
            return R.drawable.sockets_off;

        return R.drawable.sockets_off;

    }
    public interface AdapterCallback
    {
        void onMethodCallback(boolean flag);
    }

}