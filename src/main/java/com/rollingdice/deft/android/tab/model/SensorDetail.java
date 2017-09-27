package com.rollingdice.deft.android.tab.model;

/**
 * Created by koushikpal on 09/12/15.
 */
public class SensorDetail {

    private String id;
    private String roomId;
    private String sensorName;

    public int getSensorTypeId() {
        return sensorTypeId;
    }

    public void setSensorTypeId(int sensorTypeId) {
        this.sensorTypeId = sensorTypeId;
    }

    private int sensorTypeId;
    private String slaveId;
    private boolean state;
    private Integer toggle;

    public SensorDetail() {
    }

    public SensorDetail(String id, String roomId, String sensorName, String slaveId, boolean state, Integer toggle, int sensorTypeId) {
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


