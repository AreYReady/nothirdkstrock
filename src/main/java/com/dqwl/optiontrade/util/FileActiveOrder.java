package com.dqwl.optiontrade.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.dqwl.optiontrade.application.OptionApplication;
import com.dqwl.optiontrade.bean.BeanOrderResult;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by admin on 2016-11-14.
 */

public class FileActiveOrder {

    public static void saveActiveOrder(HashMap<Integer, BeanOrderResult> beanOrderResult){
        SharedPreferences mSharePreferences= OptionApplication.getSingleton().getSharedPreferences("activeOrder", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= mSharePreferences.edit();
        if(beanOrderResult.size()>0){
            Set<Integer> set = beanOrderResult.keySet();
            for (Integer index:set){
//                editor.putString(index,beanOrderResult.get(index).)
            }
        }

    }
}
