package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.rollingdice.deft.android.tab.model.Customer;


public class RecyclerItemClickListenerAndDetector extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {
    private int margin;
    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;
    private DatabaseReference localRef;

    public RecyclerItemClickListenerAndDetector(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
    }

    public RecyclerItemClickListenerAndDetector(Context context, OnItemClickListener listener) {
        mListener = listener;
        localRef=GlobalApplication.firebaseRef;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e)
    {
        try {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                //noinspection deprecation
                mListener.onItemClick(childView, view.getChildPosition(childView));
                return true;
            }
        }catch (Exception ex)
        {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = ex.getMessage();
            errorRef.setValue(currentStatus);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}