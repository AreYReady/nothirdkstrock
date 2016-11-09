package com.dqwl.optiontrade.mvp.time_chart_show.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.dqwl.optiontrade.bean.BeanHistoryRequest;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.bean.HistoryDataList;
import com.dqwl.optiontrade.mvp.baseMVPInterface;

import java.util.HashMap;

/**
 * Created by jimdaxu on 16/8/22.
 */
public interface ITimeChartShowPresenter extends baseMVPInterface {
    void getUsrInfo(Context context);
    void getHistoyCache(Context context);
    void subscribeSymbols(Context context, String preSymbol, String nowSymbol);
    double[] getTempNowPrice(HistoryDataList nowDataList);
    /**
     * 获取活动中订单
     * @param addFlag 是否增加过标志，是为增加过
     */
    void getActiveOrder(HashMap<Integer, BeanOrderResult> activeOrder, long timeOffset, boolean addFlag);
    void saveCache(HistoryDataList historyDataList);
    void saveRealTimeCache(BeanHistoryRequest cache, String realPrice, int flag, double nowPrice, int symbolPosition,
                           double firstPrice) ;
    HistoryDataList isHistoryCache(BeanHistoryRequest cache, int maxDegreeSecond);
    void writeRealTimeRequstToServer(String symbol);
    void writeHistroyRequestToServer(String historyRequest);
    void writeOrderToServer(String symbol, int derection
            , double money, int maxDegreeSecond
            , int percent);
    void getServerTime();

    void onDestroy(Context context);

    void setHandler(Handler handler);

    void initHandler(HandlerThread handlerThread);
}
