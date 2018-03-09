package com.wecu.widgetdemo.badge;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wecu on 2018/3/9.
 */


public class NotificationKeyData {
    public final String notificationKey;
    public final String shortcutId;
    public int count;

    private NotificationKeyData(String notificationKey, String shortcutId, int count) {
        this.notificationKey = notificationKey;
        this.shortcutId = shortcutId;
        this.count = Math.max(1, count);
    }

    public static NotificationKeyData fromNotification(StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        return new NotificationKeyData(sbn.getKey(), Utilities.ATLEAST_OREO ? notif.getShortcutId() : null, notif.number);
        return new NotificationKeyData(sbn.getKey(), null, notif.number);
    }

    public static List<String> extractKeysOnly(@NonNull List<NotificationKeyData> notificationKeys) {
        List<String> keysOnly = new ArrayList<>(notificationKeys.size());
        for (NotificationKeyData notificationKeyData : notificationKeys) {
            keysOnly.add(notificationKeyData.notificationKey);
        }
        return keysOnly;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationKeyData)) {
            return false;
        }
        return ((NotificationKeyData) obj).notificationKey.equals(notificationKey);
    }
}
