package com.dqwl.optiontrade.bean;

import java.io.Serializable;

/**
 * @author xjunda
 * @date 2016-07-18
 * 历史数据
 */
public class HistoryData implements Serializable{

    public HistoryData(String o, long t) {
        this.o = o;
        this.t = t;
    }

    public HistoryData(String o) {
        this.o = o;
    }

    /**
     * o : 105767|4038|-538|3137
     * t : 1448928000
     */



    private String o;
    private long t;

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }
}
