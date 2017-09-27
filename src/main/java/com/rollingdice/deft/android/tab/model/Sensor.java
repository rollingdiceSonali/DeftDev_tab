package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by koushik on 05/06/15.
 */

@Table(name = "Sensors")
public class Sensor extends Model {


    @Column(name = "sensorName")
    public String sensorName;

    @Column(name = "state")
    public String state;

    @Column(name = "slaveId")
    public String slaveId;

    @Column(name = "sensorId", index = true)
    public String sensorId;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "sensorTypeId")
    public int sensorTypeId;

    @Column(name = "customerId")
    public String customerId;

    public Sensor() {
        super();
    }

    public Sensor(String sensorName, String state, String sensorId, String roomId, String customerId, int sensorTypeId) {
        super();
        this.sensorName = sensorName;
        this.state = state;
        this.sensorId = sensorId;
        this.roomId = roomId;
        this.customerId = customerId;
        this.sensorTypeId = sensorTypeId;
    }

    public static Sensor getSensors() {
        return new Select().from(Sensor.class).orderBy("RANDOM()").executeSingle();
    }

    public List<Room> rooms() {
        return getMany(Room.class, "Sensor");
    }
}
