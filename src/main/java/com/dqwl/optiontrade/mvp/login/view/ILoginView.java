package com.dqwl.optiontrade.mvp.login.view;

import android.content.Context;

/**
 * Created by kaede on 2015/5/18.
 */
public interface ILoginView {
    void doLogin(String name, String passwd);
    Context getContext();
}