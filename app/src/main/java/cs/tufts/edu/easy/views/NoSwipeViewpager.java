package cs.tufts.edu.easy.views;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoSwipeViewpager extends ViewPager {
    public NoSwipeViewpager(Context context) {
        super(context);
    }

    public NoSwipeViewpager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
}
