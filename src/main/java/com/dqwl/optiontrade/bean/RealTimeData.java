package com.dqwl.optiontrade.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author xjunda
 * @date 2016-07-25
 * 实时数据
 */
public class RealTimeData implements Parcelable {
    public static final Creator<RealTimeData> CREATOR = new Creator<RealTimeData>() {
        @Override
        public RealTimeData createFromParcel(Parcel in) {
            return new RealTimeData(in);
        }

        @Override
        public RealTimeData[] newArray(int size) {
            return new RealTimeData[size];
        }
    };
    private String symbol;
    private float productPrice;
    private int flag;//3为平，1跌红色，2为涨绿色


    public RealTimeData(String symbol) {
        this.symbol = symbol;
    }

    protected RealTimeData(Parcel in) {
        symbol = in.readString();
        productPrice = in.readFloat();
        flag = in.readInt();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(float productPrice) {
        if (productPrice > this.productPrice) {
            flag = 2;
        } else if (productPrice < this.productPrice) {
            flag = 1;
        } else {
            flag = 3;
        }
        this.productPrice = productPrice;
    }

    public int isFlag() {
        return flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeFloat(productPrice);
        dest.writeInt(flag);
    }
}
