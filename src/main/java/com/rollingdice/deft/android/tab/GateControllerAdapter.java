package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.GateController;
import com.rollingdice.deft.android.tab.model.WaterSprinklerDetails;

import java.util.ArrayList;

/**
 * Created by Rolling Dice on 3/24/2016.
 */
public class GateControllerAdapter extends  RecyclerView.Adapter<GateControllerAdapter.ViewHolder>
{
    Context context;
    private ArrayList<WaterSprinklerDetails> gateControllers;
    DatabaseReference localRef;

    public GateControllerAdapter(ArrayList<WaterSprinklerDetails> gateControllers, Context applicationContext)
    {
        this.gateControllers =gateControllers;
        this.context=applicationContext;
        localRef=GlobalApplication.firebaseRef;
    }

    public void addGateController(int position, WaterSprinklerDetails gateController) {

        gateControllers.add(position, gateController);
        notifyItemInserted(position);
    }

    public void removeGateController(GateController gateController)
    {
        int position = gateControllers.indexOf(gateController);
        gateControllers.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public GateControllerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try {

            final WaterSprinklerDetails gateController = gateControllers.get(position);
            holder.tvGateControllerHeader.setText(gateControllers.get(position).waterSprinklerName);


                holder.GateControllerIcon.setImageResource(R.drawable.water_sprinklers_icon);


            holder.GateControllerIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalApplication.GATE_CONTROLLER_EDIT_MODE = true;
                    GlobalApplication.UPDATE_GATE_CONTROLLER_POSITION = gateControllers.size() - Integer.valueOf(gateController.waterSprinklerId.
                            substring(gateController.waterSprinklerId.lastIndexOf('_') + 1));
                    Intent intent = new Intent(v.getContext().getApplicationContext(), GateControllerDetailActivity.class);
                    intent.putExtra("GATE_CONTROLLER_ID", gateController.waterSprinklerId);
                    intent.putExtra("GATE_CONTROLLER_NAME", gateController.waterSprinklerName);
                    intent.putExtra("CURTAIN_UPDATE_MODE", true);
                    v.getContext().startActivity(intent);
                }
            });
            holder.tvGateControllerId.setText(gateControllers.get(position).waterSprinklerId);
            holder.tvGateControllerId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Footer Clicked : ", Toast.LENGTH_SHORT).show();
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
        return gateControllers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvGateControllerHeader;
        public TextView tvGateControllerId;
        public ImageView GateControllerIcon;

        public ViewHolder(View v) {
            super(v);
            tvGateControllerHeader = (TextView) v.findViewById(R.id.sensor_name);
            tvGateControllerId = (TextView) v.findViewById(R.id.sensor_id);
            GateControllerIcon = (ImageView) v.findViewById(R.id.icon);
        }
    }
}
