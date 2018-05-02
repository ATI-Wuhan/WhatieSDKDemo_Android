package com.d9lab.ati.whatie_android_demo.application;

import android.app.Application;

import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;


/**
 * Created by liz on 2018/4/24.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EHomeInterface.getINSTANCE().init(this);
    }

}
