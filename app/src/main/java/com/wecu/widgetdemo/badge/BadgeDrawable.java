package com.wecu.widgetdemo.badge;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * 角标drawable
 */
public class BadgeDrawable extends GradientDrawable {
    /**
     * 绘制的文字
     */
    private String mText;

    /**
     * 是否显示
     */
    private boolean mIsVisible = true;

    /**
     * 🖌️
     */
    private TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 角标的高度，文字大小为这个的0.8f，小圆点为这个的0.65f
     */
    private int mHeight = 0;

    /**
     *
     * @param height 角标高度
     * @param color 背景颜色
     */
    public BadgeDrawable(int height, int color) {
        setColor(color);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(height * 0.8f);
        mHeight = height;
    }

    public void layout(int x, int y, int max) {
        Rect rect = getBounds();
        // 将rect位移到指定的位置，而大小不变
        // x
        // y 是tv的paddingTop，算一算（y - 角标高度的一半），最后取0与该值的最大值
        rect.offsetTo(Math.min(x - rect.width() / 2, max - rect.width() - (int)(0.2f * mHeight)), Math.max(0, y - rect.height() / 2));
        setBounds(rect);
    }

    /**
     * 重新设置drawable的大小
     * @param w 宽
     * @param h 高
     */
    public void resize(int w, int h) {
        Rect rect = getBounds();
        setBounds(rect.left, rect.top, rect.left + w, rect.top + h);
        invalidateSelf();
    }

    /**
     * 设置角标文字
     * @param text 文字
     */
    public void setText(String text) {
        mText = text;
        setVisible(true);
        if (TextUtils.isEmpty(mText)) {
            // 小圆点
            int size = (int)(mHeight * 0.65);
            resize(size, size);
        } else {
            int width = (int)(mPaint.measureText(mText) + 0.4 * mHeight);
            // max是保证宽高最小情况为正方形
            resize(Math.max(width, mHeight), mHeight);
        }
    }

    /**
     * 设置角标数字
     * @param number 角标数字，大于99则为。。。
     */
    public void setNumber(int number) {
        if (number <= 0) {
            setVisible(false);
        } else if (number > 99) {
            setText("...");
        } else {
            setText(String.valueOf(number));
        }
    }

    /**
     * 设置是否显示角标
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (mIsVisible != visible) {
            invalidateSelf();
        }
        mIsVisible = visible;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        // 圆角为高度一半
        setCornerRadius(getBounds().height() / 2f);
    }

    @Override
    public void draw(Canvas canvas) {
        // 隐藏
        if (!mIsVisible) {
            return;
        }
        // 画背景，无文字则是小圆点
        super.draw(canvas);
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        // 画文字
        canvas.drawText(mText, getBounds().exactCenterX(), getBounds().exactCenterY() - (mPaint.descent() + mPaint.ascent()) / 2, mPaint);
    }
}
