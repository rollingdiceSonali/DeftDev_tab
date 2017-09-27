package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.NotificationDetails;


import java.util.Collections;
import java.util.List;

/**
 * Created by Rolling Dice on 3/9/2016.
 */
public class UserNotificationAdapter extends RecyclerView.Adapter<UserNotificationAdapter.ViewHolder>
{
    private List<NotificationDetails> notificationList;
    private DatabaseReference localRef;

    public UserNotificationAdapter(Context context, List<NotificationDetails> notificationList)
    {
        Context context1 = context;
        Collections.sort(notificationList);
        this.notificationList= notificationList;
        localRef= GlobalApplication.firebaseRef;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try {
            NotificationDetails notify = notificationList.get(position);
            String n_time = notify.getNotificationTime();
            String n_text = notify.getNotificationText();
            holder.tvNotificationTime.setText(n_time);
            holder.tvNotificationText.setText(n_text);
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
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNotificationTime;
        public TextView tvNotificationText;

        public CardView cvNotification;

        public ViewHolder(View v)
        {
            super(v);
            cvNotification = (CardView) v.findViewById(R.id.card_view_notification);
            tvNotificationTime = (TextView) v.findViewById(R.id.tv_user_notification_time);
            tvNotificationText = (TextView) v.findViewById(R.id.tv_user_notification_text);


        }
    }
}
