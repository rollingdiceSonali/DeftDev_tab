package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Curtain;
import com.rollingdice.deft.android.tab.model.WaterSprinklerDetails;

import java.util.List;


/**
 * Created by Rolling Dice on 12/30/2015.
 */
public class WaterSprinklerDataHelper {
    public static void addWaterSprinkler(String sprinklerID, String SprinklerName, String roomId, String roomName, String slaveID) {
        try {
            ActiveAndroid.beginTransaction();
            new Delete().from(WaterSprinklerDetails.class).where("waterSprinklerId = ?", sprinklerID).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();
            WaterSprinklerDetails waterSprinklerDetails = new WaterSprinklerDetails();
            waterSprinklerDetails.waterSprinklerId = sprinklerID;
            waterSprinklerDetails.waterSprinklerName = SprinklerName;
            waterSprinklerDetails.roomId = roomId;
            waterSprinklerDetails.roomName = roomName;
            waterSprinklerDetails.slaveId = slaveID;
            waterSprinklerDetails.save();

            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = GlobalApplication.firebaseRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }


    }

    public static List<WaterSprinklerDetails> getAllWaterSprinklerByRoomId(String roomId) {
        return new Select()
                .from(WaterSprinklerDetails.class)
                .where("roomId = ?", roomId)
                .orderBy("waterSprinklerId DESC")
                .execute();
    }

    public static List<WaterSprinklerDetails> getAllWaterSprinklerByRoomIdAndSlaveWise(String roomId,String slaveID) {
        return new Select()
                .from(WaterSprinklerDetails.class)
                .where("roomId = ?", roomId).and("slaveId = ?", slaveID)
                .orderBy("waterSprinklerId DESC")
                .execute();
    }

    public static WaterSprinklerDetails getWaterSprinkler(String waterSprinklerId) {
        return new Select().from(WaterSprinklerDetails.class)
                .where("waterSprinklerId = ?", waterSprinklerId)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<WaterSprinklerDetails> getAllWaterSprinklers() {
        return new Select().from(WaterSprinklerDetails.class)
                .orderBy("waterSprinklerId DESC")
                .execute();
    }
}
