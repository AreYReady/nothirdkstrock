package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-08-05
 * @link MyAccountActivity # onGetUserInfo   sticky = true
 * @link MinaTimeChartActivity # onGetUserInfo  sticky = true
 */
public class BeanUserInfo {

    /**
     * balance : 9999317.60
     * equity : 9999317.60
     * login : 1000
     * margin : 0.0
     * margin_free : 9999317.60
     * msg_type : 1300
     * name : 1000
     * volume : 0.0
     */

    private double balance;
    private double equity;
    private int login;
    private double margin;
    private double margin_free;
    private int msg_type;
    private String name;
    private double volume;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getEquity() {
        return equity;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getMargin_free() {
        return margin_free;
    }

    public void setMargin_free(double margin_free) {
        this.margin_free = margin_free;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
