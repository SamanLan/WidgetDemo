package com.wecu.widgetdemo.badge;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;

import java.util.Arrays;

/**
 * Created by wecu on 2018/3/9.
 */

public class PackageUserKey {
    private int mHashCode;
    public String mPackageName;
    public UserHandle mUser;

//    public static PackageUserKey fromItemInfo(ItemInfo itemInfo) {
//        return new PackageUserKey(itemInfo.getTargetComponent().getPackageName(), itemInfo.user);
//    }

    public static PackageUserKey fromNotification(StatusBarNotification statusBarNotification) {
        return new PackageUserKey(statusBarNotification.getPackageName(), Build.VERSION.SDK_INT >= 21 ? statusBarNotification.getUser() : null);
    }

    public PackageUserKey(String str, UserHandle userHandle) {
        update(str, userHandle);
    }

    private void update(String str, UserHandle userHandle) {
        this.mPackageName = str;
        this.mUser = userHandle;
        this.mHashCode = userHandle == null ? Arrays.hashCode(new Object[]{str}) : Arrays.hashCode(new Object[]{str, userHandle});
    }

//    public boolean updateFromItemInfo(ItemInfo itemInfo) {
//        if (!DeepShortcutManager.supportsShortcuts(itemInfo)) {
//            return false;
//        }
//        update(itemInfo.getTargetComponent().getPackageName(), itemInfo.user);
//        return true;
//    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof PackageUserKey)) {
            return false;
        }
        PackageUserKey packageUserKey = (PackageUserKey) obj;
        if (this.mPackageName.equals(packageUserKey.mPackageName)) {
            if (mUser != null) {
                if (packageUserKey.mUser != null) {
                    z = this.mUser.equals(packageUserKey.mUser);
                }
            } else {
                z = true;
            }
        }
        return z;
    }
}
