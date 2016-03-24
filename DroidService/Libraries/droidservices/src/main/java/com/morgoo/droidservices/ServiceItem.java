package com.morgoo.droidservices;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by zhangyong232@gmail.com on 2016/3/9.
 */
class ServiceItem {

    final String name;
    final int callingPid;
    final int callingUid;
    final IBinder binder;
    final Intent intent;

    IBinder.DeathRecipient recipient;

    ServiceItem(String name, IBinder binder, Intent intent, IBinder.DeathRecipient recipient, String callingPackage, int callingPid, int callingUid) {
        this.name = name;
        this.callingPid = callingPid;
        this.callingUid = callingUid;
        this.binder = binder;
        this.intent = intent;
        this.recipient = recipient;
    }

    void linkToDeath() throws RemoteException {
        if (binder != null && recipient != null) {
            binder.linkToDeath(recipient, 0);
        }
    }

    void unlinkToDeath() {
        if (binder != null && recipient != null) {
            binder.unlinkToDeath(recipient, 0);
        }
    }

}
