package com.example.musicplayerdome.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

public class MyRadioRelativeLayout extends RelativeLayout {

    public MyRadioRelativeLayout(Context context) {
        this(context, null);
    }

    public MyRadioRelativeLayout(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public MyRadioRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void initView() {

    }

    private boolean mScrolling;
    private float touchDownX;
    private float touchDownY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touchDownX = event.getX();
            touchDownY = event.getY();
            mScrolling = false;
            break;
        case MotionEvent.ACTION_MOVE:
            if (Math.abs(touchDownX - event.getX()) >= ViewConfiguration.get(
                    getContext()).getScaledTouchSlop()) {
                mScrolling = true;
            } else {
                mScrolling = false;
            }
            break;
        case MotionEvent.ACTION_UP:
            mScrolling = false;
            break;
        }
        return mScrolling;
    }

    float x1 = 0;
    float x2 = 0;
    float y = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            return true;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            x2 = event.getX();
            y = event.getY();
            System.out.println((touchDownX-x2)+","+(touchDownY-y));
            if ((touchDownX - x2) > 350&&(touchDownY-y)<200&&(touchDownY-y)>-200) {
                if(mSetOnSlideListener!=null){
                    mSetOnSlideListener.onRightToLeftSlide();
                }
                System.out.println("右划");
            }else if ((touchDownX - x2 )< -350&&(touchDownY-y)<200&&(touchDownY-y)>-200) {
                if(mSetOnSlideListener!=null){
                    mSetOnSlideListener.onLeftToRightSlide();
                }
                System.out.println("左划");
            }else if (-20<(touchDownX-x2)&&(touchDownX-x2)<20&&(touchDownY-y)<20&&(touchDownY-y)>-20){
                if(mSetOnSlideListener!=null){
                    mSetOnSlideListener.onClick();
                }
                System.out.println("点击");
            }
            break;
        }

        return super.onTouchEvent(event);
    }
    
    private setOnSlideListener mSetOnSlideListener;
    
    public setOnSlideListener getmSetOnSlideListener() {
        return mSetOnSlideListener;
    }

    public void setmSetOnSlideListener(setOnSlideListener mSetOnSlideListener) {
        this.mSetOnSlideListener = mSetOnSlideListener;
    }

    public interface setOnSlideListener{
        void onRightToLeftSlide();
        void onLeftToRightSlide();
        void onClick();
    }

}