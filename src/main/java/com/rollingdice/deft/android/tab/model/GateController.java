package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Rolling Dice on 3/24/2016.
 */
@Table(name = "GateController")
public class GateController extends Model
{
    @Column(name = "gateControllerId", index = true)
    public String gateControllerId;

    @Column(name = "gateControllerName")
    public String gateControllerName;

    @Column(name = "customerId")
    public String customerId;

    @Column(name="level")
    public int level;

    public GateController()
    {
        super();
    }

    public GateController(String gateControllerId, String gateControllerName, String customerId, int level) {
        this.gateControllerId = gateControllerId;
        this.gateControllerName = gateControllerName;
        this.customerId = customerId;
        this.level = level;
    }

    public static GateController getGateController() {
        return new Select().from(GateController.class).orderBy("RANDOM()").executeSingle();
    }

    public List<Room> rooms()
    {
        return getMany(Room.class, "GateController");
    }
}
