package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.rollingdice.deft.android.tab.model.ComplainsData;

import java.util.ArrayList;


/**
 * Created by Rolling Dice on 3/31/2016.
 */
public class UserComplainsAdapter extends RecyclerView.Adapter<UserComplainsAdapter.ViewHolder>
{
    Context context;
    ArrayList<ComplainsData>list;
    public UserComplainsAdapter(ArrayList<ComplainsData> compalinsList, Context applicationContext)
    {
        this.context=applicationContext;
        this.list=compalinsList;
    }


    @Override
    public UserComplainsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_complains_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserComplainsAdapter.ViewHolder holder, int position)
    {
        holder.cust_name.setText(list.get(position).getCustimerName());
        holder.comp_Id.setText(list.get(position).getCompalinsId());
        holder.state.setText(list.get(position).getState());
        holder.shrtDesc.setText(list.get(position).getShortDec());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // private ProgressBar spinner;
        public TextView cust_name;
        public TextView comp_Id;
        public TextView state;
        public TextView shrtDesc;
        public CardView cvComplains;




        public ViewHolder(View v)
        {
            super(v);
            cust_name= (TextView) v.findViewById(R.id.customer_name);
            comp_Id= (TextView) v.findViewById(R.id.complains_id);
            state= (TextView) v.findViewById(R.id.state);
            cvComplains= (CardView) v.findViewById(R.id.card_view_complains);
            shrtDesc= (TextView) v.findViewById(R.id.shortdesc);

        }
    }
}
