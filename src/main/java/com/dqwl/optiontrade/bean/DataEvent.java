package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-07-25
 * 数据message
 * @link MinaTimeChartActivity  # onDrawHistroyData
 * @link MinaTimeChartActivity  # onOrderResult
 * @link TradeRecordActivity    # onGetTradeResult     sticky = true
 */
public class DataEvent {
    private String result;
    private int type;//0为实时数据，1为历史数据,2为下单结果和请求

    public DataEvent(String result, int type) {
        this.result = result;
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public int getType() {
        return type;
    }
}
