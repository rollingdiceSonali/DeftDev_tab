package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by koushik on 05/06/15.
 */

@Table(name = "Rooms")
public class Room extends Model{
    @Column(name = "roomName")
    public String roomName;

    @Column(name = "roomId", index = true, unique = true)
    public String roomId;

    @Column(name = "roomType")
    public String roomType;

    @Column(name = "customerId")
    public String customerId;

    public Room() {
        super();
    }

    public Room(String name, String roomId, String roomType, String customerId) {
        super();
        this.roomName = name;
        this.roomId = roomId;
        this.roomType = roomType;
        this.customerId = customerId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<Customer> customers() {
        return getMany(Customer.class, "Room");
    }
}
