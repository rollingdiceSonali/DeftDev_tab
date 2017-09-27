package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by deft on 9/28/2016.
 */
@Table(name = "EnergyDatas")
public class EnergyData extends Model
{

    @Column(name = "datetime",index = true)
    public String datetime;

    @Column(name = "slaveId")
    public String slaveId;

    @Column(name = "energy")
    public int energy;

    @Column(name = "applianceName")
    public String applianceName;

    @Column(name = "applianceId")
    public String applianceId;

    @Column(name = "state")
    public Boolean state;

    @Column(name = "dimmable")
    public boolean dimmable;

    @Column(name = "dim")
    public int dim;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "customerId")
    public String customerId;

    public EnergyData() {
        super();
    }

    public EnergyData(String datetime, String slaveId, int energy, String applianceName, String applianceId,
                      Boolean state, boolean dimmable, int dim, String roomId, String customerId) {
        this.datetime = datetime;
        this.slaveId = slaveId;
        this.energy = energy;
        this.applianceName = applianceName;
        this.applianceId = applianceId;
        this.state = state;
        this.dimmable = dimmable;
        this.dim = dim;
        this.roomId = roomId;
        this.customerId = customerId;
    }

}
