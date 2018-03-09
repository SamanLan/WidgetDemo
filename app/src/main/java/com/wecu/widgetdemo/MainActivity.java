package com.wecu.widgetdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wecu.widgetdemo.badge.CustomTextView;
import com.wecu.widgetdemo.badge.NotificationKeyData;
import com.wecu.widgetdemo.badge.PackageUserKey;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LauncherNotificationService.NotificationsChangedListener {

    private AWidgetHelper mWidgetHelper;
    private CustomTextView vTextView;
    private CustomTextView vQQ;
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
        vQQ = (CustomTextView) findViewById(R.id.text2);
        vTextView.setBadgeText("123").setIcon(getResources().getDrawable(R.mipmap.ic_launcher_round)).setText("123");
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
            LauncherNotificationService.setNotificationsChangedListener(this);
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

    int notificationId = 0;

    public void sendNotification(View view) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentText("content");
        builder.setContentTitle("title");
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(notificationId++, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (hasPermission) {
//            unbindService(serviceConnection);
//        }
    }

    int notificationNum = 0;
    int qqNum = 0;

    @Override
    public void onNotificationPosted(PackageUserKey postedPackageUserKey, NotificationKeyData notificationKey, boolean shouldBeFilteredOut) {
        if (getPackageName().equals(postedPackageUserKey.mPackageName)) {
            vTextView.setBadgeText(notificationNum += notificationKey.count);
        } else if ("com.tencent.mobileqq".equals(postedPackageUserKey.mPackageName)) {
            vQQ.setBadgeText(qqNum += notificationKey.count);
        }
    }

    @Override
    public void onNotificationRemoved(PackageUserKey removedPackageUserKey, NotificationKeyData notificationKey) {
        if (getPackageName().equals(removedPackageUserKey.mPackageName)) {
            vTextView.setBadgeText((notificationNum -= notificationKey.count) == 0 ? 0 : notificationNum);
        } else if ("com.tencent.mobileqq".equals(removedPackageUserKey.mPackageName)) {
            vQQ.setBadgeText((qqNum -= notificationKey.count) == 0 ? 0 : qqNum);
        }
    }

    @Override
    public void onNotificationFullRefresh(List<StatusBarNotification> activeNotifications) {
        Toast.makeText(this, "begin", Toast.LENGTH_SHORT).show();
    }
}
