package com.wecu.widgetdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wecu.widgetdemo.badge.CustomTextView;

public class MainActivity extends AppCompatActivity {

    private AWidgetHelper mWidgetHelper;
    private CustomTextView vTextView;
    boolean show = true;
    boolean hasPermission = false;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("绑定服务");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherNotificationManager.toggleNotificationListenerService(this);
        setContentView(R.layout.activity_main);
        mWidgetHelper = new WidgetHelperV16(this);
        mWidgetHelper.setParent((ViewGroup) findViewById(R.id.content));
        vTextView = (CustomTextView) findViewById(R.id.text);
        vTextView.setBadgeText(100).setIcon(getResources().getDrawable(R.mipmap.ic_launcher_round)).setText("789");
        vTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vTextView.setBadgeVisible(show = !show);
            }
        });
        if (LauncherNotificationManager.isNotificationListenersEnabled(this)) {
            System.out.println("有权限");
//            hasPermission = true;
//            bindService(new Intent(this, LauncherNotificationService.class), serviceConnection, BIND_AUTO_CREATE);
            startService(new Intent(this, LauncherNotificationService.class));
        } else {
            System.out.println("无权限，跳转权限设置");
            LauncherNotificationManager.gotoNotificationAccessSetting(this);
        }
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

    public void getAllWidget(View view) {
        startActivity(new Intent(this, SelectWidgetActivity.class));
    }

    public void sendNotification(View view) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentText("content");
        builder.setContentTitle("title");
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (hasPermission) {
//            unbindService(serviceConnection);
//        }
    }
}
