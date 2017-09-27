package com.rollingdice.deft.android.tab.user;


import com.rollingdice.deft.android.tab.model.Scene;

/**
 * Created by Rolling Dice on 9/25/2015.
 */
public class DataObject
{
    private String mText1;
    private String id;
    private String name;
    private String starttime;
    private String endtime;
    private String listofDays;
    private Integer isActivated;
    private boolean isRepeating;



    DataObject(String text1)
    {
        mText1 = text1;

    }


    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    DataObject(Scene scene)
    {
        this.id=scene.scene_Id;
        this.name=scene.scene_Name;
        this.starttime=scene.start_Time;
        this.endtime=scene.end_Time;
        this.listofDays=scene.listOfDays;
        this.isActivated=scene.isActivated;
        this.isRepeating=scene.isRepeating;
    }

    public String getS_Id() {

        return id;
    }

    public void setS_Id(String id) {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        this.name=n;
    }

    public String getStarttime()
    {
        return starttime;
    }

    public void setStarttime(String starttime)
    {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime)
    {
        this.endtime = endtime;
    }


    public String getListofDays() {
        return listofDays;
    }

    public void setListofDays(String listofDays) {
        this.listofDays = listofDays;
    }

    public Integer isActivated() {
        return isActivated;
    }

    public void setIsActivated(Integer isActivated) {
        this.isActivated = isActivated;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setIsRepeating(boolean isRepeating) {
        this.isRepeating = isRepeating;
    }



}