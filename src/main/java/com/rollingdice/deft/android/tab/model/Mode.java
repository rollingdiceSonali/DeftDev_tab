package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by Rolling Dice on 1/28/2016.
 */
@Table(name = "Mode")
public class Mode extends Model
{
    @Column(name = "modeId", index = true)
    public String modeId;

    @Column(name = "moodName")
    public String moodName;
    @Column(name = "applianceId")
    public String applianceId;

    @Column(name = "applianceName")
    public String applianceName;

    @Column(name = "state")
    public String state;

    @Column(name = "dimmable")
    public boolean dimmable;

    @Column(name = "toggle")
    public Integer toggle;

    @Column(name = "dimableToggle")
    public Integer dimableToggle;

    @Column(name = "dimableValue")
    public int dimableValue;

    @Column(name = "roomName")
    public String roomName;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "slaveId")
    public String slaveId;

   /* @Column(name = "offAppliances")
    public String offAppliances;

    @Column(name = "moodONIRCommand")
    public String moodONIRCommand;

    @Column(name = "moodOFFIRCommand")
    public String moodOFFIRCommand;*/

    public Mode()
    {
        super();

    }

    public Mode(String modeId, String moodName, String applianceId, String applianceName, String state, boolean dimmable,
                Integer toggle, Integer dimableToggle,
                int dimableValue, String roomName, String roomId, String slaveId/*,String offAppliances,
                String moodONIRCommand,String moodOFFIRCommand*/)
    {
        super();
        this.modeId = modeId;
        this.moodName = moodName;
        this.applianceId = applianceId;
        this.applianceName = applianceName;
        this.state = state;
        this.dimmable = dimmable;
        this.toggle = toggle;
        this.dimableToggle = dimableToggle;
        this.dimableValue = dimableValue;
        this.roomName = roomName;
        this.roomId = roomId;
        this.slaveId = slaveId;
      /*  this.offAppliances = offAppliances;
        this.moodONIRCommand = moodONIRCommand;
        this.moodOFFIRCommand = moodOFFIRCommand;*/
    }

    public static Mode getModes()
    {
        return new Select().from(Mode.class).orderBy("RANDOM()").executeSingle();
    }



}
