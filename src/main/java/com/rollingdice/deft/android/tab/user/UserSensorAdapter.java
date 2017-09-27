package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.HomeActivity;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.SensorDetail;

import java.util.List;


/**
 * Created by koushik on 21/07/15.
 */
public class UserSensorAdapter extends RecyclerView.Adapter<UserSensorAdapter.ViewHolder> {
    private List<SensorDetail> sensors;
    DatabaseReference localRef;
    AdapterCallback mAdapterCallback;


    public UserSensorAdapter(List<SensorDetail> myDataset,Context context)
    {
        sensors = myDataset;
        localRef = GlobalApplication.firebaseRef;
        this.mAdapterCallback=((UserSensorAdapter.AdapterCallback)context);
    }

    @Override
    public UserSensorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_sensor, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        try {
            final SensorDetail sensor = sensors.get(position);
            holder.tvSensorName.setText(sensors.get(position).getSensorName());
            holder.tvRoomName.setText(getRoomName(sensors.get(position).getRoomId()));
            holder.tvSensorID.setText(sensors.get(position).getId());
            holder.tvSensorName.setAllCaps(true);

            if (sensor.isState()) {
                holder.userSensorToggleBtn.setChecked(true);
            } else {
                holder.userSensorToggleBtn.setChecked(false);
            }

            if(sensors.get(position).isToggle() == 1)
            {
                mAdapterCallback.onMethodCallback(true);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        mAdapterCallback.onMethodCallback(false);
                    }
                }, 2000);
            }



            holder.userSensorToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {



                    mAdapterCallback.onMethodCallback(true);
                    SensorDetail sensor = sensors.get(position);
                    String sensor_id = "0" + String.valueOf(sensor.getSensorTypeId()-1);
                    DatabaseReference applianceRef = GlobalApplication.firebaseRef
                            .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(sensor.getRoomId())
                            .child("sensors").child(sensor.getSlaveId()).child(sensor_id).child("toggle");


                    applianceRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            currentData.setValue(1);
                            return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                            //This method will be called once with the results of the transaction.
                        }
                    });
                    mAdapterCallback.onMethodCallback(false);


                }
            });
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);}



    }

    private String getRoomName(String roomId)
    {
        String roomName = "";
        try {

            for (int i = 0; i < GlobalApplication.roomList.size(); i++) {
                if (GlobalApplication.roomList.get(i).getRoomId().equals(roomId)) {
                    roomName = GlobalApplication.roomList.get(i).getRoomName().toUpperCase();
                    break;
                }
            }
        }catch (Exception e)
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
        return sensors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSensorName, tvRoomName,tvSensorID;
        public ImageView ivSensorImage;
        public SwitchCompactAir userSensorToggleBtn;

        public ViewHolder(View v) {
            super(v);
            tvSensorName = (TextView) v.findViewById(R.id.tv_user_sensor_name);
            tvRoomName = (TextView) v.findViewById(R.id.tv_user_sensor_room_name);
            ivSensorImage = (ImageView) v.findViewById(R.id.user_sensor_icon);
            userSensorToggleBtn = (SwitchCompactAir) v.findViewById(R.id.user_sensor_toggle_btn);
            tvSensorID = (TextView)v.findViewById(R.id.tv_user_sensor_Id);
        }
    }

    public  interface AdapterCallback
    {
        void onMethodCallback(boolean flag);
    }
}

