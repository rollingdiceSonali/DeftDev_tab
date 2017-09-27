package com.rollingdice.deft.android.tab.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;

import java.io.Serializable;


/**
 * Created by Rolling Dice on 3/24/2016.
 */


@Table(name="WaterSprinklerDetails")
public class WaterSprinklerDetails extends Model
{
    @Column(name="waterSprinklerId")
    public String waterSprinklerId;

    @Column(name="waterSprinklerName")
    public String waterSprinklerName;

    @Column(name="roomId")
    public String roomId;

    @Column(name="roomName")
    public String roomName;

    @Column(name="slaveId")
    public String slaveId;

    @Column(name="toggle")
    public Integer toggle;

    @Column(name="state")
    public boolean state;

    public WaterSprinklerDetails(){

    }

    public WaterSprinklerDetails(String waterSprinklerId, String waterSprinklerName, String roomId,
                                 String roomName, String slaveId, Integer toggle, boolean state)
    {

        try {

            this.waterSprinklerId = waterSprinklerId;
            this.waterSprinklerName = waterSprinklerName;
            this.roomId = roomId;
            this.roomName = roomName;
            this.slaveId = slaveId;

            this.toggle = toggle;
            this.state = state;
        }catch (Exception e)
        {
            DatabaseReference localRef= GlobalApplication.firebaseRef;
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);

        }
    }

    public String getWaterSprinklerId() {
        return waterSprinklerId;
    }

    public void setWaterSprinklerId(String waterSprinklerId) {
        this.waterSprinklerId = waterSprinklerId;
    }

    public String getWaterSprinklerName() {
        return waterSprinklerName;
    }

    public void setWaterSprinklerName(String waterSprinklerName) {
        this.waterSprinklerName = waterSprinklerName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }

    public Integer isToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
