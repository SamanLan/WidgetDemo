package com.wecu.widgetdemo;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.wecu.widgetdemo.badge.NotificationKeyData;
import com.wecu.widgetdemo.badge.PackageUserKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <code>LauncherNotificationService</code>{}
 *
 * @author :LanSaman
 * @date :2018/3/8
 * @description :
 */
public class LauncherNotificationService extends NotificationListenerService {

    private static final int MSG_NOTIFICATION_POSTED = 1;
    private static final int MSG_NOTIFICATION_REMOVED = 2;
    private static final int MSG_NOTIFICATION_FULL_REFRESH = 3;
    private static NotificationsChangedListener sNotificationsChangedListener;
    private static LauncherNotificationService sNotificationListenerInstance = null;
    private static boolean sIsConnected;

    public static void setNotificationsChangedListener(NotificationsChangedListener listener) {
        sNotificationsChangedListener = listener;

        if (sNotificationListenerInstance != null) {
            sNotificationListenerInstance.onNotificationFullRefresh();
        }
    }

    public static void removeNotificationsChangedListener() {
        sNotificationsChangedListener = null;
    }

    private final Handler mUiHandler;
    private Handler.Callback mUiCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_NOTIFICATION_POSTED:
                    if (sNotificationsChangedListener != null) {
                        NotificationPostedMsg msg = (NotificationPostedMsg) message.obj;
                        sNotificationsChangedListener.onNotificationPosted(msg.packageUserKey,
                                msg.notificationKey, false);
                    }
                    break;
                case MSG_NOTIFICATION_REMOVED:
                    if (sNotificationsChangedListener != null) {
                        Pair<PackageUserKey, NotificationKeyData> pair
                                = (Pair<PackageUserKey, NotificationKeyData>) message.obj;
                        sNotificationsChangedListener.onNotificationRemoved(pair.first, pair.second);
                    }
                    break;
                case MSG_NOTIFICATION_FULL_REFRESH:
                    if (sNotificationsChangedListener != null) {
                        sNotificationsChangedListener.onNotificationFullRefresh(
                                (List<StatusBarNotification>) message.obj);
                    }
                    break;
            }
            return true;
        }
    };

    public LauncherNotificationService() {
        mUiHandler = new Handler(Looper.getMainLooper(), mUiCallback);
        sNotificationListenerInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("服务creat");
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("服务onBind");
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        System.out.println("连接服务");
        sIsConnected = true;
        onNotificationFullRefresh();
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        sIsConnected = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        mUiHandler.obtainMessage(MSG_NOTIFICATION_POSTED, new NotificationPostedMsg(sbn))
                .sendToTarget();
    }

    @Override
    public void onNotificationRemoved(final StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Pair<PackageUserKey, NotificationKeyData> packageUserKeyAndNotificationKey
                = new Pair<>(PackageUserKey.fromNotification(sbn),
                NotificationKeyData.fromNotification(sbn));
        mUiHandler.obtainMessage(MSG_NOTIFICATION_REMOVED, packageUserKeyAndNotificationKey)
                .sendToTarget();
    }

    private void onNotificationFullRefresh() {
        mUiHandler.obtainMessage(MSG_NOTIFICATION_FULL_REFRESH).sendToTarget();
    }

    public List<StatusBarNotification> getNotificationsForKeys(List<NotificationKeyData> keys) {
        StatusBarNotification[] notifications = LauncherNotificationService.this
                .getActiveNotifications(NotificationKeyData.extractKeysOnly(keys)
                        .toArray(new String[keys.size()]));
        return notifications == null ? Collections.EMPTY_LIST : Arrays.asList(notifications);
    }

    private class NotificationPostedMsg {
        PackageUserKey packageUserKey;
        NotificationKeyData notificationKey;
//        boolean shouldBeFilteredOut;

        NotificationPostedMsg(StatusBarNotification sbn) {
            packageUserKey = PackageUserKey.fromNotification(sbn);
            notificationKey = NotificationKeyData.fromNotification(sbn);
//            shouldBeFilteredOut = shouldBeFilteredOut(sbn);
        }
    }

    public interface NotificationsChangedListener {
        void onNotificationPosted(PackageUserKey postedPackageUserKey,
                                  NotificationKeyData notificationKey, boolean shouldBeFilteredOut);

        void onNotificationRemoved(PackageUserKey removedPackageUserKey,
                                   NotificationKeyData notificationKey);

        void onNotificationFullRefresh(List<StatusBarNotification> activeNotifications);
    }
//    /**
//     * Filter out notifications that don't have an intent
//     * or are headers for grouped notifications.
//     *
//     * @see #shouldBeFilteredOut(StatusBarNotification)
//     */
//    private List<StatusBarNotification> filterNotifications(
//            StatusBarNotification[] notifications) {
//        if (notifications == null) return null;
//        Set<Integer> removedNotifications = new HashSet<>();
//        for (int i = 0; i < notifications.length; i++) {
//            if (shouldBeFilteredOut(notifications[i])) {
//                removedNotifications.add(i);
//            }
//        }
//        List<StatusBarNotification> filteredNotifications = new ArrayList<>(
//                notifications.length - removedNotifications.size());
//        for (int i = 0; i < notifications.length; i++) {
//            if (!removedNotifications.contains(i)) {
//                filteredNotifications.add(notifications[i]);
//            }
//        }
//        return filteredNotifications;
//    }

//    private boolean shouldBeFilteredOut(StatusBarNotification sbn) {
//        getCurrentRanking().getRanking(sbn.getKey(), mTempRanking);
//        if (Utilities.ATLEAST_OREO && !mTempRanking.canShowBadge()) {
//            return true;
//        }
//        Notification notification = sbn.getNotification();
//        if (Utilities.ATLEAST_OREO && mTempRanking.getChannel().getId().equals(NotificationChannel.DEFAULT_CHANNEL_ID)) {
//            // Special filtering for the default, legacy "Miscellaneous" channel.
//            if ((notification.flags & Notification.FLAG_ONGOING_EVENT) != 0) {
//                return true;
//            }
//        } else if (!Utilities.ATLEAST_OREO && (notification.flags & Notification.FLAG_ONGOING_EVENT) != 0) {
//            return true;
//        }
//        boolean isGroupHeader = (notification.flags & Notification.FLAG_GROUP_SUMMARY) != 0;
//        CharSequence title = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
//        CharSequence text = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
//        boolean missingTitleAndText = TextUtils.isEmpty(title) && TextUtils.isEmpty(text);
//        return (isGroupHeader || missingTitleAndText);
//    }
}
