package com.tcorner.msheet.ui.play;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * overrides error for photoview
 * Created by Tenten Ponce on 10/24/2017.
 */

public class SheetViewPager extends ViewPager {

    public SheetViewPager(Context context) {
        super(context);
    }

    public SheetViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
