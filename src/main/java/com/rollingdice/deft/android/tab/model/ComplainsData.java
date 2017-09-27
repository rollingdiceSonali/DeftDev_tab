package com.rollingdice.deft.android.tab.model;

/**
 * Created by Rolling Dice on 3/31/2016.
 */
public class ComplainsData
{
    String CustomerId,CustimerName,CustomerMobileNumber,CompalinsId,shortDec,description,submittedBy,state;

    public ComplainsData(String customerId, String custimerName, String customerMobileNumber, String compalinsId,
                         String shortDec, String description, String submittedBy,String state) {
        CustomerId = customerId;
        CustimerName = custimerName;
        CustomerMobileNumber = customerMobileNumber;
        CompalinsId = compalinsId;
        this.shortDec = shortDec;
        this.description = description;
        this.submittedBy = submittedBy;
        this.state=state;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getCustimerName() {
        return CustimerName;
    }

    public void setCustimerName(String custimerName) {
        CustimerName = custimerName;
    }

    public String getCustomerMobileNumber() {
        return CustomerMobileNumber;
    }

    public void setCustomerMobileNumber(String customerMobileNumber) {
        CustomerMobileNumber = customerMobileNumber;
    }

    public String getCompalinsId() {
        return CompalinsId;
    }

    public void setCompalinsId(String compalinsId) {
        CompalinsId = compalinsId;
    }

    public String getShortDec() {
        return shortDec;
    }

    public void setShortDec(String shortDec) {
        this.shortDec = shortDec;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
