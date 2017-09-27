package com.rollingdice.deft.android.tab.model;

import java.io.Serializable;

/**
 * Created by koushikpal on 06/12/15.
 */
public class RoomDetails implements Serializable {
    private String roomName;
    private String roomType;
    private String roomId;
    private String lastMotionDetected;

    public RoomDetails(String roomName, String roomType, String roomId) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomId = roomId;

    }

    public RoomDetails() {
    }

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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getLastMotionDetected() {
        return lastMotionDetected;
    }

    public void setLastMotionDetected(String lastMotionDetected) {
        this.lastMotionDetected = lastMotionDetected;
    }
}
