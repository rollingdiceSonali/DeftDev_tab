package com.rollingdice.deft.android.tab.model;

/**
 * Created by koushik on 05/06/15.
 */


public class SensorTab {


    private String id;
    private String roomId;
    private String sensorName;
    private String slaveId;
    private boolean state;
    private Integer toggle;
    private int sensorTypeId;

    public SensorTab() {
    }

    public int getSensorTypeId() {
        return sensorTypeId;
    }

    public void setSensorTypeId(int sensorTypeId) {
        this.sensorTypeId = sensorTypeId;
    }

    public SensorTab(String id, String roomId, String sensorName, String slaveId, boolean state, Integer toggle, int sensorTypeId) {
        this.id = id;
        this.roomId = roomId;
        this.sensorName = sensorName;
        this.slaveId = slaveId;
        this.state = state;
        this.toggle = toggle;
        this.sensorTypeId = sensorTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
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

    public Integer isToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }
}
