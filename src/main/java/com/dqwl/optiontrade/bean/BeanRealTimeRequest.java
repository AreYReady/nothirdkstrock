package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-07-26
 */
public class BeanRealTimeRequest {

    /**
     * msg_type : 1010
     * symbol : AUDUSDbo
     */


    private int msg_type;
    private String symbol;
    public BeanRealTimeRequest(int msg_type, String symbol) {
        this.msg_type = msg_type;
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
}
