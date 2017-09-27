package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.rollingdice.deft.android.tab.model.Remote;
import com.rollingdice.deft.android.tab.model.RemoteKeyDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonali on 7/7/17.
 */

public class RemoteKeysDataHelper {


    public static void addRemoteKey(RemoteKeyDetails remoteKey){
        String oldKey = null;
        /// Delete Old Entry of that key
        ActiveAndroid.beginTransaction();

       /* if(getRemoteKey(remoteKey.getRemoteId(),remoteKey.getOldTag()) != null){
            oldKey = getRemoteKey(remoteKey.getRemoteId(),remoteKey.getOldTag()).getOldKey();
        }*/
      //  new Delete().from(RemoteKeyDetails.class).where("irId = ? AND remoteId = ? AND keyId = ?",remoteKey.getIrId(), remoteKey.getRemoteId(),remoteKey.getOldTag()).execute();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        ActiveAndroid.beginTransaction();

        RemoteKeyDetails remoteKeys = new RemoteKeyDetails();
        remoteKeys.setIrId(remoteKey.getIrId());
        remoteKeys.setRemoteId(remoteKey.getRemoteId());
        remoteKeys.setOldKey(remoteKey.getOldKey());
        remoteKeys.setNewKey(remoteKey.getNewKey());
        remoteKeys.setOldTag(remoteKey.getOldTag());

        remoteKeys.save();

         /*  if(oldKey != null && !oldKey.equals("")){
            remoteKeys.setOldKey(oldKey);
        }else{

        }
*/
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

    }


    public static RemoteKeyDetails getRemoteKey(String irId,String remoteId,String OldTag) {
        return new Select().from(RemoteKeyDetails.class)
                .where("irId = ? AND remoteId = ? AND keyId = ?", irId,remoteId,OldTag)
                .executeSingle();
    }


    public static List<RemoteKeyDetails> getRemoteKeys(String irId, String remoteId) {
        return new Select().from(RemoteKeyDetails.class)
                .where("irId = ? AND remoteId = ?", irId,remoteId)
                .execute();
    }


    public static void updateRemoteKey(RemoteKeyDetails remoteKeyDetails){

        new Update(RemoteKeyDetails.class)
                .set("newKey= ? AND oldKey=? ",remoteKeyDetails.getNewKey(),remoteKeyDetails.getOldKey())
                .where("remoteId = ? AND irId = ? AND keyId = ? ", remoteKeyDetails.getRemoteId(),remoteKeyDetails.getIrId(),remoteKeyDetails.getOldTag())
                .execute();
    }











}
