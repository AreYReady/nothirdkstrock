package com.dqwl.optiontrade.bean;

import java.util.List;

/**
 * Created by jimdaxu on 16/8/22.
 */
public interface IHistoryDataList {
    int getMsg_type();
    int getResult_code();
    int getCount();
    int getDigits();
    List<HistoryData> getItems();
    String getSymbol();
    int getPeriod();
    long getCacheTime();
    String getNowPrice();
    int getFlag();
    double[] getPrice();
}
