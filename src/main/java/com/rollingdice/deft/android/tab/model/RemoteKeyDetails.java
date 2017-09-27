package com.rollingdice.deft.android.tab.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by sonali on 7/7/17.
 */
@Table(name = "RemoteKeyDetails")
public class RemoteKeyDetails extends Model{

    @Column(name = "remoteId")
    String remoteId;


    @Column(name = "irId")
    String irId;

    @Column(name = "newKey")
    String newKey;

    @Column(name = "oldKey")
    String oldKey;

    @Column(name = "keyId")
    String keyId;




    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }

    public void setOldKey(String oldKey) {
        this.oldKey = oldKey;
    }



    public void setOldTag(String oldTag) {
        this.keyId = oldTag;
    }


    public String getRemoteId() {
        return remoteId;
    }

    public String getNewKey() {
        return newKey;
    }


    public void setIrId(String irId) {
        this.irId = irId;
    }

    public String getIrId() {
        return irId;
    }

    public String getOldKey() {
        return oldKey;
    }

    public String getOldTag() {
        return keyId;
    }
}
