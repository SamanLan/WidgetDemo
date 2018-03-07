package com.wecu.widgetdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private AWidgetHelper mWidgetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWidgetHelper = new WidgetHelperV16(this);
        mWidgetHelper.setParent((ViewGroup) findViewById(R.id.content));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 监听widget变化
        mWidgetHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 一般无需调用停止监听widget变化
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWidgetHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void click(View view) {
        mWidgetHelper.applyWidgetId();
    }

    public void change(View view) {
        mWidgetHelper.changeWidgetSize(mWidgetHelper.getWidgetList().get(0));
        mWidgetHelper.getAllWidgets();
    }
}
