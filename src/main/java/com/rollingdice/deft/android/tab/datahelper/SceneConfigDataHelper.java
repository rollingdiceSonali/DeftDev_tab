package com.rollingdice.deft.android.tab.datahelper;

import android.provider.Settings;

import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Scene;
import com.rollingdice.deft.android.tab.model.Scene_Configuration;

import java.util.List;

public class SceneConfigDataHelper
{
    public SceneConfigDataHelper()
    {

    }


    public static void addSceneConfigTable(String sceneConfigId,String Scene_Id,String app_Id,String roomId,
                                           String appStartTime,String appEndTime)
    {
		 /*ActiveAndroid.beginTransaction();
	        (new Delete()).from(Scene_Configuration.class).where("scene_Config_Id = ?", new Object[] {sceneConfigId}).execute();
	        ActiveAndroid.setTransactionSuccessful();
	        ActiveAndroid.endTransaction();*/
        try {

            Scene_Configuration configuration = new Scene_Configuration();
            configuration.scene_Config_Id = sceneConfigId;
            configuration.scene_Id = Scene_Id;
            configuration.appliance_Id = app_Id;
            configuration.room_Id = roomId;
            configuration.appstart_Time = appStartTime;
            configuration.append_Time = appEndTime;

            configuration.save();
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

    public static List<Scene_Configuration> getAllSceneConfig()
    {
        return (new Select().from(Scene_Configuration.class).execute());

    }


    public static Scene getScene()
    {
        return  (Scene) (new Select().from(Scene.class).where("scene_Id=?")).execute();

    }





}
