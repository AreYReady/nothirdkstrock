package com.dqwl.optiontrade.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dqwl.optiontrade.mvp.login.presenter.LoginPresenterCompl;
import com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Created by jimdaxu on 16/9/6.
 * 读取服务器响应
 */
public class HandlerWrite extends Handler {
    private SSLSocketChannel<String> mSSLSocketChannel;

    public HandlerWrite(Looper looper, SSLSocketChannel<String> SSLSocketChannel) {
        super(looper);
        mSSLSocketChannel = SSLSocketChannel;
    }

    public void setSSLSocketChannel(SSLSocketChannel<String> SSLSocketChannel) {
        mSSLSocketChannel = SSLSocketChannel;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        String response = null;
        boolean flag = true;
        try {
            while (flag&&mSSLSocketChannel!=null){
                response = mSSLSocketChannel.receive();
                Log.i("123", "doLogin: Response received: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                mSSLSocketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }finally {
                mSSLSocketChannel = null;
                EventBus.getDefault().post(LoginPresenterCompl.DISCONNECT_FROM_SERVER);
                flag = false;
                Log.i("123", "doLogin: Response received: " + response);
            }
        }
        Log.i("123", "doLogin: Response received: " + response);
    }
}
