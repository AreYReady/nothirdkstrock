package com.dqwl.optiontrade.util;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.widget.TextView;

import com.dqwl.optiontrade.constant.NetworkType;

import java.lang.reflect.Field;

/**
 * @author xjunda
 * @date 2016-08-08
 */
public class SystemUtil {
    /**
     * 当前activity是否在最前面
     *
     * @param context
     * @param activityName
     * @return
     */
    public static boolean isTopActivity(Context context, String activityName) {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(activityName)) {
            isTop = true;
        }
        return isTop;
    }

    /**
     * 是否锁屏
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context){
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        return !flag;
    }

    /**
     * 判断是不是有效的网络链接 <br/>
     * 3G，wifi，GPRS（歌词，头像）
     *
     * @param context
     * @return
     */
    public static boolean isAvalidNetSetting(Context context) {
        return !(NetworkType.UNKNOWN.endsWith(getNetworkType(context)));
    }
    /**
 　　* 获取设置的最大长度
 　　*
 　　* @return
 　　*/
    public static int getMaxLength(TextView tv) {
        int length = 0;
        try {
            InputFilter[] inputFilters = tv.getFilters();
            for (InputFilter filter : inputFilters) {
                Class<?> c = filter.getClass();
                if (c.getName().equals("android.text.InputFilter$LengthFilter")) {
                    Field[] f = c.getDeclaredFields();
                    for (Field field : f) {
                        if (field.getName().equals("mMax")) {
                            field.setAccessible(true);
                            length = (Integer) field.get(filter);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();
            }
        return length;
        }
    /**
     * 获取网络类型
     *
     * @return 网络类型
     * @p)ram context
     */
    private static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            return NetworkType.UNKNOWN;
        }
        // if (!netInfo.isAvailable()) {
        // return NetworkType.UNKNOWN;
        // }
        if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetworkType.WIFI;
        }
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int netType = tm.getNetworkType();
        // 已知3G类型
        // NETWORK_TYPE_UMTS 3
        // NETWORK_TYPE_EVDO_0 5
        // NETWORK_TYPE_EVDO_A 6
        // NETWORK_TYPE_HSDPA 8
        // NETWORK_TYPE_HSUPA 9
        // NETWORK_TYPE_HSPA 10

        // NETWORK_TYPE_EVDO_B 12
        // NETWORK_TYPE_LTE 13
        // NETWORK_TYPE_EHRPD 14
        // NETWORK_TYPE_HSPAP 15
        // 已知2G类型
        // NETWORK_TYPE_GPRS 1
        // NETWORK_TYPE_EDGE 2
        // NETWORK_TYPE_CDMA 4
        // NETWORK_TYPE_1xRTT 7
        // NETWORK_TYPE_IDEN 11

        if (netType == TelephonyManager.NETWORK_TYPE_GPRS
                || netType == TelephonyManager.NETWORK_TYPE_EDGE
                || netType == TelephonyManager.NETWORK_TYPE_CDMA
                || netType == TelephonyManager.NETWORK_TYPE_1xRTT || netType == 11) {
            return NetworkType.NET_2G;
        }

        if (netType == TelephonyManager.NETWORK_TYPE_LTE) {
            return NetworkType.NET_4G;
        }

        return NetworkType.NET_3G;
    }
    public static String getTAG(Class className){
        return "hsc : "+ className.getSimpleName();
    }
    public static String getTAG(Object object){
        return "hsc : "+ object.getClass().getSimpleName();
    }
}
