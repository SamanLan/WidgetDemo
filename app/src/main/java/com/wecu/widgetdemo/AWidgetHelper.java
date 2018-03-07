package com.wecu.widgetdemo;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by wecu on 2018/3/7.
 */

public abstract class AWidgetHelper implements IWidgetCallBack {
    protected static final int HOST_ID = 1024;
    protected static final int REQUEST_APPWIDGET = 100;
    protected static final int CREAT_APPWIDGET = 101;

    protected AppWidgetHost mAppWidgetHost;
    protected AppWidgetManager mAppWidgetManager;
    protected ViewGroup vParent;
    protected Context mContext;

    protected IWidgetDragCallBack mDragCallBack;

    protected List<AppWidgetHostView> mWidgetList;
    protected WidgetModel mWidgetModel;

    public AWidgetHelper(Context context) {
        mContext = context;
        mAppWidgetHost = new AppWidgetHost(mContext, HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        mWidgetList = new ArrayList<>();
    }

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
            default:
                break;
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
     *
     * @param data 返回的数据源
     */
    private void addWidget(Intent data) {
        Bundle extra = data.getExtras();
        int appWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (appWidgetId == -1) {
            Toast.makeText(mContext, "添加窗口小部件有误", Toast.LENGTH_SHORT).show();
            return;
        }
        AppWidgetProviderInfo appWidgetProviderInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
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
        vParent.addView(hostView, new ViewGroup.LayoutParams(Math.min(width, vParent.getWidth() - vParent.getPaddingLeft() - vParent.getPaddingRight()),
                Math.min(height, vParent.getHeight() - vParent.getPaddingTop() - vParent.getPaddingBottom())));
        mWidgetList.add(hostView);
    }

    public void changeWidgetSize(View child) {
        if (vParent == null) {
            return;
        }
        ViewGroup viewGroup = ((ViewGroup) vParent.getParent());
        if (viewGroup != null && viewGroup instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) viewGroup;
            FrameLayout.LayoutParams marginLayoutParams = new FrameLayout.LayoutParams(Math.min(viewGroup.getWidth(), child.getWidth()),
                    Math.min(child.getHeight(), viewGroup.getHeight()));
            marginLayoutParams.leftMargin = child.getLeft();
            marginLayoutParams.topMargin = child.getTop();
            marginLayoutParams.bottomMargin = child.getBottom();
            marginLayoutParams.rightMargin = child.getRight();
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
     *
     * @return widget list
     */
    public abstract List<WidgetProviderInfo> getAllWidgets();

    public void beginDragWidget(View view) {

    }

    public ViewGroup getParent() {
        return vParent;
    }

    public void setParent(ViewGroup vParent) {
        this.vParent = vParent;
    }

    public IWidgetDragCallBack getDragCallBack() {
        return mDragCallBack;
    }

    public void setDragCallBack(IWidgetDragCallBack mDragCallBack) {
        this.mDragCallBack = mDragCallBack;
    }

    public List<AppWidgetHostView> getWidgetList() {
        return mWidgetList;
    }

    public void setWidgetList(List<AppWidgetHostView> mWidgetList) {
        this.mWidgetList = mWidgetList;
    }

    public WidgetModel getWidgetModel() {
        return mWidgetModel;
    }

    public void setWidgetModel(WidgetModel mWidgetModel) {
        this.mWidgetModel = mWidgetModel;
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
