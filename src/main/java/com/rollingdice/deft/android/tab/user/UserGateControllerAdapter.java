package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.CurtainDetailActivity;
import com.rollingdice.deft.android.tab.GateControllerDetailActivity;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Curtain;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.GateController;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.WaterSprinklerDetails;

import java.util.ArrayList;

/**
 * Created by Rolling Dice on 3/24/2016.
 */
public class UserGateControllerAdapter extends RecyclerView.Adapter<UserGateControllerAdapter.ViewHolder> {
    Context context;
    private ArrayList<WaterSprinklerDetails> gateControllers;
    DatabaseReference localRef;
    boolean toggled = false;
    SpinklerAdapterCallback mAdapterCallback;
    DatabaseReference applianceRef;
    WaterSprinklerDetails waterSpinklerObj;

    public UserGateControllerAdapter(Context applicationContext, ArrayList<WaterSprinklerDetails> gateControllers) {
        this.gateControllers = gateControllers;
        this.context = applicationContext;
        localRef = GlobalApplication.firebaseRef;
        this.mAdapterCallback=((UserGateControllerAdapter.SpinklerAdapterCallback)context);
    }

    public interface SpinklerAdapterCallback
    {
        void onMethodCallback(boolean flag);
    }

    public void addGateController(int position, WaterSprinklerDetails gateController) {

        gateControllers.add(position, gateController);
        notifyItemInserted(position);
    }

    public void removeGateController(WaterSprinklerDetails gateController) {
        int position = gateControllers.indexOf(gateController);
        gateControllers.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public UserGateControllerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gate_controller, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserGateControllerAdapter.ViewHolder holder, final int position) {
        try {

            waterSpinklerObj = gateControllers.get(position);

            int textSizeInSp = (int) context.getResources().getDimension(R.dimen.headline_text);
            holder.cvWaterSpinkler.setCardBackgroundColor(Color.parseColor("#4021272b"));
            holder.spinklerNameTV.setText(waterSpinklerObj.waterSprinklerName);
            holder.spinklerNameID.setText(waterSpinklerObj.waterSprinklerId);
            holder.spinklerNameID.setTextSize(textSizeInSp);
            holder.spinklerNameTV.setAllCaps(true);


            if (waterSpinklerObj.isState() && !toggled) {

                holder.switchCompactAir.setChecked(true);

            } else {
                holder.switchCompactAir.setChecked(false);
            }


            holder.switchCompactAir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                                         @Override
                                                                         public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                                                                             if (GlobalApplication.isApplianceClickable
                                                                                     && GlobalApplication.isOnDevicesClickable
                                                                                     && GlobalApplication.isOnFansClickable && GlobalApplication.isOnLightsClickable
                                                                                     && GlobalApplication.isOnSocketsClickable && GlobalApplication.isgateControllerClickable) {
                                                                                 mAdapterCallback.onMethodCallback(true);

                                                                                 Toast.makeText(context, "" + isChecked, Toast.LENGTH_SHORT).show();

                                                                                 applianceRef = localRef.child("waterSprinkler").child(Customer.getCustomer().customerId).child("waterSprinklerDetails")
                                                                                         .child(waterSpinklerObj.waterSprinklerId).child("toggle");
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
                                                                                        // getApplianceList();

                                                                                     }

                                                                                 });
                                                                             }
                                                                         }
                                                                     }
            );


            if(waterSpinklerObj.isToggle() == 1 && GlobalApplication.isApplianceClickable
                    && GlobalApplication.isOnDevicesClickable
                    && GlobalApplication.isOnFansClickable && GlobalApplication.isOnLightsClickable
                    &&GlobalApplication.isgateControllerClickable
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



        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }


    }

    @Override
    public int getItemCount() {
        return gateControllers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView spinklerNameTV;
        public TextView spinklerNameID;
        public ImageView GateControllerIcon;
        public com.rollingdice.deft.android.tab.user.SwitchCompactAir switchCompactAir;
        public CardView cvWaterSpinkler;

        public ViewHolder(View v) {
            super(v);
            spinklerNameTV = (TextView) v.findViewById(R.id.waterspinklerNameTV);
            spinklerNameID = (TextView) v.findViewById(R.id.waterspinklerIDTV);

            GateControllerIcon = (ImageView) v.findViewById(R.id.icon);
            cvWaterSpinkler = (CardView) v.findViewById(R.id.card_view_appliance);
            switchCompactAir = (com.rollingdice.deft.android.tab.user.SwitchCompactAir) v.findViewById(R.id.spinkler_toggle_btn);

        }
    }



}
