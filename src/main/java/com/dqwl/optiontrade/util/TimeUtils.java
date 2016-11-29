package com.dqwl.optiontrade.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;

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
     * @author huangsc
     * Created at 2016-11-16 10:46
     * 10位 unix时间戳
     */
    public static String getCurrentTimeNoS() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static Long getCurrentTimeHHMMNoS() throws ParseException {
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        Date currentTime = new Date();
//        String dateString = formatter.format(currentTime);
//        return dateToLong(currentTime);
        Time time = new Time();
        time.setToNow();
        return stringToLong(time.hour + ":" + time.minute, "HH:mm");
    }

    //算法是当前时间减去8:00得出来的毫毛值
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
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
