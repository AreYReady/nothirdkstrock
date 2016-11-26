package com.dqwl.optiontrade.util;

import com.dqwl.optiontrade.bean.BeanOrderResult;

import java.util.HashMap;

/**
 * Created by admin on 2016-11-14.
 */

public class NoCompleteActiveOrder {

    private volatile static NoCompleteActiveOrder mNoCompleteActiveOrder;
    private HashMap<Integer,BeanOrderResult> beanOrderResult;
    private NoCompleteActiveOrder() {
    }

    public static NoCompleteActiveOrder getSingleton() {
        if (mNoCompleteActiveOrder == null) {
            synchronized (NoCompleteActiveOrder.class) {
                if (mNoCompleteActiveOrder == null) {
                    mNoCompleteActiveOrder = new NoCompleteActiveOrder();
                }
            }
        }
        return mNoCompleteActiveOrder;
    }
    public void saveActiveOrder(HashMap<Integer,BeanOrderResult> beanOrderResult){
        this.beanOrderResult=beanOrderResult;
    }
    public HashMap<Integer,BeanOrderResult> getActiveOrder(){
        return beanOrderResult;
    }
}
