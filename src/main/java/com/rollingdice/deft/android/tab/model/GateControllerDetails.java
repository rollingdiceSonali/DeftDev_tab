package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 3/24/2016.
 */
public class GateControllerDetails
{
    private String gateControllerId;

    public GateControllerDetails(String gateControllerId, String gateControllerName, int gateControllerLevel, Integer toggle) {
        this.gateControllerId = gateControllerId;
        this.gateControllerName = gateControllerName;
        this.gateControllerLevel = gateControllerLevel;
        this.toggle = toggle;
    }

    public String getGateControllerName() {
        return gateControllerName;

    }

    public void setGateControllerName(String gateControllerName) {
        this.gateControllerName = gateControllerName;
    }

    public int getGateControllerLevel() {
        return gateControllerLevel;
    }

    public void setGateControllerLevel(int gateControllerLevel) {
        this.gateControllerLevel = gateControllerLevel;
    }

    public Integer getToggle() {
        return toggle;
    }

    private String gateControllerName;
    private int gateControllerLevel;
    private Integer toggle;

    public String getGateControllerId()
    {
        return gateControllerId;
    }

    public void setGateControllerId(String gateControllerId)
    {
        this.gateControllerId = gateControllerId;
    }

    public Integer isToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }

}
