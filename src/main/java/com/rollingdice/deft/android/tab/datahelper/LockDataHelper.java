package com.rollingdice.deft.android.tab.datahelper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.rollingdice.deft.android.tab.model.Lock;


import java.util.List;


/**
 * Created by Rolling Dice on 6/10/2016.
 */
public class LockDataHelper
{
    public static void addLock(String lockId, String lockName, boolean lockState, Integer lockToggle)
    {

        ActiveAndroid.beginTransaction();
        Lock lock=new Lock();
        lock.lockId=lockId;
        lock.lockName=lockName;
        lock.state=lockState;
        lock.toggle=lockToggle;
        lock.save();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

    }

    public static List<Lock> getAllLocks()
    {
        return new Select().all()
                .from(Lock.class).execute();
    }

    public static Lock deleteLock(String lockName)
    {
        return (Lock) new Delete().from(Lock.class).where("lockName=?", lockName).execute();
    }

    public LockDataHelper() {
    }
}
