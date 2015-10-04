package com.mxhacks.hack.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by José Ángel García Salinas on 03/10/15.
 */
public class ValueSave {

    private static final String SHARED_PREFS_ID = "MXHACK_APP_SHAREDPREFS_ID" ;


    public static void  saveString(Context context,String id,String value){
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.putString(id,value);
        edit.apply();
    }

    public static String getString(Context context,String id){
        return getSharedPreferences(context).getString(id,null);
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SHARED_PREFS_ID,Context.MODE_PRIVATE);
    }
}
