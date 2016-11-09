package com.dqwl.optiontrade.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xjunda
 * @date 2016-08-03
 * @link TradeIndexActivity # onGetActiviteOrder    sticky = true
 */
public class BeanOrderRecord implements Parcelable{
    private int ordercount;
    private int count;
    private int msg_type;
    /**
     * close_price : 1.33219
     * close_time : 2016-08-03 11:03:12
     * commision_level : 80
     * direction : 0
     * money : 5
     * open_price : 1.33221
     * open_time : 2016-08-03 11:02:12
     * result : 2
     * symbol : GBPUSDbo
     * ticket : 4518299
     * time_span : 60
     */

    /**
     * 已经完成的订单
     */
    private List<BeanOrderResult> items;

    /**
     * 未完成订单
     */
    private List<BeanOrderResult> orders = new ArrayList<>();

    protected BeanOrderRecord(Parcel in) {
        ordercount = in.readInt();
        count = in.readInt();
        msg_type = in.readInt();
    }

    public static final Creator<BeanOrderRecord> CREATOR = new Creator<BeanOrderRecord>() {
        @Override
        public BeanOrderRecord createFromParcel(Parcel in) {
            return new BeanOrderRecord(in);
        }

        @Override
        public BeanOrderRecord[] newArray(int size) {
            return new BeanOrderRecord[size];
        }
    };

    public int getOrdercount() {
        return ordercount;
    }

    public void setOrdercount(int ordercount) {
        this.ordercount = ordercount;
    }

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

    public List<BeanOrderResult> getItems() {
        return items;
    }

    public void setItems(List<BeanOrderResult> items) {
        this.items = items;
    }

    public List<BeanOrderResult> getOrders() {
        return orders;
    }

    public void setOrders(List<BeanOrderResult> orders) {
        this.orders = orders;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordercount);
        dest.writeInt(count);
        dest.writeInt(msg_type);
    }

    public static class ItemsBean {
        private double close_price;
        private String close_time;
        private int commision_level;
        private int direction;
        private int money;
        private double open_price;
        private String open_time;
        private int result;
        private String symbol;
        private int ticket;
        private int time_span;
        private boolean showDetail;

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

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
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
    }
}
