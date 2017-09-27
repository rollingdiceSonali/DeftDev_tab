package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Rolling Dice on 12/30/2015.
 */
@Table(name = "Curtains")
public class Curtain extends Model
{
    @Column(name = "curtainId", index = true)
    public String curtainId;

    @Column(name = "curtainName")
    public String curtainName;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "roomName")
    public String roomName;

    @Column(name = "customerId")
    public String customerId;

    @Column(name="level")
    public int dimValue;

    public String curtainType;

    public Curtain()
    {
        super();
    }

    public Curtain(String curtainId, String curtainName,String roomId,String roomName, String customerId, int dimValue, String curtainType) {
        this.curtainId = curtainId;
        this.curtainName = curtainName;
        this.roomId = roomId;
        this.roomName=roomName;
        this.customerId = customerId;
        this.dimValue = dimValue;
        this.curtainType = curtainType;
    }

    public static Curtain getCurtain() {
        return new Select().from(Sensor.class).orderBy("RANDOM()").executeSingle();
    }

    public List<Room> rooms()
    {
        return getMany(Room.class, "Curtain");
    }

}
