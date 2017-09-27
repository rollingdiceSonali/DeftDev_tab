package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="Scene")
		
public class Scene extends Model
{
	@Column(name="scene_Id")
	public String scene_Id;
	
	@Column(name="scene_Name")
	public String scene_Name;
	
	@Column(name="start_Time")
	public String start_Time;
	
	@Column(name="end_Time")
	public String end_Time;

	@Column(name="isActivated")
	public Integer isActivated;

	@Column(name="isRepeating")
	public boolean isRepeating;

	@Column(name="isAlarmSet")
	public boolean isAlarmSet;

	@Column(name="listOfDays")
	public String listOfDays;



	
	public Scene()
	{
		super();
	}

	public Scene(String scene_Id, String scene_Name, String start_Time,String end_Time,String listOfDays,
				 Integer isActivated,boolean isRepeating,boolean isAlarmSet)
	{
		super();
		this.scene_Id = scene_Id;
		this.scene_Name = scene_Name;
		this.start_Time = start_Time;
		this.end_Time = end_Time;
		this.listOfDays=listOfDays;
		this.isActivated=isActivated;
		this.isRepeating=isRepeating;
		this.isAlarmSet=isAlarmSet;

	}
	

}
