package com.rollingdice.deft.android.tab.Voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rollingdice.deft.android.tab.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_start );

        findViewById( R.id.btn_hound_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(StartActivity.this, HoundVoiceSearchExampleActivity.class));
            }
        });

        findViewById( R.id.btn_basic_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(StartActivity.this, CustomSearchActivity.class));
            }
        });

        findViewById( R.id.btn_text_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(StartActivity.this, TextSearchActivity.class));
            }
        });

    }

}
