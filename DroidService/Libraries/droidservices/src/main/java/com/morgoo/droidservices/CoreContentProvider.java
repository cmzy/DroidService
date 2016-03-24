package com.morgoo.droidservices;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by zhangyong232@gmail.com on 2016/3/9.
 */
public class CoreContentProvider extends ContentProvider {

    public CoreContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mServiceManager = new IServiceManagerImpl(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private IBinder mServiceManager = null;


    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        if (ContentProviderBinderTransact.METHOD_GET_SERVICE_MANAGER.equals(method)) {
            extras.setClassLoader(getContext().getClassLoader());
            CompatUtils.BundleCompat.putBinder(extras, ContentProviderBinderTransact.EXTRA_BINDER, mServiceManager);
        }
        return extras;
    }


}
