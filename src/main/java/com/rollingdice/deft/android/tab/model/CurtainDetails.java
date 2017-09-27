package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 12/31/2015.
 */
public class CurtainDetails
{
    private String curtainId;
    private String curtainName;
    private String roomId;
    private String slaveId;
    private String curtainRoomName;
    private int curtainLevel;
    private Integer toggle;
    private String curtainType;

    public String getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }

    public String getCurtainType() { return curtainType; }

    public void setCurtainType(String curtainType) { this. curtainType = curtainType;}

    public String getCurtainId()
    {
        return curtainId;
    }

    public void setCurtainId(String curtainId)
    {
        this.curtainId = curtainId;
    }

    public String getCurtainName()
    {
        return curtainName;
    }

    public void setCurtainName(String curtainName)
    {
        this.curtainName = curtainName;
    }

    public int getcurtainLevel()
    {
        return curtainLevel;
    }

    public void setcurtainLevel(int dimValue)
    {
        this.curtainLevel = dimValue;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getcurtainRoomName()
    {
        return curtainRoomName;
    }

    public void setcurtainRoomName(String roomName)
    {
        this.curtainRoomName = roomName;
    }

    public Integer isToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }
}
