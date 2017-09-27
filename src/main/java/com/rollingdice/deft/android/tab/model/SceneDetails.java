package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 3/10/2016.
 */
public class SceneDetails
{
    String sceneId,sceneName,startTime,endTime,listOfDays,appStartTime,appEndTime,applainceId,roomId,sceneConfigId;
    String applianceName;
    Integer isActivated;
    boolean isRepeating;
            boolean isAlarmSet;
    int pendingIntentOnId;
    int  pendingIntentOffId;
    String day,applianceType;
    int curtainLevel;


    public SceneDetails(String sceneId,String sceneName,String startTime,String endTime,String days,boolean isRepeating,String appStartTime,String appEndTime,
                        String applainceId,String applianceName ,String roomId, int pendingIntentOnId,int pendingIntentOffId ,String sceneConfigId,
                        Integer isActivated,boolean isAlarmSet,String day,String applianceType,int curtainLevel)
    {
        this.sceneId = sceneId;
        this.sceneName = sceneName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.listOfDays = days;
        this.isRepeating=isRepeating;
        this.appStartTime = appStartTime;
        this.appEndTime = appEndTime;
        this.applainceId = applainceId;
        this.applianceName=applianceName;
        this.roomId = roomId;
        this.pendingIntentOnId= pendingIntentOnId;
        this.pendingIntentOffId=pendingIntentOffId;
        this.sceneConfigId = sceneConfigId;
        this.isActivated = isActivated;
        this.isAlarmSet=isAlarmSet;
        this.day=day;
        this.applianceType=applianceType;
        this.curtainLevel=curtainLevel;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getListOfDays() {
        return listOfDays;
    }

    public void setListOfDays(String listOfDays) {
        this.listOfDays = listOfDays;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAppStartTime() {
        return appStartTime;
    }

    public void setAppStartTime(String appStartTime) {
        this.appStartTime = appStartTime;
    }

    public String getAppEndTime() {
        return appEndTime;
    }

    public void setAppEndTime(String appEndTime) {
        this.appEndTime = appEndTime;
    }

    public String getApplainceId() {
        return applainceId;
    }

    public void setApplainceId(String applainceId) {
        this.applainceId = applainceId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getPendingIntentOnId() {
        return pendingIntentOnId;
    }

    public void setPendingIntentOnId(int pendingIntentOnId) {
        this.pendingIntentOnId = pendingIntentOnId;
    }

    public int getPendingIntentOffId() {
        return pendingIntentOffId;
    }

    public void setPendingIntentOffId(int pendingIntentOffId) {
        this.pendingIntentOffId = pendingIntentOffId;
    }

    public String getSceneConfigId() {
        return sceneConfigId;
    }

    public void setSceneConfigId(String sceneConfigId) {
        this.sceneConfigId = sceneConfigId;
    }

    public Integer isActivated() {
        return isActivated;
    }

    public void setIsActivated(Integer isActivated) {
        this.isActivated = isActivated;
    }

    public String getApplianceType() {
        return applianceType;
    }

    public void setApplianceType(String applianceType) {
        this.applianceType = applianceType;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setIsRepeating(boolean isRepeating) {
        this.isRepeating = isRepeating;
    }

    public int getCurtainLevel() {
        return curtainLevel;
    }

    public void setCurtainLevel(int curtainLevel) {
        this.curtainLevel = curtainLevel;
    }

    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    public void setIsAlarmSet(boolean isAlarmSet) {
        this.isAlarmSet = isAlarmSet;
    }
}
