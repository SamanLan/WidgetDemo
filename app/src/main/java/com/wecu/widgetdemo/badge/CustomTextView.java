package com.wecu.widgetdemo.badge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 一个可以在右上角设置一个角标（文字、数字、圆点）的Textview
 * 拓展性不强，但效率性强，view层级减少
 */
public class CustomTextView extends TextView {

    private BadgeDrawable mBadgeDrawable;

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBadgeDrawable = new BadgeDrawable(40, 0xffFF4081);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = getMeasuredWidth();
        if (getCompoundDrawables()[1] != null) {
            // 假如有个drawableTop的话，设置角标开始于drawable的末尾
            mBadgeDrawable.layout((width + getCompoundDrawables()[1].getIntrinsicWidth()) / 2, getPaddingTop(), width);
        } else {
            // 设置角标位置行文字的末尾
            mBadgeDrawable.layout((width + (int) getLayout().getLineWidth(0)) / 2, getPaddingTop(), width);
        }
        mBadgeDrawable.draw(canvas);
    }

    public CustomTextView setBadgeText(String text) {
        mBadgeDrawable.setText(text);
        invalidate();
        return this;
    }

    public CustomTextView setBadgeText(int text) {
        mBadgeDrawable.setNumber(text);
        invalidate();
        return this;
    }

    public CustomTextView setIcon(Drawable drawable) {
        if (drawable != null && drawable.getBounds().isEmpty()) {
            drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
        }
        Drawable[] cds = getCompoundDrawables();
        setCompoundDrawables(cds[0], drawable, cds[2], cds[3]);
//        invalidate();
        return this;
    }

    public CustomTextView setBadgeVisible(boolean visible) {
        mBadgeDrawable.setVisible(visible);
        invalidate();
        return this;
    }
}
