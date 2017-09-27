package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 12/18/2015.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table( name = "Shortcut")
public class Shortcut extends Model
{

    @Column(name = "shortcutId")
    public String shortcutId;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "slaveId", index = true)
    public String slaveId;

    @Column(name="applainceId")
    public String applianceId;

    @Column(name="isState")
    public boolean isState;

    @Column(name = "applianceName")
    public String applianceName;

    @Column(name = "applianceType")
    public String applianceType;

    @Column(name = "applianceTypeId")
    public String applianceTypeId;

    @Column(name = "dim")
    public int dim;

    @Column(name = "dimmable")
    public boolean dimmable;

    public Shortcut(){
        super();
    }

    public Shortcut(String shortcutId, String roomId, String slaveId,
                    String applianceId, boolean isState, String applianceName,String applianceType,String applianceTypeId ,int dim, boolean dimmable)
    {
        super();
        this.shortcutId = shortcutId;
        this.roomId = roomId;
        this.slaveId = slaveId;
        this.applianceId = applianceId;
        this.isState = isState;
        this.applianceName = applianceName;
        this.applianceType=applianceType;
        this.applianceTypeId=applianceTypeId;
        this.dim = dim;
        this.dimmable = dimmable;
    }


}
