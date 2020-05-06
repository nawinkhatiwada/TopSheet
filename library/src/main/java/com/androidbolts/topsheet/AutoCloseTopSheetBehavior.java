package com.androidbolts.topsheet;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AutoCloseTopSheetBehavior<V extends View> extends TopSheetBehavior<V> {

    public AutoCloseTopSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                getState() == TopSheetBehavior.STATE_EXPANDED) {

            Rect outRect = new Rect();
            child.getGlobalVisibleRect(outRect);

            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                setState(TopSheetBehavior.STATE_COLLAPSED);
            }
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }
}