package com.dqwl.optiontrade.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.view.View;

import com.dqwl.optiontrade.bean.HistoryDataList;

import java.math.BigDecimal;

/**
 * @author xjunda
 * @date 2016/7/16
 */
public class ChartUtils {
    private final static int TEXT_SIZE = 10;//画布文字大小sp
    public static final int YOFFSET = 30;//x轴时间及向下距离
    public static final int XOFFSET = 58;//Y轴价格及向右距离
    /**
     * 两个数据之间的间隔时间60秒
     */
    public static final int INTERVAL_TIME = 60;
    /**
     * 一屏的数据个数60个，包括没有显示的点
     */
    public static final int COUNT = 60;

    /**
     * 获取当前价格坐标
     *
     * @param nowPrice
     * @return
     */
    public static float getNowPrice(View view, double nowPrice, double maxPrice, double minPrice) {
        //        Log.i("123", "resultttttttttttt: " + result);
        // TODO: 2016-07-26 一直刷屏的问题
        return (float) ((view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET))
                * (1 - (nowPrice - minPrice) / (maxPrice - minPrice)));
    }

    /**
     * 计算最大价格，最小价格
     *
     * @param historyDataList
     * @return [0]为最小值，[1]为最大值
     */
    public static double[] calcMaxMinPrice(HistoryDataList historyDataList, int digits) {
        double[] result = new double[2];
        double minPrice = 0;
        double maxPrice = 0;
        BigDecimal minP, maxP;
        int size = historyDataList.getItems().size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String price[] = historyDataList.getItems().get(i).getO().split("\\|");
                if (i == 0) {
//                    minPrice = MoneyUtil.addPrice(price[0], price[2], digits);
//                    maxPrice = MoneyUtil.addPrice(price[0], price[1], digits);
                    minPrice = Double.valueOf(price[0]) + Double.valueOf(price[2]);
                    maxPrice = Double.valueOf(price[0]) + Double.valueOf(price[1]);
                } else {
//                    double tempi = MoneyUtil.addPrice(price[0], price[2], digits);
                    double tempi = Double.valueOf(price[0]) + Double.valueOf(price[2]);
                    double tempa = Double.valueOf(price[0]) + Double.valueOf(price[1]);
//                    double tempa = MoneyUtil.addPrice(price[0], price[1], digits);
                    if (tempa > maxPrice) {
                        maxPrice = tempa;
                    }
                    if (tempi < minPrice) {
                        minPrice = tempi;
                    }
                }
            }
//            double offset = MoneyUtil.mulPrice(MoneyUtil.subPrice(maxPrice, minPrice), 0.1);
//            maxPrice = MoneyUtil.addPrice(maxPrice, offset);//最大值上面部分的空间
//            minPrice = MoneyUtil.subPrice(maxPrice, offset);//最小值下面部分的空间
            maxPrice = maxPrice + (maxPrice - minPrice) * 0.1;
            minPrice = minPrice - (maxPrice - minPrice) * 0.1;
            minP = new BigDecimal(minPrice).movePointLeft(digits);
            maxP = new BigDecimal(maxPrice).movePointLeft(digits);
            result[0] = minP.doubleValue();
            result[1] = maxP.doubleValue();
