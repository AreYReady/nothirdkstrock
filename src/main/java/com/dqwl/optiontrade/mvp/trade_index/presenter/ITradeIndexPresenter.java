package com.dqwl.optiontrade.mvp.trade_index.presenter;

import android.content.Context;
import android.os.Handler;

import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.EventBusAllSymbol;
import com.dqwl.optiontrade.bean.RealTimeDataList;
import com.dqwl.optiontrade.mvp.baseMVPInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xjunda
 * @date 2016-08-22
 */
public interface ITradeIndexPresenter extends baseMVPInterface {
    void releaseSession();
    ArrayList<BeanSymbolConfig.SymbolsBean> writeRealTimeData(List<BeanSymbolConfig.SymbolsBean> symbolShow, List<EventBusAllSymbol.ItemSymbol> allSymbol);
    ArrayList<BeanSymbolConfig.SymbolsBean> getRealTimeData();
    void subscribeSymbol(String activeSymbol);
    void unSubscribeSymbol(String activeSymbol);
    void subscribleFavorSymbole(Context context);
    void subscribleAllSymbole();
    void showRealTimeData(RealTimeDataList realTimeDataList);
    void responseHeartBeat();
    void onDestroy();

    void setHandler(Handler sslSocketChannel);
}
