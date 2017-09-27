package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.Remote;
import com.rollingdice.deft.android.tab.model.Room;

import java.util.List;

/**
 * Created by koushik on 08/06/15.
 */
public class RoomDataHelper
{
    private static DatabaseReference localRef;

    public static void addRoom(String roomId, String roomType, String roomName) {
        try {

            localRef = GlobalApplication.firebaseRef;
            ActiveAndroid.beginTransaction();
            new Delete().from(Room.class).where("roomId = ?", roomId).execute();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            Room room = new Room();
            room.customerId = Customer.getCustomer().customerId;
            room.roomId = roomId;
            room.roomType = roomType;
            room.roomName = roomName;
            room.save();
        } catch (Exception e) {
            if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
    }

    public static List<Room> getAllRooms()
    {
        List<Room> roomList=null;
        try {


        roomList=new Select()
                .from(Room.class)
                .where("customerId = ?", Customer.getCustomer().customerId)
                .orderBy("roomId ASC")
                .execute();
        } catch (Exception e)
        {
            if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null
                    && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage() + "StackTrace " + e.getStackTrace().toString();
                errorRef.setValue(currentStatus);
            }
        }
        return roomList;
    }

    public static Room getRoomDetaildFromId(String roomId){
        return new Select().from(Room.class)
                .where("roomId = ?" , roomId)
                .executeSingle();
    }

    public static void deleteRoom(String Id) {
    }

    public static void updateRoom(String roomId) {
    }

}
