package com.dqwl.optiontrade.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dqwl.optiontrade.bean.BeanHistoryRequest;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.HistoryDataList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author xjunda
 * @date 2016-08-09
 */
public class CacheUtil {

    private static final String USER_INFO = "USER_INFO";
    private static final String USER_NAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    public static final String FAVOR_SYMBOL = "FAVOR_SYMBOL";
    public static final String KLINE_CACHE = "KLINE_CACHE";


    /**
     * 保存用户名和密码到sp
     *
     * @param context
     * @param userName
     * @param password
     */
    public static void saveuserInfo(Context context, String userName, String password) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(PASSWORD, password);
        editor.apply();
    }


    /**
     * 获取用户信息
     *
     * @param context
     * @return 0位用户名， 1位密码
     */
    public static String[] getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        String userName = sp.getString(USER_NAME, "");
        String password = sp.getString(PASSWORD, "");
        return new String[]{userName, password};
    }

    /**
     * 增加喜爱的产品到sp
     *
     * @param context
     * @param symbol
     * @param save    ture为保存，false为移除
     */
    public static void saveFavor(Context context, String symbol, String symbolDetail, boolean save) {
        SharedPreferences sp = context.getSharedPreferences(FAVOR_SYMBOL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (save) {
            editor.putString(symbol, symbolDetail);
        } else {
            editor.remove(symbol);
        }
        editor.apply();
    }

    /**
     * 获取所有喜爱的产品
     *
     * @param context
     * @return
     */
    public static ArrayList<BeanSymbolConfig.SymbolsBean> favorProducts(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FAVOR_SYMBOL, Context.MODE_PRIVATE);
        Set<String> symbols = sp.getAll().keySet();
        ArrayList<BeanSymbolConfig.SymbolsBean> datas = new ArrayList<>();
        for (String symbol : symbols) {
            BeanSymbolConfig.SymbolsBean realTimeData =
                    new Gson().fromJson(sp.getString(symbol, ""), BeanSymbolConfig.SymbolsBean.class);
            datas.add(realTimeData);
        }
        return datas;
    }

    /**
     * 获取k线图，折线图缓存
     * @return
     */
    public static HashMap<BeanHistoryRequest, HistoryDataList> getKLineCache(Context context){
        SharedPreferences sp = context.getSharedPreferences(KLINE_CACHE, Context.MODE_PRIVATE);
        String cache = sp.getString(KLINE_CACHE, "");
        if(!TextUtils.isEmpty(cache)){
            return new Gson().fromJson(cache,
                    new TypeToken<HashMap<BeanHistoryRequest, HistoryDataList>>(){}.getType());
        }
        return new HashMap<>();
    }

    /**
     * 写k线图，折线图缓存
     * @param context
     * @param kLineCache
     */
    public static void writeKLineCache(Context context
            , HashMap<BeanHistoryRequest, HistoryDataList> kLineCache){
        SharedPreferences sp = context.getSharedPreferences(KLINE_CACHE, Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .create();
        String cache = gson.toJson(kLineCache);
        sp.edit().putString(KLINE_CACHE, cache).apply();
    }
}
