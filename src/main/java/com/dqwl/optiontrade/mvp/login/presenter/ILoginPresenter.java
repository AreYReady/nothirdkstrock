package com.dqwl.optiontrade.mvp.login.presenter;

/**
 * Created by kaede on 2015/10/12.
 */
public interface ILoginPresenter {
	void doLogin(String name, String passwd);
	void onDestroy();
}