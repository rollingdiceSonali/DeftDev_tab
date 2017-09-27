package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rolling Dice on 10/13/2016.
 */

@Table(name = "EnergyData")
public class EnergyInfo extends Model
{
    @Column(name = "idString")
    public List<String> idString;

    @Column(name = "energyString")
    public List<Integer> energyString;


    public EnergyInfo()
    {
        super();
    }

    public EnergyInfo(List<String> idString,List<Integer> energyString)
    {
        this.idString = idString;
        this.energyString = energyString;
    }
}
