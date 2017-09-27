package com.rollingdice.deft.android.tab.user;

import android.content.Context;
import android.graphics.Typeface;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;


import java.util.Hashtable;

/**
 * Created by Rolling Dice on 11/20/2015.
 */
public class FontCache
{

    public static Hashtable fontCache = new Hashtable();

    public FontCache()
    {
        DatabaseReference localRef = GlobalApplication.firebaseRef;
    }

    public static Typeface get(String s, Context context)
    {
        Typeface typeface1 = (Typeface)fontCache.get(s);
        Typeface typeface = typeface1;
        if (typeface1 == null)
        {
            try
            {
                typeface = Typeface.createFromAsset(context.getAssets(), s);
            }
            // Misplaced declaration of an exception variable
            catch (Exception e)
            {
                /*Firebase errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);*/
                return null;
            }
            fontCache.put(s, typeface);
        }
        return typeface;
    }

}
