package com.dqwl.optiontrade.mvp.trade_record.view;

import android.content.Context;

import com.dqwl.optiontrade.widget.SmallProgressBar;

/**
 * Created by jimdaxu on 16/8/22.
 */
public interface TradeRecordView {
    Context getContext();
    void addSmallProgressBar(SmallProgressBar smallProgressBar);
}
