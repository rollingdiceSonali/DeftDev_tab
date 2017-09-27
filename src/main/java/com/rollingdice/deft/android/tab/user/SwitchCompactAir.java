package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;

/**
 * Created by Rolling Dice on 11/20/2015.
 */
public class SwitchCompactAir extends ToggleButton
{

    private Context context;
    private DatabaseReference localRef;

    public SwitchCompactAir(Context context1)
    {
        super(context1);
        context = context1;
        initView(context1);
        localRef= GlobalApplication.firebaseRef;
    }

    public SwitchCompactAir(Context context1, AttributeSet attributeset)
    {
        super(context1, attributeset);
        context = context1;
        initView(context1);
        localRef= GlobalApplication.firebaseRef;
    }

    public SwitchCompactAir(Context context1, AttributeSet attributeset, int i)
    {
        super(context1, attributeset, i);
        context = context1;
        initView(context1);
        localRef= GlobalApplication.firebaseRef;
    }

    private void playTune(int i)
    {
        try
        {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, i);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                final SwitchCompactAir this$0;

                public void onCompletion(MediaPlayer mediaplayer)
                {
                    mediaplayer.reset();
                    mediaplayer.release();
                }


                {

                    this$0 = SwitchCompactAir.this;
                   // super();

                }
            });
            mediaPlayer.start();
            return;
        }
        catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    public void initView(Context context1)
    {
        try {
            Typeface temp;
            temp = FontCache.get("fonts/regular.otf", context1);
            if (context1 != null) {
                setTypeface(temp);
            }
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    public void playTone()
    {
        if (isChecked())
        {
            playTune(0x7f060002);
            return;
        } else
        {
            playTune(0x7f060001);
            return;
        }
    }

    public void setChecked(boolean flag)
    {
        if (flag)
        {
            setTextColor(Color.rgb(36, 168, 19));
        } else
        {
            setTextColor(Color.rgb(90, 90, 90));
        }
        super.setChecked(flag);
    }
}

