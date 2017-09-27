package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomDetails;

import java.util.Date;
import java.util.List;

/**
 * Created by koushik on 11/07/15.
 */
public class UserRoomAdapter extends RecyclerView.Adapter<UserRoomAdapter.ViewHolder> {
    private List<RoomDetails> roomList;
    private int cardBackgroundColor;
    private UserApplianceAdapter.AdapterCallback mAdapterCallback;

    DatabaseReference localRef;


    public UserRoomAdapter(List<RoomDetails> roomList, Context context, int cardBackgroundColor) {
        this.roomList = roomList;
        Context context1 = context;
        this.cardBackgroundColor = cardBackgroundColor;
        localRef = GlobalApplication.firebaseRef;
        this.mAdapterCallback=((UserApplianceAdapter.AdapterCallback)context);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {

        try {
            mAdapterCallback.onMethodCallback(true);

            final RoomDetails room = roomList.get(position);
            String s = roomList.get(position).getRoomName();
            holder.cvRoom.setCardBackgroundColor(cardBackgroundColor);
            holder.tvRoomName.setText(s);
            holder.tvRoomType.setText(roomList.get(position).getRoomType());
            holder.userRoomIcon.setImageResource(getImageResourceId(room.getRoomType()));

            DatabaseReference globalref=localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("globalOff");
            globalref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String flag=dataSnapshot.getValue().toString();
                    if(flag.equals("true"))
                    {
                        holder.tvLastMotionDetected.setVisibility(View.VISIBLE);
                        holder.tvLastMotionDetectedAt.setVisibility(View.VISIBLE);
                        holder.tvLastMotionDetectedAt.setText((new Date(System.currentTimeMillis())).toString());
                    }else
                    {
                        holder.tvLastMotionDetected.setVisibility(View.GONE);
                        holder.tvLastMotionDetectedAt.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

                }
            });

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapterCallback.onMethodCallback(false);

                }
            }, 3000);
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }



    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvRoomName,tvLastMotionDetected,tvLastMotionDetectedAt;
        public TextView tvRoomType;
        public ImageView userRoomIcon;
        public CardView cvRoom;
        public RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            cvRoom = (CardView) v.findViewById(R.id.card_view_room);
            userRoomIcon = (ImageView) v.findViewById(R.id.user_room_icon);
            tvRoomName = (TextView) v.findViewById(R.id.tv_user_room_name);
            tvRoomType = (TextView) v.findViewById(R.id.tv_user_room_type);
            tvLastMotionDetected= (TextView) v.findViewById(R.id.tv_last_motion_detected);
            tvLastMotionDetectedAt= (TextView) v.findViewById(R.id.tv_last_motion_detected_at);
            layout= (RelativeLayout) v.findViewById(R.id.user_gate_controller_layout);

        }
    }


    public int getImageResourceId(String roomType)
    {

        if(roomType.equals(GlobalApplication.ROOM_TYPE[0]))
            return R.drawable.background_bedroom;
        else if(roomType.equals(GlobalApplication.ROOM_TYPE[1]))
            return R.drawable.background_childroom;
        else if(roomType.equals(GlobalApplication.ROOM_TYPE[2]))
            return R.drawable.background_bathrrom;
        else if(roomType.equals(GlobalApplication.ROOM_TYPE[3]))
            return R.drawable.background_living_room;
        else if(roomType.equals(GlobalApplication.ROOM_TYPE[4]))
            return R.drawable.background_terrace;
        else if(roomType.equals(GlobalApplication.ROOM_TYPE[5]))
            return R.drawable.background_kitchen;
        else if(roomType.equals(GlobalApplication.ROOM_TYPE[6]))
            return R.drawable.background_dining;
        else
            return R.drawable.heart;
    }
}

