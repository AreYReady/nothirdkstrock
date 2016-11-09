package com.dqwl.optiontrade.handler;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.dqwl.optiontrade.util.SocketUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Created by jimdaxu on 16/9/6.
 * 发送子线程的handler
 */
public class HandlerSend extends Handler {
    public final static int CONNECT = 1;
    public final static int CLOSE = 2;
    private Context mContext;
    private HandlerThread mHandlerThread;
    private SSLSocketChannel mSSLSocketChannel;
    private HandlerWrite mHandlerWrite;

    public HandlerSend(Looper looper, Context context
            , HandlerThread handlerThread, SSLSocketChannel SSLSocketChannel
            , HandlerWrite handlerWrite) {
        super(looper);
        mContext = context;
        mHandlerThread = handlerThread;
        mSSLSocketChannel = SSLSocketChannel;
        mHandlerWrite = handlerWrite;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case CONNECT://连接
                try {
                    if (mSSLSocketChannel != null){
                        mSSLSocketChannel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    mSSLSocketChannel = SocketUtil.connectToServer(mContext, mHandlerThread, mHandlerWrite);
                    EventBus.getDefault().postSticky(this);
                }
                break;
            case CLOSE://关闭
                try {
                    if (mSSLSocketChannel != null){
                        mSSLSocketChannel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    mSSLSocketChannel = null;
//                    EventBus.getDefault().post(LoginPresenterCompl.DISCONNECT_FROM_SERVER);
                }
                break;
            default:
                String data = (String) msg.obj;
                if (mSSLSocketChannel!=null)
                mSSLSocketChannel.send(data);
                break;
        }
    }

    public SSLSocketChannel getSSLSocketChannel() {
        return mSSLSocketChannel;
    }
}
