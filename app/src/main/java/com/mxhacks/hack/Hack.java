package com.mxhacks.hack;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by José Ángel García Salinas on 04/10/15.
 */
public class Hack extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
