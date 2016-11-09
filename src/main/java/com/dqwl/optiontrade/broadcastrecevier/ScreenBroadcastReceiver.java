package com.dqwl.optiontrade.broadcastrecevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.dqwl.optiontrade.application.SysApplication;
import com.dqwl.optiontrade.base.BaseActivity;
import com.dqwl.optiontrade.util.CacheUtil;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.dqwl.optiontrade.widget.CustomProgressBar;

/**
 * 锁屏广播接收器
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {
    public static final String CONNECT = "connect";
    private BaseActivity activity;
    private String userInfo[];
    private CustomProgressBar cpbNetworkError;
    private SSLSocketChannel<String> mSSLSocketChannel;
    private Handler mHandler;
    /**
     * 倒数计时锁屏3分钟后关闭应用
     */
    private TimeCount mTimeCount = new TimeCount(3*60*1000, 1000);

    public ScreenBroadcastReceiver(BaseActivity activity, Handler handlerRead, CustomProgressBar cpbNetworkError) {
        this.activity = activity;
        this.userInfo = CacheUtil.getUserInfo(activity);
        this.cpbNetworkError = cpbNetworkError;
        mHandler = handlerRead;
    }

    private Runnable lockRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("123", "run: fffffffffffffffffffffffffff");
            SysApplication.getInstance().exit();
        }
    };

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            // 开屏
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            // 锁屏
//            mHandler.sendEmptyMessage(HandlerSend.CLOSE);
            Log.i("123", "onReceive: fffffffffffffffffffffff");
//            mHandler.postDelayed(lockRunnable, 60*1000);
            mTimeCount.start();
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            // 解锁
            mTimeCount.cancel();
//            mHandler.removeCallbacks(lockRunnable);
//            if (cpbNetworkError != null) {
//                cpbNetworkError.startLoadingAnimation();
//                activity.showPopupLoading(cpbNetworkError);
//            }
////            if (SystemUtil.isTopActivity(context, activity.getLocalClassName())) {
////                mHandler.sendEmptyMessage(HandlerSend.CONNECT);
////            }
//            EventBus.getDefault().post(CONNECT);
        }
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    private class TimeCount extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Log.i("123", "run: fffffffffffffffffffffffffff");
            SysApplication.getInstance().exit();
        }
    }
}