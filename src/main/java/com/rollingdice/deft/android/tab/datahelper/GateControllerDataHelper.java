package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.GateController;

import java.util.List;

/**
 * Created by deft on 29/12/2016.
 */
public class GateControllerDataHelper {

    public static void addGateController(String gateControllerId, String gateControllerName,
                                         String customerId, int dimValue)
    {
        try {
            ActiveAndroid.beginTransaction();
            new Delete().from(GateController.class).where("gateControllerId = ?", gateControllerId).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();
            GateController gateController = new GateController();
            gateController.gateControllerId = gateControllerId;
            gateController.gateControllerName = gateControllerName;
            gateController.customerId = customerId;
            gateController.level = dimValue;
            gateController.save();
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

    public static GateController getGateController(String gateControllerId) {
        return new Select().from(GateController.class)
                .where("gateControllerId = ?", gateControllerId)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<GateController> getAllGateController()
    {
        return new Select()
                .from(GateController.class)
                .orderBy("gateControllerId DESC")
                .execute();
    }
}
