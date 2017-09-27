package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.SceneDetails;

import java.util.List;

/**
 * Created by Rolling Dice on 3/30/2016.
 */
public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder>
{
    Context context;
    List<SceneDetails> list;
    public ViewAdapter(List<SceneDetails> appLianceListWithTime, Context appcontext)
    {
        this.context=appcontext;
        this.list=appLianceListWithTime;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_appaliance_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.tvApplianceName.setText(list.get(position).getApplianceName());
        holder.tvStartTime.setText(list.get(position).getAppStartTime());
        holder.tvEndTime.setText(list.get(position).getAppEndTime());
        holder.tvDay.setText(list.get(position).getDay());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvApplianceName, tvStartTime,tvEndTime,tvDay;

        public ViewHolder(View v) {
            super(v);
            tvApplianceName = (TextView) v.findViewById(R.id.appliance_name);
            tvStartTime = (TextView) v.findViewById(R.id.app_start_time);
            tvEndTime = (TextView) v.findViewById(R.id.app_end_Time);
            tvDay= (TextView) v.findViewById(R.id.txt_day);

        }
    }

}
