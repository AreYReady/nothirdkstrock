package com.dqwl.optiontrade.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author xjunda
 * @date 2016-07-18
 * 历史数据列表
 */
public class HistoryDataList implements Serializable, IHistoryDataList {

    /**
     * msg_type : 1101
     * result_code : 0
     * count : 2
     * digits : 5
     */

    private int msg_type;
    private int result_code;
    private int count;
    private int digits;
    private List<HistoryData> items;
    private String symbol;
    private int period;
    private long cacheTime;
    /**
     * 历史最近一次价格
     */
    private String nowPrice = "0.00000";
    private int flag;
    private double[] price;

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public List<HistoryData> getItems() {
        return items;
    }

    public void setItems(List<HistoryData> items) {
        this.items = items;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public String getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(String nowPrice) {
        this.nowPrice = nowPrice;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public double[] getPrice() {
        return price;
    }

    public void setPrice(double[] price) {
        this.price = price;
    }

}
