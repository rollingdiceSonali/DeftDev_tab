package com.rollingdice.deft.android.tab.model;

import java.util.List;

/**
 * Created by Rolling Dice on 11/30/2015.
 */
public class  EnergyLog
{


    String customerObject;



    List<EnergyRoom> roomObjectCollection;


    public EnergyLog()
    {

    }

    public EnergyLog(String customerObject,List<EnergyRoom> roomObjectCollection)
    {
        this.customerObject = customerObject;
        this.roomObjectCollection = roomObjectCollection;
    }

    //CustomerGetter Setter

    public void setCustomerObject(String CustomerObject)
    {
        customerObject = CustomerObject;
    }

    public String getCustomerObject()
    {
        return customerObject;
    }


    //Room Getter Setter


    public void setRoomObjectCollection(List<EnergyRoom> roomObject)
    {
        this.roomObjectCollection = roomObject;
    }

    public List<EnergyRoom> getRoomObjectCollection()
    {
        return roomObjectCollection;
    }







}
