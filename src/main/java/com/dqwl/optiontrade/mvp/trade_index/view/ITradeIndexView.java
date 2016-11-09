package com.dqwl.optiontrade.mvp.trade_index.view;

import android.content.Context;

/**
 * Created by jimdaxu on 16/8/22.
 */
public interface ITradeIndexView {
    Context getContext();

    /**
     * 局部更新文本
     * @param position
     */
    void setRealTimeData(int position);
}
