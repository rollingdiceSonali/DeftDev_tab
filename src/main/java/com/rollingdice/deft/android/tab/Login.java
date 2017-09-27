package com.rollingdice.deft.android.tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class Login extends AppCompatActivity {
    SimpleArcDialog dialog;
    private Dialog authDialog;
    //Spinner userProfileSpinner;
    GifDrawable imageView;

    EditText etUID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= 19) {
            //Set status bar to semi-transparent
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        dialog = new SimpleArcDialog(this);
        ArcConfiguration configuration = new ArcConfiguration(this);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        configuration.setText("Please wait...");
        configuration.setAnimationSpeedWithIndex(SimpleArcLoader.SPEED_SLOW);
        dialog.setConfiguration(configuration);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        try {
            imageView = new GifDrawable(getResources(), R.drawable.deft_gif);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //userProfileSpinner= (Spinner) findViewById(R.id.spinner);
        etUID = (EditText) findViewById(R.id.etCustomerPassword);
        try {
            //noinspection ConstantConditions
            findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null && !isFinishing()) {
                        dialog.show();
                        //startActivity(new Intent(Login.this, HomeActivity.class));
                    }
                    loginTo(etUID.getText().toString());

                }
            });
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = GlobalApplication.firebaseRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void loginTo(String toString) {
        if (toString.equals("APPLEBOY")) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            startActivity(new Intent(Login.this, AddUserDetails.class));

        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }


          /*  TextView myMsg = new TextView(this);
            myMsg.setText("Please Check your password and try again");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
*/

            authDialog = new AlertDialog.Builder(this)

                    .setMessage("Please Check your password and try again")
                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();




            authDialog.show();

        }
    }

}

