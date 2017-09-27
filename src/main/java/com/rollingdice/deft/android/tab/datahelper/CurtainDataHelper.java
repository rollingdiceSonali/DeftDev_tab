package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Curtain;


import java.util.List;


/**
 * Created by Rolling Dice on 12/30/2015.
 */
public class CurtainDataHelper
{
    public static void addCurtain(String curtainId, String curtainName, String roomId,String roomName, String customerId, int dimValue)
    {
        try {
            ActiveAndroid.beginTransaction();
            new Delete().from(Curtain.class).where("curtainId = ?", curtainId).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();
            Curtain curtain = new Curtain();
            curtain.curtainId = curtainId;
            curtain.curtainName = curtainName;
            curtain.roomId = roomId;
            curtain.roomName = roomName;
            curtain.customerId = customerId;
            curtain.dimValue = dimValue;
            curtain.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        }
        catch (Exception e)
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

    public static List<Curtain> getAllCurtainsByRoomId(String roomId)
    {
        return new Select()
                .from(Curtain.class)
                .where("roomId = ?", roomId)
                .orderBy("curtainId DESC")
                .execute();
    }

    public static Curtain getCurtain(String curtainId) {
        return new Select().from(Curtain.class)
                .where("curtainId = ?", curtainId)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<Curtain> getAllCurtains()
    {
        return new Select()
                .from(Curtain.class)
                .orderBy("curtainId DESC")
                .execute();
    }
}
