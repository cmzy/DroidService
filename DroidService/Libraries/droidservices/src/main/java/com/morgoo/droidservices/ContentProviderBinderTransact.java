package com.morgoo.droidservices;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by zhangyong232@gmail.com on 2016/3/9.
 */
class ContentProviderBinderTransact {

    static final String METHOD_GET_SERVICE_MANAGER = "GetServiceManager";
    static final String EXTRA_BINDER = "com.morgoo.droidservice.EXTRA_BINDER";

    static IBinder getBinder(Context context, Uri mUri) {
        Bundle extra = new Bundle();
        extra.setClassLoader(context.getClassLoader());
        Bundle data = CompatUtils.ContentResolverCompat.call(context.getContentResolver(), mUri, METHOD_GET_SERVICE_MANAGER, null, extra);
        return CompatUtils.BundleCompat.getBinder(data, EXTRA_BINDER);
    }
}
