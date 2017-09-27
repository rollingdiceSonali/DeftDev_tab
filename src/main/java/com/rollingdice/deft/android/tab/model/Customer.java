package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by koushik on 04/06/15.
 */

@Table(name = "Customers")
public class Customer extends Model{

    @Column(name = "customerName")
    public String name;

    @Column(name = "customerPhone")
    public String customerPhone;

    @Column(name = "customerEmail")
    public String customerEmail;

    @Column(name = "customerId", index = true)
    public String customerId;

    @Column(name = "customerPassword")
    public String customerPassword;

    @Column(name = "customerMasterDeviceId")
    public String customerMasterDeviceId;

    @Column(name="userType")
    public String userType;

    @Column(name="ipAddress")
    public String ipAddress;

    @Column(name="port")
    public int port;




    public Customer() {
        super();
    }

    public Customer(String name, String customerPhone, String customerEmail, String customerId, String customerPassword,
                    String customerMasterDeviceId,String userType,String ipAddress,int port) {
        super();
        this.name = name;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerId = customerId;
        this.customerPassword = customerPassword;
        this.customerMasterDeviceId = customerMasterDeviceId;
        this.userType = userType;
        this.ipAddress=ipAddress;
        this.port=port;
    }

    public static Customer getCustomer() {
        return new Select().from(Customer.class).orderBy("RANDOM()").executeSingle();
    }
}
