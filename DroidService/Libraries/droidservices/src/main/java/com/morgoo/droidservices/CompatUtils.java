package com.morgoo.droidservices;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by zhangyong232@gmail.com on 2016/3/9.
 */
class CompatUtils {

    static class ContentResolverCompat {

        public static Bundle call(ContentResolver resolver, Uri uri, String method,
                                  String arg, Bundle extras) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return callAPI11(resolver, uri, method, arg, extras);
            } else {
                return callAPI10(resolver, uri, method, arg, extras);
            }
        }

        private static Bundle callAPI10(ContentResolver resolver, Uri uri, String method, String arg, Bundle extras) {
            try {
                Method call = null;
                try {
                    call = ContentResolver.class.getDeclaredMethod("call", Uri.class, String.class, String.class, Bundle.class);
                } catch (NoSuchMethodException e) {
                }
                if (call == null) {
                    call = ContentResolver.class.getDeclaredMethod("call", Uri.class, String.class, String.class, Bundle.class);
                }
                return (Bundle) call.invoke(resolver, uri, method, arg, extras);
            } catch (Exception e) {
                RuntimeException exception = new RuntimeException(e);
                exception.initCause(e);
                throw exception;
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private static Bundle callAPI11(ContentResolver resolver, Uri uri, String method, String arg, Bundle extras) {
            return resolver.call(uri, method, arg, extras);
        }
    }

    static class BundleCompat {

        private static final String TAG = "Bundle";

        public static IBinder getBinder(Bundle data, String key) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return getBinerAPI18(data, key);
            } else {
                return getBinerAPI10(data, key);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        private static IBinder getBinerAPI18(Bundle data, String key) {
            return data.getBinder(key);
        }

        private static IBinder getBinerAPI10(Bundle data, String key) {
            Object o = data.get(key);
            if (o == null) {
                return null;
            }
            try {
                return (IBinder) o;
            } catch (ClassCastException e) {
                typeWarning(key, o, "IBinder", e);
                return null;
            }
        }

        public static void putBinder(Bundle data, String key, IBinder value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                putBinerAPI18(data, key, value);
            } else {
                putBinerAPI10(data, key, value);
            }
        }

        private static void putBinerAPI10(Bundle data, String key, IBinder value) {
            //public void putIBinder(String key, IBinder value)
            try {
                Method putIBinder = null;
                try {
                    putIBinder = Bundle.class.getDeclaredMethod("putIBinder", String.class, IBinder.class);
                } catch (NoSuchMethodException e) {
                }
                if (putIBinder == null) {
                    putIBinder = Bundle.class.getMethod("putIBinder", String.class, IBinder.class);
                }
                putIBinder.invoke(data, key, value);
            } catch (Exception e) {
                RuntimeException exception = new RuntimeException(e);
                exception.initCause(e);
                throw exception;
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        private static void putBinerAPI18(Bundle data, String key, IBinder value) {
            data.putBinder(key, value);
        }


        private static void typeWarning(String key, Object value, String className,
                                        ClassCastException e) {
            typeWarning(key, value, className, "<null>", e);
        }

        private static void typeWarning(String key, Object value, String className,
                                        Object defaultValue, ClassCastException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Key ");
            sb.append(key);
            sb.append(" expected ");
            sb.append(className);
            sb.append(" but value was a ");
            sb.append(value.getClass().getName());
            sb.append(".  The default value ");
            sb.append(defaultValue);
            sb.append(" was returned.");
            Log.w(TAG, sb.toString());
            Log.w(TAG, "Attempt to cast generated internal exception:", e);
        }
    }


}
