package com.rollingdice.deft.android.tab.user;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.SceneDetails;

import java.util.ArrayList;


/**
 * Created by Rolling Dice on 9/25/2015.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder>
{
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<SceneDetails> mDataset;
    private static MyClickListener myClickListener;
    private DatabaseReference localRef= GlobalApplication.firebaseRef;


    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener


    {


        TextView S_name,txt1,txt2,txt_startTime,txt_endTime;
        TextView label,repeatingalarm;
        LinearLayout linearLayout;
        Switch aSwitch;
        Button btn_Sun,btn_Mon,btn_Tue,btn_Wed,btn_Thu,btn_Fri,btn_Sat;
        RelativeLayout relativeLayout;


            public DataObjectHolder(View itemView)
            {
                super(itemView);
                label = (TextView) itemView.findViewById(R.id.textView);
                S_name = (TextView) itemView.findViewById(R.id.textView1);
                linearLayout= (LinearLayout) itemView.findViewById(R.id.daylinrealayout);
                aSwitch= (Switch) itemView.findViewById(R.id.switch1);
                relativeLayout= (RelativeLayout) itemView.findViewById(R.id.relative_layout);
                btn_Sun= (Button) itemView.findViewById(R.id.button);
                btn_Mon= (Button) itemView.findViewById(R.id.button2);
                btn_Tue= (Button) itemView.findViewById(R.id.button3);
                btn_Wed= (Button) itemView.findViewById(R.id.button4);
                btn_Thu= (Button) itemView.findViewById(R.id.button5);
                btn_Fri= (Button) itemView.findViewById(R.id.button6);
                btn_Sat= (Button) itemView.findViewById(R.id.button7);
                txt1= (TextView) itemView.findViewById(R.id.txt1);
                txt2= (TextView) itemView.findViewById(R.id.txt2);
                txt_startTime=(TextView) itemView.findViewById(R.id.starttime);
                txt_endTime=(TextView) itemView.findViewById(R.id.endtime);
                repeatingalarm= (TextView) itemView.findViewById(R.id.txt_repeting);



                Log.i(LOG_TAG, "Adding Listener");
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick (View v)
            {
                //noinspection deprecation
                myClickListener.onItemClick(getPosition(), v);
            }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        MyRecyclerViewAdapter.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<SceneDetails> myDataset)
    {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        DataObjectHolder dataObjectHolder=null;
        try
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_layout, parent, false);
            dataObjectHolder = new DataObjectHolder(view);
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position)
    {
        try
        {



            if(position==0)
            {
                holder.linearLayout.setVisibility(View.INVISIBLE);
                holder.aSwitch.setVisibility(View.INVISIBLE);
                holder.S_name.setVisibility(View.INVISIBLE);
                holder.label.setText(mDataset.get(0).getSceneName());
                holder.label.setVisibility(View.VISIBLE);
                holder.txt1.setVisibility(View.INVISIBLE);
                holder.txt2.setVisibility(View.INVISIBLE);
                holder.txt_startTime.setVisibility(View.INVISIBLE);
                holder.txt_endTime.setVisibility(View.INVISIBLE);
                holder.repeatingalarm.setVisibility(View.INVISIBLE);

            }
            else
            {

                holder.txt1.setVisibility(View.VISIBLE);
                holder.txt2.setVisibility(View.VISIBLE);
                holder.txt_startTime.setVisibility(View.VISIBLE);
                holder.txt_startTime.setText(mDataset.get(position).getStartTime());
                holder.txt_endTime.setVisibility(View.VISIBLE);
                holder.txt_endTime.setText(mDataset.get(position).getEndTime());
                holder.label.setVisibility(View.INVISIBLE);
                holder.S_name.setText(mDataset.get(position).getSceneName());
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.aSwitch.setVisibility(View.VISIBLE);

                holder.aSwitch.setChecked(mDataset.get(position).isActivated() == 1 ? true : false);
                holder.aSwitch.setText((mDataset.get(position).isActivated()==0)?"DeActivated":"Activated");
                holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (GlobalApplication.isSchedularClickable) {
                            if (isChecked) {

                                DatabaseReference sceneRef = localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails")
                                        .child(mDataset.get(position).getSceneId()).child("isActivated");
                                sceneRef.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        mutableData.setValue(1);
                                   /* List<Scene> s= SceneDataHelper.getScene(mDataset.get(position).getS_Id());
                                    s.get(0).isActivated=true;
                                    s.get(0).save();*/
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                                        Toast.makeText(UserSchedularActivity.appcontext, mDataset.get(position).getSceneName() + " Schedular is Activated", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                DatabaseReference deActive = localRef.child("scene").child(Customer.getCustomer().customerId).child("sceneDetails")
                                        .child(mDataset.get(position).getSceneId()).child("isActivated");
                                deActive.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        mutableData.setValue(0);
                                   /* List<Scene> s= SceneDataHelper.getScene(mDataset.get(position).getS_Id());
                                    s.get(0).isActivated=false;
                                    s.get(0).save();
*/
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                                        Toast.makeText(UserSchedularActivity.appcontext, mDataset.get(position).getSceneName() + " Schedular  is DeActivated", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }


                        }
                    }
                });

               if(mDataset.get(position).isRepeating())
               {
                   holder.repeatingalarm.setText("Repeating Alarm");
               }
                else
               {
                   holder.repeatingalarm.setText("Non Repeating Alarm");
               }
                String ListOfday=mDataset.get(position).getListOfDays();

                String array[]=ListOfday.split(",");

                for (String anArray : array) {
                    switch (anArray) {
                        case "Sunday":
                            holder.btn_Sun.setTextColor(Color.GREEN);
                            break;
                        case "Monday":
                            holder.btn_Mon.setTextColor(Color.GREEN);
                            break;
                        case "Tuesday":
                            holder.btn_Tue.setTextColor(Color.GREEN);
                            break;
                        case "Wednsday":
                            holder.btn_Wed.setTextColor(Color.GREEN);
                            break;
                        case "Thursday":
                            holder.btn_Thu.setTextColor(Color.GREEN);
                            break;
                        case "Friday":
                            holder.btn_Fri.setTextColor(Color.GREEN);
                            break;
                        case "Saturday":
                            holder.btn_Sat.setTextColor(Color.GREEN);
                            break;
                        default:
                            break;
                    }

                }
            }

            if (position != 0)
            {
                holder.relativeLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
                {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        String Scene_Id = mDataset.get(position).getSceneId();
                        String Scene_Name = mDataset.get(position).getSceneName();
                        Toast.makeText(UserSchedularActivity.appcontext, "" + Scene_Id, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra("SceneId", Scene_Id);
                        intent.putExtra("SceneName", Scene_Name);
                        menu.add(0, v.getId(), 0, "Delete Scene").setIntent(intent);
                        menu.add(0, v.getId(), 0, "View Appliance Details").setIntent(intent);
                    }
                });


            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }

    public void addItem(SceneDetails dataObj, int index)
    {

        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index)
    {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }



}