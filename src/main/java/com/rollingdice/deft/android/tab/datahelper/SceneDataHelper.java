package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Scene;

import java.util.List;


public class SceneDataHelper
{
	
	public SceneDataHelper() 
	{
		// TODO Auto-generated constructor stub
		
	}
	
	public static void addSceneTable(String SceneId,String SceneName,String StartTime,String EndTime,String listOfDays,
									 Integer isActivated,boolean isRepeating,boolean isAlarmSet)
	{
		try {
			ActiveAndroid.beginTransaction();
			(new Delete()).from(Scene.class).where("scene_Id = ?", new Object[]{SceneId}).execute();
			ActiveAndroid.setTransactionSuccessful();
			ActiveAndroid.endTransaction();

			Scene scene = new Scene();

			scene.scene_Id = SceneId;

			scene.scene_Name = SceneName;

			scene.start_Time = StartTime;

			scene.end_Time = EndTime;

			scene.listOfDays = listOfDays;

			scene.isActivated = isActivated;

			scene.isRepeating = isRepeating;

			scene.isAlarmSet = isAlarmSet;

			scene.save();
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
	
	public static List<Scene> getScene(String sceneId)
	{
		return  (new Select().from(Scene.class).where("scene_Id=?",sceneId)).execute();
		
	}

	public static List<Scene> getAllScene()
 	{

		return (new Select().from(Scene.class).execute());

	}

}