//            result[0] = minPrice;
//            result[1] = maxPrice;
            historyDataList.setPrice(result);
        }
        return result;
    }

    //画竖线
    private static void paintXBackgroud(View view, Canvas canvas, Paint paint, int xPostion) {
        Path path = new Path();
        path.moveTo(xPostion, 0);
        path.lineTo(xPostion, view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET - 10));
        canvas.drawPath(path, paint);
    }

    //画横线
    private static void paintYBackgroud(View view, Canvas canvas, Paint paint, int yPostion) {
        Path path = new Path();
        path.moveTo(0, yPostion);
        path.lineTo(view.getRight() - DensityUtil.dip2px(view.getContext(), XOFFSET -20), yPostion);
        canvas.drawPath(path, paint);
    }

    //画右边价格
    private static void paintYPrice(View view, Canvas canvas, Paint paintText, float viewXWidth, int yPostion, double price) {
        paintText.setTextSize(DensityUtil.sp2px(view.getContext(), TEXT_SIZE));
        canvas.drawText(MoneyUtil.moneyFormat(price),
                viewXWidth, yPostion, paintText);
    }

    //画下面时间
    private static void paintXTime(View view, Canvas canvas, Paint paintText, float xPosition, long time, boolean day) {
        paintText.setTextSize(DensityUtil.sp2px(view.getContext(), TEXT_SIZE));
        String timeStr;
        if(day){
            timeStr = TimeUtils.getXTimeDay(time);
        }else {
            timeStr = TimeUtils.getXTime(time);
        }
        canvas.drawText(timeStr, xPosition - DensityUtil.dip2px(view.getContext(), 15),
                view.getBottom() - DensityUtil.dip2px(view.getContext(), 10), paintText);
    }


    /**
     * 画背景线
     *
     * @param view
     * @param canvas
     */
    public static void drawBackgoundLine(View view, Canvas canvas, float xWidth) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        float offset = DensityUtil.dip2px(view.getContext(), 0.5f);
        PathEffect effects = new DashPathEffect(new float[]{offset, offset, offset, offset}, offset);
        paint.setPathEffect(effects);
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        int viewHeight = view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET) - view.getTop();
        int y = viewHeight;
        for (int i = 9; i > 0; i--) {
            y -= viewHeight / 10;
            paintYBackgroud(view, canvas, paint, y);
        }
        int x = 0;
//        int viewWidth = (view.getRight() - view.getLeft())/6*5;
        for (int i = 0; i < 5; i++) {
            x += xWidth / 5;
            paintXBackgroud(view, canvas, paint, x);
        }
        paint.setPathEffect(null);
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, 0, view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET - 10), paint);//左边y坐标
        canvas.drawLine(0, view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET), view.getRight(), view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET), paint);//底部x坐标
    }

    /**
     * 画价格和时间
     *  @param view
     * @param canvas
     * @param price
     * @param historyDataList
     * @param viewXWidth
     */
    public static void drawPriceTime(View view, Canvas canvas, double[] price, HistoryDataList historyDataList,
                                     int index, float viewXWidth) {
        boolean day;
        day = historyDataList.getPeriod() * historyDataList.getCount() >= COUNT * INTERVAL_TIME;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        float offset = DensityUtil.dip2px(view.getContext(), 0.5f);
        PathEffect effects = new DashPathEffect(new float[]{offset, offset, offset, offset}, offset);
        paint.setPathEffect(effects);
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        int viewHeight = view.getBottom() - DensityUtil.dip2px(view.getContext(), YOFFSET) - view.getTop();
        int y = viewHeight;
        for (int i = 9; i > 0; i--) {
            y -= viewHeight / 10;
            if (i > 0 && i % 2 == 0) {
                paintYPrice(view, canvas, paintText,viewXWidth, y, (price[0] + (price[1] - price[0]) / 10 * (10 - i)));
            }
        }
        float x = 0;
//        int viewWidth = (view.getRight() - view.getLeft())/6*5;
        int size = historyDataList.getCount();
        long time = 0;
        if(index!=0){
            if(index<size){
                time = historyDataList.getItems().get(0).getT() + historyDataList.getItems().get(index).getT();
            }
        }else {
            time = historyDataList.getItems().get(0).getT();
        }
//        long timeInterval = historyDataList.getItems().get(size-1).getT()/5;
        long timeInterval = INTERVAL_TIME*COUNT/5;
        for (int i = 0; i < 5;i++) {
            time += timeInterval;
            x += viewXWidth/5;
            paintXTime(view, canvas, paintText, x, time, day);
        }
    }
}
