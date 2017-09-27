package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.Configuration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Shortcut;

import java.util.List;

/**
 * Created by Rolling Dice on 12/15/2015.
 */
public class ShortcutDataHelper
{

    public ShortcutDataHelper()
    {

    }


    public static void addShortcut(String shortcutId, String roomId, String slaveId, String applianceId, boolean isState, String applianceName,String applianceType,String applianceTypeId ,boolean isDimmable , int dim)
    {
        try
        {
            ActiveAndroid.beginTransaction();
            new Delete().from(Shortcut.class).where("applianceName = ?", applianceName).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            ActiveAndroid.beginTransaction();

            Shortcut shortcut = new Shortcut();
            shortcut.shortcutId = shortcutId;
            shortcut.roomId = roomId;
            shortcut.slaveId = slaveId;
            shortcut.applianceId = applianceId;
            shortcut.isState = isState;
            shortcut.applianceName = applianceName;
            shortcut.applianceType = applianceType;
            shortcut.applianceTypeId = applianceTypeId;
            shortcut.dimmable = isDimmable;
            shortcut.dim = dim;

            shortcut.save();

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

    public static List<Shortcut> getAllShortcuts()
    {
     return new Select().all().from(Shortcut.class).execute() ;
    }

    public static Shortcut getShortcut(String shortcuId)
    {
        return new Select().from(Shortcut.class).where("shortcueId = ?", shortcuId).executeSingle();
    }

    public static Shortcut deleteShortcut(String applianceName)
    {
        return (Shortcut) new Delete().from(Shortcut.class).where("applianceName=?",applianceName).execute();
    }
}
