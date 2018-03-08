package com.wecu.widgetdemo.badge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by wecu on 2018/3/8.
 */

public class BadgeManager {
    private Paint mBorderPaint;
    private Paint mBgPaint;
    private Paint mTextPaint;
    private float density;

    public BadgeManager(Context context) {
        density = context.getResources().getDisplayMetrics().density;
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void doOnAfterDraw(@NonNull IBadgeCallBack badgeCallBack, @NonNull Canvas canvas, @NonNull String text) {
        Drawable drawable = badgeCallBack.getViewDrawable();
        if (drawable == null) {
            return;
        }
        if (badgeCallBack instanceof TextView) {
            BadgeConfig config = initBadge(badgeCallBack);
            int h = measureBadgeHeight(config, text);
            int w = measureBadgeWidth(config, text);
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            TextView textView = ((TextView) badgeCallBack);
            int dl = textView.getCompoundPaddingLeft();
            int dt = textView.getCompoundPaddingTop();
            int resultW = dl + dw - w / 2;
            int resultH = dt - h / 2;
            resultH = 0;
            if (resultW > textView.getWidth()) {
                resultW = textView.getWidth() - w;
            }
            if (resultH < 0) {
                resultH = 0;
            }
//            canvas.save();
//            canvas.translate(resultW, resultH);
            drawBorder(canvas);
            drawBg(canvas, config, w, h);
            drawText(canvas, text, w, h);
//            canvas.restore();
        }
    }

    private BadgeConfig initBadge(IBadgeCallBack badgeCallBack) {
        BadgeConfig badgeConfig = badgeCallBack.getBadgeConfig();
        if (badgeConfig == null) {
            badgeConfig = new BadgeConfig();
        }
        mTextPaint.setColor(badgeConfig.textColor);
        mBorderPaint.setColor(badgeConfig.borderColor);
        mBgPaint.setColor(badgeConfig.bgColor);
        mTextPaint.setTextSize(badgeConfig.textSize);
        return badgeConfig;
    }

    private int measureBadgeHeight(@NonNull BadgeConfig badgeConfig, String text) {
        return badgeConfig.textSize + badgeConfig.paddingTop + badgeConfig.paddingBottom;
    }

    private int measureBadgeWidth(@NonNull BadgeConfig badgeConfig, String text) {
        return (int) (mTextPaint.measureText(text) + badgeConfig.paddingTop + badgeConfig.paddingBottom);
    }

    private void drawBorder(@NonNull Canvas canvas) {

    }

    private void drawBg(@NonNull Canvas canvas, BadgeConfig config, int width, int height) {
        canvas.drawRoundRect(new RectF(config.borderSize, config.borderSize, width - config.borderSize, height - config.borderSize),
                0,0, mBgPaint);
    }

    private void drawText(@NonNull Canvas canvas, String text, int width, int height) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        canvas.drawText(cutNumber(text, width), width / 2, height / 2 - (fontMetrics.top + fontMetrics.bottom) / 2, mTextPaint);
    }

    private String cutNumber(String number, int width) {
        if (mTextPaint.measureText(number) < width) {
            return number;
        }
        return "…";
    }

    public interface IBadgeCallBack {
        Drawable getViewDrawable();

        /**
         * 返回角标属性
         *
         * @return 返回角标属性
         */
        BadgeConfig getBadgeConfig();
    }

    /**
     * 角标属性
     */
    public class BadgeConfig {
        int textSize, borderSize = 1;
        int paddingLeft, paddingRight, paddingTop, paddingBottom;
        int textColor, borderColor = Color.WHITE, bgColor = Color.RED;

        public BadgeConfig() {
            this(48);
        }

        public BadgeConfig(int textSize) {
            this(textSize, Color.BLACK);
        }

        public BadgeConfig(int textSize, int textColor) {
            this(textSize, textColor, 2, 2, 1, 1);
        }

        public BadgeConfig(int textSize, int textColor, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
            this.textSize = textSize;
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.paddingTop = paddingTop;
            this.paddingBottom = paddingBottom;
            this.textColor = textColor;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public void setPaddingLeft(int paddingLeft) {
            this.paddingLeft = paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public void setPaddingRight(int paddingRight) {
            this.paddingRight = paddingRight;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public void setPaddingTop(int paddingTop) {
            this.paddingTop = paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }

        public void setPaddingBottom(int paddingBottom) {
            this.paddingBottom = paddingBottom;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(int borderColor) {
            this.borderColor = borderColor;
        }

        public int getBgColor() {
            return bgColor;
        }

        public void setBgColor(int bgColor) {
            this.bgColor = bgColor;
        }

        public int getBorderSize() {
            return borderSize;
        }

        public void setBorderSize(int borderSize) {
            this.borderSize = borderSize;
        }
    }
}
