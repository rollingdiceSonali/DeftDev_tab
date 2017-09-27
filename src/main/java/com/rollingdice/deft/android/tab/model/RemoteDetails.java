package com.rollingdice.deft.android.tab.model;

import java.io.Serializable;

/**
 * Created by Rolling Dice on 7/28/2016.
 */
public class RemoteDetails implements Serializable
{
    String Ids;
    String roomId;
    String roomName;
    String IRId;
    String remoteType;
    String remoteId;
    String brand;


    public RemoteDetails(){

    }
    public RemoteDetails(String Id,String roomId, String roomName, String IRId, String remoteType, String remoteId, String brand)
    {
        super();
        this.Ids = Id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.IRId = IRId;
        this.remoteType = remoteType;
        this.remoteId = remoteId;
        this.brand = brand;
    }


    public String getId() {
        return Ids;
    }

    public void setId(String id) {
        this.Ids = id;
    }

    String id;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getIRId() {
        return IRId;
    }

    public void setIRId(String IRId) {
        this.IRId = IRId;
    }

    public String getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(String remoteType) {
        this.remoteType = remoteType;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
