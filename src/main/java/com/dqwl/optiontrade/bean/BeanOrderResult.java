package com.dqwl.optiontrade.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author xjunda
 * @date 2016-08-01
 * 下单返回结果
 */
public class BeanOrderResult implements Parcelable, Serializable {

    /**
     * close_price : 0.84335
     * close_time : 2016-08-01 10:17:19
     * commision_level : 80
     * direction : 0
     * money : 5
     * msg_type : 300
     * open_price : 0.84336
     * open_time : 2016-08-01 10:16:19
     * result : 2
     * status : 1
     * symbol : EURGBPbo
     * ticket : 4515301
     * time_span : 60
     */

    private double close_price;
    private String close_time;
    private int commision_level;
    private int direction;
    private String desc;
    private double money;
    private int msg_type;
    private double open_price;
    private String open_time;
    private int result = -1;//-1为请求订单
    private int status;
    private String symbol;
    private int ticket;
    private int time_span;
    private int left_time;
    /**
     * 是否显示详情
     */
    private boolean showDetail;
    /**
     * 是否实时数据
     */
    private boolean isRealTime = false;

    protected BeanOrderResult(Parcel in) {
        close_price = in.readDouble();
        close_time = in.readString();
        commision_level = in.readInt();
        direction = in.readInt();
        money = in.readDouble();
        msg_type = in.readInt();
        open_price = in.readDouble();
        open_time = in.readString();
        result = in.readInt();
        status = in.readInt();
        symbol = in.readString();
        ticket = in.readInt();
        time_span = in.readInt();
        left_time = in.readInt();
        showDetail = in.readByte() != 0;
        isRealTime = in.readByte() != 0;
    }

    public static final Creator<BeanOrderResult> CREATOR = new Creator<BeanOrderResult>() {
        @Override
        public BeanOrderResult createFromParcel(Parcel in) {
            return new BeanOrderResult(in);
        }

        @Override
        public BeanOrderResult[] newArray(int size) {
            return new BeanOrderResult[size];
        }
    };

    public double getClose_price() {
        return close_price;
    }

    public void setClose_price(double close_price) {
        this.close_price = close_price;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public int getCommision_level() {
        return commision_level;
    }

    public void setCommision_level(int commision_level) {
        this.commision_level = commision_level;
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

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public double getOpen_price() {
        return open_price;
    }

    public void setOpen_price(double open_price) {
        this.open_price = open_price;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public int getTime_span() {
        return time_span;
    }

    public void setTime_span(int time_span) {
        this.time_span = time_span;
    }

    public boolean isShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public boolean isRealTime() {
        return isRealTime;
    }

    public void setRealTime(boolean realTime) {
        isRealTime = realTime;
    }

    public int getLeft_time() {
        return left_time;
    }

    public void setLeft_time(int left_time) {
        this.left_time = left_time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(close_price);
        dest.writeString(close_time);
        dest.writeInt(commision_level);
        dest.writeInt(direction);
        dest.writeDouble(money);
        dest.writeInt(msg_type);
        dest.writeDouble(open_price);
        dest.writeString(open_time);
        dest.writeInt(result);
        dest.writeInt(status);
        dest.writeString(symbol);
        dest.writeInt(ticket);
        dest.writeInt(time_span);
        dest.writeInt(left_time);
        dest.writeByte((byte) (showDetail ? 1 : 0));
        dest.writeByte((byte) (isRealTime ? 1 : 0));
    }
}
