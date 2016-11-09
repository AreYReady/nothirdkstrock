package com.dqwl.optiontrade.bean;

import java.io.Serializable;

/**
 * @author xjunda
 * @date 2016-07-26
 */
public class BeanHistoryRequest implements Serializable{

    /**
     * msg_type : 1100
     * symbol : EURUSD
     * bar_count : 20
     * period : m5
     */

    private int msg_type = 1100;
    private String symbol;
    private int bar_count;
    /**
     * 暂时只用m1，后期再扩展
     */
    private String period = "m1";

    public BeanHistoryRequest(String symbol, int bar_count) {
        this.symbol = symbol;
        this.bar_count = bar_count;
    }

    public BeanHistoryRequest(String symbol) {
        this.symbol = symbol;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getBar_count() {
        return bar_count;
    }

    public void setBar_count(int bar_count) {
        this.bar_count = bar_count;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object obj) {
        // 首先需要判断obj是否为null， 如果为null，返回false
        if (obj == null) {
            return false;
        }
        // 判断测试的是否为同一个对象，
        // 如果是同一个对象，无庸置疑，它应该返回true
        if (this == obj) {
            return true;
        }
        // 判断它们的类型是否相等，
        // 如果不相等，则肯定返回false
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        // 将参数中传入的对象造型为Citizen类型
        BeanHistoryRequest c = (BeanHistoryRequest) obj;
        // 比较两个对象的所有属性是否一样，就可以得出这两个对象是否相等
        return (this.period).equals(c.period) && (this.symbol).equals(c.symbol);
    }

    @Override
    public int hashCode() {
        int result = getSymbol().hashCode();
        result = 29 * result + getPeriod().hashCode();
        return result;

    }
}
