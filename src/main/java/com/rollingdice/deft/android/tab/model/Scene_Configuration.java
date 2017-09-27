package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

@Table(name="Scene_Configuration")
public class Scene_Configuration extends Model implements Serializable
{
	
	@Column(name="scene_Config_Id")
	public String scene_Config_Id;
	
	@Column(name="scene_Id")
	public String scene_Id;
	
	@Column(name="appliance_Id")
	public String appliance_Id;

	@Column(name="room_Id")
	public String room_Id;

	@Column(name="start_Time")
	public String appstart_Time;
	
	@Column(name="end_Time")
	public String append_Time;
	
	
	public Scene_Configuration()
	{
		super();
	}

	
	public Scene_Configuration(String scene_Config_Id, String scene_Id,
							   String appliance_Id,String room_Id ,String start_Time,
			                   String end_Time) {
		super();
		this.scene_Config_Id = scene_Config_Id;
		this.scene_Id = scene_Id;
		this.appliance_Id = appliance_Id;
		this.room_Id=room_Id;
		this.appstart_Time = start_Time;
		this.append_Time = end_Time;


	}



	
	

}
