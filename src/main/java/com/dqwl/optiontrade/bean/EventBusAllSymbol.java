package com.dqwl.optiontrade.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author xjunda
 * @date 2016-08-11
 */
public class EventBusAllSymbol implements Parcelable{

    private int count;
    private int msg_type;
    private int result_code;
    /**
     * digits : 5
     * symbol : AUDCAD
     */

    private ArrayList<ItemSymbol> items;

    protected EventBusAllSymbol(Parcel in) {
        count = in.readInt();
        msg_type = in.readInt();
        result_code = in.readInt();
    }

    public static final Creator<EventBusAllSymbol> CREATOR = new Creator<EventBusAllSymbol>() {
        @Override
        public EventBusAllSymbol createFromParcel(Parcel in) {
            return new EventBusAllSymbol(in);
        }

        @Override
        public EventBusAllSymbol[] newArray(int size) {
            return new EventBusAllSymbol[size];
        }
    };

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

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public ArrayList<ItemSymbol> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemSymbol> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeInt(msg_type);
        dest.writeInt(result_code);
    }

    public static class ItemSymbol implements Parcelable{
        private int digits;
        private String symbol;

        protected ItemSymbol(Parcel in) {
            digits = in.readInt();
            symbol = in.readString();
        }

        public static final Creator<ItemSymbol> CREATOR = new Creator<ItemSymbol>() {
            @Override
            public ItemSymbol createFromParcel(Parcel in) {
                return new ItemSymbol(in);
            }

            @Override
            public ItemSymbol[] newArray(int size) {
                return new ItemSymbol[size];
            }
        };

        public int getDigits() {
            return digits;
        }

        public void setDigits(int digits) {
            this.digits = digits;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(digits);
            dest.writeString(symbol);
        }
    }
}
