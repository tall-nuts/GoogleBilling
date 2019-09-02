package com.google.billing.utils;

import android.util.Log;

/**
 * @author gaopengfei on 2019/05/13.
 */
public class LogUtils {

    private static final String TAG = "JZ-PAYMENT";
    private static boolean mDebug = true;

    public static void setDebug(boolean debug){
        mDebug = debug;
    }

    public static void v(String log){
        if (mDebug){
            Log.v(TAG, log);
        }
    }

    public static void d(String log){
        if (mDebug){
            Log.d(TAG, log);
        }
    }

    public static void i(String log){
        if (mDebug){
            Log.i(TAG, log);
        }
    }

    public static void w(String log){
        if (mDebug){
            Log.w(TAG, log);
        }
    }

    public static void e(String log){
        if (mDebug){
            Log.e(TAG, log);
        }
    }
}
