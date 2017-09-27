package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Sensor;
import com.rollingdice.deft.android.tab.model.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koushik on 09/06/15.
 */
public class SensorDataHelper {

    public static void addSensor(String roomId, String slaveId, String sensorName, String state, String sensorId,int typeID) {
        try {
            ActiveAndroid.beginTransaction();
            new Delete().from(Sensor.class).where("sensorId = ?", sensorId).and("roomId = ?", roomId).and("sensorTypeId = ?",typeID).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();
            Sensor sensor = new Sensor();
            sensor.customerId = Customer.getCustomer().customerId;
            sensor.roomId = roomId;
            sensor.slaveId = slaveId;
            sensor.sensorName = sensorName;
            sensor.state = state;
            sensor.sensorId = sensorId;
            sensor.sensorTypeId = typeID;
            sensor.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        }
        catch(Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = GlobalApplication.firebaseRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

    public static List<Sensor> getAllSensorIds() {

        List<Sensor> sensorArrayList = new ArrayList<Sensor>();
        sensorArrayList = new Select().distinct().from(Sensor.class).groupBy("sensorId").execute();

        return sensorArrayList;

    }
    public static boolean isSensorAvailable(String roomId, int typeId,String sensorId){
        List<Sensor> list = new ArrayList<Sensor>();
        list= new Select()
                .from(Sensor.class)
                .where("roomId = ? and sensorTypeId = ? and sensorId = ?", roomId,typeId,sensorId)
                .orderBy("sensorId DESC")
                .execute();


                if(list.size()>0){
                    return true;
                }else {
                    return false;
                }

    }

    public static List<Sensor> getAllSensors(String roomId, String slaveId) {
        return new Select()
                .from(Sensor.class)
                .where("roomId = ? and slaveId = ?", roomId,slaveId)
                .orderBy("sensorId DESC")
                .execute();
    }


    public static List<Sensor> getAllSensors() {
        return new Select()
                .from(Sensor.class)
                .orderBy("sensorId DESC")
                .execute();
    }


    public static int getSensorIdCount(String sensorID) {
        List<Sensor> list = new ArrayList<Sensor>();
        list = new Select()
                .from(Sensor.class)
                .where("sensorId = ?", sensorID)
                .orderBy("sensorId DESC")
                .execute();

        return list.size()-1;
    }

    public static String getRoomNameFromSensorId(String sensorID){
        String roomId = null;
        List<Sensor> list = new ArrayList<Sensor>();
        list = new Select()
                .from(Sensor.class)
                .where("sensorId = ?", sensorID)
                .execute();
        roomId = RoomDataHelper.getRoomDetaildFromId(list.get(0).roomId).roomName;
        return roomId;

    }

    public static boolean isSonsorAddedtoSlave(String slaveId,String roomId){
        List<Sensor> list = new ArrayList<Sensor>();
        list = new Select()
                .from(Sensor.class)
                .where("slaveId = ? and roomId = ?", slaveId,roomId)
                .execute();

       if(list.size() == 4){
           return true;
       }else
           return false;
    }

    public static List<Sensor> getAllSensorsByRoomId(String roomId) {
        return new Select()
                .from(Sensor.class)
                .where("roomId = ?", roomId)
                .orderBy("sensorId DESC")
                .execute();
    }

    public static List<Sensor> getAllSensorsByRoomIdAndSlaveId(String roomId, String slaveId) {
        return new Select()
                .from(Sensor.class)
                .where("roomId = ?", roomId)
                .where("slaveId = ?", slaveId)
                .orderBy("sensorId DESC")
                .execute();
    }

    public static Sensor getSensor(String sensorId,String roomId,String slaveID,int typeId) {
        return new Select().from(Sensor.class)
                .where("sensorId = ? AND  roomId = ? AND slaveId= ? AND sensorTypeId = ?", sensorId,roomId,slaveID,typeId)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static void deleteSensor(String roomId, String sensorId) {
        //  new Delete().from(Sensor.class).where("sensorId = ?", sensorId).execute();

    }

    public static void updateSensorState(String roomId, String sensorId, boolean state) {
        Sensor sensor = new Select().from(Sensor.class)
                .where("sensorId = ?", sensorId)
                .orderBy("RANDOM()")
                .executeSingle();
        sensor.state = state ? "ON" : "OFF";
        sensor.save();
    }
}
