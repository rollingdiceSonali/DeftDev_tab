package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Mode;


import java.util.List;


/**
 * Created by Rolling Dice on 1/28/2016.
 */
public class ModeDataHelper {
    public static void addMode(String modeId, String moodName, String applianceId, String applianceName,
                               String state, boolean dimmable, Integer toggle, Integer dimableToggle,
                               int dimableValue, String roomName, String roomId, String slaveId/*,String offAppliances,
                               String moodONIRCommand,String moodOFFIRCommand*/) {

        try {


            ActiveAndroid.beginTransaction();

            Mode mode = new Mode();
            mode.modeId = modeId;
            mode.moodName = moodName;
            mode.applianceId = applianceId;
            mode.applianceName = applianceName;
            mode.state = state;
            mode.dimmable = dimmable;
            mode.toggle = toggle;
            mode.dimableToggle = dimableToggle;
            mode.dimableValue = dimableValue;
            mode.roomName = roomName;
            mode.roomId = roomId;
            mode.slaveId = slaveId;
       /*     mode.moodONIRCommand = moodONIRCommand;
            mode.moodOFFIRCommand = moodOFFIRCommand;
            mode.offAppliances = offAppliances;*/


            mode.save();
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

    public static Mode deleteMode(String ModeName)
    {
        return (Mode) new Delete().from(Mode.class).where("moodName=?", ModeName).execute();
    }

    public static List<Mode> getAllModes()
    {
        return new Select().from(Mode.class) .execute();

    }




public ModeDataHelper(){}


}
