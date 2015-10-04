package com.mxhacks.hack.utils;

import android.util.Log;

/**
 * Created by José Ángel García Salinas on 03/10/15.
 */
public class Logger {

    private final static String APP_TAG = "MXHACKS_APP";

    public static void E(String value,Exception e){
        Log.e(APP_TAG,value,e);
    }
    public static void E(String value){
        Log.e(APP_TAG,value);
    }

    public static void D(String value){
        Log.d(APP_TAG,value);
    }
}
