package com.example.jobschedulerdemo.utils;

import android.util.Log;

import com.example.jobschedulerdemo.BuildConfig;

public class Constant {
    private static final String TAG = "zyConstant";
    public static final String MESSENGER_INTENT_KEY
            = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";
    public static final String WORK_DURATION_KEY =
            BuildConfig.APPLICATION_ID + ".WORK_DURATION_KEY";
    public static final int MSG_UNCOLOR_START = 0;
    public static final int MSG_UNCOLOR_STOP = 1;
    public static final int MSG_COLOR_START = 2;
    public static final int MSG_COLOR_STOP = 3;

    public static void tag(String tag, Object o) {
        Log.d(TAG, tag + "---: " + o);
    }
}
