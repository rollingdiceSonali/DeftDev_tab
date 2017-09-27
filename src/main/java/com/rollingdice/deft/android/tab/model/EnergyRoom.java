package com.rollingdice.deft.android.tab.model;

import java.util.List;

/**
 * Created by Rolling Dice on 12/2/2015.
 */
public class EnergyRoom
{
    private String roomObject;
    private double energyRoomCount;


    List<EnergySlave> slaveObjectCollection;

    public double getEnergyRoomCount()
    {
        return energyRoomCount;
    }

    public void setEnergyRoomCount(double energyRoomCount) {
        this.energyRoomCount = energyRoomCount;
    }




    public EnergyRoom()
    {

    }


    public EnergyRoom(String roomObject,List<EnergySlave> slaveObjectCollection)
    {
        this.roomObject = roomObject;
        this.slaveObjectCollection = slaveObjectCollection;

    }

    public String getRoomObject() {
        return roomObject;
    }

    public void setRoomObject(String roomObject) {
        this.roomObject = roomObject;
    }

    public List<EnergySlave> getSlaveObjectCollection() {
        return slaveObjectCollection;
    }

    public void setSlaveObjectCollection(List<EnergySlave> slaveObjectCollection) {
        this.slaveObjectCollection = slaveObjectCollection;
    }

    /**
     * Created by Rolling Dice on 12/2/2015.
     */
    public static class EnergyTime
    {

        private String energy,level,state;
        private double energyTimeCount;
        private String EnergyTimeObject;


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

        public String getState() {
            return state;
        }

        public double getEnergyTimeCount() {
            return energyTimeCount;
        }

        public void setEnergyTimeCount(double energyTimeCount) {
            this.energyTimeCount = energyTimeCount;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getEnergyTimeObject() {
            return EnergyTimeObject;
        }

        public void setEnergyTimeObject(String energyTimeObject) {
            EnergyTimeObject = energyTimeObject;
        }

        public EnergyTime()
        {

        }
        public EnergyTime(String Energy,String State,String Level)
        {
            this.energy = Energy;
            this.state = State;
            this.level = Level;
        }

    }
}
