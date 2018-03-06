package com.wecu.widgetdemo;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
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

public class WidgetHelper implements IWidgetCallBack {

//    private static class Holder {
//        private final static WidgetHelper INSTANCE = new WidgetHelper(null);
//    }

    private final String TAG = "WidgetHelper";

    private static final int HOST_ID = 1024;
    private static final int REQUEST_APPWIDGET = 100;
    private static final int CREAT_APPWIDGET = 101;

    private AppWidgetHost mAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;
    private ViewGroup vParent;
    private Context mContext;

    private IWidgetDragCallBack mDragCallBack;

    private List<View> mWidgetList;

    public WidgetHelper(Context context) {
        this.mContext = context;
        mAppWidgetHost = new AppWidgetHost(mContext, HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        mWidgetList = new ArrayList<>();
    }
//
//    public WidgetHelper getInstance() {
//        return Holder.INSTANCE;
//    }

    public void onStart() {
        mAppWidgetHost.startListening();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case REQUEST_APPWIDGET:
                int id = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    AppWidgetProviderInfo appWidgetProviderInfo = mAppWidgetManager.getAppWidgetInfo(id);
//                    if (appWidgetProviderInfo != null) {
//                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
//                        intent.setComponent(appWidgetProviderInfo.configure);
//                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
//                        startActivityForResult(intent, CREAT_APPWIDGET);
//                    } else {
                    onActivityResult(CREAT_APPWIDGET, RESULT_OK, data);
//                    }
                }
                break;
            case CREAT_APPWIDGET:
                addWidget(data);
                break;
            default:break;
        }
    }

    /**
     * 申请widget列表，打开所有widget列表
     */
    public void applyWidgetId() {
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        // 申请ID
        int newAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
        // 作为Intent附加值 ， 该appWidgetId将会与选定的AppWidget绑定
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);
        // 选择某项AppWidget后，立即返回，即回调onActivityResult()方法
        ((Activity) mContext).startActivityForResult(pickIntent, REQUEST_APPWIDGET);
    }

    /**
     * 添加widget
     * @param data 返回的数据源
     */
    private void addWidget(Intent data) {
        Bundle extra = data.getExtras() ;
        int appWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID , -1) ;
        if(appWidgetId == -1){
            Toast.makeText(mContext, "添加窗口小部件有误", Toast.LENGTH_SHORT).show();
            return ;
        }
        AppWidgetProviderInfo appWidgetProviderInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId) ;
        AppWidgetHostView hostView = mAppWidgetHost.createView(mContext, appWidgetId, appWidgetProviderInfo);
        hostView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("....");
            }
        });

        int height = appWidgetProviderInfo.minHeight;
        int width = appWidgetProviderInfo.minWidth;

        if (vParent == null) {
            return;
        }
        vParent.addView(hostView,new ViewGroup.LayoutParams(width,height));
        mWidgetList.add(hostView);
    }

    public void changeWidgetSize(View child) {
        if (vParent == null) {
            return;
        }
        ViewGroup viewGroup = ((ViewGroup) vParent.getParent());
        if (viewGroup != null && viewGroup instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) viewGroup;
            FrameLayout.LayoutParams marginLayoutParams = new FrameLayout.LayoutParams(child.getWidth(), child.getHeight());
            marginLayoutParams.leftMargin = child.getLeft();
            marginLayoutParams.topMargin = child.getTop();
            vParent.removeView(child);
            child.setLayoutParams(marginLayoutParams);
            WidgetDragView widgetDragView = new WidgetDragView(mContext);
            widgetDragView.setCallBack(this);
            widgetDragView.addView(child);
            frameLayout.addView(widgetDragView, new ViewGroup.LayoutParams(-1, -1));
        }
    }

    /**
     * 获取所有widgets
     * @return widget list
     */
    public List<AppWidgetProviderInfo> getAllWidgets() {
        List<AppWidgetProviderInfo> list = mAppWidgetManager.getInstalledProviders();
        for (AppWidgetProviderInfo appWidgetProviderInfo : list) {
            System.out.println(appWidgetProviderInfo.label);
        }
        return list;
    }

    public void beginDragWidget(View view) {

    }

    public ViewGroup getParent() {
        return vParent;
    }

    public WidgetHelper setParent(ViewGroup vParent) {
        this.vParent = vParent;
        return this;
    }

    public IWidgetDragCallBack getDragCallBack() {
        return mDragCallBack;
    }

    public WidgetHelper setDragCallBack(IWidgetDragCallBack mDragCallBack) {
        this.mDragCallBack = mDragCallBack;
        return this;
    }

    public List<View> getWidgetList() {
        return mWidgetList;
    }

    public WidgetHelper setWidgetList(List<View> mWidgetList) {
        this.mWidgetList = mWidgetList;
        return this;
    }


    @Override
    public void stopDragMode(View deleteView, View addView) {
        ViewGroup viewGroup = ((ViewGroup) vParent.getParent());
        if (viewGroup != null && viewGroup instanceof FrameLayout) {
            viewGroup.removeView(deleteView);
        }
        vParent.addView(addView);
    }
}
