package com.dqwl.optiontrade.bean;

/**
 * Created by admin on 2016-11-25.
 */

public class BeanChangePercent {
    /**
     * 0为改变,1为取消掉选项
     */
    private int type;
    private String symbol;
    private int percent;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 记录和主数据相对index;
     */
    private int index;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private int tz_delta;
    private String desc;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getTz_delta() {
        return tz_delta;
    }

    public void setTz_delta(int tz_delta) {
        this.tz_delta = tz_delta;
    }
}
