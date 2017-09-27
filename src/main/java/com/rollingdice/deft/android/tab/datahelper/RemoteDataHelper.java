package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Remote;
import com.rollingdice.deft.android.tab.model.RemoteKeyDetails;

import java.util.List;

/**
 * Created by deft on 23/03/2017.
 */
public class RemoteDataHelper {

    public static void addRemote(String remoteId, String brandName, String remoteApplianceId,String roomId)
    {
        try {
            ActiveAndroid.beginTransaction();
           // new Delete().from(Remote.class).where("remoteId = ? AND IRId = ?" , remoteId,remoteApplianceId).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();
            Remote remote = new Remote();
            remote.remoteId = remoteId;
            remote.brandName = brandName;
            remote.IRId = remoteApplianceId;
            remote.roomId = roomId;
            remote.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        }
        catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer() != null
                    && Customer.getCustomer().customerId!= null) {
                DatabaseReference errorRef = GlobalApplication.firebaseRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }


    }


    public static Remote isRemoteAlreadyAvailable(String irId, String remoteId) {
        return new Select().from(Remote.class)
                .where("IRId = ?  AND  remoteId = ?" , irId,remoteId)
                .executeSingle();
    }

    //  Use to get New Remote Id For Specific remote
    public static List<Remote> getAllRemotesByRoomId(String IRID)
    {
        return new Select()
                .from(Remote.class)
                .where("IRId = ?", IRID)
                .orderBy("remoteId DESC")
                .execute();
    }

    public static Remote getRemote(String remoteId) {
        return new Select().from(Remote.class)
                .where("remoteId = ?", remoteId)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<Remote> getAllRemotes()
    {
        return new Select()
                .from(Remote.class)
                .orderBy("remoteId DESC")
                .execute();
    }


    public static void deleteRemote(String remoteId,String IRID){
        new Delete().from(Remote.class).where("remoteId = ?",remoteId).and("IRId = ?",IRID).execute();

    }
}
