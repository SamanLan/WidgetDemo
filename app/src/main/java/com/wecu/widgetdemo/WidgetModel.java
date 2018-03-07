package com.wecu.widgetdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wecu on 2018/3/7.
 */

public class WidgetModel {
    public HashMap<String, PackageItemInfo> mPackageItemInfos = new HashMap<>();

    // TODO: 2018/3/7 重写hashcode和equals
    public HashMap<PackageItemInfo, ArrayList<Object>> mWidgetsList = new HashMap<>();

    public void setWidgetList(List<WidgetProviderInfo> list) {
        mWidgetsList.clear();
        mPackageItemInfos.clear();
        for (WidgetProviderInfo widgetProviderInfo : list) {
            String pckName = widgetProviderInfo.provider.getPackageName();
            PackageItemInfo tmpInfo = mPackageItemInfos.get(pckName);
            if (tmpInfo == null) {
                tmpInfo = new PackageItemInfo(pckName);
                mPackageItemInfos.put(pckName, tmpInfo);
                ArrayList<Object> addList = new ArrayList<>();
                addList.add(widgetProviderInfo);
                mWidgetsList.put(tmpInfo, addList);
            } else {
                ArrayList<Object> tmpList = mWidgetsList.get(tmpInfo);
                if (tmpList == null) {
                    ArrayList<Object> addList = new ArrayList<>();
                    addList.add(widgetProviderInfo);
                    mWidgetsList.put(new PackageItemInfo(pckName), addList);
                } else {
                    tmpList.add(widgetProviderInfo);
                }
            }
        }
    }
}
