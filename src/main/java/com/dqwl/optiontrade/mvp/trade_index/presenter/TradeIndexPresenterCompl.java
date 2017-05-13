package com.dqwl.optiontrade.mvp.trade_index.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.dqwl.optiontrade.bean.BeanChangePercent;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.EventBusAllSymbol;
import com.dqwl.optiontrade.bean.RealTimeDataList;
import com.dqwl.optiontrade.constant.MessageType;
import com.dqwl.optiontrade.handler.HandlerSend;
import com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity;
import com.dqwl.optiontrade.mvp.trade_index.view.ITradeIndexView;
import com.dqwl.optiontrade.util.CacheUtil;
import com.dqwl.optiontrade.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xjunda
 * @date 2016-08-22
 */
public class TradeIndexPresenterCompl implements ITradeIndexPresenter {
    private ArrayList<BeanSymbolConfig.SymbolsBean> realTimeDatas = new ArrayList<>();//实时数据
    private ArrayList<BeanSymbolConfig.SymbolsBean> realTimeDatasTemp = new ArrayList<>();//保存实时数据，以便恢复
    private Handler mHandler;
    private ITradeIndexView mTradeIndexView;
    private String TAG= SystemUtil.getTAG(this);



    public TradeIndexPresenterCompl(ITradeIndexView tradeIndexView) {
        mTradeIndexView = tradeIndexView;
    }

    public TradeIndexPresenterCompl(ITradeIndexView tradeIndexView, Handler handlerRead) {
        mTradeIndexView = tradeIndexView;
        mHandler = handlerRead;
        sendHeartBeat();
    }

    /**
     * 客户端主动发送心跳
     */
    private void sendHeartBeat(){
        if(mHandler!=null){
//            mHandler.post(heartBeatRunnable);
            sendMessageToServer(MessageType.TYPE_ORDER_SREVER_TIME);
        }
    }

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            sendMessageToServer("{\"msg_type\":1}");
            mHandler.postDelayed(this, 30*1000);
        }
    };

    @Override
    public void releaseSession() {
        mHandler.sendEmptyMessage(HandlerSend.CLOSE);
    }

    @Override
    public ArrayList<BeanSymbolConfig.SymbolsBean> writeRealTimeData(List<BeanSymbolConfig.SymbolsBean> symbolShow, List<EventBusAllSymbol.ItemSymbol> allSymbol) {
        realTimeDatas.clear();
        for (EventBusAllSymbol.ItemSymbol symbolName : allSymbol) {
            for (BeanSymbolConfig.SymbolsBean symbol : symbolShow) {
                if (!symbol.getSymbol().equalsIgnoreCase(symbolName.getSymbol())) {
                    continue;
                } else {
                    realTimeDatas.add(symbol);
                    sendMessageToServer("{\"msg_type\":1010,\"symbol\":\"" + symbol.getSymbol() + "\"}");
                }
            }
        }
        return realTimeDatas;
    }



    private void sendMessageToServer(String data) {
        Message message = new Message();
        message.obj = (data);
        mHandler.sendMessage(message);
    }

    @Override
    public ArrayList<BeanSymbolConfig.SymbolsBean> getRealTimeData() {
        return realTimeDatas;
    }

    @Override
    public void subscribeSymbol(String activiteSymbol) {
        if (mHandler != null){
            for (BeanSymbolConfig.SymbolsBean symbol : realTimeDatas) {
                if(symbol.getSymbol().equalsIgnoreCase(activiteSymbol)){
                    continue;
                }
                sendMessageToServer("{\"msg_type\":1010,\"symbol\":\"" + symbol.getSymbol() + "\"}");
            }
        }
    }

    @Override
    public void unSubscribeSymbol(String activitSymbol) {
        if (mHandler != null)
            for (BeanSymbolConfig.SymbolsBean symbol : realTimeDatas) {
                if(symbol.getSymbol().equalsIgnoreCase(activitSymbol)){
                    continue;
                }
                sendMessageToServer("{\"msg_type\":1020,\"symbol\":\"" + symbol.getSymbol() + "\"}");
            }
    }



    @Override
    public void subscribleFavorSymbole(Context context) {
        realTimeDatasTemp.clear();
        unSubscribeSymbol("");
        realTimeDatasTemp.addAll(realTimeDatas);
        realTimeDatas.clear();
        realTimeDatas.addAll(CacheUtil.favorProducts(context));
        subscribeSymbol("");
    }

    @Override
    public void subscribleAllSymbole() {
        unSubscribeSymbol("");
        realTimeDatas.clear();
        realTimeDatas.addAll(realTimeDatasTemp);
        subscribeSymbol("");
    }

    /**
     * 根据数据，遍历item是否在显示。如果在则更新状态，数字和颜色
     * @param realTimeDataList
     */
    @Override
    public void showRealTimeData(RealTimeDataList realTimeDataList) {
        if (realTimeDataList == null || realTimeDatas == null) {
            return;
        }
        for (int i = 0; i < realTimeDatas.size(); i++) {
            BeanSymbolConfig.SymbolsBean data = realTimeDatas.get(i);
            for (RealTimeDataList.BeanRealTime symbols : realTimeDataList.getQuotes()) {
                if (symbols.getSymbol().equalsIgnoreCase(data.getSymbol())) {
                    data.setProductPrice((float) symbols.getBid());
                    mTradeIndexView.setRealTimeData(i);
                } else {
                    data.setFlag(0);
                }
            }
        }
    }

    /**
     * 发送心跳
     */
    @Override
    public void responseHeartBeat() {
        sendMessageToServer("{\"msg_type\":2}");
    }

    @Override
    public void onDestroy() {
        unSubscribeSymbol("");
        releaseSession();
    }
//    @Subscribe(threadMode = ThreadMode.BACKGROUND){
//
//    }
    @Override
    public void setHandler(Handler handler) {
        if(mHandler!=null){
            mHandler.removeCallbacks(heartBeatRunnable);
        }
        mHandler = handler;
        sendHeartBeat();
    }

    @Override
    public Map<String, BeanChangePercent> writePercentTimeData(List<BeanSymbolConfig.SymbolsBean> symbolShow) {
        Map<String,BeanChangePercent> mBeanChangePercents= new TreeMap<>();
        BeanChangePercent mBeanChangePercent=new BeanChangePercent();
        for(BeanSymbolConfig.SymbolsBean symbol:symbolShow) {
            mBeanChangePercent.setTz_delta(TradeIndexActivity.tz_detla);
            mBeanChangePercent.setSymbol(symbol.getSymbol());
            if (symbol.getCycles() != null) {
                for (BeanSymbolConfig.SymbolsBean.CyclesBean cyclesBean : symbol.getCycles()) {
                    mBeanChangePercent.setPercent(cyclesBean.getPercent());
                    //加入改变时间
                    mBeanChangePercent.setType(0);
                    if (cyclesBean.getTimes() != null) {
                        mBeanChangePercents.put(cyclesBean.getTimes().get(0).getB(), mBeanChangePercent);
                        mBeanChangePercent.setType(1);
                        //加入消失时间
                        mBeanChangePercents.put(cyclesBean.getTimes().get(0).getE(), mBeanChangePercent);
                    }
                }
            }
        }
        return mBeanChangePercents;
    }

    @Override
    public void connectToMinaServer() {
        mHandler.sendEmptyMessage(HandlerSend.CONNECT);
    }

}
