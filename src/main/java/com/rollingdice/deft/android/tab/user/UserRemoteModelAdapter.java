package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RemoteDetails;

import java.util.List;

/**
 * Created by Rolling Dice on 12/19/2016.
 */
public class UserRemoteModelAdapter extends RecyclerView.Adapter<UserRemoteModelAdapter.ViewHolder> {

    private List<RemoteDetails> remoteDetailsList;
    private int cardBackgroundColor;
    private DatabaseReference localRef;


    public UserRemoteModelAdapter(List<RemoteDetails> remoteDetailsList, Context context) {
        this.remoteDetailsList = remoteDetailsList;
        Context context1 = context;
        localRef= GlobalApplication.firebaseRef;

        //this.cardBackgroundColor = cardBackgroundColor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_remote_model, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        try {

            final RemoteDetails remoteDetails = remoteDetailsList.get(position);
            String remoteName = remoteDetailsList.get(position).getRemoteType();
            String remoteRoomName = remoteDetailsList.get(position).getRoomName();
            final String remoteId = remoteDetailsList.get(position).getRemoteId();
            String remoteBrandName = remoteDetailsList.get(position).getBrand();

            holder.tvRemoteApplianceName.setText(remoteName);
            holder.tvRemoteRoomName.setText(remoteRoomName);
            holder.tvRemoteId.setText(remoteId);
            holder.tvRemoteBrand.setText(remoteBrandName);
            holder.userRemoteIcon.setImageResource(getImageResourceId(remoteDetails.getRemoteType()));


            holder.remotelayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
            {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    String irId = remoteDetailsList.get(position).getIRId();
                    String id = remoteDetailsList.get(position).getId();


                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra("remoteId", remoteId);
                    intent.putExtra("irID", irId);
                    intent.putExtra("ID", id);
                    intent.putExtra("remoteType", remoteDetailsList.get(position).getRemoteType());
                    menu.add(0, v.getId(), 0, "Delete Remote").setIntent(intent);
                    menu.add(0, v.getId(), 0, "Edit Remote Details").setIntent(intent);
                    menu.add(0, v.getId(), 0, "Edit Remote Configration").setIntent(intent);
                }
            });

        }catch (Exception e)
        {
            if( Customer.getCustomer() != null && Customer.getCustomer() != null
                    && Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    @Override
    public int getItemCount() {
        return remoteDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public TextView tvRemoteApplianceName;
        public TextView tvRemoteRoomName;
        public TextView tvRemoteId;
        public TextView tvRemoteBrand;
        public ImageView userRemoteIcon;
        public CardView cvRemote;
        public RelativeLayout remotelayout;


        public ViewHolder(View v) {
            super(v);
            cvRemote = (CardView) v.findViewById(R.id.card_view_user_remote);
            userRemoteIcon= (ImageView) v.findViewById(R.id.remote_icon);
            tvRemoteApplianceName = (TextView) v.findViewById(R.id.txt_applianceName);
            tvRemoteRoomName = (TextView) v.findViewById(R.id.txt_RoomName);
            remotelayout= (RelativeLayout) v.findViewById(R.id.user_remote_layout);
            tvRemoteId = (TextView) v.findViewById(R.id.txt_RemoteId);
            tvRemoteBrand = (TextView) v.findViewById(R.id.txt_Remote_Brand);


        }

    }

    public int getImageResourceId(String remoteType){

        if(remoteType.equals("TV"))
            return R.drawable.tv_2;
        else if(remoteType.equals("AC"))
            return R.drawable.ac_2;
        else if(remoteType.equals("PROJECTOR"))
            return R.drawable.projector_2;
        else if(remoteType.equals("DVD"))
            return R.drawable.dvdplayer_2;
        else if(remoteType.equals("Blueray"))
            return R.drawable.videoplayer;
        else if(remoteType.equals("SETTOP BOX"))
            return R.drawable.settopbox_2;
        else if(remoteType.equals("MOOD LIGHT"))
            return R.drawable.moodlight_2;
        else
            return R.drawable.videoplayer;
    }
}
