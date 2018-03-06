package com.wecu.widgetdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wecu on 2018/3/6.
 */

public class WidgetDragView extends FrameLayout {

    private int mCircleSize;
    private float mDensity;
    private Paint mPaint;
    private RectF[] mPointRectF;
    private View mChildView;
    private boolean mCanDrag = false;
    private MarginLayoutParams mLayoutParams;
    private @IWidgetDragCallBack.WidgetDragDirection int mDragPointIndex = -1;

    public WidgetDragView(Context context) {
        this(context, null);
    }

    public WidgetDragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mCircleSize = (int) (mDensity * 5);
        mPointRectF = new RectF[4];
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDensity * 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mChildView = getChildAt(0);
        if (mChildView != null) {
            mLayoutParams = (MarginLayoutParams) mChildView.getLayoutParams();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mChildView != null) {
            canvas.drawRect(mChildView.getLeft(), mChildView.getTop(), mChildView.getRight(), mChildView.getBottom(), mPaint);
            int midWidth = (mChildView.getLeft() + mChildView.getRight()) / 2 + mChildView.getLeft();
            int midHeight = (mChildView.getTop() + mChildView.getBottom()) / 2 + mChildView.getTop();
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle(midWidth, mChildView.getTop(), mCircleSize, mPaint);
            canvas.drawCircle(midWidth, mChildView.getBottom(), mCircleSize, mPaint);
            canvas.drawCircle(mChildView.getLeft(), midHeight, mCircleSize, mPaint);
            canvas.drawCircle(mChildView.getRight(), midHeight, mCircleSize, mPaint);
            // 左
            mPointRectF[0] = new RectF(mChildView.getLeft() - mCircleSize, midHeight - mCircleSize, mChildView.getLeft() + mCircleSize, midHeight + mCircleSize);
            // 上
            mPointRectF[1] = new RectF(midWidth - mCircleSize, mChildView.getTop() - mCircleSize, midWidth + mCircleSize, mChildView.getTop() + mCircleSize);
            // 右
            mPointRectF[2] = new RectF(mChildView.getRight() - mCircleSize, midHeight - mCircleSize, mChildView.getRight() + mCircleSize, midHeight + mCircleSize);
            // 下
            mPointRectF[3] = new RectF(midWidth - mCircleSize, mChildView.getBottom() - mCircleSize, midWidth + mCircleSize, mChildView.getBottom() + mCircleSize);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 编辑模式拦截所有触摸，只能处理widget的改变大小操作
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mChildView == null || mLayoutParams ==null)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCanDrag = canDrag(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCanDrag) {
                    dealDrag(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                releasePoint();
                break;
        }
        return true;
    }

    /**
     * 触摸点是否四点
     *
     * @param event 点击event
     * @return 能够滑动widget
     */
    private boolean canDrag(MotionEvent event) {
        float downX = event.getX();
        float dowY = event.getY();
        for (int i = 0; i < mPointRectF.length; i++) {
            if (isInPointRect(mPointRectF[i], downX, dowY)) {
                mDragPointIndex = i;
                return true;
            }
        }
        return false;
    }

    /**
     * 是否在移动的四点内
     * @param rectF 点的rect
     * @param x 点击的x
     * @param y 点击的y
     * @return 是否在
     */
    private boolean isInPointRect(RectF rectF, float x, float y) {
        if (x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom) {
            return true;
        }
        return false;
    }

    /**
     * 处理滑动
     * @param event 事件
     */
    private void dealDrag(MotionEvent event) {
        int lastLeft = mLayoutParams.leftMargin;
        int lastWidth = mChildView.getWidth();
        int moveX = (int) event.getX();
        int offsetX = moveX - lastLeft;
        if (mDragPointIndex == IWidgetDragCallBack.LEFT) {
            mLayoutParams.width = lastWidth - offsetX;
            mLayoutParams.leftMargin = moveX;
            mChildView.setLayoutParams(mLayoutParams);
        } else if (mDragPointIndex == IWidgetDragCallBack.TOP) {
            mLayoutParams.height = (int) event.getY();
            mChildView.setLayoutParams(mLayoutParams);
        } else if (mDragPointIndex == IWidgetDragCallBack.RIGHT) {
            // 右边滑动不需要特殊处理
            mLayoutParams.leftMargin = lastLeft;
            mLayoutParams.width = (int) event.getX() - lastLeft;
            mChildView.setLayoutParams(mLayoutParams);
        } else if (mDragPointIndex == IWidgetDragCallBack.BOTTOM) {
            // 下面滑动也不需要特殊处理
            mLayoutParams.height = (int) event.getY();
            mChildView.setLayoutParams(mLayoutParams);
        }
    }

    /**
     * 释放手指后的操作
     */
    private void releasePoint() {
        mDragPointIndex = -1;
        mCanDrag = false;
    }
}
