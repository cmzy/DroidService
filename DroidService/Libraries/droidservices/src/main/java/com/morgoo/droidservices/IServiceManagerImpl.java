package com.morgoo.droidservices;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangyong232@gmail.com on 2016/3/9.
 */
class IServiceManagerImpl extends IServiceManager.Stub {

    private Map<String, ServiceItem> sAliveServices = new HashMap<>();
    private Map<String, ServiceItem> sDiedServices = new HashMap<>();

    private Context mContext;

    IServiceManagerImpl(Context context) {
        mContext = context;
    }

    @Override
    public synchronized void addService(final String callingPackageName, final String name, final IBinder binder) throws RemoteException {
        if (sAliveServices.containsKey(name)) {
            ServiceItem item = sAliveServices.get(name);
            throw new RuntimeException(String.format("Service %s has registed by pid:%s,uid:%s", name, item.callingPid, item.callingUid));
        }

        IBinder.DeathRecipient recipient = new DeathRecipient() {
            @Override
            public void binderDied() {
                ServiceItem item = sAliveServices.remove(name);
                if (item != null) {
                    sDiedServices.put(name, item);
                }
            }
        };

        final ServiceItem item = new ServiceItem(name, binder, null, recipient, callingPackageName, Binder.getCallingPid(), Binder.getCallingUid());
        sAliveServices.put(name, item);
        sDiedServices.remove(name);
        item.linkToDeath();
    }

    @Override
    public void addIntentService(String callingPackageName, String name, Intent intent) {

    }

    private static void ensureNotOnMainThread() {
        Looper looper = Looper.myLooper();
        if (looper != null && looper == Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "calling this from your main thread can lead to deadlock");
        }
    }

    private IBinder connectToService(Context context, Intent service) throws InterruptedException {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        ensureNotOnMainThread();
        final BlockingQueue<IBinder> q = new LinkedBlockingQueue<IBinder>(1);
        ServiceConnection serviceConnection = new ServiceConnection() {
            volatile boolean mConnectedAtLeastOnce = false;

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (!mConnectedAtLeastOnce) {
                    mConnectedAtLeastOnce = true;
                    try {
                        q.put(service);
                    } catch (InterruptedException e) {
                        // will never happen, since the queue starts with one available slot
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        boolean isBound = context.bindService(service,
                serviceConnection,
                Context.BIND_AUTO_CREATE);
        if (!isBound) {
            throw new RuntimeException("Could not bind to Service:" + service);
        }
        return q.take();
    }

    @Override
    public IBinder getService(final String callingPackageName, final String name) throws RemoteException {
        ServiceItem item = sAliveServices.get(name);
        if (item == null) {
            item = sDiedServices.get(name);
        }
        if (item != null) {
            return item.binder;
        }
        Log.e("IServiceManagerImpl", "getService in :" + Thread.currentThread() + ",l=" + Looper.myLooper());
        return null;
    }


    @Override
    public void removeService(final String callingPackageName, final String name) throws RemoteException {
        ServiceItem item = sAliveServices.remove(name);
        if (item != null) {
            item.unlinkToDeath();
        }
        sDiedServices.remove(name);
    }
}
