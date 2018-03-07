package com.wecu.widgetdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WidgetHelperV21 extends AWidgetHelper{

    private final UserManager mUserManager;
    private final PackageManager mPm;
    public WidgetHelperV21(Context context) {
        super(context);
        mPm = context.getPackageManager();
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
    }

    /**
     * 获取所有widgets
     *
     * @return widget list
     */
    @Override
    public List<WidgetProviderInfo> getAllWidgets() {
        ArrayList<AppWidgetProviderInfo> providers = new ArrayList<>();
        for (UserHandle user : mUserManager.getUserProfiles()) {
            providers.addAll(mAppWidgetManager.getInstalledProvidersForProfile(user));
        }
        List<WidgetProviderInfo> resultList = new ArrayList<>();
        for (AppWidgetProviderInfo appWidgetProviderInfo : providers) {
            resultList.add(WidgetProviderInfo.fromProviderInfo(mContext, appWidgetProviderInfo));
        }
        if (mWidgetModel == null) {
            mWidgetModel = new WidgetModel();
        }
        mWidgetModel.setWidgetList(resultList);
        return resultList;
    }
}
