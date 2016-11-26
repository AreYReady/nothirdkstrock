package com.dqwl.optiontrade.mvp.time_chart_show.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.dqwl.optiontrade.bean.BeanCurrentServerTime;
import com.dqwl.optiontrade.bean.BeanHistoryRequest;
import com.dqwl.optiontrade.bean.BeanOrder;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.bean.BeanRealTimeRequest;
import com.dqwl.optiontrade.bean.BeanSubscrible;
import com.dqwl.optiontrade.bean.HistoryDataList;
import com.dqwl.optiontrade.handler.HandlerSend;
import com.dqwl.optiontrade.mvp.time_chart_show.view.ITimeChartShowView;
import com.dqwl.optiontrade.util.CacheUtil;
import com.dqwl.optiontrade.util.ChartUtils;
import com.dqwl.optiontrade.util.MoneyUtil;
import com.dqwl.optiontrade.util.SystemUtil;
import com.dqwl.optiontrade.util.TimeUtils;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by jimdaxu on 16/8/22.
 */
public class TimeChartShowPresenterCompl implements ITimeChartShowPresenter{
    private ITimeChartShowView mTimeChartShowView;
    private String[] userInfo;
    private HashMap<BeanHistoryRequest, HistoryDataList> historyCache = new HashMap<>();
    private Handler mHandler;

    public TimeChartShowPresenterCompl(ITimeChartShowView timeChartShowView) {
        mTimeChartShowView = timeChartShowView;
    }

    public TimeChartShowPresenterCompl(ITimeChartShowView timeChartShowView, Handler handlerRead) {
        mTimeChartShowView = timeChartShowView;
        mHandler = handlerRead;
    }

    @Override
    public void getUsrInfo(Context context) {
        userInfo = CacheUtil.getUserInfo(context);
    }

    @Override
    public void getHistoyCache(Context context) {
        historyCache = CacheUtil.getKLineCache(context);
    }

    @Override
    public void connectToMinaServer() {
        mHandler.sendEmptyMessage(HandlerSend.CONNECT);
    }

    @Override
    public double[] getTempNowPrice(HistoryDataList nowDataList) {
        int digits = nowDataList.getDigits();//当前页面产品的小数位
        String[] lastData = nowDataList.getItems().get(nowDataList.getCount() - 1).getO().split("\\|");
        String[] preData = new String[]{"0","0","0","0"};
        if (nowDataList.getCount()>1)
            nowDataList.getItems().get(nowDataList.getCount() - 2).getO().split("\\|");
        double lastPrice = MoneyUtil.addPrice(lastData[0], lastData[3], digits);
        double prePrice = MoneyUtil.addPrice(preData[0], preData[3], digits);
        mTimeChartShowView.getTempNowPrice(new double[]{lastPrice, prePrice});
        return new double[]{lastPrice, prePrice};
    }

    @Override
    public void subscribeSymbols(Context context, final String preSymbol, final String nowSymbol) {
        if (SystemUtil.isAvalidNetSetting(context) &&
                mHandler!=null) {
            BeanSubscrible subscriblePre = new BeanSubscrible(1020, preSymbol);
            String subSymblePre = new Gson().toJson(subscriblePre, BeanSubscrible.class);
            BeanSubscrible subscribleNow = new BeanSubscrible(1010, nowSymbol);
            String subSymbleNow = new Gson().toJson(subscribleNow, BeanSubscrible.class);
//            sendMessageToSubThread(subSymblePre);有写缓存，注释掉了，不用取消订阅前一个symbol
            sendMessageToSubThread(subSymbleNow);
        }
    }

    private void sendMessageToSubThread(String subSymblePre) {
        Message messagePre = new Message();
        messagePre.obj = subSymblePre;
        mHandler.sendMessage(messagePre);
    }

    @Override
    public void getActiveOrder(final HashMap<Integer, BeanOrderResult> activeOrder, long timeOffset, boolean addFlag) {
        for (Integer key : activeOrder.keySet()) {
            final BeanOrderResult order = activeOrder.get(key);
            if (order != null) {
                    final long timeLeft = (BeanCurrentServerTime.getInstance(1).getCurrentServerTime() -
                            TimeUtils.getOrderStartTimeNoTimeZone(order.getOpen_time()))/1000;//秒
                    if (timeLeft >= order.getTime_span()) {//当前时间大于到期时间，说明已经结账，要删除
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("hsc", "run: activeOrder.remove(order.getTicket()");
                                activeOrder.remove(order.getTicket());
                            }
                        }, 200);
                    } else if(!addFlag){
                        mTimeChartShowView.addProgressBar(order,
                                order.getTime_span() - (int)timeLeft);//没有到期则要显示
                    }
            }
        }
