package com.rollingdice.deft.android.tab.user;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.LockDetails;

import java.util.ArrayList;
import java.util.List;

import in.arjsna.passcodeview.PassCodeView;

/**
 * Created by Rolling Dice on 6/10/2016.
 */
public class UserLockAdapter  extends RecyclerView.Adapter<UserLockAdapter.ViewHolder>
{
    private List<LockDetails> lockList = new ArrayList<>();
    DatabaseReference localRef;
    AdapterCallback mAdapterCallback;
    String lockPassword;
    Dialog dialog;
    Context context1;
    ViewHolder holder;

    public UserLockAdapter(Context context, List<LockDetails> lockList, int cardBackgroundColor) {
        context1 = context;
        this.lockList = lockList;
        int cardBackgroundColor1 = cardBackgroundColor;
        localRef= GlobalApplication.firebaseRef;
        this.mAdapterCallback=((UserLockAdapter.AdapterCallback)context);

    }


    public UserLockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_lock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        try {
            this.holder = holder;
            LockDetails lock = lockList.get(position);
            holder.tvLockName.setText(lockList.get(position).getLockName());

            if(lockList.get(position).isLockToggle() == 1)
            {
                mAdapterCallback.onMethodCallback(true);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        mAdapterCallback.onMethodCallback(false);
                    }
                }, 2000);
            }



            holder.toogleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {




                DatabaseReference lockRef=localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                lockRef.addValueEventListener(new ValueEventListener()
                {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    lockPassword = dataSnapshot.child("lockPassword").getValue(String.class);
                    showAuthenticationDialog(position);

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
                });

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
        return lockList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvLockName;
        public CardView cvLock;
        SwitchCompactAir toogleButton;
        public RelativeLayout layout;


        public ViewHolder(View v) {
            super(v);
            cvLock = (CardView) v.findViewById(R.id.card_view_lock);
            tvLockName= (TextView) v.findViewById(R.id.tv_user_lock_name);
            toogleButton= (SwitchCompactAir) v.findViewById(R.id.user_lock_toggle_btn);
            layout= (RelativeLayout) v.findViewById(R.id.user_lock_layout);

        }
    }
    public  interface AdapterCallback
    {
        void onMethodCallback(boolean flag);
    }



    private void showAuthenticationDialog(final int position)
    {
        try
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(context1);
            LayoutInflater inflater = (LayoutInflater)context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view=inflater.inflate(R.layout.lock_auth_layout,null);

            final PassCodeView passCodeView = (PassCodeView)view.findViewById(R.id.pass_code_view);
            TextView promptView = (TextView) view.findViewById(R.id.promptview);
            passCodeView.setDigitLength(lockPassword.length());
            passCodeView.setKeyTextColor(R.color.colorPrimary);

            builder .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            });

            passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
                @Override
                public void onTextChanged(String text)
                {
                    if(text.length()==lockPassword.length())
                    {
                        if (text.equals(lockPassword))
                        {
                            DatabaseReference lockRef= localRef.child("Locks").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lockDetails")
                                    .child(lockList.get(position).getLockId()).child("lockToggle");
                            lockRef.runTransaction(new Transaction.Handler()
                            {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData)
                                {
                                    mutableData.setValue(1);
                                    return Transaction.success(mutableData);


                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot)
                                {
                                    dialog.dismiss();

                                }
                            });
                        }
                        else
                        {
                            passCodeView.setError(true);
                        }
                    }



                }
            });



            builder.setView(view);
            builder.setCancelable(false);
            dialog=builder.create();
            // builder.create();
            dialog.show();
            Toast.makeText(context1, "Authentication Dialog", Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }

    }



}
