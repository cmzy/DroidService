// IServiceManager.aidl
package com.morgoo.droidservices;

import android.os.IBinder;
import android.content.Intent;

interface IServiceManager {

    void addService(in String callingPackageName, in String name, in IBinder binder);

    void addIntentService(in String callingPackageName, in String name, in Intent intent);

    IBinder getService(in String callingPackageName, in String name);

    void removeService(in String callingPackageName, in String name);
}
