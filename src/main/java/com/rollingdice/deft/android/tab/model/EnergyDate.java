package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 12/2/2015.
 */
public class EnergyDate
{
    private String energyDateObject;

    private double energyDateCount;

    private String energy,level,state;



    public EnergyDate()
    {

    }

    public EnergyDate(String level,String state,String energy)
    {
        this.energy=energy;
        this.level=level;
        this.state=state;

    }

    public String getEnergyDateObject() {
        return energyDateObject;
    }

    public  void setEnergyDateObject(String energyDateObject) {
        this.energyDateObject = energyDateObject;
    }
    public double getEnergyDateCount() {
        return energyDateCount;
    }

    public void setEnergyDateCount(double energyDateCount) {
        this.energyDateCount = energyDateCount;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

}
