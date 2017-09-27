package com.rollingdice.deft.android.tab.model;

import java.util.List;

/**
 * Created by Rolling Dice on 12/2/2015.
 */
public class EnergySlave
{
    private String energySlaveObject;
    private double energySlaveCount;
    private List<EnergyAppliance> energyApplianceCollection;

    public String getEnergySlaveObject() {
        return energySlaveObject;
    }

    public void setEnergySlaveObject(String energySlaveObject) {
        this.energySlaveObject = energySlaveObject;
    }

    public List<EnergyAppliance> getEnergyApplianceCollection() {
        return energyApplianceCollection;
    }

    public void setEnergyApplianceCollection(List energyApplianceCollection) {
        this.energyApplianceCollection = energyApplianceCollection;
    }

    public EnergySlave()
    {

    }

    public EnergySlave(String energySlaveObject,List<EnergyAppliance> energyApplianceCollection)
    {
        this.energySlaveObject = energySlaveObject;
        this.energyApplianceCollection = energyApplianceCollection;
        this.energyApplianceCollection = energyApplianceCollection;
    }

    public double getEnergySlaveCount() {
        return energySlaveCount;
    }

    public void setEnergySlaveCount(double energySlaveCount) {
        this.energySlaveCount = energySlaveCount;
    }

    public List<EnergyAppliance> setEnergyApplainces(List<EnergyAppliance> energyApplainces) {
        List<EnergyAppliance> energyApplainces1 = energyApplainces;
        return energyApplainces;
    }
}
