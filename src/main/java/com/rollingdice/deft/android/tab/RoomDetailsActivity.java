package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rollingdice.deft.android.tab.Voice.StartActivity;
import com.rollingdice.deft.android.tab.datahelper.ApplianceDataHelper;
import com.rollingdice.deft.android.tab.datahelper.CurtainDataHelper;
import com.rollingdice.deft.android.tab.datahelper.GateControllerDataHelper;
import com.rollingdice.deft.android.tab.datahelper.RoomDataHelper;
import com.rollingdice.deft.android.tab.datahelper.SensorDataHelper;
import com.rollingdice.deft.android.tab.datahelper.WaterSprinklerDataHelper;
import com.rollingdice.deft.android.tab.model.Appliance;
import com.rollingdice.deft.android.tab.model.Curtain;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.GateController;
import com.rollingdice.deft.android.tab.model.Room;
import com.rollingdice.deft.android.tab.model.Sensor;
import com.rollingdice.deft.android.tab.model.WaterSprinklerDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoomDetailsActivity extends Activity {

    EditText etRoomName, etRoomId;
    RadioGroup radioGroup;
    Button btnSave, btnAddSensor, btnAddCurtain, btnAddAppliance, btnGateController;
    Spinner spinnerRoomType;
    String[] roomType = GlobalApplication.ROOM_TYPE;
    String[] slaveIDArray = {"1", "2", "3", "4"};
    String customerId = "";
    String slaveID = "1";
    boolean ROOM_EDIT_MODE = false;
    String updateRoomId, updateRoomName;
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private ArrayList<Curtain> curtains = new ArrayList<>();
    private ArrayList<WaterSprinklerDetails> gateControllers = new ArrayList<>();
    private Context context;
    int index = 0;

    private ArrayList<Appliance> appliances = new ArrayList<>();
    private RecyclerView.Adapter adapterSensor, adapterCurtain, adapterAppliance, adapterGateController;

    DatabaseReference localRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
        localRef = GlobalApplication.firebaseRef;
        context = this;

        try {

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                ROOM_EDIT_MODE = extras.getBoolean("ROOM_EDIT_MODE");
                updateRoomId = extras.getString("ROOM_ID");
                updateRoomName = extras.getString("ROOM_NAME");
                customerId = extras.getString("CUSTOMER_ID");
                GlobalApplication.CUSTOMER_ID = extras.getString("CUSTOMER_ID");
            }

            etRoomName = (EditText) findViewById(R.id.et_room_name);
            etRoomId = (EditText) findViewById(R.id.et_room_id);
            spinnerRoomType = (Spinner) findViewById(R.id.spinner_room_type);
            btnSave = (Button) findViewById(R.id.btn_add_room);
            btnAddSensor = (Button) findViewById(R.id.btn_add_sensor);
            btnAddCurtain = (Button) findViewById(R.id.btn_add_curtain);
            btnAddAppliance = (Button) findViewById(R.id.btn_add_appliance);
            btnGateController = (Button) findViewById(R.id.btn_add_gateController);
            radioGroup = (RadioGroup) findViewById(R.id.rgSlave);

            etRoomId.setText((ROOM_EDIT_MODE) ? updateRoomId : generateAutoRoomId());
            etRoomName.setText((ROOM_EDIT_MODE) ? updateRoomName : "");

            btnSave.setText((ROOM_EDIT_MODE) ? "UPDATE" : "ADD ROOM");


            setUpSensorUI();
            setUpApplianceUI();
            setUpCurtainUI();
            setUpGateControllerUI();
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomType);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRoomType.setAdapter(spinnerArrayAdapter);
            if(ROOM_EDIT_MODE){

                for(int x = 0;x<GlobalApplication.ROOM_TYPE.length;x++){
                    if(GlobalApplication.ROOM_TYPE[x].equals( RoomDataHelper.getRoomDetaildFromId(updateRoomId).getRoomType() )){
                        index = x;
                        break;
                    }
                }

                spinnerRoomType.setSelection(index);

            }

            //globalOff
            DatabaseReference globalOffRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child("globalOff");
            Map<String, Object> globalOffFlag = new HashMap<>();
            globalOffFlag.put("globalOffState", false);
            globalOffFlag.put("toggle", 0);
            globalOffRef.setValue(globalOffFlag);

            //Service reset
            DatabaseReference serviceReset = localRef.child("serviceReset").child(Customer.getCustomer().customerId);
            Map<String, Object> serviceResetFlag = new HashMap<>();
            serviceResetFlag.put("serviceResetFlag", false);
            serviceReset.setValue(serviceResetFlag);

            //set notification parameters
            //energy
            DatabaseReference energyNotification = localRef.child("notification").child("energy").child(Customer.getCustomer().customerId);
            Map<String, Object> energyNotificationDetails = new HashMap<>();
            energyNotificationDetails.put("" + System.currentTimeMillis(), "DEFT energy notification is configured!!!");
            energyNotificationDetails.put("isEnergyNotificationSubscribe", false);
            energyNotification.setValue(energyNotificationDetails);

            //error
            DatabaseReference errorNotification = localRef.child("notification").child("error").child(Customer.getCustomer().customerId);
            Map<String, Object> errorNotificationDetails = new HashMap<>();
            errorNotificationDetails.put("" + System.currentTimeMillis(), "DEFT error notification is configured!!!");
            errorNotificationDetails.put("isErrorNotificationSubscribe", false);
            errorNotification.setValue(errorNotificationDetails);

            //schedular

            DatabaseReference schedularNotification = localRef.child("notification").child("schedular").child(Customer.getCustomer().customerId);
            Map<String, Object> schedularNotificationDetails = new HashMap<>();
            schedularNotificationDetails.put("" + System.currentTimeMillis(), "DEFT schedular notification is configured!!!");
            schedularNotificationDetails.put("isSchedularNotificationSubscribe", false);
            schedularNotification.setValue(schedularNotificationDetails);

            ///Global Off

            DatabaseReference globalOffNotification = localRef.child("notification").child("globalOff").child(Customer.getCustomer().customerId);
            Map<String, Object> globalOffNotificationDetails = new HashMap<>();
            globalOffNotificationDetails.put("" + System.currentTimeMillis(), "DEFT globalOff notification is configured!!!");
            globalOffNotification.setValue(globalOffNotificationDetails);


            // EnergyInfo
            DatabaseReference energyConsumptiondRef = localRef.child("Energy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("consumed");
            Map<String, Object> energyConsumptionDetails = new HashMap<>();
            energyConsumptionDetails.put("roomWiseConsumed", false);
            energyConsumptionDetails.put("applinceWiseConsumed", false);
            energyConsumptiondRef.updateChildren(energyConsumptionDetails);


            //Information
            DatabaseReference informationRef = localRef.child("notification").child("Information").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, Object> informationDetails = new HashMap<>();
            informationDetails.put("" + System.currentTimeMillis(), "DEFT Info is configured!!!");
            informationDetails.put("isInformationNotificationSubscribe", false);
            informationRef.updateChildren(informationDetails);


            //MotionDetection
            DatabaseReference motionDetectionRef = localRef.child("notification").child("motionDetection").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, Object> motionDetails = new HashMap<>();
            motionDetails.put("" + System.currentTimeMillis(), "DEFT Motion Detection is configured!!!");
            motionDetails.put("isMotionDetectionSubscribe", false);
            motionDetectionRef.updateChildren(motionDetails);

            //LockNotification
            DatabaseReference lockRef = localRef.child("notification").child("lock").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, Object> details = new HashMap<>();
            details.put("" + System.currentTimeMillis(), "DEFT Lock Notification is configured!!!");
            details.put("isLockNotificationSubscribe", false);
            lockRef.updateChildren(details);


            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String item =spinnerRoomType.getSelectedItem().toString();
                    RoomDataHelper.addRoom(etRoomId.getText().toString(),
                            spinnerRoomType.getSelectedItem().toString(),
                            etRoomName.getText().toString());

                    String Data = spinnerRoomType.getSelectedItem().toString();

                    //Create FireBase url
                    DatabaseReference roomDetailsRef = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(etRoomId.getText().toString());
                    Map<String, Object> roomDetails = new HashMap<>();
                    roomDetails.put("roomType", spinnerRoomType.getSelectedItem().toString());
                    roomDetails.put("roomName", etRoomName.getText().toString());
                    roomDetails.put("roomId", etRoomId.getText().toString());

                    roomDetailsRef.updateChildren(roomDetails);

                    if (!ROOM_EDIT_MODE) {

                        DatabaseReference roomControllermapping = localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomController").child(etRoomId.getText().toString());

                        Map<String, String> roomControllerDetails = new HashMap<>();
                        roomControllerDetails.put("1", "false");
                        roomControllerDetails.put("2", "false");
                        roomControllerDetails.put("3", "false");
                        roomControllerDetails.put("4", "false");

                        roomControllermapping.setValue(roomControllerDetails);
                        DatabaseReference roomApplianceCountRef = localRef.child("rooms").child(Customer.getCustomer().customerId)
                                .child("roomdetailsCount")
                                .child("roomCount");

                        roomApplianceCountRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                if (currentData.getValue() == null) {
                                    currentData.setValue(1);
                                } else {
                                    currentData.setValue((Long) currentData.getValue() + 1);
                                }
                                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.
                            }
                        });
                    }

                    btnSave.setText("UPDATE");
                    btnSave.setEnabled(false);
                    btnAddSensor.setEnabled(true);
                    btnAddCurtain.setEnabled(true);
                    btnAddAppliance.setEnabled(true);
                    btnGateController.setEnabled(true);
                }
            });

            btnAddSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!SensorDataHelper.isSonsorAddedtoSlave(slaveID,etRoomId.getText().toString())){

                        Intent intent = new Intent(v.getContext().getApplicationContext(), SensorDetailActivity.class);
                        intent.putExtra("ROOM_ID", etRoomId.getText().toString());
                        intent.putExtra("SLAVE_ID", slaveID);
                        intent.putExtra("SENSOR_ID", generateAutoSensorId());
                        intent.putExtra("SENSOR_NAME", "");
                        intent.putExtra("CUSTOMER_ID", customerId);
                        intent.putExtra("SENSOR_UPDATE_MODE", false);
                        intent.putExtra("SENSOR_TYPE_ID", 0);
                        GlobalApplication.UPDATE_SENSOR_POSITION = 0;
                        startActivity(intent);

                     }else {

                            new AlertDialog.Builder(context)
//Please Check your password and try again
                                    .setMessage("Sensor Already added To Slave Id : "+slaveID)
                                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();


                    }

                }
            });

            btnAddCurtain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext().getApplicationContext(), CurtainDetailActivity.class);
                    intent.putExtra("ROOM_ID", etRoomId.getText().toString());
                    intent.putExtra("SLAVE_ID", slaveID);
                    intent.putExtra("ROOM_NAME", etRoomName.getText().toString());
                    intent.putExtra("CURTAIN_ID", generatedAutoCurtainID());
                    intent.putExtra("CURTAIN_NAME", "");
                    intent.putExtra("CUSTOMER_ID", customerId);
                    intent.putExtra("CURTAIN_UPDATE_MODE", false);
                    GlobalApplication.UPDATE_CURTAIN_POSITION = 0;
                    startActivity(intent);

                }
            });

            btnGateController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext().getApplicationContext(), GateControllerDetailActivity.class);
                    intent.putExtra("WaterSprinklerName", "");
                    intent.putExtra("WaterSprinklerId", generatedSprinklerId());
                    intent.putExtra("roomID", etRoomId.getText().toString());
                    intent.putExtra("roomName", etRoomName.getText().toString() );
                    intent.putExtra("slaveId", slaveID);
                    intent.putExtra("SPRINKLER_UPDATE_MODE", false);
                   /* intent.putExtra("toggle", 0);
                    intent.putExtra("state", false);*/

                    GlobalApplication.UPDATE_GATE_CONTROLLER_POSITION = 0;
                    startActivity(intent);
                }
            });

            btnAddAppliance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext().getApplicationContext(), ApplianceDetailsActivity.class);
                    intent.putExtra("ROOM_ID", etRoomId.getText().toString());
                    intent.putExtra("SLAVE_ID", slaveID);
                    intent.putExtra("APPLIANCE_ID", generateAutoApplianceId());
                    intent.putExtra("APPLIANCE_NAME", "");
                    intent.putExtra("CUSTOMER_ID", customerId);
                    intent.putExtra("APPLIANCE_UPDATE_MODE", false);

                    GlobalApplication.UPDATE_APPLIANCE_POSITION = 0;
                    startActivity(intent);
                }
            });


            findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(RoomDetailsActivity.this, Customer.getCustomer().name, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RoomDetailsActivity.this, RoomListActivity.class));
                    finish();
                }
            });
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.slaveOne:
                if (checked) {
                    slaveID = slaveIDArray[0];
                    Toast.makeText(getApplicationContext(), "slaveOne", Toast.LENGTH_SHORT).show();
                    setUpSensorUI();
                    setUpCurtainUI();
                    setUpApplianceUI();
                    setUpGateControllerUI();
                    break;
                }
            case R.id.slaveTwo:
                if (checked) {
                    slaveID = slaveIDArray[1];
                    setUpSensorUI();
                    setUpCurtainUI();
                    setUpApplianceUI();
                    setUpGateControllerUI();
                    Toast.makeText(getApplicationContext(), "slaveTwo", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.slaveThree:
                if (checked) {
                    slaveID = slaveIDArray[2];
                    Toast.makeText(getApplicationContext(), "slaveThree", Toast.LENGTH_SHORT).show();
                    setUpSensorUI();
                    setUpCurtainUI();
                    setUpApplianceUI();
                    setUpGateControllerUI();
                    break;
                }
            case R.id.slaveFour:
                if (checked) {
                    slaveID = slaveIDArray[3];
                    Toast.makeText(getApplicationContext(), "slaveFour", Toast.LENGTH_SHORT).show();
                    setUpSensorUI();
                    setUpCurtainUI();
                    setUpApplianceUI();
                    setUpGateControllerUI();
                    break;
                }
        }
    }

    private void setUpApplianceUI() {
        try {
            RecyclerView rvAppliance = (RecyclerView) findViewById(R.id.recycler_view_appliance);
            RecyclerView.LayoutManager lmAppliance = new LinearLayoutManager(this);
            rvAppliance.setLayoutManager(lmAppliance);
            appliances.clear();
            for (Appliance appliance : ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(etRoomId.getText().toString(), slaveID)) {
                appliances.add(appliance);
            }

            adapterAppliance = new ApplianceAdapter(appliances, getApplicationContext());
            rvAppliance.setAdapter(adapterAppliance);
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void setUpSensorUI() {
        try {

            RecyclerView rvSensor = (RecyclerView) findViewById(R.id.recycler_view_sensor);
            RecyclerView.LayoutManager lmSensor = new LinearLayoutManager(this);
            rvSensor.setLayoutManager(lmSensor);
            sensors.clear();
            for (Sensor sensor : SensorDataHelper.getAllSensors(updateRoomId,slaveID)) {
                sensors.add(sensor);
            }
            adapterSensor = new SensorAdapter(sensors, getApplicationContext());
            rvSensor.setAdapter(adapterSensor);
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void setUpCurtainUI() {
        try {
            RecyclerView rvCurtain = (RecyclerView) findViewById(R.id.recycler_view_curtain);
            RecyclerView.LayoutManager lmCurtain = new LinearLayoutManager(this);
            rvCurtain.setLayoutManager(lmCurtain);
            curtains.clear();
            for (Curtain curtain : CurtainDataHelper.getAllCurtainsByRoomId(etRoomId.getText().toString())) {
                curtains.add(curtain);
            }

            adapterCurtain = new CurtainAdapter(curtains, getApplicationContext());
            rvCurtain.setAdapter(adapterCurtain);
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }


    }

    private void setUpGateControllerUI() {
        try {
            RecyclerView rvGateController = (RecyclerView) findViewById(R.id.recycler_view_GATE_CONTROLLER);
            RecyclerView.LayoutManager lmGateController = new LinearLayoutManager(this);
            rvGateController.setLayoutManager(lmGateController);
            gateControllers.clear();
            for (WaterSprinklerDetails gateController : WaterSprinklerDataHelper.getAllWaterSprinklerByRoomIdAndSlaveWise(
                    etRoomId.getText().toString(), slaveID)) {

                gateControllers.add(gateController);
            }
            adapterGateController = new GateControllerAdapter(gateControllers, getApplicationContext());
            rvGateController.setAdapter(adapterGateController);
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (GlobalApplication.SENSOR_ADD_MODE) {
                updateSensorList(GlobalApplication.UPDATE_SENSOR_POSITION);
                GlobalApplication.SENSOR_ADD_MODE = false;
            }

            if (GlobalApplication.APPLIANCE_ADD_MODE) {
                updateApplianceList(GlobalApplication.UPDATE_APPLIANCE_POSITION);
                GlobalApplication.APPLIANCE_ADD_MODE = false;
            }
            if (GlobalApplication.CURTAIN_ADD_MODE) {
                updateCurtainList(GlobalApplication.UPDATE_CURTAIN_POSITION);
                GlobalApplication.CURTAIN_ADD_MODE = false;

            }
            if (GlobalApplication.GATE_CONTROLLER_ADD_MODE) {
                updateGateControllerList(GlobalApplication.UPDATE_GATE_CONTROLLER_POSITION);
                GlobalApplication.GATE_CONTROLLER_ADD_MODE = false;
            }
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }

    }


    private void updateSensorList(long position) {
        try {

            if (GlobalApplication.SENSOR_EDIT_MODE) {
                sensors.set((int) position, SensorDataHelper.getSensor(GlobalApplication.SENSOR_ID,GlobalApplication.ROOM_ID,slaveID,
                        GlobalApplication.SENSOR_TYPE_ID));
                GlobalApplication.SENSOR_EDIT_MODE = false;
            } else {
                sensors.add((int) position, SensorDataHelper.getSensor(GlobalApplication.SENSOR_ID,GlobalApplication.ROOM_ID,slaveID,
                        GlobalApplication.SENSOR_TYPE_ID));
            }
            adapterSensor.notifyDataSetChanged();
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }

    }

    private void updateCurtainList(long position) {
        try {


            if (GlobalApplication.CURTAIN_EDIT_MODE) {
                curtains.set((int) position, CurtainDataHelper.getCurtain(GlobalApplication.CURTAIN_ID));
                GlobalApplication.CURTAIN_EDIT_MODE = false;
            } else {
                curtains.add((int) position, CurtainDataHelper.getCurtain(GlobalApplication.CURTAIN_ID));
            }
            adapterCurtain.notifyDataSetChanged();
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }

    }

    private void updateGateControllerList(long updateGateControllerPosition) {
        try {
            if (GlobalApplication.GATE_CONTROLLER_EDIT_MODE) {
                gateControllers.set((int) updateGateControllerPosition, WaterSprinklerDataHelper.getWaterSprinkler(GlobalApplication.GATE_CONTROLLER_ID));
                GlobalApplication.GATE_CONTROLLER_EDIT_MODE = false;
            } else {
                gateControllers.add((int) updateGateControllerPosition, WaterSprinklerDataHelper.getWaterSprinkler(GlobalApplication.GATE_CONTROLLER_ID));
            }
            adapterGateController.notifyDataSetChanged();
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }


    private void updateApplianceList(long position) {
        try {

            if (GlobalApplication.APPLIANCE_EDIT_MODE) {
                appliances.set((int) position, ApplianceDataHelper.getAppliance(GlobalApplication.APPLIANCE_ID, GlobalApplication.ROOM_ID,slaveID));
                GlobalApplication.APPLIANCE_EDIT_MODE = false;
            } else {

                appliances.add((int) position, ApplianceDataHelper.getAppliance(GlobalApplication.APPLIANCE_ID, GlobalApplication.ROOM_ID,slaveID));
            }
            adapterAppliance.notifyDataSetChanged();
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }

    }

    private String generateAutoRoomId() {
        int addedRooms = 1 + RoomDataHelper.getAllRooms().size();
        return String.format("%02d", addedRooms);
    }

    private String generateAutoSensorId() {
        int addedSensor = SensorDataHelper.getAllSensors().size();
        //int addedSensor=1+SensorDataHelper.getAllSensors().size();
        return ("0" + String.valueOf(addedSensor/4));

    }

    private String generatedAutoCurtainID() {
        //int addedCurtain=CurtainDataHelper.getAllCurtains().size();
        int addedCurtain = CurtainDataHelper.getAllCurtains().size();
        return String.valueOf(addedCurtain);
    }

    private String generateAutoApplianceId() {

        // int addedRooms = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(etRoomId.getText().toString(),slaveID).size();

        int addedRooms = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(etRoomId.getText().toString(), slaveID).size();
        return String.valueOf(addedRooms);

    }

    private String generatedSprinklerId() {
        int sprinklerCount = WaterSprinklerDataHelper.getAllWaterSprinklers().size();
        return String.valueOf(sprinklerCount);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (RoomDataHelper.getAllRooms().size() > 0) {

            startActivity(new Intent(this, RoomListActivity.class));
            finish();

        } else {
            startActivity(new Intent(this, AddUserDetails.class));
            finish();
        }


    }
}

