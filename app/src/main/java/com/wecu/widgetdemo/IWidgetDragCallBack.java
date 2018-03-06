package com.wecu.widgetdemo;

import android.graphics.RectF;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wecu on 2018/3/6.
 */

public interface IWidgetDragCallBack {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    public static final int ALL = 4;

    @IntDef({LEFT, TOP, RIGHT, BOTTOM, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WidgetDragDirection {
    }

    public boolean canResize(@WidgetDragDirection int type, RectF rectF);
}
