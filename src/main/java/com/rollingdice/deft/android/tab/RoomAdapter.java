package com.rollingdice.deft.android.tab;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Room;

import java.util.ArrayList;


/**
 * Created by koushik on 04/06/15.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder>
{

    private ArrayList<Room> rooms;
    private DatabaseReference localRef;


    public RoomAdapter(ArrayList<Room> myDataset) {
        rooms = myDataset;
        localRef=GlobalApplication.firebaseRef;
    }

    public void addRoom(int position, Room item) {
        rooms.add(position, item);
        notifyItemInserted(position);
    }

    public void removeRoom(Room item) {
        int position = rooms.indexOf(item);
        rooms.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Room room = rooms.get(position);
            final int p = position;
            holder.txtHeader.setText(rooms.get(position).roomName);
            holder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext().getApplicationContext(), RoomDetailsActivity.class);
                    intent.putExtra("ROOM_EDIT_MODE", true);
                    intent.putExtra("ROOM_ID", rooms.get(p).roomId);
                    intent.putExtra("ROOM_NAME", rooms.get(p).roomName);
                    v.getContext().startActivity(intent);
                }
            });
            holder.txtFooter.setText(rooms.get(position).roomType);
            holder.txtFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Footer Clicked : " + rooms.get(p), Toast.LENGTH_SHORT).show();
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
        return rooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtHeader;
        public TextView txtFooter;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.sensor_name);
            txtFooter = (TextView) v.findViewById(R.id.sensor_id);
        }
    }
}