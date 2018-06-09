package com.d9lab.ati.whatie_android_demo.demonActivity.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.d9lab.ati.whatie_android_demo.R;

/**
 * Created by 神火 on 2018/6/7.
 */


public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    //通过文档我们可以知道SwipeRefreshLayout只不过是继承了ViewGroup, 故此布局内只能有一个直接子View。
    public MySwipeRefreshLayout(Context context) {
        super(context);
        setColorSchemeColors(getResources().getColor(R.color.ic_text_color_selected));
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeColors(getResources().getColor(R.color.ic_text_color_selected));
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }


}
