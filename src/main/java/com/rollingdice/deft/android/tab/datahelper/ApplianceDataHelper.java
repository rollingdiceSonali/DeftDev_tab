package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Appliance;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.EnergyData;

import java.util.List;

/**
 * Created by koushik on 08/06/15.
 */
public class ApplianceDataHelper
{

    private static DatabaseReference localRef;


    public static void addAppliance(int applianceTypeId,
                                    String slaveId,
                                    String applianceName,
                                    String applianceId,
                                    String state,
                                    boolean dimmable,
                                    int dim,
                                    String roomId)
    {

        try
        {
            localRef = GlobalApplication.firebaseRef;
            ActiveAndroid.beginTransaction();

            Appliance old = new Select().from(Appliance.class).where("applianceId = ?", applianceId).and("roomId = ?", roomId)
                    .and("slaveId = ?",slaveId).executeSingle();


            if(old != null) {
                new Update(EnergyData.class).set("applianceName =?", applianceName).where("applianceName =?", old.applianceName)
                        .where("roomId =?", old.roomId).execute();
            }

            new Delete().from(Appliance.class).where("applianceId = ?", applianceId).and("roomId = ?", roomId)
                    .and("slaveId = ?",slaveId).execute();
            //        .and("slaveId = ? ",slaveId).execute();


            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();
            Appliance appliance = new Appliance();
            appliance.applianceTypeId = applianceTypeId;
            appliance.applianceName = applianceName;
            appliance.applianceId = applianceId;
            appliance.slaveId = slaveId;
            appliance.state = state;
            appliance.dimmable = dimmable;
            appliance.dim = dim;
            appliance.roomId = roomId;
            appliance.customerId = Customer.getCustomer().customerId;
            appliance.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        }catch (Exception e)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
            errorRef.setValue(currentStatus);
        }

    }

    public static int getSlaveCountFromRoom(String roomId){

        List<Appliance> applianceArrayList= new Select().distinct().from(Appliance.class).groupBy("slaveId").where("roomId = ?",roomId).execute();
        if(applianceArrayList != null){
            return applianceArrayList.size();
        }else {
            return 0;
        }

    }


    public static List<Appliance> getAllAppliances(String roomId)
    {  List<Appliance> appListRoomWise=null;
        try
        {
            appListRoomWise= new Select()
                    .from(Appliance.class)
                    .where("roomId = ?", roomId)
                    .orderBy("applianceId DESC")
                    .execute();

        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
            errorRef.setValue(currentStatus);
        }
        return appListRoomWise;
    }

    public static List<Appliance> getAllAppliancesByRoomIdAndSlaveId(String roomId, String slaveId)
    {
        List<Appliance> appListRoomWiseSlaveWise=null;
        try
        {
        appListRoomWiseSlaveWise=new Select ()
            .from(Appliance.class)
                .where("roomId = ?", roomId)
                .where("slaveId = ?", slaveId)
                .orderBy("applianceId DESC")
                .execute();

        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
            errorRef.setValue(currentStatus);
        }
        return appListRoomWiseSlaveWise;

    }

    public static List<Appliance> getAllAppliances() {
        return new Select().from(Appliance.class)
                .orderBy("applianceId DESC")
                .execute();
    }

    public static Appliance getAppliance(String applianceId,String roomId,String slaveId)
    {
        Appliance appListApplianceWiswRoomWise=null;
        try{
            appListApplianceWiswRoomWise=new Select().from(Appliance.class)
                .where("applianceId = ? AND roomId = ? AND slaveId = ?", applianceId,roomId,slaveId)
               /* .where("roomId = ?",roomId)
                .where("slaveId = ?",slaveId)*/
                .orderBy("RANDOM()")
                .executeSingle();
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
            errorRef.setValue(currentStatus);
        }
        return appListApplianceWiswRoomWise;

    }

    public static void updateApplianceState(String roomId, String applianceId, boolean state) {

        Appliance appliance = new Select().from(Appliance.class)
                .where("applianceId = ?", applianceId)
                .orderBy("RANDOM()")
                .executeSingle();
        appliance.state = state ? "ON" : "OFF";
        appliance.save();
    }

    public static List<Appliance> getAllPoweredOnDevices() {
        return new Select().from(Appliance.class)
                .where("state = ?", "ON")
                .orderBy("applianceId DESC")
                .execute();
    }

    public static List<Appliance> getAllPoweredOnAppliances(int applianceTypeId) {
        return new Select().from(Appliance.class)
                .where("state = ?", "ON")
                .and("applianceTypeId = ?", applianceTypeId)
                .orderBy("applianceId DESC")
                .execute();
    }

    public static List<Appliance> getAllAppliances(int applianceTypeId) {
        return new Select().from(Appliance.class)
                .where("applianceTypeId = ?", applianceTypeId)
                .orderBy("applianceId DESC")
                .execute();
    }

    public static void updateApplianceDim(String roomId, String applianceId, int dim) {

        Appliance appliance = new Select().from(Appliance.class)
                .where("applianceId = ?", applianceId)
                .orderBy("RANDOM()")
                .executeSingle();
        appliance.dim = dim;
        appliance.save();
    }

    public static void deleteAppliance(String roomId, String applianceId) {

    }
}
