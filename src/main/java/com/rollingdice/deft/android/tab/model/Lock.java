package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by Rolling Dice on 6/10/2016.
 */
@Table(name = "Lock")
public class Lock extends Model
{
    @Column(name = "lockId", index = true)
    public String lockId;

    @Column(name = "lockName")
    public String lockName;


    @Column(name = "state")
    public boolean state;

    @Column(name = "toggle")

    public Integer toggle;

    public Lock()
    {
        super();
    }


    public Lock(String lockId, String lockName, boolean lockState, Integer lockToggle)
    {
        super();
        this.lockId = lockId;
        this.lockName = lockName;
        this.state = lockState;
        this.toggle = lockToggle;
    }


    public static Lock getLocks()
    {
        return new Select().from(Lock.class).orderBy("RANDOM()").executeSingle();
    }



}
