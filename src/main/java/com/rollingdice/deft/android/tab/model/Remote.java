package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by deft on 23/03/2017.
 */
@Table(name = "Remote")
public class Remote extends Model {

    @Column(name = "remoteId", index = true)
    public String remoteId;

    @Column(name = "brandName")
    public String brandName;

    @Column(name = "IRId")
    public String IRId;

    @Column(name = "roomId")
    public String roomId;

    @Column(name = "slaveId")
    public String slaveId;


    public Remote()
    {
        super();

    }

    public Remote(String remoteId, String brandName, String IRId, String roomId, String slaveId)
    {
        super();
        this.remoteId = remoteId;
        this.brandName = brandName;
        this.IRId = IRId;
        this.roomId = roomId;
        this.slaveId = slaveId;
    }

    public static Remote getRemotes()
    {
        return new Select().from(Remote.class).orderBy("RANDOM()").executeSingle();
    }
}