//        TradeIndexActivity.activeOrder=activeOrder;
        mTimeChartShowView.setBadgeNum(activeOrder.size());
    }

    @Override
    public void saveCache(HistoryDataList historyDataList) {
        historyDataList.setCacheTime(System.currentTimeMillis());//记录缓存的时间
//        historyDataList.setPrice(price);//保存最大、最小价格
        BeanHistoryRequest cache = new BeanHistoryRequest(historyDataList.getSymbol());
        historyCache.put(cache, historyDataList);//缓存数据
    }

    @Override
    public void saveRealTimeCache(BeanHistoryRequest cache, String realPrice, int flag, double nowPrice, int symbolPosition,
                                  double firstPrice) {
        HistoryDataList historyDataList = historyCache.get(cache);
        if(historyDataList == null){
            return;
        }
        historyDataList.setNowPrice(realPrice);
        historyDataList.setFlag(flag);//保存背景色

        double[] price;//最高价[1]，最低价[0]
        int digits = historyDataList.getDigits();
        price = ChartUtils.calcMaxMinPrice(historyDataList, digits);
        if (nowPrice > price[1] - (price[1]- price[0])*0.1
                || nowPrice < price[0] + (price[1] - price[0])*0.1) {//最高价、最低价越界则重绘
            if (nowPrice > price[1] - (price[1]- price[0])*0.1) {
                price[1] = nowPrice + (nowPrice - price[0]) *0.1;
            }
            if (nowPrice < price[0] + (price[1] - price[0])*0.1) {
                price[0] = nowPrice - (price[1] - nowPrice) *0.1;
            }
            historyDataList.setPrice(price);
            mTimeChartShowView.checkChartCache(symbolPosition, true);
        }
        historyCache.put(cache, historyDataList);
        mTimeChartShowView.setRealTimeTextBackGround(nowPrice, firstPrice, price);
    }

    @Override
    public HistoryDataList isHistoryCache(BeanHistoryRequest cache, int maxDegreeSecond) {
        HistoryDataList nowDataList = null;
        HistoryDataList tempDataList = historyCache.get(cache);
        if (tempDataList != null && tempDataList.getPeriod() * 60 == maxDegreeSecond) {
            //在周期内就使用缓存
            if (System.currentTimeMillis() - tempDataList.getCacheTime() < maxDegreeSecond * 1000) {
                nowDataList = tempDataList;
            }
        }
        return nowDataList;
    }

    @Override
    public void writeRealTimeRequstToServer(final String symbol) {
                BeanRealTimeRequest realTimeRequest = new BeanRealTimeRequest(1010, symbol);
                String realTimeRequestStr = new Gson().toJson(realTimeRequest, BeanRealTimeRequest.class);
                sendMessageToSubThread(realTimeRequestStr);
                Log.i("123", "writeRealTimeRequstToServer: " + realTimeRequestStr);
    }

    @Override
    public void writeHistroyRequestToServer(final String historyRequest) {
        sendMessageToSubThread(historyRequest);
    }

    @Override
    public void writeOrderToServer(final String symbol, final int derection
            , final double money, final int maxDegreeSecond
            , int percent) {
               BeanOrder order = new BeanOrder(Integer.valueOf(userInfo[0]),
                       symbol,
                       derection, money, maxDegreeSecond, percent);
               String orderStr = new Gson().toJson(order, BeanOrder.class);
        sendMessageToSubThread(orderStr);
    }

    /**
     * 获取服务器时间
     */
    @Override
    public void getServerTime() {
        sendMessageToSubThread("{\"msg_type\":280}");
    }

    @Override
    public void onDestroy(Context context) {
//        CacheUtil.writeKLineCache(context, historyCache);
        mTimeChartShowView = null;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void initHandler(HandlerThread handlerThread) {

    }
}
