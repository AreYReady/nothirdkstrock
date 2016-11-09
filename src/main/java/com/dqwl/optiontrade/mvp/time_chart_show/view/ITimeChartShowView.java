package com.dqwl.optiontrade.mvp.time_chart_show.view;

import android.content.Context;

import com.dqwl.optiontrade.bean.BeanOrderResult;

/**
 * Created by jimdaxu on 16/8/22.
 */
public interface ITimeChartShowView {
    double[] getTempNowPrice(double[] nowPrice);
    void setBadgeNum(int num);
    void checkChartCache(int position, boolean flag);
    void setRealTimeTextBackGround(double nowPrice, double prePrice, double[] price);
    void addProgressBar(BeanOrderResult orderResult, final int progeress);
    Context getContext();
}
