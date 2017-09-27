package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 1/28/2016.
 */
public class modeDetails
{
    String modeId;
    String moodName;
    String applianceId;
    String applianceName;
    boolean state;
    boolean dimmable;
    Integer toggle;
    Integer dimableToggle;
    int dimableValue;
    String roomName;
    String roomId;
    String slaveId;
    String offAppliances;
    String moodONIRCommand;
    String moodOFFIRCommand;


    public String getMoodOFFIRCommand() {
        return moodOFFIRCommand;
    }

    public void setMoodOFFIRCommand(String moodOFFIRCommand) {
        this.moodOFFIRCommand = moodOFFIRCommand;
    }


    public void setMoodONIRCommand(String moodONIRCommand) {
        this.moodONIRCommand = moodONIRCommand;
    }

    public String getMoodONIRCommand() {
        return moodONIRCommand;
    }

    public String getOffAppliances() {
        return offAppliances;
    }
    public void setOffAppliances(String offAppliances) {
        this.offAppliances = offAppliances;
    }

    public String getModeId() {
        return modeId;
    }
    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    public String getMoodName() {
        return moodName;
    }

    public void setMoodName(String moodName) {
        this.moodName = moodName;
    }

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public Boolean isState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public boolean isDimmable() {
        return dimmable;
    }

    public void setDimmable(boolean dimmable) {
        this.dimmable = dimmable;
    }

    public Integer isToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }

    public Integer isDimableToggle() {
        return dimableToggle;
    }

    public void setDimableToggle(Integer dimableToggle) {
        this.dimableToggle = dimableToggle;
    }

    public int getDimableValue() {
        return dimableValue;
    }

    public void setDimableValue(int dimableValue) {
        this.dimableValue = dimableValue;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }




}
