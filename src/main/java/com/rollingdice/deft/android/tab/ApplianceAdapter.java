package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Appliance;
import com.rollingdice.deft.android.tab.model.Customer;

import java.util.ArrayList;


/**
 * Created by koushik on 09/06/15.
 */
public class ApplianceAdapter extends RecyclerView.Adapter<ApplianceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Appliance> appliances;
    private DatabaseReference localRef;


    public ApplianceAdapter(ArrayList<Appliance> appliances, Context context) {
        this.appliances = appliances;
        this.context = context;
        localRef = GlobalApplication.firebaseRef;
    }

    public void addAppliance(int position, Appliance appliance) {
        appliances.add(position, appliance);
        notifyItemInserted(position);
    }

    public void removeAppliance(Appliance appliance) {
        int position = appliances.indexOf(appliance);
        appliances.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ApplianceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appliance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try {

            final Appliance appliance = appliances.get(position);

            String s = appliances.get(position).applianceName;
            Log.i("APPLIANCE_NAME", appliances.get(position).applianceName);
            holder.applianceIcon.setImageResource(getImageResourceId(appliance.applianceTypeId));
            holder.tvApplianceName.setText(s);
            holder.applianceIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalApplication.APPLIANCE_EDIT_MODE = true;
                    //GlobalApplication.UPDATE_APPLIANCE_POSITION = appliances.size() - Integer.valueOf(appliance.applianceId.substring(appliance.applianceId.lastIndexOf('_') + 1));
                    GlobalApplication.UPDATE_APPLIANCE_POSITION = appliances.size() - (Integer.valueOf(appliance.applianceId)+1);
                    Intent intent = new Intent(v.getContext().getApplicationContext(), ApplianceDetailsActivity.class);
                    intent.putExtra("APPLIANCE_TYPE_ID", appliance.applianceTypeId);
                    intent.putExtra("ROOM_ID", appliance.roomId);
                    intent.putExtra("APPLIANCE_ID", appliance.applianceId);
                    intent.putExtra("APPLIANCE_NAME", appliance.applianceName);
                    intent.putExtra("SLAVE_ID", appliance.slaveId);
                    intent.putExtra("APPLIANCE_UPDATE_MODE", true);
                    v.getContext().startActivity(intent);
                }
            });
            holder.tvApplianceID.setText(appliances.get(position).applianceId);
            holder.tvApplianceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Footer Clicked : ", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    @Override
    public int getItemCount() {
        return appliances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

            public TextView tvApplianceName;
            public TextView tvApplianceID;
            public ImageView applianceIcon;

            public ViewHolder(View v){
            super(v);
            tvApplianceName = (TextView) v.findViewById(R.id.tv_appliance_name);
            tvApplianceID = (TextView) v.findViewById(R.id.tv_appliance_id);
            applianceIcon = (ImageView) v.findViewById(R.id.icon);
            }
    }

    private int getImageResourceId(int applianceTypeId)
    {
        switch (applianceTypeId)
        {
            case 0: return R.mipmap.bulb;
            case 1: return R.mipmap.fan;
            case 2: return R.drawable.television;
            case 3: return R.drawable.refrigerator;
            case 4: return R.drawable.washing_machine;
            case 5: return R.drawable.heart;
            case 6: return R.drawable.heart;
            case 7: return R.mipmap.socket;
            case 8: return R.mipmap.ic_add_circle;
            default:return R.drawable.heart;

        }

    }
}