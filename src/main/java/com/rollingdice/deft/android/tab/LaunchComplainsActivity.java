package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.model.ComplainsData;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.user.DividerItemDecoration;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rolling Dice on 3/31/2016.
 */
public class LaunchComplainsActivity extends Activity
{
    DatabaseReference localRef;
    LinearLayout linearLayout;
    Button newComplains,submit;
    MaterialEditText customerId,customerName,mobileNumber,complainsId,shortDescription,description,c_state;
    String shortDesc,desc,com_id,state;
    RecyclerView rvComplainsDetails;
    RecyclerView.Adapter adpterComplains;
    ArrayList<ComplainsData> compalinsList;
    SimpleArcDialog mDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_complains);
        mDialog = new SimpleArcDialog(this);
        ArcConfiguration configuration=new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
        mDialog.setConfiguration(configuration);
        mDialog.setCancelable(false);
        if(!isFinishing())
        mDialog.show();

        localRef=GlobalApplication.firebaseRef;

        final DatabaseReference compalinsData=localRef.child("complainsData");
        compalinsData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) 
            {
                compalinsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String cId = snapshot.child("CustomerId").getValue(String.class);
                    String cN = snapshot.child("CustomerName").getValue(String.class);
                    String cMN = snapshot.child("CustomerMobileNumber").getValue(String.class);
                    String com_Id = snapshot.child("CompalinsId").getValue(String.class);
                    String shortDec = snapshot.child("shortDec").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String submittedBy = snapshot.child("submittedBy").getValue(String.class);
                    String state = snapshot.child("state").getValue(String.class);
                    ComplainsData complainsData = new ComplainsData(cId, cN, cMN, com_Id, shortDec, description, submittedBy, state);
                    if(cId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        compalinsList.add(complainsData);
                    }
                }

                    adpterComplains = new UserComplainsAdapter(compalinsList, getApplicationContext());
                    rvComplainsDetails.setAdapter(adpterComplains);
                    if(!isFinishing())
                    mDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {

            }
        });


        linearLayout= (LinearLayout) findViewById(R.id.complains_form);

        newComplains= (Button)findViewById(R.id.btn_new_complains);

        newComplains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                rvComplainsDetails.setVisibility(View.GONE);

            }
        });

        customerId= (MaterialEditText)findViewById(R.id.edt_customer_id);
        customerId.setText(Customer.getCustomer().customerId);
        customerId.setTextColor(Color.WHITE);

        customerName= (MaterialEditText)findViewById(R.id.edt_customer_name);
        customerName.setText(Customer.getCustomer().name);
        customerName.setTextColor(Color.WHITE);

        mobileNumber= (MaterialEditText)findViewById(R.id.edt_mobile_number);
        mobileNumber.setText(Customer.getCustomer().customerPhone);
        mobileNumber.setTextColor(Color.WHITE);

        c_state= (MaterialEditText) findViewById(R.id.edit_state);
        c_state.setTextColor(Color.WHITE);


        complainsId=(MaterialEditText)findViewById(R.id.edt_complains_id);
        com_id=String.valueOf(System.currentTimeMillis());
        complainsId.setText(com_id);
        complainsId.setTextColor(Color.WHITE);

        shortDescription= (MaterialEditText)findViewById(R.id.edt_short_description);
        shortDescription.setTextColor(Color.WHITE);


        description= (MaterialEditText)findViewById(R.id.edt_description);
        description.setTextColor(Color.WHITE);


        rvComplainsDetails= (RecyclerView) findViewById(R.id.recycler_view_complains_details);
        RecyclerView.LayoutManager lmComplains = new GridLayoutManager(this, 1);
        rvComplainsDetails.addItemDecoration(new DividerItemDecoration(LaunchComplainsActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
        rvComplainsDetails.setLayoutManager(lmComplains);



        submit= (Button) findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shortDesc=shortDescription.getText().toString();
                desc=description.getText().toString();
                state=c_state.getText().toString();
                DatabaseReference complainsRef= localRef.child("complainsData").child(com_id);

                Map<String,Object> compalinsDetails=new  HashMap<>();
                compalinsDetails.put("CustomerId",Customer.getCustomer().customerId);
                compalinsDetails.put("CustomerName",Customer.getCustomer().name);
                compalinsDetails.put("CustomerMobileNumber",Customer.getCustomer().customerPhone);
                compalinsDetails.put("CompalinsId",com_id);
                compalinsDetails.put("shortDec",shortDesc);
                compalinsDetails.put("description",desc);
                compalinsDetails.put("submittedBy",Customer.getCustomer().name);
                compalinsDetails.put("state",state);
                complainsRef.setValue(compalinsDetails);
                linearLayout.setVisibility(View.GONE);
                rvComplainsDetails.setVisibility(View.VISIBLE);

            }
        });


    }
}
