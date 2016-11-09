package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-07-26
 * 下单实体类
 */
public class BeanOrder {

    /**
     * msg_type : 210
     * login : 2013
     * symbol : EURJPYbo
     * direction : 1
     * money : 100
     * time : 30
     * commision_level : 80
     */

    private int msg_type;
    private int login;
    private String symbol;
    private int direction;
    private double money;
    private int time;
    private int commision_level;

    public BeanOrder(int login, String symbol, int direction, double money, int time, int commision_level) {
        this.msg_type = 210;
        this.login = login;
        this.symbol = symbol;
        this.direction = direction;
        this.money = money;
        this.time = time;
        this.commision_level = commision_level;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCommision_level() {
        return commision_level;
    }

    public void setCommision_level(int commision_level) {
        this.commision_level = commision_level;
    }
}
