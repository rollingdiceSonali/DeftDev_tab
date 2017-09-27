package com.rollingdice.deft.android.tab.model;

import java.util.List;

/**
 * Created by Rolling Dice on 12/2/2015.
 */
public class EnergyAppliance
{
    private String energyApplianceObject;
    private List<EnergyDate> energyDateCollection;
    private double energyAlpplianceCount;


    public String getEnergyApplianceObject() {
        return energyApplianceObject;
    }

    public void setEnergyApplianceObject(String energyApplianceObject) {
        this.energyApplianceObject = energyApplianceObject;
    }

    public List<EnergyDate> getEnergyDateCollection() {
        return energyDateCollection;
    }

    public void setEnergyDateCollection(List energyDateCollection) {
        this.energyDateCollection = energyDateCollection;
    }

    public EnergyAppliance()
    {

    }

    public EnergyAppliance(String energyApplianceObject, List<EnergyDate> energyDateCollection)
    {
        this.energyDateCollection = energyDateCollection;
        this.energyApplianceObject = energyApplianceObject;
    }

    public double getEnergyAlpplianceCount() {
        return energyAlpplianceCount;
    }

    public void setEnergyAlpplianceCount(double energyAlpplianceCount) {
        this.energyAlpplianceCount = energyAlpplianceCount;
    }
}
