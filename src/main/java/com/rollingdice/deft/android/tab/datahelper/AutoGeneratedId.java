package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.query.Select;
import com.rollingdice.deft.android.tab.model.Mode;
import com.rollingdice.deft.android.tab.model.Scene;
import com.rollingdice.deft.android.tab.model.Scene_Configuration;

public class AutoGeneratedId 
{
	//static Context c;


	public AutoGeneratedId(/*Activity activity*/) 
	{
		//c=activity;
		
		// TODO Auto-generated constructor stub
	}


	
	public static String autoGenerated_Scene_ID()
	{
		int i=(new Select().all().from(Scene.class).execute()).size();

		String scene_Id = "0" + String.valueOf((i + 1));
		
		//Toast.makeText(c, ""+scene_Id, Toast.LENGTH_SHORT).show();
		
		return scene_Id;
		
		
	}

	public static String autoGenerated_MODE_ID()
	{
		int i=(new Select().all().from(Mode.class).execute()).size();

		//Toast.makeText(c, ""+scene_Id, Toast.LENGTH_SHORT).show();

		return "0" + String.valueOf((i + 1));


	}

	public static String autoGenerated_Scene_Config_ID()
	{
		int j=(new Select().all().from(Scene_Configuration.class).execute()).size();

		String sceneConfig_Id = "0";

		sceneConfig_Id = sceneConfig_Id +(j+1);

		//Toast.makeText(c, ""+scene_Id, Toast.LENGTH_SHORT).show();

		return sceneConfig_Id;


	}


	

}