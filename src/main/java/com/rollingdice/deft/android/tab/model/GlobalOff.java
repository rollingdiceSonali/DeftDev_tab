package com.rollingdice.deft.android.tab.model;

/**
 * Created by Administrator on 8/9/2016.
 */
public class GlobalOff {

    private boolean globalOffState;
    private Integer toggle;

    public Integer isToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }

    public boolean isGlobalOffState() {
        return globalOffState;
    }

    public void setGlobalOffState(boolean globalOffState) {
        this.globalOffState = globalOffState;
    }
}
