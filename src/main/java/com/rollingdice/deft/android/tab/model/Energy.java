package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.util.List;

/**
 * Created by Rolling Dice on 11/30/2015.
 */
public class Energy extends Model
{

    @Column(name = "slaveId")
    public String slaveId;

    @Column(name = "applianceId")
    public String applianceId;

    @Column(name = "state")
    public String state;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "customerId")
    public String customerId;

    @Column(name="energy")
    public int energy;

    @Column(name="time")
    public String time;


    public Energy() {
        super();
    }

    public Energy( String applianceId, String state,String roomId, String customerId,String slaveId,int energy,String time)
    {
        super();

        this.applianceId = applianceId;
        this.state = state;
        this.roomId = roomId;
        this.customerId = customerId;
        this.slaveId=slaveId;
        this.energy=energy;
        this.time=time;

    }


    public List<Room> rooms() {
        return getMany(Room.class, "Appliance");
    }

}


