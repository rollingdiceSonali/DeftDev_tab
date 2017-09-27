package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.HomeActivity;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 12/31/2015.
 */
public class UserCurtainAdapter extends RecyclerView.Adapter<UserCurtainAdapter.ViewHolder>
{
    private List<CurtainDetails> curtains;
    private int cardBackgroundColor;
    DatabaseReference localRef;
    AdapterCallback mAdapterCallback;

    public UserCurtainAdapter(List<CurtainDetails> curatins, Context applicationContext, int color)
    {
        this.curtains = curatins;
        Context context = applicationContext;
        this.cardBackgroundColor=color;
        localRef = GlobalApplication.firebaseRef;
        this.mAdapterCallback=((UserCurtainAdapter.AdapterCallback) context);
    }
    @Override
    public UserCurtainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_curtain, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserCurtainAdapter.ViewHolder holder, final int position)
    {
        try {
            final CurtainDetails curtainDetails = curtains.get(position);
            String s = curtains.get(position).getCurtainName();
            String r = curtains.get(position).getcurtainRoomName();

            Log.i("APPLIANCE_NAME", curtains.get(position).getCurtainName());
            holder.cvCurtain.setCardBackgroundColor(cardBackgroundColor);
            holder.tvCurtainName.setText(s);
            holder.tvCurtainRoomName.setText(r);

            if(curtains.get(position).getCurtainType().equals("AC"))
            {
                holder.btnReverse.setVisibility(View.VISIBLE);
                holder.btnStop.setVisibility(View.VISIBLE);
                holder.btnForward.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.discreteSeekBarCurtain.setVisibility(View.VISIBLE);
                holder.discreteSeekBarCurtain.setProgress(curtains.get(position).getcurtainLevel());
            }
            if(curtains.get(position).isToggle() == 1)
            {
                mAdapterCallback.onMethodCallback(true);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        mAdapterCallback.onMethodCallback(false);
                    }
                }, 2000);
            }

            holder.btnReverse.setOnClickListener(new View.OnClickListener()
            {
                 @Override
                 public void onClick(View v) {

                     if(GlobalApplication.isCurtainClickable) {
                         mAdapterCallback.onMethodCallback(true);
                         CurtainDetails curtainDetails1 = curtains.get(position);

                         DatabaseReference curtainRef = GlobalApplication.firebaseRef
                                 .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                 .child(curtainDetails.getCurtainId()).child("curtainLevel");

                         curtainRef.runTransaction(new Transaction.Handler() {
                             @Override
                             public Transaction.Result doTransaction(MutableData mutableData) {
                                 if (mutableData.getValue() == null) {
                                     mutableData.setValue(2);
                                 } else {
                                     mutableData.setValue(2);
                                 }
                                 return Transaction.success(mutableData);
                             }

                             @Override
                             public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                                 getCurtainList();

                             }
                         });

                         DatabaseReference dimableRef = GlobalApplication.firebaseRef
                                 .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                 .child(curtainDetails.getCurtainId()).child("toggle");
                         dimableRef.runTransaction(new Transaction.Handler() {
                             @Override
                             public Transaction.Result doTransaction(MutableData mutableData) {
                                 mutableData.setValue(1);
                                 return Transaction.success(mutableData);
                             }

                             @Override
                             public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                 getCurtainList();

                             }
                         });
                         mAdapterCallback.onMethodCallback(false);
                     }


                 }
                }
            );

            holder.btnForward.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View v) {

                         if(GlobalApplication.isCurtainClickable) {
                             mAdapterCallback.onMethodCallback(true);
                             CurtainDetails curtainDetails1 = curtains.get(position);

                             DatabaseReference curtainRef = GlobalApplication.firebaseRef
                                     .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                     .child(curtainDetails.getCurtainId()).child("curtainLevel");

                             curtainRef.runTransaction(new Transaction.Handler() {
                                 @Override
                                 public Transaction.Result doTransaction(MutableData mutableData) {
                                     if (mutableData.getValue() == null) {
                                         mutableData.setValue(1);
                                     } else {
                                         mutableData.setValue(1);
                                     }
                                     return Transaction.success(mutableData);
                                 }

                                 @Override
                                 public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                                     getCurtainList();

                                 }
                             });

                             DatabaseReference dimableRef = GlobalApplication.firebaseRef
                                     .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                     .child(curtainDetails.getCurtainId()).child("toggle");
                             dimableRef.runTransaction(new Transaction.Handler() {
                                 @Override
                                 public Transaction.Result doTransaction(MutableData mutableData) {
                                     mutableData.setValue(1);
                                     return Transaction.success(mutableData);
                                 }

                                 @Override
                                 public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                     getCurtainList();

                                 }
                             });
                             mAdapterCallback.onMethodCallback(false);
                         }


                     }
                 }
            );


            holder.btnStop.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View v) {

                         if(GlobalApplication.isCurtainClickable) {
                             mAdapterCallback.onMethodCallback(true);
                             CurtainDetails curtainDetails1 = curtains.get(position);

                             DatabaseReference curtainRef = GlobalApplication.firebaseRef
                                     .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                     .child(curtainDetails.getCurtainId()).child("curtainLevel");

                             curtainRef.runTransaction(new Transaction.Handler() {
                                 @Override
                                 public Transaction.Result doTransaction(MutableData mutableData) {
                                     if (mutableData.getValue() == null) {
                                         mutableData.setValue(0);
                                     } else {
                                         mutableData.setValue(0);
                                     }
                                     return Transaction.success(mutableData);
                                 }

                                 @Override
                                 public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                                     getCurtainList();

                                 }
                             });

                             DatabaseReference dimableRef = GlobalApplication.firebaseRef
                                     .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                     .child(curtainDetails.getCurtainId()).child("toggle");
                             dimableRef.runTransaction(new Transaction.Handler() {
                                 @Override
                                 public Transaction.Result doTransaction(MutableData mutableData) {
                                     mutableData.setValue(1);
                                     return Transaction.success(mutableData);
                                 }

                                 @Override
                                 public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                     getCurtainList();

                                 }
                             });
                             mAdapterCallback.onMethodCallback(false);
                         }


                     }
                 }
            );




            holder.discreteSeekBarCurtain.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                }




                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final DiscreteSeekBar seekBar)
                {
                    if(GlobalApplication.isCurtainClickable) {
                        mAdapterCallback.onMethodCallback(true);
                        CurtainDetails curtainDetails1 = curtains.get(position);

                        DatabaseReference curtainRef = GlobalApplication.firebaseRef
                                .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                .child(curtainDetails.getCurtainId()).child("curtainLevel");

                        curtainRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                if (mutableData.getValue() == null) {
                                    mutableData.setValue(seekBar.getProgress());
                                } else {
                                    mutableData.setValue(seekBar.getProgress());
                                }
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                                getCurtainList();

                            }
                        });

                        DatabaseReference dimableRef = GlobalApplication.firebaseRef
                                .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                                .child(curtainDetails.getCurtainId()).child("toggle");
                        dimableRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                mutableData.setValue(1);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                getCurtainList();

                            }
                        });
                        mAdapterCallback.onMethodCallback(false);
                    }


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

    private void getCurtainList()
    {
        DatabaseReference curtainDetails = localRef.child("curtain").child(Customer.getCustomer().customerId).child("roomdetails");

        curtainDetails.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<CurtainDetails> curtainList = new ArrayList<>();
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    //Getting each room
                    for (DataSnapshot type : roomSnapshot.getChildren()) {
                        if (type.getRef().toString().indexOf("curtainDetails") != 0) {
                            for (DataSnapshot curtain : type.getChildren()) {
                                CurtainDetails curtaindetails = curtain.getValue(CurtainDetails.class);
                                curtainList .add(curtaindetails);


                            }
                            break;
                        }

                    }

                }
                GlobalApplication.curtainList=curtainList;

            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                System.out.println("The read failed: " + DatabaseError.getMessage());

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return curtains.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCurtainName;
        public TextView tvCurtainRoomName;
        public DiscreteSeekBar discreteSeekBarCurtain;
        public ImageView userCurtainIcon;
        public CardView cvCurtain;
        public RelativeLayout curtainlayout;
        public ImageButton btnForward;
        public ImageButton btnStop;
        public ImageButton btnReverse;


        public ViewHolder(View v) {
            super(v);
            cvCurtain = (CardView) v.findViewById(R.id.card_view_curtains);
            userCurtainIcon= (ImageView) v.findViewById(R.id.user_curtain_icon);
            tvCurtainName = (TextView) v.findViewById(R.id.tv_user_curtain_name);
            tvCurtainRoomName = (TextView) v.findViewById(R.id.tv_user_curtain_room_name);
            discreteSeekBarCurtain = (DiscreteSeekBar) v.findViewById(R.id.discrete_seek_bar_curtain);
            curtainlayout= (RelativeLayout) v.findViewById(R.id.user_curtain_layout);
            btnForward = (ImageButton) v.findViewById(R.id.btnForward);
            btnStop = (ImageButton) v.findViewById(R.id.btnStop);
            btnReverse = (ImageButton) v.findViewById(R.id.btnReverse);
        }
    }

    public interface AdapterCallback
    {
        void onMethodCallback(boolean flag);
    }
}
