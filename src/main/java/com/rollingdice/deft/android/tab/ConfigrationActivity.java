package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rollingdice.deft.android.tab.datahelper.ApplianceDataHelper;
import com.rollingdice.deft.android.tab.datahelper.RoomDataHelper;
import com.rollingdice.deft.android.tab.datahelper.SensorDataHelper;
import com.rollingdice.deft.android.tab.model.Appliance;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Room;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.rollingdice.deft.android.tab.model.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class ConfigrationActivity extends AppCompatActivity {
    private String TAG = ConfigrationActivity.class.getSimpleName();
    private Spinner configSpinner, oldSlaveIDSpinner, oldRoomIDSpinner, newSlaveIDSpinner, newRoomIDSpinner;
    private Button sendCommand, showCommand;
    private DatabaseReference configSlaveRef;

    private Context context;


    ////     Assign Slave
    private LinearLayout sensorSlaveLL, cmdLL;
    private RadioGroup stepRG;
    private MaterialSpinner sensorTypeSpinner, sensorIdSpinner;
    private int sensorIdpos, sensorPos;
    private String[] roomList, slaveList;
    private String sensorId;



    List<Appliance> applianceList = new ArrayList<Appliance>();
    List<Room> roomLists;

    ///   store value in veriable
    private String[] rID, sType, noOfAppliance;

    private TextView responseTv, cmdTv;
    private ArrayList<String> selectedAppliancesList = new ArrayList<String>();
    private Customer customer;

    /// Master Config
    private EditText pswdEt, ssidEt, routerIpEt, masterIpEt, portNoEt, ipIdEt, newIpIdEt;
    private TextInputLayout pswdTIL, ssidTIL, routerTIL, masterTIL, portNoTIL, ipIdTIL, newIpIdTIL;

    private ArrayAdapter<String> dataAdapter;
    String[] masterConfigType = {" Select Configration Type ", " Assign SSID ", " Assign PSWD ", "Assign IP "};
    String[] sensorConfigType = {" Select Configration Type ", " Assign ID ", " Assign New ID ", "Assign Slave "};
    String[] configType = {"Select Configration Type", "New Id Assign", "Change ID", "Individual Forgot ID", "All Forgot ID"};
    String roomID[] = {"Room ID", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
    String slaveID[] = {"Slave ID", "1", "2", "3", "4"};
    String roomID1[] = {"New Room ID", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
    String slaveID1[] = {"New Slave ID", "1", "2", "3", "4"};

    String sensorType[] = {"Temperature", "Light", "Motion"};
    String sensorValue, roomValue;
    int no_of_appliances;


    String oldRoomID, oldSlaveID, newRoomId, newSlaveID, command, message = "";
    int configTypePos, oldRoomIDPos, oldSlaveIDPos, newRoomIdPos, newSlaveIDPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configSlaveRef = GlobalApplication.firebaseRef.child("SlaveConfigCommand").child(Customer.getCustomer().customerId);

        context = this;

        if (getIntent().getAction().equals("Master Config")) {

            setContentView(R.layout.activity_master_configration);
            ssidEt = (EditText) findViewById(R.id.ssid_et);
            ssidTIL = (TextInputLayout) findViewById(R.id.ssid_til);

            pswdEt = (EditText) findViewById(R.id.pswd_et);
            pswdTIL = (TextInputLayout) findViewById(R.id.pswd_til);

            portNoEt = (EditText) findViewById(R.id.port_et);
            portNoTIL = (TextInputLayout) findViewById(R.id.port_til);

            routerIpEt = (EditText) findViewById(R.id.router_ip_et);
            routerTIL = (TextInputLayout) findViewById(R.id.router_ip_til);

            masterIpEt = (EditText) findViewById(R.id.master_ip_et);
            masterTIL = (TextInputLayout) findViewById(R.id.master_ip_til);

            masterConfig();

        } else if (getIntent().getAction().equals("Sensor Config")) {

            setContentView(R.layout.activity_sensor_configration);

            ipIdEt = (EditText) findViewById(R.id.old_ip_et);
            ipIdTIL = (TextInputLayout) findViewById(R.id.old_ip_til);

            newIpIdEt = (EditText) findViewById(R.id.new_ip_et);
            newIpIdTIL = (TextInputLayout) findViewById(R.id.new_ip_til);

            sensorSlaveLL = (LinearLayout) findViewById(R.id.sensor_layout);
            cmdLL = (LinearLayout) findViewById(R.id.cmd_details_ll);


            sensorTypeSpinner = (MaterialSpinner) findViewById(R.id.select_sensor_type_sp);

            sensorIdSpinner = (MaterialSpinner) findViewById(R.id.sensor_id);

            slaveConfig();
           /* slaveListSpinner = (Spinner)findViewById(R.id.slave_spinner);
            applianceListSpinner = (Spinner)findViewById(R.id.appliance_list_spinner);*/


        } else {

            setContentView(R.layout.activity_slave_configration);

            oldRoomIDSpinner = (Spinner) findViewById(R.id.old_roomId);
            oldSlaveIDSpinner = (Spinner) findViewById(R.id.old_slave_ID);
            newRoomIDSpinner = (Spinner) findViewById(R.id.new_roomId);
            newSlaveIDSpinner = (Spinner) findViewById(R.id.new_slaveID);


            slaveConfig();
        }

        showCommand = (Button) findViewById(R.id.show_cmd_btn);
        showCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCommands();

            }
        });

        responseTv = (TextView) findViewById(R.id.response);

        cmdTv = (TextView) findViewById(R.id.cmd);

        configSpinner = (Spinner) findViewById(R.id.slaveconfig);
        configSpinner.requestFocus();

        if (getIntent().getAction().equals("Master Config")) {

            dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, masterConfigType);

        } else if (getIntent().getAction().equals("Sensor Config")) {

            dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorConfigType);

        } else {
            dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, configType);
        }


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        configSpinner.setAdapter(dataAdapter);
        configSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                configTypePos = i;
                responseTv.setText("");
                cmdTv.setText("");
                configSlave(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sendCommand = (Button) findViewById(R.id.btn);

        sendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getIntent().getAction().equals("Master Config")) {

                    if (configTypePos == 0) {

                    } else if (configTypePos == 1) {

                        if (!ssidEt.getText().toString().equals("")) {

                            command = "W012" + ssidEt.getText().toString() + "0F";
                            writeCmdToFirebase(command);
                            command = "";

                        } else {
                            ssidTIL.setError("Please Enter SSID");
                        }

                    } else if (configTypePos == 2) {

                        if (!pswdEt.getText().toString().equals("")) {
                            command = "X012" + pswdEt.getText().toString() + "0F";
                            writeCmdToFirebase(command);
                            command = "";

                        } else {
                            pswdTIL.setError("Please Enter Router PSWD");
                        }

                    } else if (configTypePos == 3) {

                        if (!routerIpEt.getText().toString().equals("") && !masterIpEt.getText().toString().equals("")
                                && !portNoEt.getText().toString().equals("")) {

                            command = "Y012" + routerIpEt.getText().toString() + ":" + masterIpEt.getText().toString() + ":" + portNoEt.getText().toString() + ":0F";
                            writeCmdToFirebase(command);
                            command = "";

                        } else {
                            if (routerIpEt.getText().toString().equals("")) {
                                routerTIL.setError(" Please Enter Router IP ");
                            } else if (masterIpEt.getText().toString().equals("")) {
                                masterTIL.setError(" Please Enter master IP ");
                            }
                            if (portNoEt.getText().toString().equals("")) {
                                portNoTIL.setError(" Please Enter Port ");
                            }

                        }
                    }

                } else if (getIntent().getAction().equals("Sensor Config")) {

                    if (configTypePos == 0) {

                    } else {

                        if (configTypePos == 1) {

                            if (!ipIdEt.getText().toString().equals("") && ipIdEt.getText().toString().length() < 3) {

                                if (ipIdEt.getText().toString().length() == 2) {
                                    command = "J0123" + ipIdEt.getText().toString() +"__"+ "0F";

                                } else if (ipIdEt.getText().toString().length() == 1) {

                                    command = "J01230" + ipIdEt.getText().toString()+ "__" + "0F";
                                }

                                writeCmdToFirebase(command);
                                command = "";

                            } else if (ipIdEt.getText().toString().length() > 3) {

                                ipIdTIL.setError("Please Enter Correct i/p IP");

                            } else {
                                ipIdTIL.setError("Please Enter i/p IP");
                            }


                        } else if (configTypePos == 2) {


                            if (!newIpIdEt.getText().toString().equals("") && newIpIdEt.getText().toString().length() < 3 &&
                                 !ipIdEt.getText().toString().equals("") && ipIdEt.getText().toString().length() < 3) {

                                if (newIpIdEt.getText().toString().length() == 2 && ipIdEt.getText().toString().length() == 2) {

                                    command = "J0123" +ipIdEt.getText().toString()+ newIpIdEt.getText().toString() + "0F";

                                } else if (ipIdEt.getText().toString().length() == 2 && newIpIdEt.getText().toString().length() == 1) {

                                    command = "J0123" +ipIdEt.getText().toString()+"0"+ newIpIdEt.getText().toString() + "0F";

                                }else if(ipIdEt.getText().toString().length() == 1 && newIpIdEt.getText().toString().length() == 2){

                                    command = "J01230" +ipIdEt.getText().toString()+ newIpIdEt.getText().toString() + "0F";

                                }else if(ipIdEt.getText().toString().length() == 1 && newIpIdEt.getText().toString().length() == 1){

                                    command = "J01230" +ipIdEt.getText().toString()+ "0"+newIpIdEt.getText().toString() + "0F";
                                }


                                writeCmdToFirebase(command);
                                command = "";

                            }else if(ipIdEt.getText().toString().length() > 3){

                                ipIdTIL.setError("Please Enter Correct i/p IP");

                            } else if(ipIdEt.getText().toString().equals("")){

                                ipIdTIL.setError("Please Enter i/p IP");

                            }else if (newIpIdEt.getText().toString().length() > 3) {

                                newIpIdTIL.setError("Please Enter New i/p IP");

                            } else {
                                newIpIdTIL.setError("Please Enter New i/p IP");
                            }


                        }
                        if (configTypePos == 3) {


                            getSelectedAppliances(sensorSlaveLL);
                            StringBuffer stringBuffer = new StringBuffer();

                            for (int i = 0; i < selectedAppliancesList.size(); i++) {

                                stringBuffer.append(selectedAppliancesList.get(i) + ":");
                            }

                            if(sensorId != null){

                                command = "Q012" +sensorId+""+sensorPos+ stringBuffer.toString() + "0F";
                                 writeCmdToFirebase(command);
                                cmdTv.setText(command);
                                selectedAppliancesList = new ArrayList<>();
                                command = "";
                            }else {

                                sensorIdSpinner.setError("Plsease Select Sensor ID");
                            }



                        }

                    }


                } else {

                    if (configTypePos == 0) {

                    } else {

                        if (configTypePos == 1) {
                            if (oldRoomIDPos != 0 && oldSlaveIDPos != 0) {
                                command = "D012" + roomID[oldRoomIDPos] + "" + slaveID[oldSlaveIDPos] + "___" + "0F";
                                writeCmdToFirebase(command);
                                command = "";
                            } else {
                                if (oldRoomIDPos == 0 && oldSlaveIDPos == 0) {

                                    message = "Select Slave Id and RoomId";

                                } else if (oldRoomIDPos == 0) {

                                    message = "Select RoomId";

                                } else if (oldSlaveIDPos == 0) {

                                    message = "Select Slave ID";

                                }

                            }

                        } else if (configTypePos == 2) {

                            if (oldRoomIDPos != 0 && oldSlaveIDPos != 0 && newRoomIdPos != 0 && newSlaveIDPos != 0) {
                                command = "D012" + roomID[oldRoomIDPos] + "" + slaveID[oldSlaveIDPos] + "" + roomID1[newRoomIdPos] + "" + slaveID1[newSlaveIDPos] + "" + "0F";
                                writeCmdToFirebase(command);
                                command = "";
                            } else {

                                if (oldRoomIDPos == 0) {

                                }

                                if (oldSlaveIDPos == 0) {

                                }

                                if (newRoomIdPos == 0) {

                                }

                                if (newSlaveIDPos == 0) {

                                }

                            }

                        } else if (configTypePos == 3) {
                            if (oldRoomIDPos != 0 && oldSlaveIDPos != 0) {
                                command = "D012" + roomID[oldRoomIDPos] + "" + slaveID[oldSlaveIDPos] + ":__" + "0F";
                                writeCmdToFirebase(command);
                                command = "";
                            } else {
                                if (oldRoomIDPos == 0 && oldSlaveIDPos == 0) {


                                } else if (oldRoomIDPos == 0) {


                                } else if (oldSlaveIDPos == 0) {

                                }

                            }

                        } else if (configTypePos == 4) {

                            command = "D012011:::0F";
                            writeCmdToFirebase(command);
                            command = "";

                        }
                    }
                }
            }
        });


    }


    public void showCommands() {


        if (getIntent().getAction().equals("Master Config")) {

            if (configTypePos == 0) {

            } else if (configTypePos == 1) {

                if (!ssidEt.getText().toString().equals("")) {

                    command = "W012" + ssidEt.getText().toString() + "0F";
                    cmdTv.setText(command);
                   /* writeCmdToFirebase(command);
                    command = "";*/

                } else {
                    ssidTIL.setError("Please Enter SSID");
                }

            } else if (configTypePos == 2) {

                if (!pswdEt.getText().toString().equals("")) {
                    command = "X012" + pswdEt.getText().toString() + "0F";
                    cmdTv.setText(command);
                  /*  writeCmdToFirebase(command);
                    command = "";*/

                } else {
                    pswdTIL.setError("Please Enter Router PSWD");
                }

            } else if (configTypePos == 3) {

                if (!routerIpEt.getText().toString().equals("") && !masterIpEt.getText().toString().equals("")
                        && !portNoEt.getText().toString().equals("")) {

                    command = "Y012" + routerIpEt.getText().toString() + ":" + masterIpEt.getText().toString() + ":" + portNoEt.getText().toString() + ":0F";
                    cmdTv.setText(command);
                  /*  writeCmdToFirebase(command);
                    command = "";*/

                } else {
                    if (routerIpEt.getText().toString().equals("")) {
                        routerTIL.setError(" Please Enter Router IP ");
                    } else if (masterIpEt.getText().toString().equals("")) {
                        masterTIL.setError(" Please Enter master IP ");
                    }
                    if (portNoEt.getText().toString().equals("")) {
                        portNoTIL.setError(" Please Enter Port ");
                    }

                }
            }

        } else if (getIntent().getAction().equals("Sensor Config")) {


            if (configTypePos == 0) {

            } else {

                if (configTypePos == 1) {

                    if (!ipIdEt.getText().toString().equals("") && ipIdEt.getText().toString().length() < 3) {

                        if (ipIdEt.getText().toString().length() == 2) {
                            command = "J0123" + ipIdEt.getText().toString() +"__"+ "0F";

                        } else if (ipIdEt.getText().toString().length() == 1) {

                            command = "J01230" + ipIdEt.getText().toString()+ "__" + "0F";
                        }

                        cmdTv.setText(command);
                      //  writeCmdToFirebase(command);
                        command = "";

                    } else if (ipIdEt.getText().toString().length() > 3) {

                        ipIdTIL.setError("Please Enter Correct i/p IP");

                    } else {
                        ipIdTIL.setError("Please Enter i/p IP");
                    }


                } else if (configTypePos == 2) {


                    if (!newIpIdEt.getText().toString().equals("") && newIpIdEt.getText().toString().length() < 3 &&
                            !ipIdEt.getText().toString().equals("") && ipIdEt.getText().toString().length() < 3) {

                        if (newIpIdEt.getText().toString().length() == 2 && ipIdEt.getText().toString().length() == 2) {

                            command = "J0123" +ipIdEt.getText().toString()+ newIpIdEt.getText().toString() + "0F";

                        } else if (ipIdEt.getText().toString().length() == 2 && newIpIdEt.getText().toString().length() == 1) {

                            command = "J0123" +ipIdEt.getText().toString()+"0"+ newIpIdEt.getText().toString() + "0F";

                        }else if(ipIdEt.getText().toString().length() == 1 && newIpIdEt.getText().toString().length() == 2){

                            command = "J01230" +ipIdEt.getText().toString()+ newIpIdEt.getText().toString() + "0F";
                        }else if(ipIdEt.getText().toString().length() == 1 && newIpIdEt.getText().toString().length() == 1){
                            command = "J01230" +ipIdEt.getText().toString()+"0"+newIpIdEt.getText().toString() + "0F";
                        }

                          cmdTv.setText(command);
                      //  writeCmdToFirebase(command);
                        command = "";

                    }else if(ipIdEt.getText().toString().length() > 3){

                        ipIdTIL.setError("Please Enter Correct i/p IP");

                    } else if(ipIdEt.getText().toString().equals("")){

                        ipIdTIL.setError("Please Enter i/p IP");

                    }else if (newIpIdEt.getText().toString().length() > 3) {

                        newIpIdTIL.setError("Please Enter New i/p IP");

                    } else {
                        newIpIdTIL.setError("Please Enter New i/p IP");
                    }


                }
                if (configTypePos == 3) {


                    getSelectedAppliances(sensorSlaveLL);
                    StringBuffer stringBuffer = new StringBuffer();

                    for (int i = 0; i < selectedAppliancesList.size(); i++) {

                        stringBuffer.append(selectedAppliancesList.get(i) + ":");
                    }

                    if(sensorId != null){

                        command = "Q012" +sensorId+""+sensorPos+ stringBuffer.toString() + "0F";
                       // writeCmdToFirebase(command);
                        cmdTv.setText(command);
                        selectedAppliancesList = new ArrayList<>();
                        command = "";
                    }else {

                        sensorIdSpinner.setError("Plsease Select Sensor ID");
                    }



                }

            }


        } else {

            if (configTypePos == 0) {

            } else {

                if (configTypePos == 1) {
                    if (oldRoomIDPos != 0 && oldSlaveIDPos != 0) {
                        command = "D012" + roomID[oldRoomIDPos] + "" + slaveID[oldSlaveIDPos] + "___" + "0F";
                        cmdTv.setText(command);
                       /* writeCmdToFirebase(command);
                        command = "";*/
                    } else {
                        if (oldRoomIDPos == 0 && oldSlaveIDPos == 0) {

                            message = "Select Slave Id and RoomId";

                        } else if (oldRoomIDPos == 0) {

                            message = "Select RoomId";

                        } else if (oldSlaveIDPos == 0) {

                            message = "Select Slave ID";

                        }

                    }

                } else if (configTypePos == 2) {

                    if (oldRoomIDPos != 0 && oldSlaveIDPos != 0 && newRoomIdPos != 0 && newSlaveIDPos != 0) {
                        command = "D012" + roomID[oldRoomIDPos] + "" + slaveID[oldSlaveIDPos] + "" + roomID1[newRoomIdPos] + "" + slaveID1[newSlaveIDPos] + "" + "0F";
                        cmdTv.setText(command);
                        /*writeCmdToFirebase(command);

                        command = "";*/
                    } else {

                        if (oldRoomIDPos == 0) {

                        }

                        if (oldSlaveIDPos == 0) {

                        }

                        if (newRoomIdPos == 0) {

                        }

                        if (newSlaveIDPos == 0) {

                        }

                    }

                } else if (configTypePos == 3) {
                    if (oldRoomIDPos != 0 && oldSlaveIDPos != 0) {
                        command = "D012" + roomID[oldRoomIDPos] + "" + slaveID[oldSlaveIDPos] + ":__" + "0F";
                        cmdTv.setText(command);
                       /* writeCmdToFirebase(command);
                        command = "";*/
                    } else {
                        if (oldRoomIDPos == 0 && oldSlaveIDPos == 0) {


                        } else if (oldRoomIDPos == 0) {


                        } else if (oldSlaveIDPos == 0) {

                        }
                    }

                } else if (configTypePos == 4) {

                    command = "D012011:::0F";
                    cmdTv.setText(command);
                    /*writeCmdToFirebase(command);
                    command = "";*/

                }

            }
        }

    }


    public void getSelectedAppliances(LinearLayout linearLayout) {


        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View v = (View) linearLayout.getChildAt(i);
            if (v instanceof CheckBox) {

                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {

                    selectedAppliancesList.add("" + checkBox.getTag());
                }

            } else if (v instanceof LinearLayout) {

                getSelectedAppliances((LinearLayout) v);
            }
        }


    }


    public void masterConfig() {


    }


    public void slaveConfig() {

        if (getIntent().getAction().equals("Sensor Config")) {


            ArrayAdapter<String> sensorAdapter11 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorType);
            sensorAdapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sensorTypeSpinner.setAdapter(sensorAdapter11);
            //sensorTypeSpinner.setVisibility(View.GONE);

            sensorTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    sensorPos = i;

                    if(sensorPos >= 0){

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


///////////////////     get Sensor IS

            List<Sensor> sensorList =  SensorDataHelper.getAllSensorIds();
            final String[] sensorIdList = new String[sensorList.size()];


            for(int i=0 ; i<sensorList.size();i++){

                sensorIdList[i] = sensorList.get(i).sensorId;
            }



            ArrayAdapter<String> sensorIDAdapter11 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,sensorIdList );
            sensorIDAdapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sensorIdSpinner.setAdapter(sensorIDAdapter11);

            sensorIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {



                   if(i >=  0 ){

                        sensorId = sensorIdList[i];

                        showApplianceList();


                    }






                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



            /////////////////////////



            // applianceList = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(roomList[roomPos],slaveIdPos);


        } else {



            /*roomListSpinner.setAdapter();*/


            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomID);
            dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            oldRoomIDSpinner.setAdapter(dataAdapter1);
            oldRoomIDSpinner.setVisibility(View.GONE);

            oldRoomIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    oldRoomIDPos = i;
              /*  if(oldRoomIDPos == 0){

                }*/

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slaveID);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            oldSlaveIDSpinner.setAdapter(dataAdapter2);
            oldSlaveIDSpinner.setVisibility(View.GONE);
            oldSlaveIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    oldSlaveIDPos = i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomID1);
            dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            newRoomIDSpinner.setAdapter(dataAdapter3);
            newRoomIDSpinner.setVisibility(View.GONE);
            newRoomIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    newRoomIdPos = i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slaveID1);
            dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            newSlaveIDSpinner.setAdapter(dataAdapter4);
            newSlaveIDSpinner.setVisibility(View.GONE);
            newSlaveIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    newSlaveIDPos = i;


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }


    }



    public void showApplianceList(){

        roomLists = new ArrayList<Room>();
        roomLists = RoomDataHelper.getAllRooms();
        roomList = new String[roomLists.size() + 1];

        for (int i = 0; roomLists.size() > 0 && i < roomLists.size(); i++) {


            View v1 = LayoutInflater.from(context).inflate(R.layout.datafield_header, null);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(20, 20, 0, 0); // llp.setMargins(left, top, right, bottom);
            ((TextView) v1.findViewById(R.id.header)).setHint("ROOM NAME  :  " + roomLists.get(i).roomName);
            LinearLayout ll1 = (LinearLayout) v1.findViewById(R.id.ll);
            sensorSlaveLL.addView(v1);

            //roomList[i+1] = roomLists.get(i).roomName;

            int slaveCount = 0;
            slaveCount = ApplianceDataHelper.getSlaveCountFromRoom(roomLists.get(i).roomId);


            for (int j = 1; j <= slaveCount; j++) {

                View v = LayoutInflater.from(context).inflate(R.layout.datafield_header, null);
                ((TextView) v.findViewById(R.id.header)).setHint("Select Appliance From Slave ID :  " + j);
                LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll);


                applianceList = ApplianceDataHelper.getAllAppliancesByRoomIdAndSlaveId(roomLists.get(i).roomId, "" + j);

                renderCheckBoxToLinearLayout(applianceList, ll, roomLists.get(i).roomId, j);

                sensorSlaveLL.addView(v);

            }

        }


    }

    public void renderCheckBoxToLinearLayout(List<Appliance> applianceList, LinearLayout ll, String roomID, int slave) {

        for (int i = 0; i < applianceList.size(); i++) {
            try {
                CheckBox cb = new CheckBox(context);

                if (cb != null) {
                    Log.i(TAG, " not null checbox");
                } else {
                    Log.i(TAG, " null checbox");
                }

                cb.setTextSize(16);

                //cb.setTextColor(context.getResources().getColor(R.color.textColorGray));
                cb.setText("" + applianceList.get(i).applianceName);
                cb.setId(i + 1);
                cb.setTag(roomID + "" + slave + applianceList.get(i).applianceId);
                cb.setGravity(Gravity.CENTER_HORIZONTAL);
                ll.addView(cb);
            } catch (Exception e) {

            }
        }
    }


    public void writeCmdToFirebase(String command) {

        Map<String, Object> slaveCommandMap = new HashMap<>();
        slaveCommandMap.put("Command", command);
        slaveCommandMap.put("Response", "0");
        configSlaveRef.setValue(slaveCommandMap);

        //cmdTv.setText("Command  :    " +command);

        customer = new Select().from(Customer.class).executeSingle();


        configSlaveRef.child("Response").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String response = dataSnapshot.getValue(String.class);
                    if (!response.equals("0")) {
                        if (response.endsWith("1F")) {

                            if (getIntent().getAction().equals("Master Config")) {
                                if (configTypePos == 3) {

                                    ///////////////  Port change

                                    GlobalApplication.firebaseRef.child("IPAddress").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("port").
                                            setValue(Integer.parseInt(portNoEt.getText().toString()));
                                    GlobalApplication.firebaseRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                            child("port").setValue(Integer.parseInt(portNoEt.getText().toString()));

                                    /// change from db
                                    Customer model = selectField("port", "" + customer.port);
                                    model.port = Integer.parseInt(portNoEt.getText().toString());
                                    model.save();


                                    ///////////////////  IP change

                                    GlobalApplication.firebaseRef.child("IPAddress").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ipAddres").setValue(masterIpEt.getText().toString());
                                    GlobalApplication.firebaseRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                            child("ipAddress").setValue(masterIpEt.getText().toString());


                                    /// change from db
                                    Customer model1 = selectField("ipAddress", customer.ipAddress);
                                    model1.ipAddress = masterIpEt.getText().toString();
                                    model1.save();


                                }
                            }

                            responseTv.setText("Response  : " + response);
                            configSlaveRef.child("Response").setValue("0");


                        } else if (response.endsWith("2F")) {

                            responseTv.setText("Response  : " + response);
                            configSlaveRef.child("Response").setValue("0");

                        } else if (response.endsWith("0F")) {

                            responseTv.setText("Response  : " + response);
                            configSlaveRef.child("Response").setValue("0");

                        }

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public Customer selectField(String fieldName, String fieldValue) {

        if (fieldName.equals("port")) {
            return new Select().from(Customer.class)
                    .where(fieldName + " = ?", Integer.parseInt(fieldValue)).executeSingle();
        } else {
            return new Select().from(Customer.class)
                    .where(fieldName + " = ?", fieldValue).executeSingle();
        }
    }

    public void configSlave(int pos) {

        if (getIntent().getAction().equals("Master Config")) {

            if (pos == 0) {
                pswdTIL.setVisibility(View.GONE);
                ssidTIL.setVisibility(View.GONE);
                routerTIL.setVisibility(View.GONE);
                masterTIL.setVisibility(View.GONE);
                portNoTIL.setVisibility(View.GONE);


            } else if (pos == 1) {

                // ssidEt.setHint(" Enter Router SSID ");
                //  ssidTIL.setHint("Enter Router SSID");

                ssidTIL.setVisibility(View.VISIBLE);
                pswdTIL.setVisibility(View.GONE);
                routerTIL.setVisibility(View.GONE);
                masterTIL.setVisibility(View.GONE);
                portNoTIL.setVisibility(View.GONE);


            } else if (pos == 2) {
                //ssidEt.setHint(" Enter Router PSWD ");
                //  ssidTIL.setHint("Enter Router PSWD");
                ssidTIL.setVisibility(View.GONE);
                pswdTIL.setVisibility(View.VISIBLE);
                routerTIL.setVisibility(View.GONE);
                masterTIL.setVisibility(View.GONE);
                portNoTIL.setVisibility(View.GONE);

            } else if (pos == 3) {

                ssidTIL.setVisibility(View.GONE);

                // routerTIL.setHint("Enter Router IP");
                //routerIpEt.setHint("Enter Router IP");
                pswdTIL.setVisibility(View.GONE);
                routerTIL.setVisibility(View.VISIBLE);

                // masterTIL.setHint("Enter Master IP");
                // masterIpEt.setHint("Enter Master IP");
                masterTIL.setVisibility(View.VISIBLE);


                //portNoTIL.setHint("Enter Port ");
                //portNoEt.setHint("Enter Port ");
                portNoTIL.setVisibility(View.VISIBLE);

            }


        } else if (getIntent().getAction().equals("Sensor Config")) {

            if (pos == 0) {

                ipIdTIL.setVisibility(View.GONE);
                newIpIdTIL.setVisibility(View.GONE);
                sensorSlaveLL.setVisibility(View.GONE);
                cmdLL.setVisibility(View.GONE);

            } else {

                if (pos == 1) {

                    ipIdTIL.setVisibility(View.VISIBLE);
                    newIpIdTIL.setVisibility(View.GONE);
                    sensorSlaveLL.setVisibility(View.GONE);
                    cmdLL.setVisibility(View.VISIBLE);

                } else if (pos == 2) {

                    ipIdTIL.setVisibility(View.VISIBLE);
                    newIpIdTIL.setVisibility(View.VISIBLE);
                    sensorSlaveLL.setVisibility(View.GONE);
                    cmdLL.setVisibility(View.VISIBLE);

                } else if (pos == 3) {

                    ipIdTIL.setVisibility(View.GONE);
                    newIpIdTIL.setVisibility(View.GONE);
                    sensorSlaveLL.setVisibility(View.VISIBLE);
                    cmdLL.setVisibility(View.VISIBLE);


                }
            }


        } else {

            if (pos == 0) {

            } else if (pos == 1) {

                oldRoomIDSpinner.setVisibility(View.VISIBLE);
                oldSlaveIDSpinner.setVisibility(View.VISIBLE);
                newRoomIDSpinner.setVisibility(View.GONE);
                newSlaveIDSpinner.setVisibility(View.GONE);

            } else if (pos == 2) {

                roomID[0] = " Old Room ID ";
                slaveID[0] = " Old Slave ID ";
                roomID1[0] = " New Room ID ";
                slaveID1[0] = " New Slave ID ";

                oldRoomIDSpinner.setVisibility(View.VISIBLE);
                oldSlaveIDSpinner.setVisibility(View.VISIBLE);
                newRoomIDSpinner.setVisibility(View.VISIBLE);
                newSlaveIDSpinner.setVisibility(View.VISIBLE);

            } else if (pos == 3) {

                oldRoomIDSpinner.setVisibility(View.VISIBLE);
                oldSlaveIDSpinner.setVisibility(View.VISIBLE);
                newRoomIDSpinner.setVisibility(View.GONE);
                newSlaveIDSpinner.setVisibility(View.GONE);

            } else if (pos == 4) {

                oldRoomIDSpinner.setVisibility(View.GONE);
                oldSlaveIDSpinner.setVisibility(View.GONE);
                newRoomIDSpinner.setVisibility(View.GONE);
                newSlaveIDSpinner.setVisibility(View.GONE);

            }

        }
    }


}
