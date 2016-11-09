package com.dqwl.optiontrade.mvp.trade_record.presenter;

import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.mvp.baseMVPInterface;

/**
 * Created by jimdaxu on 16/8/22.
 */
public interface ITradeRecordPresenter extends baseMVPInterface {
    void releaseSession();
    void subscribeNewSymbol(BeanOrderResult orderResult, String symbol);
    void onDestroy();
}
