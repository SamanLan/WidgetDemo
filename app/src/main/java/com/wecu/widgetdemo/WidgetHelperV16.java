package com.wecu.widgetdemo;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by wecu on 2018/3/6.
 */

public class WidgetHelperV16 extends AWidgetHelper{

    public WidgetHelperV16(Context context) {
        super(context);
    }

    /**
     * 获取所有widgets
     *
     * @return widget list
     */
    @Override
    public List<WidgetProviderInfo> getAllWidgets() {
        List<AppWidgetProviderInfo> list = mAppWidgetManager.getInstalledProviders();
        List<WidgetProviderInfo> resultList = new ArrayList<>();
        for (AppWidgetProviderInfo appWidgetProviderInfo : list) {
            resultList.add(WidgetProviderInfo.fromProviderInfo(mContext, appWidgetProviderInfo));
        }
        if (mWidgetModel == null) {
            mWidgetModel = new WidgetModel();
        }
        mWidgetModel.setWidgetList(resultList);
        return resultList;
    }
}
