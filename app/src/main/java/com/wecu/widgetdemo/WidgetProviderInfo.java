package com.wecu.widgetdemo;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;

/**
 * Created by wecu on 2018/3/7.
 */

public class WidgetProviderInfo extends AppWidgetProviderInfo{
    public static WidgetProviderInfo fromProviderInfo(Context context,AppWidgetProviderInfo info) {

        Parcel p = Parcel.obtain();
        info.writeToParcel(p, 0);
        p.setDataPosition(0);
        WidgetProviderInfo lawpi = new WidgetProviderInfo(p);
        p.recycle();
        return lawpi;
    }

    public WidgetProviderInfo(Parcel in) {
        super(in);
    }

    public String toString(PackageManager pm) {
        return String.format("WidgetProviderInfo provider:%s package:%s short:%s label:%s",
                provider.toString(), provider.getPackageName(), provider.getShortClassName(), getLabel(pm));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getLabel(PackageManager packageManager) {
        return super.loadLabel(packageManager);
    }

    /**
     * 计算格子数
     */
    public void calSpanSize() {

    }
}
