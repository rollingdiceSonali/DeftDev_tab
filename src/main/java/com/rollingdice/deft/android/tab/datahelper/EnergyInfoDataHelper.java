package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.EnergyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 10/13/2016.
 */
public class EnergyInfoDataHelper
{

    private static DatabaseReference localRef;
    public static void addToEnegyInfo(List<String> idString, List<Integer> energyString)
    {
        try
        {
            localRef = GlobalApplication.firebaseRef;

            ActiveAndroid.beginTransaction();
            EnergyInfo energyInfo=new EnergyInfo();

            energyInfo.idString=idString;
            energyInfo.energyString=energyString;
            energyInfo.save();

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

    public static List<EnergyInfo> getAllEneryInfo()
    {
        return new Select().from(EnergyInfo.class).execute();
    }
}
