package com.wecu.widgetdemo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

/**
 * <code>LauncherNotificationService</code>{}
 *
 * @author :LanSaman
 * @date :2018/3/8
 * @description :
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class LauncherNotificationService extends NotificationListenerService {

    Handler h = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            System.out.println(" NotificationListenerService onCreate, notification count ：" + getActiveNotifications().length);
            Toast.makeText(LauncherNotificationService.this, msg.arg1 + "", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("服务creat");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.e("", " NotificationListenerService onCreate, notification count ：" + getActiveNotifications().length);
            }
        },1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("服务onBind");
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("服务onStartCommand");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.e("", " NotificationListenerService onCreate, notification count ：" + getActiveNotifications().length);
            }
        },1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        System.out.println("连接服务");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Message m = h.obtainMessage();
        m.arg1 = getActiveNotifications().length;
        h.sendMessage(m);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Message m = h.obtainMessage();
        m.arg1 = getActiveNotifications().length;
        h.sendMessage(m);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }
}
