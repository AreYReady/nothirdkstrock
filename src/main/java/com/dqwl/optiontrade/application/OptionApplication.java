package com.dqwl.optiontrade.application;

import android.app.Application;

import com.dqwl.optiontrade.bean.BeanSymbolConfig;

/**
 * @author xjunda
 * @date 2016-10-05
 */

public class OptionApplication extends Application {
    public static BeanSymbolConfig symbolShow;
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
