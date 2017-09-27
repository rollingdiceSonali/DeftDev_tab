package com.rollingdice.deft.android.tab.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by koushik on 05/06/15.
 */

@Table(name = "Appliances")
public class Appliance extends Model
{

    @Column(name = "applianceTypeId")
    public int applianceTypeId;

    @Column(name = "slaveId")
    public String slaveId;

    @Column(name = "energy")
    public String energy;

    @Column(name = "applianceName")
    public String applianceName;

    @Column(name = "applianceId", index = true)
    public String applianceId;

    @Column(name = "state")
    public String state;

    @Column(name = "dimmable")
    public boolean dimmable;

    @Column(name = "dim")
    public int dim;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "customerId")
    public String customerId;

    public Appliance() {
        super();
    }

    public Appliance(int applianceTypeId, String applianceName, String applianceId, String state, boolean dimmable, int dim, String roomId, String customerId) {
        super();
        this.applianceTypeId = applianceTypeId;
        this.applianceName = applianceName;
        this.applianceId = applianceId;
        this.state = state;
        this.dimmable = dimmable;
        this.dim = dim;
        this.roomId = roomId;
        this.customerId = customerId;
    }

    public static Appliance getAppliances() {
        return new Select().from(Appliance.class).orderBy("RANDOM()").executeSingle();
    }

    public List<Room> rooms() {
        return getMany(Room.class, "Appliance");
    }

}
