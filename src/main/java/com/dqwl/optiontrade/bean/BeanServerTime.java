package com.dqwl.optiontrade.bean;

/**
 * Created by jimda on 2016/10/17.
 * @link MinaTimeChartActivity #getServerTime
 * @link TradeIndexActivity  #onGetCurrentServerTime
 */

public class BeanServerTime {
    private int msg_type;
    private String time;

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
