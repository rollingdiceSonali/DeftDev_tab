package com.rollingdice.deft.android.tab.model;

/**
 * Created by koushikpal on 10/10/15.
 */
public class RoomAppliance {

    private String applianceName;
    private String applianceType;
    private String applianceTypeId;
    private String id;
    private boolean dimmable;
    private int dimableValue;
    private String roomId;
    private String slaveId;
    private boolean state;
    private int toggle;
    private int dimableToggle;
    private String roomName;
    private String energy;

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    private boolean processing;

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }



    public Integer isDimableToggle() {
        return dimableToggle;
    }

    public void setDimableToggle(Integer dimableToggle) {
        this.dimableToggle = dimableToggle;
    }

    public int isToggle() {
        return toggle;
    }

    public void setToggle(int toggle) {
        this.toggle = toggle;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public String getApplianceType() {
        return applianceType;
    }

    public void setApplianceType(String applianceType) {
        this.applianceType = applianceType;
    }

    public String getApplianceTypeId() {
        return applianceTypeId;
    }

    public void setApplianceTypeId(String applianceTypeId) {
        this.applianceTypeId = applianceTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isDimmable() {
        return dimmable;
    }

    public void setDimmable(boolean dimmable) {
        this.dimmable = dimmable;
    }

    public int getDimableValue() {
        return dimableValue;
    }

    public void setDimableValue(int dimableValue) {
        this.dimableValue = dimableValue;
    }
}
