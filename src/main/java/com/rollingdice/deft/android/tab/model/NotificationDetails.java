package com.rollingdice.deft.android.tab.model;

import android.support.annotation.NonNull;

/**
 * Created by Rolling Dice on 3/9/2016.
 */
public class NotificationDetails implements Comparable<NotificationDetails>
{

    String notificationTime;
    String notificationText;

    public NotificationDetails(String notificationTime, String notificationText) {
        this.notificationTime = notificationTime;
        this.notificationText = notificationText;
    }

    public String getNotificationTime()
    {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime)
    {
        this.notificationTime = notificationTime;
    }

    public String getNotificationText()
    {
        return notificationText;
    }

    public void setNotificationText(String notificationText)
    {
        this.notificationText = notificationText;
    }

    @Override
    public int compareTo(@NonNull NotificationDetails notificationDetails) {
        if(this.getNotificationTime() != null && notificationDetails.getNotificationTime() != null){
            return this.getNotificationTime().compareTo(notificationDetails.getNotificationTime());
        }
        return 0;
    }
}
