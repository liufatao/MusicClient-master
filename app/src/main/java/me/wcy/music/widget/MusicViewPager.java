package me.wcy.music.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 */

public class MusicViewPager extends ViewPager {

    boolean isSliding = false;

    public MusicViewPager(Context context) {
        super(context);
    }

    public MusicViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSliding){
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSliding){
            return false;
        }else {
            return super.onTouchEvent(ev);
        }
    }

    public void setSliding(boolean sliding) {
        isSliding = sliding;
    }
}
