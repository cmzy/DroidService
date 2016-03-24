package com.morgoo.droidservices;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 服务器管理器。
 * 对于任意的服务提供者，都可以向该服务系统中注册服务。
 * 服务注册有两种方式：
 * 1、直接注册：ServiceManager.getInstance().registerService(Context context, String name, IBinder binder);
 * 2、间接注册：ServiceManager.getInstance().registerService(Context context, String name,  Intent intent);
 * <p/>
 * Created by zhangyong232@gmail.com on 2016/3/9.
 */
public class ServiceManager {


    private ServiceManager() {

    }


    private static class SingleHodler {
        private static final ServiceManager sInstance = new ServiceManager();
    }


    public static ServiceManager getInstance() {
        return SingleHodler.sInstance;
    }


    private IServiceManager sIServiceManager;


    private synchronized void doGetIServiceManager(final Context context) throws RemoteException {
        if (sIServiceManager == null) {
            IBinder binder = ContentProviderBinderTransact.getBinder(context, mUri);
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    try {
                        doGetIServiceManager(context);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 0);
            sIServiceManager = IServiceManager.Stub.asInterface(binder);
        }
    }

    private IServiceManager getIServiceManager(Context context) throws RemoteException {
        doGetIServiceManager(context);
        return sIServiceManager;
    }

    private Uri mUri = Uri.parse("content://com.morgoo.droidservice/");

    public void setAuthorities(String authorities) {
        mUri = Uri.parse("content://" + authorities + "/");
    }

    public void registerService(final Context context, final String name, IBinder binder) throws RemoteException {
        getIServiceManager(context).addService(context.getPackageName(), name, binder);
    }

    public void registerService(final Context context, final String name, Intent intent) throws RemoteException {
        getIServiceManager(context).addIntentService(context.getPackageName(), name, intent);
    }

    public void unregisterService(final Context context, final String name) throws RemoteException {
        getIServiceManager(context).removeService(context.getPackageName(), name);
    }

    public void getService(final Context context, final String name) throws RemoteException {
        getIServiceManager(context).getService(context.getPackageName(), name);
    }

}
