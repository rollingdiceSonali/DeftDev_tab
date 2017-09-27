package com.rollingdice.deft.android.tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Created by Rolling Dice on 3/28/2016.
 */
public class UserHelpActivity extends DialogFragment
{


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.help_layout, null);

        final Button userManual,complaint,trroubleshooting;

        userManual= (Button) view.findViewById(R.id.btn_user_manual);
        userManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "UserManual", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), HelpActivity.class));

            }
        });
     /*   trroubleshooting= (Button) view.findViewById(R.id.btn_troubleshooting);
        trroubleshooting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "TroubleShooting", Toast.LENGTH_SHORT).show();
            }
        });*/
        complaint= (Button) view.findViewById(R.id.btn_complaint);
        complaint.setOnClickListener(new View.OnClickListener() {
            public boolean flag = false;

            @Override
            public void onClick(View v)
            {
                final CheckBox checkBox;

                Toast.makeText(getActivity(), "Complaint", Toast.LENGTH_SHORT).show();

                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.alter_dialog_for_luanch_complains, null);
                checkBox = (CheckBox) view.findViewById(R.id.checkBox11);
                /*checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {

                    }
                });*/

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Wait");
                builder.setView(view);
                builder.setMessage("Before Lunch A Complains Please go Through Troubleshooting");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                        startActivity(new Intent(getContext(),LaunchComplainsActivity.class));
                       /* if (checkBox.isChecked())
                        {

                            RecyclerView rvComplainsDetails;

                            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.launch_complains, null);

                            rvComplainsDetails= (RecyclerView)view.findViewById(R.id.recycler_view_complains_details);
                            newComplains= (Button) view.findViewById(R.id.btn_new_complains);

                            newComplains.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    startActivity(new Intent(getContext(),LaunchComplainsActivity.class));
                                }
                            });



                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Launch Complains");
                            builder.setView(view);
                            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(getActivity(), "Complain Submitted", Toast.LENGTH_SHORT).show();

                                }
                            });
                            builder.show();

                        }*/


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                }).show();
            }


        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Help Dialog");
        builder.setMessage("Select Option");
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        return builder.create();
    }


}
