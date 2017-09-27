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

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Curtain;
import com.rollingdice.deft.android.tab.model.Customer;

import java.util.ArrayList;

/**
 * Created by Rolling Dice on 12/30/2015.
 */
public class  CurtainAdapter extends  RecyclerView.Adapter<CurtainAdapter.ViewHolder>
{

    Context context;
    private ArrayList<Curtain> curtains;
    DatabaseReference localRef;

    public CurtainAdapter(ArrayList<Curtain> curtains, Context applicationContext)
    {
        this.curtains=curtains;
        this.context=applicationContext;
        localRef=GlobalApplication.firebaseRef;
    }

    public void addCurtain(int position, Curtain curtain) {

        curtains.add(position, curtain);
        notifyItemInserted(position);
    }

    public void removeCurtain(Curtain curtain)
    {
        int position = curtains.indexOf(curtain);
        curtains.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public CurtainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.curtain, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CurtainAdapter.ViewHolder holder, int position)
    {
        try {

            final Curtain curtain = curtains.get(position);
            holder.tvCurtainHeader.setText(curtains.get(position).curtainName);
            holder.CurtainIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalApplication.CURTAIN_EDIT_MODE = true;
                    GlobalApplication.UPDATE_CURTAIN_POSITION = curtains.size() - Integer.valueOf(curtain.curtainId.substring(curtain.curtainId.lastIndexOf('_') + 1));
                    Intent intent = new Intent(v.getContext().getApplicationContext(), CurtainDetailActivity.class);
                    intent.putExtra("ROOM_ID", curtain.roomId);
                    intent.putExtra("CURTAIN_ID", curtain.curtainId);
                    intent.putExtra("CURTAIN_NAME", curtain.curtainName);
                    intent.putExtra("CURTAIN_UPDATE_MODE", true);
                    v.getContext().startActivity(intent);
                }
            });
            holder.tvCurtainId.setText(curtains.get(position).curtainId);
            holder.tvCurtainId.setOnClickListener(new View.OnClickListener() {
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
        return curtains.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCurtainHeader;
        public TextView tvCurtainId;
        public ImageView CurtainIcon;

        public ViewHolder(View v) {
            super(v);
            tvCurtainHeader = (TextView) v.findViewById(R.id.curtain_name);
            tvCurtainId = (TextView) v.findViewById(R.id.curtain_id);
            CurtainIcon = (ImageView) v.findViewById(R.id.icon);
        }
    }
}
