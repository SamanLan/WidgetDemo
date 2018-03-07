package com.wecu.widgetdemo;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wecu on 2018/3/6.
 */

public class WidgetDragView extends FrameLayout implements View.OnClickListener {

    private int mCircleSize;
    private float mDensity;
    private Paint mPaint;
    private RectF[] mPointRectF;
    private View vChildView;
    private boolean mCanDrag = false;
    private MarginLayoutParams mLayoutParams;
    private @IWidgetDragCallBack.WidgetDragDirection int mDragPointIndex = -1;
    private IWidgetCallBack mCallBack;
    private IWidgetDragCallBack mDragCallBack;
    private WidgetProviderInfo mWidgetInfo;
    private Point mLastMovePoint;

    public WidgetDragView(Context context) {
        this(context, null);
    }

    public WidgetDragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mCircleSize = (int) (mDensity * 15);
        mPointRectF = new RectF[4];
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDensity * 2);
        setOnClickListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        vChildView = getChildAt(0);
        if (vChildView != null) {
            mLayoutParams = (MarginLayoutParams) vChildView.getLayoutParams();
            mLayoutParams.topMargin += mCircleSize;
            mLayoutParams.leftMargin += mCircleSize;
            mLayoutParams.rightMargin += mCircleSize;
            mLayoutParams.bottomMargin += mCircleSize;
            vChildView.setLayoutParams(mLayoutParams);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (vChildView != null) {
            canvas.drawRect(vChildView.getLeft(), vChildView.getTop(), vChildView.getRight(), vChildView.getBottom(), mPaint);
            int midWidth = (vChildView.getRight() - vChildView.getLeft()) / 2 + vChildView.getLeft();
            int midHeight = (vChildView.getBottom() - vChildView.getTop()) / 2 + vChildView.getTop();
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle(midWidth, vChildView.getTop(), mCircleSize, mPaint);
            canvas.drawCircle(midWidth, vChildView.getBottom(), mCircleSize, mPaint);
            canvas.drawCircle(vChildView.getLeft(), midHeight, mCircleSize, mPaint);
            canvas.drawCircle(vChildView.getRight(), midHeight, mCircleSize, mPaint);
            // 左
            mPointRectF[0] = new RectF(vChildView.getLeft() - mCircleSize, midHeight - mCircleSize, vChildView.getLeft() + mCircleSize, midHeight + mCircleSize);
            // 上
            mPointRectF[1] = new RectF(midWidth - mCircleSize, vChildView.getTop() - mCircleSize, midWidth + mCircleSize, vChildView.getTop() + mCircleSize);
            // 右
            mPointRectF[2] = new RectF(vChildView.getRight() - mCircleSize, midHeight - mCircleSize, vChildView.getRight() + mCircleSize, midHeight + mCircleSize);
            // 下
            mPointRectF[3] = new RectF(midWidth - mCircleSize, vChildView.getBottom() - mCircleSize, midWidth + mCircleSize, vChildView.getBottom() + mCircleSize);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 编辑模式拦截所有触摸，只能处理widget的改变大小操作
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (vChildView == null || mLayoutParams ==null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCanDrag = canDrag(event);
                mLastMovePoint = new Point(((int) event.getX()), ((int) event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCanDrag && canKeepMove(event)) {
                    dealDrag(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                releasePoint();
                break;
            default:break;
        }
        return mCanDrag || super.onTouchEvent(event);
    }

    /**
     * 能否继续滑动，判断规则为是否滑动会小于widget的最小宽高
     * @param event 滑动event
     * @return 能否继续滑动
     */
    private boolean canKeepMove(MotionEvent event) {
        if (vChildView instanceof AppWidgetHostView) {
            if (mWidgetInfo == null) {
                AppWidgetHostView view = ((AppWidgetHostView) vChildView);
                AppWidgetProviderInfo appWidgetProviderInfo = view.getAppWidgetInfo();
                mWidgetInfo = WidgetProviderInfo.fromProviderInfo(getContext(), appWidgetProviderInfo);
            }
            // TODO: 2018/3/7 这里可以继续加超过屏幕的限制
            if (mDragPointIndex == IWidgetDragCallBack.LEFT &&
                    (mLayoutParams.width - event.getX() + mLastMovePoint.x < mWidgetInfo.minResizeWidth)) {
                return false;
            }
            if (mDragPointIndex == IWidgetDragCallBack.RIGHT &&
                    (mLayoutParams.width + event.getX() - mLastMovePoint.x < mWidgetInfo.minResizeWidth)) {
                return false;
            }
            if (mDragPointIndex == IWidgetDragCallBack.TOP &&
                    (mLayoutParams.height - event.getY() + mLastMovePoint.y < mWidgetInfo.minResizeHeight)) {
                return false;
            }
            if (mDragPointIndex == IWidgetDragCallBack.BOTTOM &&
                    (mLayoutParams.height + event.getY() - mLastMovePoint.y < mWidgetInfo.minResizeHeight)) {
                return false;
            }
        }
        mLastMovePoint = new Point(((int) event.getX()), ((int) event.getY()));
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
        int lastTop = mLayoutParams.topMargin;
        int lastWidth = vChildView.getWidth();
        int lastHeight = vChildView.getHeight();
        int moveX = (int) event.getX();
        int moveY = (int) event.getY();
        int offsetX = moveX - lastLeft;
        int offsetY = moveY - lastTop;
        if (mDragPointIndex == IWidgetDragCallBack.LEFT) {
            mLayoutParams.width = lastWidth - offsetX;
            mLayoutParams.leftMargin = moveX;
            vChildView.setLayoutParams(mLayoutParams);
        } else if (mDragPointIndex == IWidgetDragCallBack.TOP) {
            mLayoutParams.height = lastHeight - offsetY;
            mLayoutParams.topMargin = moveY;
            vChildView.setLayoutParams(mLayoutParams);
        } else if (mDragPointIndex == IWidgetDragCallBack.RIGHT) {
            // 右边滑动不需要特殊处理
            mLayoutParams.leftMargin = lastLeft;
            mLayoutParams.width = (int) event.getX() - lastLeft;
            vChildView.setLayoutParams(mLayoutParams);
        } else if (mDragPointIndex == IWidgetDragCallBack.BOTTOM) {
            // 下面滑动也不需要特殊处理
            mLayoutParams.topMargin = lastTop;
            mLayoutParams.height = (int) event.getY() - lastTop;
            vChildView.setLayoutParams(mLayoutParams);
        }
    }

    /**
     * 释放手指后的操作
     */
    private void releasePoint() {
        if (mDragCallBack != null) {
            if (mDragCallBack.canResize(mDragPointIndex, new RectF(vChildView.getLeft(), vChildView.getTop(),
                    vChildView.getRight(), vChildView.getBottom()))) {

            } else {
                // 返回滑动范围与当前父亲所能支持的最大的范围的最大交集

            }
        }
        mDragPointIndex = IWidgetDragCallBack.NONE;
        mCanDrag = false;
    }

    @Override
    public void onClick(View v) {
        if (mCallBack != null) {
            removeView(vChildView);
            mCallBack.stopDragMode(this, vChildView);
        }
    }

    public IWidgetCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(IWidgetCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public IWidgetDragCallBack getDragCallBack() {
        return mDragCallBack;
    }

    public void setDragCallBack(IWidgetDragCallBack mDragCallBack) {
        this.mDragCallBack = mDragCallBack;
    }
}
