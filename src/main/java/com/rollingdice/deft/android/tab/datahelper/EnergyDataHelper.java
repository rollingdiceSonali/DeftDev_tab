package com.rollingdice.deft.android.tab.datahelper;

/**
 * Created by deft on 9/28/2016.
 */

import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.EnergyData;

import java.util.List;

public class EnergyDataHelper
{

    private static DatabaseReference localRef=GlobalApplication.firebaseRef;



    public static void addEnergy(String datetime,
                                    String slaveId,
                                    int energy,
                                    String applianceName,
                                    String applianceId,
                                    Boolean state,
                                    boolean dimmable,
                                    int dim,
                                    String roomId)
    {

        try
        {
            ActiveAndroid.beginTransaction();
            EnergyData energyData = new EnergyData();
            energyData.datetime = datetime;
            energyData.applianceName = applianceName;
            energyData.applianceId = applianceId;
            energyData.energy = energy;
            energyData.slaveId = slaveId;
            energyData.state = state;
            energyData.dimmable = dimmable;
            energyData.dim = dim;
            energyData.roomId = roomId;
            energyData.customerId = Customer.getCustomer().customerId;
            energyData.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        }catch (Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }

    }

    public static List<EnergyData> getAllEnergyDataRoomWise(String roomId)
    {
        List<EnergyData> energyDataRoomWise=null;
        try
        {
            energyDataRoomWise= new Select()
                    .from(EnergyData.class)
                    .where("roomId = ?", roomId)
                    .orderBy("energy DESC")
                    .execute();

        } catch (Exception e)
        {
            try {
                if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                        && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                    DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(String.valueOf(System.currentTimeMillis()));
                    String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                    errorRef.setValue(currentStatus);
                }
            }catch (NullPointerException e1)
            {
                Toast.makeText(GlobalApplication.context, ""+e1.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return energyDataRoomWise;
    }

    /*public static List<Appliance> getAllAppliancesByRoomIdAndSlaveId(String roomId, String slaveId)
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
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
        return appListRoomWiseSlaveWise;

    }*/

    public static List<EnergyData> getAllEnergyData() {
        return new Select().from(EnergyData.class)
                .orderBy("applianceId DESC")
                .execute();
    }

    public static List<EnergyData>getAllApplinaceWiseData(String applianceId,String roomId)
    {
        return new Select().from(EnergyData.class).where("applianceId = ?",applianceId)
                .where("roomId = ?",roomId).execute();
    }

   /* public static Appliance getAppliance(String applianceId,String roomId)
    {
        Appliance appListApplianceWiswRoomWise=null;
        try{
            appListApplianceWiswRoomWise=new Select().from(Appliance.class)
                    .where("applianceId = ?", applianceId)
                    .where("roomId = ?",roomId)
                    .orderBy("RANDOM()")
                    .executeSingle();
        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
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

    }*/
}
