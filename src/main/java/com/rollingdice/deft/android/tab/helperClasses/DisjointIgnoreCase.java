package com.rollingdice.deft.android.tab.helperClasses;

/**
 * Created by sudarshan on 12/12/2015.
 */


import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.model.Customer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@SuppressWarnings("ConstantConditions")
public final class DisjointIgnoreCase
{
    public static void main(String[] args) {
        try {
            Collection<String> coll1 = Arrays.asList("donald", "Duck");
            Collection<String> coll2 = Arrays.asList("DONALd", "Donut");
            Collection<String> coll3 = Arrays.asList("Homer", "DONUT");
            Collection<String> coll4 = Arrays.asList("DONALD", "duck");

        /*// will all print false
        System.out.println(disjointIgnoreCase(coll1, coll2));
        System.out.println(disjointIgnoreCase(coll2, coll3));
        System.out.println(disjointIgnoreCase(coll1, coll4));

        // will print true (no common elements)
        System.out.println(disjointIgnoreCase(coll1, coll3));*/
        } catch (Exception e)
        {
            DatabaseReference localRef= GlobalApplication.firebaseRef;
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);

        }
    }

    public static boolean disjointIgnoreCase(Collection<String> coll1, Collection<String> coll2) {
        return Collections.disjoint(lowercased(coll1), lowercased(coll2));
    }

    public static Collection<String> lowercased(Collection<String> coll) {
        return Collections2.transform(coll, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.toLowerCase(Locale.US);
            }
        });
    }
}
