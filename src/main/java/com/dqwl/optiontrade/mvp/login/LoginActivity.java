package com.dqwl.optiontrade.mvp.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.base.BaseActivity;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.bean.EventBusAllSymbol;
import com.dqwl.optiontrade.bean.ResponseEvent;
import com.dqwl.optiontrade.mvp.login.presenter.LoginPresenterCompl;
import com.dqwl.optiontrade.mvp.login.view.ILoginView;
import com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity;
import com.dqwl.optiontrade.util.AesEncryptionUtil;
import com.dqwl.optiontrade.util.CacheUtil;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.dqwl.optiontrade.util.SystemUtil;
import com.dqwl.optiontrade.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements ILoginView {
    public static final String ACTIVEORDER = "ACTIVEORDER";
    public static final String ALL_SYMBOL = "allSymbol";
    private Button btnLogin;
    private EditText etUserName, etPassword;
    private LoginPresenterCompl mLoginPresenterCompl;
    private TextView mTextViewVersionName;
    /**
     * 未完成的订单
     */
    private HashMap<Integer, BeanOrderResult> activeOrder = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initIntentData() {

    }

    @Override
    protected void initViewAndLsnr() {
        setContentView(R.layout.activity_login_layout);
        btnLogin = (Button) findViewById(R.id.btn_login);
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etPassword = (EditText) findViewById(R.id.et_user_password);
        mTextViewVersionName = (TextView) findViewById(R.id.loginAppVersionTextView);
        mTextViewVersionName.setText(VersionUtil.getVersionName(this));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void onLogin() {
        if (TextUtils.isEmpty(etUserName.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())) {
            showToast(R.string.input_your_name_password);
            return;
        }
        if (!SystemUtil.isAvalidNetSetting(LoginActivity.this)) {
            showToast(R.string.check_your_network);
            return;
        }
        showPopupLoading(btnLogin);
        btnLogin.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doLogin(etUserName.getText().toString(),
                        AesEncryptionUtil.encrypt(etPassword.getText().toString()));
            }
        }).start();
    }

    @Override
    protected void initData() {
        String[] user = CacheUtil.getUserInfo(this);
        if(user!=null){
            etUserName.setText(user[0]);
            etPassword.setText(AesEncryptionUtil.decrypt(user[1]));
        }
        mLoginPresenterCompl = new LoginPresenterCompl(this);
    }

    @Override
    protected void initRegister() {
        EventBus.getDefault().register(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if(popupWindowLoading!=null&&popupWindowLoading.isShowing())
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeOut(String timeout){
        if(timeout.equalsIgnoreCase(SSLSocketChannel.TIMEOUT)){
            if(popupWindowLoading!=null)
            popupWindowLoading.dismiss();
            showToast(R.string.net_or_server_error);
            btnLogin.setEnabled(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginResult(ResponseEvent loginEvent) {
        int code = loginEvent.getResult_code();
        if (code == 0) {
            CacheUtil.saveuserInfo(this, etUserName.getText().toString(),
                    AesEncryptionUtil.encrypt(etPassword.getText().toString()));
        } else {
            if(code==-100){//服务器出错，超时等
                showToast(R.string.net_or_server_error);
            }else {
                showToast(R.string.username_or_pwd_incorrect);
            }
            if (popupWindowLoading != null) {
                popupWindowLoading.dismiss();
            }
            btnLogin.setEnabled(true);
        }
    }


    /**
     * 获取所有产品，并且订阅
     *
     * @param allSymbol
     */
    @Subscribe
    public void onGetAllSymbol(EventBusAllSymbol allSymbol) {
        startActivity(new Intent(LoginActivity.this, TradeIndexActivity.class)
                .putParcelableArrayListExtra(ALL_SYMBOL, allSymbol.getItems()));

        Log.i("123", "onGetAllSymbol: 被执行了");
        LoginActivity.this.finish();
    }


    @Override
    public void doLogin(String name, String passwd) {
        mLoginPresenterCompl.doLogin(name, passwd);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
