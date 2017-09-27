package com.rollingdice.deft.android.tab.user;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.ConnectionChangeReceiver;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.SocketCommunicationService;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.modeDetails;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rolling Dice on 1/28/2016.
 */
public class UserModeAdapter extends RecyclerView.Adapter<UserModeAdapter.ViewHolder>
{

    private List<modeDetails> modeList = new ArrayList<>();
    private int cardBackgroundColor;
    private List<RoomAppliance> appliances = new ArrayList<>();
    private String switchOffAppliances = "";
    AdapterCallback mAdapterCallback;
    private Context context;

    DatabaseReference localRef;

    public UserModeAdapter(Context context,List<RoomAppliance> appliances, List<modeDetails> modeList, int cardBackgroundColor) {
        this.context = context;
        this.modeList = modeList;
        this.cardBackgroundColor = cardBackgroundColor;
        localRef = GlobalApplication.firebaseRef;
        this.appliances = appliances;
        this.mAdapterCallback=((UserModeAdapter.AdapterCallback)context);
    }

    public UserModeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_mode, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(UserModeAdapter.ViewHolder holder, final int position)
    {
        try {
            final modeDetails mode = modeList.get(position);
            holder.tvModeName.setText(modeList.get(position).getMoodName().toUpperCase());
            holder.cvMode.setCardBackgroundColor(cardBackgroundColor);
            holder.tv_mode_room_name.setText(modeList.get(position).getRoomName().toUpperCase());

            holder.tv_appliances_added.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> applianceName = new ArrayList<>();

                    modeDetails mode = modeList.get(position);
                    String name = mode.getApplianceName();
                    applianceName.add(name);


                    ListView listView;
                    LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = inflater.inflate(R.layout.mode_appliance_list, null);
                    listView = (ListView) view.findViewById(R.id.mode_appliance_list);
                    listView.setAdapter(new ArrayAdapter<>(v.getContext(), android.R.layout.simple_list_item_1, applianceName));

                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(v.getContext());

                    alertDialog.setView(view);
                    alertDialog.setTitle("Appliances");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                        }
                    });

                    alertDialog.show();

                }
            });

            holder.layout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
                {
                    int modeId = Integer.parseInt(mode.getModeId());
                    menu.add(position, v.getId(), modeId, "Delete Mode");

                }
            });

            if (mode.isState()) {
                holder.toogleButton.setChecked(true);
            } else {
                holder.toogleButton.setChecked(false);
            }

            if(modeList.get(position).isToggle()  == 1)
            {
                mAdapterCallback.onMethodCallback(true);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        mAdapterCallback.onMethodCallback(false);
                    }
                }, 2000);
            }


            holder.toogleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    switchOffAppliances = "";

                    if(GlobalApplication.isMoodClickable) {
                        mAdapterCallback.onMethodCallback(true);

                        String[] modeAppliances = mode.getApplianceId().split(",");



                        if(!mode.isState()){

                            for (int i = 0; i < appliances.size(); i++) {

                                if (appliances.get(i).getRoomId().equals(mode.getRoomId()) && appliances.get(i).isState()/* && !(Arrays.asList(modeAppliances).contains(appliances.get(i).getId()))*/) {
                                    switchOffAppliances = switchOffAppliances +appliances.get(i).getSlaveId()+ appliances.get(i).getId() + ",";
                                }

                            }

                            if (switchOffAppliances.isEmpty()) {
                                switchOffAppliances ="";
                                //switchOffAppliances = switchOffAppliances.substring(0, switchOffAppliances.length() - 1);
                            }


                            DatabaseReference offAppliances = localRef
                                    .child("mode").child(Customer.getCustomer().customerId).child("modeDetails")
                                    .child(String.valueOf(mode.getModeId())).child("offAppliances");

                            offAppliances.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData currentData) {
                                    currentData.setValue(switchOffAppliances);
                                    return Transaction.success(currentData);
                                    //we can also abort by calling Transaction.abort()
                                }

                                @Override
                                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                    //This method will be called once with the results of the transaction.
                                }
                            });


                        }


                        DatabaseReference applianceRef = localRef
                                .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(String.valueOf(mode.getModeId())).child("toggle");

                        applianceRef.runTransaction(new Transaction.Handler() {
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
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }


    @Override
    public int getItemCount()
    {
        return modeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvModeName,tv_mode_room_name,tv_appliances_added;
        public CardView cvMode;
        SwitchCompactAir toogleButton;
        public RelativeLayout layout;


        public ViewHolder(View v) {
            super(v);
            cvMode = (CardView) v.findViewById(R.id.card_view_mode);
            tvModeName = (TextView) v.findViewById(R.id.tv_user_mode_name);
            toogleButton= (SwitchCompactAir) v.findViewById(R.id.user_mode_toggle_btn);
            layout= (RelativeLayout) v.findViewById(R.id.user_mode_layout);
            tv_mode_room_name= (TextView) v.findViewById(R.id.tv_mode_room_name);
            tv_appliances_added= (TextView) v.findViewById(R.id.tv_appliances_added);


        }
    }
    public  interface AdapterCallback
    {
        void onMethodCallback(boolean flag);
    }
}