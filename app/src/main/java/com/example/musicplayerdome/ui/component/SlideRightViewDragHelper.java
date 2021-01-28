package com.example.musicplayerdome.ui.component;
 
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.customview.widget.ViewDragHelper;

/**
 * 向右滑动控件
 * Created by Jing on 2016/2/16.
 */
public class SlideRightViewDragHelper extends LinearLayout {
    private ViewDragHelper viewDragHelper;
    private View child;
    private Point childPosition = new Point();
    private Point childEndPosition = new Point();
    private OnReleasedListener onReleasedListener;
    private int oldX;
    private int screenWidth=0;
    public SlideRightViewDragHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth=displayMetrics.widthPixels;
        //新建viewDragHelper ,viewGroup, 灵敏度，回调(子view的移动)
        viewDragHelper = ViewDragHelper.create(this, 0.5f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }
 
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                oldX = left;
                return Math.max(0, left);
            }
 
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if(oldX > (int)screenWidth*0.6f){
                    viewDragHelper.settleCapturedViewAt(childEndPosition.x, childEndPosition.y);
                    invalidate(); //必须刷新,因为其内部使用的是mScroller.startScroll，所以别忘了需要invalidate()以及结合computeScroll方法一起。
                    if(onReleasedListener != null)
                        onReleasedListener.onReleased();
                }else{
                    viewDragHelper.settleCapturedViewAt(childPosition.x, childPosition.y); //反弹
                    invalidate();
                }
                super.onViewReleased(releasedChild, xvel, yvel);
            }
        });
    }
 
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child = getChildAt(0);
    }
 
    @Override   //用viewDragHelper拦截-true
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }
 
    @Override  //viewDragHelper拦截事件
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
 
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //定位一开始的坐标
        childPosition.x = child.getLeft();
        childPosition.y = child.getTop();
        //滑动成功后定位坐标
        childEndPosition.x = child.getRight();
        childEndPosition.y = child.getTop();
    }
 
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
 
    public void setOnReleasedListener(OnReleasedListener onReleasedListener){
        this.onReleasedListener = onReleasedListener;
    }
 
    public interface OnReleasedListener{
        void onReleased();
    }
}