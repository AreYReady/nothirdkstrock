package com.dqwl.optiontrade.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author xjunda
 * @date 2016-07-16
 */
public class TimeUtils {
    /**
     * @author xjunda
     * Created at 2016-07-16 10:46
     * 获取x轴时间格式 10位 unix时间戳
     */
    public static String getXTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
        return sdf.format(new Date(time * 1000));
    }

    /**
     * 获取时间天、小时单为位
     *
     * @param time
     * @return
     */
    public static String getXTimeDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH");
        sdf.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
        return sdf.format(new Date(time * 1000));
    }

    /**
     * @author xjunda
     * Created at 2016-07-16 11:01
     * 获取当前时间格式
     */
    public static String getShowTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
        return sdf.format(new Date(time));
    }

    /**
     * @author xjunda
     * Created at 2016-07-16 11:01
     * 获取当前时间格式没有时区
     */
    public static String getShowTimeNoTimeZone(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }

    /**
     * 获取订单开始时间
     *
     * @param open_time
     * @return
     */
    public static long getOrderStartTime(String open_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
        try {
            return sdf.parse(open_time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取订单开始时间
     *
     * @param open_time
     * @return
     */
    public static long getOrderStartTimeNoTimeZone(String open_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(open_time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    private static String createGmtOffsetString(boolean includeGmt,
                                                boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
//        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    public static final int SERVICER_TIME = 1;

    /**
     * 获取服务器时间
     */
    public static void getServiceTime(final Context context, final String url, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL urlTime = new URL(url);//取得资源对象
                    URLConnection uc = urlTime.openConnection();//生成连接对象
                    uc.connect(); //发出连接
                    long ld = uc.getDate(); //取得网站日期时间
                    Date date = new Date(ld); //转换为标准时间对象
                    Message message = new Message();
                    message.what = SERVICER_TIME;
                    message.obj = date;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
