package com.dqwl.optiontrade.mvp.trade_record.presenter;

import android.os.Handler;
import android.os.Message;

import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.mvp.trade_record.view.TradeRecordView;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimdaxu on 16/8/22.
 */
public class TradeRecordPresenterCompl implements ITradeRecordPresenter{
    private TradeRecordView mTradeRecordView;
    private Map<String, Integer> symbolNotCompelete = new HashMap<>();
    private SSLSocketChannel<String> mSSLSocketChannel;
    private Handler mHandler;

    public TradeRecordPresenterCompl(TradeRecordView tradeRecordView
            , Handler handlerRead) {
        mTradeRecordView = tradeRecordView;
        mHandler = handlerRead;
    }

    @Override
    public void releaseSession() {
       if(mSSLSocketChannel!=null){
           try {
               mSSLSocketChannel.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    @Override
    public void subscribeNewSymbol(BeanOrderResult orderResult, String symbol) {
        if (!orderResult.getSymbol().equalsIgnoreCase(symbol)
                && !symbolNotCompelete.containsKey(orderResult.getSymbol())) {
            symbolNotCompelete.put(orderResult.getSymbol(), orderResult.getTicket());
            sendMessageToServer("{\"msg_type\":1010,\"symbol\":\"" + orderResult.getSymbol() + "\"}");
        }
    }

    private void sendMessageToServer(String data) {
        Message message = new Message();
        message.obj = (data);
        mHandler.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        mTradeRecordView =  null;
        if (symbolNotCompelete.size() > 0) {
            for (String symbol : symbolNotCompelete.keySet()) {
                sendMessageToServer("{\"msg_type\":1020,\"symbol\":\"" + symbol + "\"}");
            }
        }
    }


    @Override
    public void connectToMinaServer() {
    //        MinaUtil.connectToServerWithLocalSP(mTradeRecordView.getContext());
    }

    public void setIoSession(SSLSocketChannel<String> ioSession) {
         mSSLSocketChannel = ioSession;
    }
}
