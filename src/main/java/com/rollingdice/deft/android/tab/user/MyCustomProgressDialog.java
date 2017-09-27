package com.rollingdice.deft.android.tab.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.model.Customer;

public class MyCustomProgressDialog extends ProgressDialog
{
  private AnimationDrawable animation;
  private DatabaseReference localRef;

  public static ProgressDialog ctor(Context context)
  {
    MyCustomProgressDialog dialog = new MyCustomProgressDialog(context, R.style.TransparentTheme);
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);


    return dialog;
  }

  public MyCustomProgressDialog(Context context) {
    super(context);
    localRef= GlobalApplication.firebaseRef;
  }

  public MyCustomProgressDialog(Context context, int theme)
  {
    super(context, theme);
    localRef= GlobalApplication.firebaseRef;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_custom_progress_dialog);
    try {

      ImageView la = (ImageView) findViewById(R.id.animation);
      //la.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
      animation = (AnimationDrawable) la.getBackground();
    }catch (Exception e)
    {
      DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
              .child(String.valueOf(System.currentTimeMillis()));
      String currentStatus = e.getMessage();
      errorRef.setValue(currentStatus);
    }
  }

  @Override
  public void show()
  {
    super.show();
    animation.start();
  }

  @Override
  public void dismiss() {
    super.dismiss();
    animation.stop();
  }
}
