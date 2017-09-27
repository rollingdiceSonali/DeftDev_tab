package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 7/28/2016.
 */
public class BrandDetails
{
    private String appliance;
    private String id;
    private String brand;

    public BrandDetails(String appliance,String brand, String id) {
        this.appliance = appliance;
        this.id = id;
        this.brand = brand;
    }

    public String getAppliance() {
        return appliance;
    }

    public void setAppliance(String appliance) {
        this.appliance = appliance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
