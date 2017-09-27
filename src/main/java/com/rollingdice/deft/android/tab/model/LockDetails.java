package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 6/7/2016.
 */
public class LockDetails
{
    String lockName;
    String lockId;
    boolean state;
    Integer toggle;

    public LockDetails(String lockName, String lockId, boolean state, Integer toggle) {
        this.lockName = lockName;
        this.lockId = lockId;
        this.state = state;
        this.toggle = toggle;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public boolean isLockState() {
        return state;
    }

    public void setLockState(boolean lockState) {
        this.state = lockState;
    }

    public Integer isLockToggle() {
        return toggle;
    }

    public void setLockToggle(Integer lockToggle) {
        this.toggle = lockToggle;
    }
}
