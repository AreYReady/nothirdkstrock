package com.dqwl.optiontrade.application;

import android.app.Application;

import com.dqwl.optiontrade.bean.BeanSymbolConfig;

/**
 * @author xjunda
 * @date 2016-10-05
 */

public class OptionApplication extends Application {
    public static BeanSymbolConfig symbolShow;
    private volatile static OptionApplication mOptionApplication;

    private OptionApplication() {
    }

    public static OptionApplication getSingleton() {
        if (mOptionApplication == null) {
            synchronized (OptionApplication.class) {
                if (mOptionApplication == null) {
                    mOptionApplication = new OptionApplication();
                }
            }
        }
        return mOptionApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
