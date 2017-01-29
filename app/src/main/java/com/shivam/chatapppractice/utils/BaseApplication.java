package com.shivam.chatapppractice.utils;

import android.app.Application;

import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Shivam on 28-01-2017.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        RxPaparazzo.register(this);
    }
}
