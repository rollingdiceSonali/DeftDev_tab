package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Sensor;

import java.util.ArrayList;

/**
 * Created by koushik on 09/06/15.
 */
public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    Context context;
    private ArrayList<Sensor> sensors;
    private DatabaseReference localRef;


    public SensorAdapter(ArrayList<Sensor> sensors, Context context)
    {
        this.sensors = sensors;
        this.context = context;
        localRef=GlobalApplication.firebaseRef;

    }

    public void addSensor(int position, Sensor sensor) {

        sensors.add(position, sensor);
        notifyItemInserted(position);
    }

    public void removeSensor(Sensor sensor) {
        int position = sensors.indexOf(sensor);
        sensors.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SensorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try {

            final Sensor sensor = sensors.get(position);
            holder.tvSensorHeader.setText(sensors.get(position).sensorName);
            holder.sensorIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalApplication.SENSOR_EDIT_MODE = true;
                    GlobalApplication.UPDATE_SENSOR_POSITION = Integer.valueOf(sensor.sensorId )+ 1;
                    Intent intent = new Intent(v.getContext().getApplicationContext(), SensorDetailActivity.class);
                    intent.putExtra("ROOM_ID", sensor.roomId);
                    intent.putExtra("SLAVE_ID", sensor.slaveId);
                    intent.putExtra("SENSOR_ID", sensor.sensorId);
                    intent.putExtra("SENSOR_NAME", sensor.sensorName);
                    intent.putExtra("SENSOR_UPDATE_MODE", true);
                    intent.putExtra("SENSOR_TYPE_ID", sensor.sensorTypeId);
                    v.getContext().startActivity(intent);
                }
            });
            holder.tvSensorId.setText(sensors.get(position).sensorId);
            holder.tvSensorId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Footer Clicked : ", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e)
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
    public int getItemCount() {
        return sensors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSensorHeader;
        public TextView tvSensorId;
        public ImageView sensorIcon;

        public ViewHolder(View v) {
            super(v);
            tvSensorHeader = (TextView) v.findViewById(R.id.sensor_name);
            tvSensorId = (TextView) v.findViewById(R.id.sensor_id);
            sensorIcon = (ImageView) v.findViewById(R.id.icon);
        }
    }
}