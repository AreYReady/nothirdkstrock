package com.dqwl.optiontrade.bean;

import java.util.List;

/**
 * @author xjunda
 * @date 2016-07-20
 * @link MinaTimeChartActivity # onDrawRealTimeData
 * @link TradeIndexActivity # onGetRealTimeData
 */
public class RealTimeDataList {
    /**
     * count : 2
     * msg_type : 1000
     * quotes : [{"ask":"125.711","bid":"125.710","symbol":"EURJPYbo","time":"2016-04-06 12:13:52"},{"ask":"1.13749","bid":"1.13731","symbol":"EURUSD","time":"2016-04-06 12:13:52"}]
     */

    private int count;
    private int msg_type;
    /**
     * ask : 125.711
     * bid : 125.710
     * symbol : EURJPYbo
     * time : 2016-04-06 12:13:52
     */

    private List<BeanRealTime> quotes;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public List<BeanRealTime> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<BeanRealTime> quotes) {
        this.quotes = quotes;
    }

    public static class BeanRealTime {
        private double ask;
        private double bid;
        private String symbol;
        private String time;

        public double getAsk() {
            return ask;
        }

        public void setAsk(double ask) {
            this.ask = ask;
        }

        public double getBid() {
            return bid;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
