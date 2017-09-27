package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Rolling Dice on 3/31/2016.
 */
public class MyActivity extends AppCompatActivity
{
    TextView txt;
    private static Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_layout);
        txt= (TextView) findViewById(R.id.txt_error);
        Bundle extras=getIntent().getExtras();
        String message=extras.getString("Error");
        txt.setText(message);
        this.setFinishOnTouchOutside(false);


    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        if (intent.getBooleanExtra("close_activity", false)) {
            this.finish();

        }
    }
}

