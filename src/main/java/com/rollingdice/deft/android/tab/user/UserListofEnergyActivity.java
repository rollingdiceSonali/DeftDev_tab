package com.rollingdice.deft.android.tab.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.datahelper.EnergyDataHelper;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.EnergyData;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Sudarsha on 7/29/2015.
 */
public class UserListofEnergyActivity extends AppCompatActivity {
    DatabaseReference ref;
    private HorizontalBarChart barChart;
    private PieChart pieChart;
    RadioButton radioButton;
    RadioGroup radioGroupEnergyConsumption;
    RadioGroup radioGroupEnergyConsumed;
    List<String> roomIdList;
    List<String> roomId, applianceNameList;
    List<RoomAppliance> appliancesList;
    EditText fromDateEtxt;
    EditText toDateEtxt;
    DatePickerDialog fromDatePickerDialog;
    DatePickerDialog toDatePickerDialog;
    SimpleDateFormat dateFormatter;
    String fromDate, toDate;
    private List<Double> roomWiseEnergyConsumed;
    private List<Double> applianceWiseEnergyConsumed;
    private SimpleArcDialog mDialog;

    RadioButton rdb_roomWiseConsumed, rdb_applinaceWiseConsumed;

    RadioButton roomWiseConsumption, applinaceWiseConsumption;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_energy);
        Intent intent = getIntent();
        ref = GlobalApplication.firebaseRef;
        mDialog = new SimpleArcDialog(this);
        ArcConfiguration configuration = new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC);
        mDialog.setConfiguration(configuration);
        mDialog.setCancelable(false);
        if (!isFinishing())
            mDialog.show();


        GetRoomAndApplianceDetails();
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lineargraphlayout);
        if (linearLayout != null) {
            linearLayout.removeAllViewsInLayout();
        }
        final LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        roomWiseConsumption = (RadioButton) findViewById(R.id.rd_btn_roomWise);

        applinaceWiseConsumption = (RadioButton) findViewById(R.id.rd_btn_applianceWise);

        rdb_roomWiseConsumed = (RadioButton) findViewById(R.id.rd_btn_consumed_roomWise);

        rdb_applinaceWiseConsumed = (RadioButton) findViewById(R.id.rd_btn_consumed_applianceWise);


        roomWiseConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applinaceWiseConsumption.setChecked(false);
                rdb_roomWiseConsumed.setChecked(false);
                rdb_applinaceWiseConsumed.setChecked(false);


                View view1 = inflater1.inflate(R.layout.bar_chart_layout, linearLayout);
                barChart = (HorizontalBarChart) view1.findViewById(R.id.barChart);


                List<Double> finalRoomWiswEnergy = new ArrayList<>();
                for (int i = 0; i < roomIdList.size(); i++) {
                    Double totalEnergy = 0.0;
                    String roomId = roomIdList.get(i);
                    for (int j = 0; j < appliancesList.size(); j++) {
                        RoomAppliance roomAppliance = appliancesList.get(j);
                        String r_id = roomAppliance.getRoomId();
                        int energy = Integer.parseInt(roomAppliance.getEnergy());

                        if (roomId.equals(r_id) && roomAppliance.isState()) {
                            totalEnergy = totalEnergy + energy;
                        }
                    }
                    finalRoomWiswEnergy.add(totalEnergy);
                }
                if (roomIdList.size() != 0 && finalRoomWiswEnergy.size() != 0) {
                    drawRoomWiseBarGraph(roomIdList, finalRoomWiswEnergy);
                } else {
                    Toast.makeText(UserListofEnergyActivity.this, "All The Applinaces Are Switched Off In Rooms", Toast.LENGTH_SHORT).show();
                }


            }
        });


        applinaceWiseConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomWiseConsumption.setChecked(false);
                rdb_roomWiseConsumed.setChecked(false);
                rdb_applinaceWiseConsumed.setChecked(false);


                View view2 = inflater1.inflate(R.layout.bar_chart_layout, linearLayout);
                barChart = (HorizontalBarChart) view2.findViewById(R.id.barChart);

                List<Double> finalApplianceWiswEnergy = new ArrayList<>();
                List<String> applinceNameList = new ArrayList<String>();

                for (int i = 0; i < appliancesList.size(); i++) {

                    RoomAppliance roomAppliance = appliancesList.get(i);
                    String roomId = roomAppliance.getRoomId();
                    String applianceName = roomAppliance.getApplianceName();
                    double energy = Integer.parseInt(roomAppliance.getEnergy());
                    if (roomAppliance.isState()) {
                        applinceNameList.add(applianceName);
                        finalApplianceWiswEnergy.add(energy);

                    }
                }
                if (applinceNameList.size() != 0 && finalApplianceWiswEnergy.size() != 0) {


                    drawApplianceWiseBarGraph(applinceNameList, finalApplianceWiswEnergy);
                } else {
                    Toast.makeText(UserListofEnergyActivity.this, "Currently No Appliances Are Switched ON ", Toast.LENGTH_SHORT).show();
                }


            }
        });


        rdb_roomWiseConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomWiseConsumption.setChecked(false);
                applinaceWiseConsumption.setChecked(false);
                rdb_applinaceWiseConsumed.setChecked(false);


                View view1 = inflater1.inflate(R.layout.bar_chart_layout, linearLayout);
                barChart = (HorizontalBarChart) view1.findViewById(R.id.barChart);
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.calender_layout, null);
                fromDateEtxt = (EditText) view.findViewById(R.id.etxt_fromdate);
                toDateEtxt = (EditText) view.findViewById(R.id.etxt_todate);
                setDateTimeField();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserListofEnergyActivity.this, R.style.DialogSlideAnim);
                alertDialog.setView(view);
                alertDialog.setTitle("Select Date");
                alertDialog.setCancelable(true);
                alertDialog.setNeutralButton("Show Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (fromDate != null && toDate != null) {
                            getRoomWiseEnergyConsumed(fromDate, toDate);
                            dialog.dismiss();
                        }else {
                            Toast.makeText(UserListofEnergyActivity.this, "Select Date First", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.show();


                fromDateEtxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fromDatePickerDialog.show();

                    }
                });
                toDateEtxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toDatePickerDialog.show();

                    }
                });

            }
        });


        rdb_applinaceWiseConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomWiseConsumption.setChecked(false);
                applinaceWiseConsumption.setChecked(false);
                rdb_roomWiseConsumed.setChecked(false);

                View view1 = inflater1.inflate(R.layout.bar_chart_layout, linearLayout);
                barChart = (HorizontalBarChart) view1.findViewById(R.id.barChart);
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.calender_layout, null);
                fromDateEtxt = (EditText) view.findViewById(R.id.etxt_fromdate);
                toDateEtxt = (EditText) view.findViewById(R.id.etxt_todate);
                setDateTimeField();

                // Toast.makeText(getApplicationContext(), "applianceWiseClicked", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserListofEnergyActivity.this, R.style.DialogSlideAnim);
                alertDialog.setView(view);
                alertDialog.setTitle("Select Date");
                alertDialog.setCancelable(true);
                alertDialog.setNeutralButton("Show Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (fromDate != null && toDate != null) {
                            getApplianceWiseEnergyConsumed(fromDate, toDate);
                            dialog.dismiss();

                        } else {

                            if (fromDate == null || toDate == null) {
                                Toast.makeText(UserListofEnergyActivity.this, "Select Date First", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                alertDialog.show();


                fromDateEtxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fromDatePickerDialog.show();

                    }
                });
                toDateEtxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toDatePickerDialog.show();

                    }
                });
            }
        });

    }

    private void getApplianceWiseEnergyConsumed(String fromDate, String toDate) {
        applianceWiseEnergyConsumed = new ArrayList<>();
        applianceNameList = new ArrayList<>();

        if (appliancesList.size() != 0 && appliancesList != null) {


            for (int i = 0; i < appliancesList.size(); i++) {
                RoomAppliance roomAppliance = appliancesList.get(i);
                String roomId = roomAppliance.getRoomId();
                String applinaceId = roomAppliance.getId();
                String app = roomAppliance.getApplianceName();
                //String id=roomAppliance.getId();
                applianceNameList.add(app);
                List<EnergyData> applinceWiseList = EnergyDataHelper.getAllApplinaceWiseData(applinaceId, roomId);
                double finalEnergyCount = 0;
                if (applinceWiseList.size() != 0 && applinceWiseList != null) {

                    for (int j = 0; j < applinceWiseList.size(); j++) {
                        EnergyData energyData = applinceWiseList.get(j);
                        boolean state = energyData.state;
                        String onTime = energyData.datetime;
                        int energy = energyData.energy;
                        if (state) {
                            if (applinceWiseList.size() != 1) {
                                for (int k = j + 1; k < applinceWiseList.size(); k++) {

                                    EnergyData data = applinceWiseList.get(j + 1);
                                    boolean s = data.state;
                                    String offTime = data.datetime;
                                    int e = data.energy;
                                    String applianceName = data.applianceName;
                                    //String id1=data.applianceId;
                                    if (!s) {
                                        if (applianceNameList.size() != 0 && !applianceNameList.contains(applianceName) && data.roomId.equals(roomId)) {
                                            applianceNameList.add(applianceName);

                                        } else if (applianceNameList.size() == 0) {
                                            applianceNameList.add(applianceName);
                                        }
                                        if (ValidateDateForRoom(fromDate, toDate, offTime)) {

                                            double difference = getDifference(onTime, offTime);

                                            double finalEnergy = (e * difference);
                                            finalEnergyCount = finalEnergyCount + finalEnergy;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Date Mismatch", Toast.LENGTH_SHORT);
                                        }

                                        break;
                                    }

                                }

                            }
                        }
                    }
                } else {
                    //Toast.makeText(UserListofEnergyActivity.this, "Data is not Available", Toast.LENGTH_SHORT).show();
                }
                if (finalEnergyCount < 0) {
                    finalEnergyCount = 0;
                }
                applianceWiseEnergyConsumed.add(finalEnergyCount);
            }
        }
        if (applianceNameList.size() == applianceWiseEnergyConsumed.size() && applianceNameList != null && applianceWiseEnergyConsumed != null) {


            drawApplianceWiseBarGraph(applianceNameList, applianceWiseEnergyConsumed);
        } else {
            Toast.makeText(UserListofEnergyActivity.this, "Improper Data", Toast.LENGTH_SHORT).show();
        }

    }

    private double getDifference(String onTime, String offTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss");
        double hour = 0.0000;
        try {
            Calendar cal = Calendar.getInstance();
            Date on = sdf.parse(onTime);
            cal.setTime(on);
            long time = cal.getTimeInMillis();

            Calendar cal1 = Calendar.getInstance();
            Date off = sdf.parse(offTime);
            cal1.setTime(off);
            long time1 = cal1.getTimeInMillis();
            long diff = time1 - time;
            if (diff > 5000) {
              /*  long diffHours = diff / (60 * 60 * 1000) % 24;*/
                hour = diff / (60 * 60 * 1000) % 24;
            } else {
                hour = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return hour;
    }

    private void setDateTimeField() {
        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy-hh:mm:ss");

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDate = dateFormatter.format(newDate.getTime());
                fromDateEtxt.setText(fromDate);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDate = dateFormatter.format(newDate.getTime());
                toDateEtxt.setText(toDate);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }

    private void getRoomWiseEnergyConsumed(String fromDate, String toDate) {
        // Toast.makeText(UserListofEnergyActivity.this, "Show Details", Toast.LENGTH_SHORT).show();
        roomWiseEnergyConsumed = new ArrayList<>();

        if (roomIdList.size() != 0 && roomIdList != null && appliancesList.size() != 0 && appliancesList != null) {

            for (int i = 0; i < roomIdList.size(); i++) {
                String roomId = roomIdList.get(i);
                double finalEnergyCount = 0;
                double energyCount = 0;
                for (int j = 0; j < appliancesList.size(); j++) {

                    if (appliancesList.get(j).getRoomId().equals(roomId)) {
                        RoomAppliance roomAppliance = appliancesList.get(j);
                        String applianceId = roomAppliance.getId();

                        List<EnergyData> dataList = EnergyDataHelper.getAllApplinaceWiseData(applianceId, roomId);

                        if (dataList.size() != 0 && dataList != null) {

                            for (int k = 0; k < dataList.size(); k++) {

                                EnergyData energyData = dataList.get(k);
                                boolean state = energyData.state;
                                String onTime = energyData.datetime;
                                if (state) {
                                    if (dataList.size() != 1) {
                                        for (int m = k + 1; m < dataList.size(); m++) {
                                            EnergyData data = dataList.get(k + 1);
                                            boolean s = data.state;
                                            String offTime = data.datetime;
                                            int e = data.energy;

                                            if (!s) {
                                                if (ValidateDateForRoom(fromDate, toDate, offTime)) {
                                                    double difference = getDifference(onTime, offTime);
                                                    double finalEnergy = (e * difference);
                                                    energyCount = energyCount + finalEnergy;
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Date Mismatch", Toast.LENGTH_SHORT);
                                                }
                                            }
                                            break;
                                        }

                                    }
                                }

                            }
                        } else {
                            //Toast.makeText(UserListofEnergyActivity.this, "Data Is Not Available", Toast.LENGTH_SHORT).show();
                        }


                    }

                }
                finalEnergyCount = finalEnergyCount + energyCount;
                roomWiseEnergyConsumed.add(finalEnergyCount);
                Toast.makeText(UserListofEnergyActivity.this, "" + finalEnergyCount, Toast.LENGTH_SHORT).show();


            }
            if (roomIdList.size() == roomWiseEnergyConsumed.size() && roomIdList != null && roomWiseEnergyConsumed != null) {

                drawRoomWiseBarGraph(roomIdList, roomWiseEnergyConsumed);
            } else {
                Toast.makeText(UserListofEnergyActivity.this, "Improper Data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean ValidateDateForRoom(String fromDate, String toDate, String currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy"); //edit here
        boolean result = false;
        try {
            Date from = sdf.parse(fromDate.substring(0, fromDate.length() - 9));
            Date to = sdf.parse(toDate.substring(0, toDate.length() - 9));
            Date current = sdf.parse(currentDate.substring(0, currentDate.length() - 9));

            int r = current.compareTo(to);
            int r1 = current.compareTo(from);

            if (r1 == -1 || r == 1) {
                result = false;
            } else {
                result = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return result;
    }


    private void GetRoomAndApplianceDetails() {
        DatabaseReference roomDetails = ref.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
        roomDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                roomIdList = new ArrayList<>();
                appliancesList = new ArrayList<RoomAppliance>();

                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    //Getting each room
                    String roomId = roomSnapshot.child("roomId").getValue(String.class);
                    if (roomId != null) {
                        roomIdList.add(roomId);
                        for (DataSnapshot type : roomSnapshot.getChildren()) {

                            for (DataSnapshot slave : type.getChildren()) {

                                if (slave.getRef().toString().contains("appliance")) {
                                    for (DataSnapshot applianc : slave.getChildren()) {

                                        RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                        appliancesList.add(roomAppliance);
                                    }
                                    break;
                                }
                            }
                        }

                    }


                }
                if (!isFinishing())
                    mDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                System.out.println("The read failed: " + DatabaseError.getMessage());
            }
        });
    }

    private void drawRoomWiseBarGraph(List<String> roomId, List<Double> roomEnergy) {
        try {
            BarData data = new BarData(roomId, getDataSet(roomEnergy));
            barChart.setData(data);
            barChart.setDescriptionColor(Color.WHITE);
            barChart.setBackgroundColor(Color.WHITE);
            barChart.setDrawValueAboveBar(true);
            barChart.getAxisLeft().setDrawLabels(false);
            barChart.getAxisRight().setDrawLabels(false);
            barChart.animateXY(2000, 2000);
            barChart.invalidate();
        } catch (Exception e) {
            DatabaseReference errorRef = ref.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);

        }
    }

    private void drawApplianceWiseBarGraph(List<String> applianceId, List<Double> applianceEnergy) {
        try {
            BarData data = new BarData(applianceId, getDataSet(applianceEnergy));
            barChart.setData(data);
            barChart.setBackgroundColor(Color.WHITE);
            barChart.setDrawValueAboveBar(true);
            barChart.getAxisLeft().setDrawLabels(false);
            barChart.getAxisRight().setDrawLabels(false);
            barChart.setDescription("");
            barChart.setDescriptionColor(Color.WHITE);

            barChart.animateXY(2000, 2000);
            barChart.invalidate();
        } catch (Exception e) {
            DatabaseReference errorRef = ref.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);

        }

    }

    private List<BarDataSet> getDataSet(List<Double> Energy) {
        ArrayList<BarDataSet> dataSets = null;
        try {

            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int i = 0; i < Energy.size(); i++) {
                entries.add(new BarEntry((Float.parseFloat(Energy.get(i).toString()) / 1000), i));
            }
            BarDataSet barDataSet = new BarDataSet(entries, "Energy Consumption in KWH");
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            dataSets = new ArrayList<>();
            dataSets.add(barDataSet);
        } catch (Exception e) {
            DatabaseReference errorRef = ref.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);

        }

        return dataSets;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}