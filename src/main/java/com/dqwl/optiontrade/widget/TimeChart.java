package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.HistoryData;
import com.dqwl.optiontrade.bean.HistoryDataList;
import com.dqwl.optiontrade.util.ChartUtils;
import com.dqwl.optiontrade.util.DensityUtil;
import com.dqwl.optiontrade.util.MoneyUtil;
import com.dqwl.optiontrade.util.TimeUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xjunda
 * @date 2016-07-13
 * 分时图
 */
public class TimeChart extends FrameLayout {
    /**
     * 两个数据之间的间隔时间60秒
     */
    public static final int INTERVAL_TIME = 60;
    /**
     * 一屏的数据个数60个，包括没有显示的点
     */
    public static final int COUNT = 60;
    private int KWIDTH;//k线柱宽度
    private double[] price;//最高价[1]，最低价[0]
    private HistoryDataList historyDataList;//历史数据
    private Map<Integer, HistoryData> historyPosition = new HashMap();//历史数据x坐标对应价格
    private ClickPriceLine clickPriceLine;
    private NowPriceLine nowPriceLine;//当前时间线
    private boolean isKLine;//是否K线图，真为是，否为不是
    private Paint paintClear;
    private PorterDuffXfermode clearMode;
    private PorterDuffXfermode srcMode;

    public TimeChart(Context context) {
        this(context, null);
    }

    public TimeChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        KWIDTH = DensityUtil.dip2px(getContext(), 1.5f);
        clickPriceLine = new ClickPriceLine(context);
        nowPriceLine = new NowPriceLine(context);
        addView(nowPriceLine);
        addView(clickPriceLine);
        paintClear = new Paint();//清除画布
        clearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        srcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (historyDataList == null || historyDataList.getItems() == null) {
            paintClear.setXfermode(clearMode);
            canvas.drawPaint(paintClear);
            paintClear.setXfermode(srcMode);
            canvas.drawColor(Color.WHITE);
        }
//        ChartUtils.drawBackgoundLine(this, canvas, getWidth()/6*5);
        if (isKLine) {
            drawKMinute(canvas);
        } else {
            drawHistoryData(canvas);
        }
    }


    public void setData(HistoryDataList historyDataList, double[] price, boolean isKLine) {
        this.historyDataList = historyDataList;
        this.price = price;
        this.isKLine = isKLine;
        historyPosition.clear();
        postInvalidate();
    }

    /**
     * 画折线图历史数据
     *
     * @param canvas
     */
    private void drawHistoryData(Canvas canvas) {
        if (historyDataList == null || historyDataList.getItems() == null) {
            return;
        }
        float viewXWidth = (getRight() - getLeft()) / 6 * 5;//x轴长度
        Path path = new Path();//里面实心部分
        Path path2 = new Path();//外面
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 2));
        paint.setStyle(Paint.Style.STROKE);
        int size = historyDataList.getItems().size() - 1;
        long total = 1;
        if (size > 1)
            total = historyDataList.getItems().get(size - 1).getT() / INTERVAL_TIME;
        float xInterval = viewXWidth / (size - 1);
        int i = 0;
        float x = 0;
        int firstI = 0;//补充的点数数据数
        if (total > size) {
//            xInterval = viewXWidth/(total - 1);
            i = (int) total - size + 1;
            long firstTime = historyDataList.getItems().get(size).getT() - INTERVAL_TIME * COUNT;
            for (int j = size; j > 0; j--) {
                if (historyDataList.getItems().get(j).getT() <= firstTime) {
                    i = j;
                    firstI = COUNT - (int) ((historyDataList.getItems().get(size).getT()
                            - historyDataList.getItems().get(j).getT()) / INTERVAL_TIME);
//                    if(firstI<0){
//                        firstI = 0;
//                    }
                    break;
                }
            }
        }
        int index = i;//起始绘制数据的位置
        float xLenth = xInterval * size + xInterval;//x轴总长
        if (size > 0) {
            for (; i < size; i++) {
                double close;
                String[] data = historyDataList.getItems().get(i).getO().split("\\|");
                close = MoneyUtil.addPrice(data[0], data[3], historyDataList.getDigits());
                if (i == index) {
                    path.moveTo(x, getBottom() - DensityUtil.dip2px(getContext(), ChartUtils.YOFFSET));
                    path2.moveTo(x, ChartUtils.getNowPrice(this, close, price[1], price[0]));
                    x += xInterval * firstI;
//                    if(i==0){
//                        x += xInterval;
//                    }
                } else if (i < size) {
                    if ((historyDataList.getItems().get(i).getT()
                            - historyDataList.getItems().get(i - 1).getT())
                            > INTERVAL_TIME) {
                        x += xInterval * (historyDataList.getItems().get(i).getT()
                                - historyDataList.getItems().get(i - 1).getT()) / INTERVAL_TIME;
                    } else {
                        x += xInterval;
                    }
                }
                if (total > size) {
                    xLenth = xInterval * total + xInterval;
                }
//                if(x<=xLenth){//限制宽度
                path.lineTo(x, ChartUtils.getNowPrice(this, close, price[1], price[0]));
                path2.lineTo(x, ChartUtils.getNowPrice(this, close, price[1], price[0]));
                historyPosition.put((int) x, historyDataList.getItems().get(i));
//                }
            }

            if (nowPriceLine.isShown() && nowPriceLine.getTranslationY() > 0) {//绘制实时历史数据
                x += xInterval;
                float y = nowPriceLine.getTranslationY()
                        + nowPriceLine.getNowPriceTextview().getHeight() / 2;
                path.lineTo(x, y);
                path2.lineTo(x, y);
                historyPosition.put((int) x, historyDataList.getItems().get(historyDataList.getCount() - 1));
            }
            path.lineTo(x, getBottom() - DensityUtil.dip2px(getContext(), ChartUtils.YOFFSET));
            ChartUtils.drawBackgoundLine(this, canvas, x);
            ChartUtils.drawPriceTime(this, canvas, price, historyDataList, index, x);

            canvas.drawPath(path2, paint);
            paint.setAlpha(50);
            paint.setStyle(Paint.Style.FILL);
            Shader mShader = new LinearGradient(0, 0,
                    0, getBottom() - DensityUtil.dip2px(getContext(), ChartUtils.YOFFSET),
                    Color.GREEN, 0xffACDC7F
                    , Shader.TileMode.CLAMP);
            paint.setShader(mShader);
            canvas.drawPath(path, paint);
        }
    }


    /**
     * 画k线历史数据
     *
     * @param canvas
     */
    private void drawKMinute(Canvas canvas) {
        if (historyDataList == null || historyDataList.getItems() == null) {
            return;
        }
        float viewXWidth = (getRight() - getLeft()) / 6 * 5;//x轴长度
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        double close;
        double high;
        double low;
        double open;
        int size = historyDataList.getItems().size() - 1;
        long total = 1;
        if (size > 1)
            total = historyDataList.getItems().get(size - 1).getT() / INTERVAL_TIME;
        float xInterval = viewXWidth / (size - 1);
        int i = 0;
        int firstI = 0;
        if (total > size) {
//            xInterval = viewXWidth/(total - 1);
            i = (int) total - size + 1;
            long firstTime = historyDataList.getItems().get(size).getT() - INTERVAL_TIME * COUNT;
            for (int j = size; j > 0; j--) {
                if (historyDataList.getItems().get(j).getT() <= firstTime) {
                    i = j;
                    firstI = COUNT - (int) ((historyDataList.getItems().get(size).getT()
                            - historyDataList.getItems().get(j).getT()) / INTERVAL_TIME);
//                    if(firstI<0){
//                        firstI = 0;
//                    }
                    break;
                }
            }
            KWIDTH = DensityUtil.dip2px(getContext(), 1.5f);
        } else {
            KWIDTH = DensityUtil.dip2px(getContext(), 1.5f);
        }
        int index = i;
        float x = DensityUtil.dip2px(getContext(), KWIDTH);
        int digits = historyDataList.getDigits();
        if (size > 0) {
            for (; i < size; i++) {
                paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 1));

                String[] data = historyDataList.getItems().get(i).getO().split("\\|");
                close = MoneyUtil.addPrice(data[0], data[3], digits);
                high = MoneyUtil.addPrice(data[0], data[1], digits);
                low = MoneyUtil.addPrice(data[0], data[2], digits);
                open = new BigDecimal(data[0]).movePointLeft(digits).doubleValue();

                if (close > open) {
                    paint.setColor(Color.RED);
                    canvas.drawRect(x - KWIDTH, ChartUtils.getNowPrice(this, close, price[1], price[0]), x + KWIDTH, ChartUtils.getNowPrice(this, open, price[1], price[0]), paint);
                } else if (close < open) {
                    paint.setColor(Color.GREEN);
                    canvas.drawRect(x - KWIDTH, ChartUtils.getNowPrice(this, open, price[1], price[0]), x + KWIDTH, ChartUtils.getNowPrice(this, close, price[1], price[0]), paint);
                } else {
                    paint.setColor(Color.GRAY);
                    canvas.drawLine(x - KWIDTH, ChartUtils.getNowPrice(this, open, price[1], price[0]), x + KWIDTH, ChartUtils.getNowPrice(this, close, price[1], price[0]), paint);
                }
                canvas.drawLine(x, ChartUtils.getNowPrice(this, high, price[1], price[0]), x, ChartUtils.getNowPrice(this, low, price[1], price[0]), paint);
                historyPosition.put((int) x, historyDataList.getItems().get(i));
                if (i == index) {
                    x += xInterval * firstI;
                    if (i == 0) {
                        x += xInterval;
                    }
                } else if (i > 0) {
                    if ((historyDataList.getItems().get(i).getT()
                            - historyDataList.getItems().get(i - 1).getT())
                            > INTERVAL_TIME) {
                        x += xInterval * (historyDataList.getItems().get(i).getT()
                                - historyDataList.getItems().get(i - 1).getT()) / INTERVAL_TIME;
                    } else {
                        x += xInterval;
                    }
                }
            }
            String[] lastK = historyDataList.getItems()
                    .get(historyDataList.getCount() - 1).getO().split("\\|");
            close = MoneyUtil.addPrice(lastK[0], lastK[3], digits);
            high = MoneyUtil.addPrice(lastK[0], lastK[1], digits);
            low = MoneyUtil.addPrice(lastK[0], lastK[2], digits);
            open = new BigDecimal(lastK[0]).movePointLeft(digits).doubleValue();

            if (close > open) {
                paint.setColor(Color.GREEN);
                canvas.drawRect(x - KWIDTH, ChartUtils.getNowPrice
                        (this, close, price[1], price[0]), x + KWIDTH, ChartUtils.getNowPrice(this, open, price[1], price[0]), paint);
            } else if (close < open) {
                paint.setColor(Color.RED);
                canvas.drawRect(x - KWIDTH, ChartUtils.getNowPrice
                        (this, open, price[1], price[0]), x + KWIDTH, ChartUtils.getNowPrice(this, close, price[1], price[0]), paint);
            } else {
                paint.setColor(Color.GRAY);
                canvas.drawLine(x - KWIDTH, ChartUtils.getNowPrice
                        (this, open, price[1], price[0]), x + KWIDTH, ChartUtils.getNowPrice(this, close, price[1], price[0]), paint);
            }
            canvas.drawLine(x, ChartUtils.getNowPrice
                    (this, high, price[1], price[0]), x, ChartUtils.getNowPrice(this, low, price[1], price[0]), paint);
            historyPosition.put((int) x, historyDataList.getItems().get(historyDataList.getCount() - 1));
            ChartUtils.drawPriceTime(this, canvas, price, historyDataList, index, x);
            ChartUtils.drawBackgoundLine(this, canvas, x);
        }
    }


    /**
     * @author xjunda
     * Created at 2016-07-22 15:45
     * flag false为绿色，true为红色
     */
    public void showNowPrice(final double nowPrice, final double[] price, int flag) {
        final View view = getChildAt(0);
        if (view != null && nowPrice > 0) {
            final TextView tvNowPriceView = ((NowPriceLine) view).getNowPriceTextview();
            tvNowPriceView.setText(MoneyUtil.moneyFormat(nowPrice));
            if (flag == 1) {
                tvNowPriceView.setBackgroundResource(R.drawable.bg_green_now_price);
            } else if (flag == 2) {
                tvNowPriceView.setBackgroundResource(R.drawable.bg_red_now_price);
            } else {
                tvNowPriceView.setBackgroundResource(R.drawable.bg_gray_now_price);
            }
            if (getBottom() > 0) {
                view.setTranslationY(ChartUtils.getNowPrice(this, nowPrice, price[1], price[0]) - tvNowPriceView.getHeight() / 2);
                if (view.getTranslationY() > 0) {
                    view.setVisibility(VISIBLE);
                } else {
                    view.setVisibility(INVISIBLE);
                }
            } else {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setTranslationY(ChartUtils.getNowPrice(TimeChart.this, nowPrice, price[1], price[0]) - tvNowPriceView.getHeight() / 2);
                        if (view.getTranslationY() > 0)
                            view.setVisibility(VISIBLE);
                    }
                }, 50);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (historyDataList == null || historyDataList.getItems() == null) {
            return true;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            clickPriceLine.setVisibility(GONE);
        } else {
            float x = event.getX();
            /**
             * 触摸点减去手指的像素
             */
            float y = event.getY() - DensityUtil.dip2px(getContext(), 80);
            int xData = getTouchData((int) x);//有值得x坐标
            HistoryData historyData = historyPosition.get(xData);
//            clickPriceLine.setTranslationX(x);
            float viewXWidth = getRight() - getLeft() - DensityUtil.dip2px(getContext(), ChartUtils.XOFFSET);//x轴长度
            int size = historyDataList.getItems().size();
            long total = historyDataList.getItems().get(size - 1).getT() / historyDataList.getItems().get(1).getT();
            float xInterval = viewXWidth / (size - 1);
            if (total > size) {
                xInterval = viewXWidth / (total - 1);
                size = (int) total;
            }
//            if (x > xInterval*size +xInterval) {//数据之外
////                clickPriceLine.setVisibility(GONE);
//            } else {
            if (historyData != null) {
                boolean day;//是否绘制天数或者只要时间
                day = historyDataList.getPeriod() * historyDataList.getCount() >= 3600;
                clickPriceLine.setVisibility(VISIBLE);
                int digits = historyDataList.getDigits();
                clickPriceLine.getClickPriceTextview().setVisibility(VISIBLE);
                String[] data = historyData.getO().split("\\|");
                clickPriceLine.getClickPriceTextview().setBackgroundColor(getResources().getColor(R.color.trans_black));
                long startTime = historyDataList.getItems().get(0).getT();
                long time = startTime + historyData.getT();
                String timeStr;
                if (day) {
                    timeStr = TimeUtils.getXTimeDay(time);
                } else {
                    timeStr = TimeUtils.getXTime(time);
                }
//                    if (isKLine) {
                double openPrice = new BigDecimal(data[0]).movePointLeft(digits).doubleValue();
                double closePrice = MoneyUtil.addPrice(data[0], data[3], digits);
                double minPrice = MoneyUtil.addPrice(data[0], data[2], digits);
                double maxPrice = MoneyUtil.addPrice(data[0], data[1], digits);
                clickPriceLine.getClickPriceTextview().setText("时间：" + timeStr + "\n开盘价：" + openPrice + "\n"
                        + "最高价：" + maxPrice + "\n" + "最低价：" + minPrice + "\n"
                        + "收盘价：" + closePrice);
                clickPriceLine.getClickPriceTextview().setTranslationY(y - DensityUtil.dip2px(getContext(), 30));
//                    } else {
//                        double xprice = 0;
//                        xprice = MoneyUtil.addPrice(data[0], data[3], digits);
//                        clickPriceLine.getClickPriceTextviewRight().setText("时间：" +timeStr + "\n收盘价：" + xprice);
//                        clickPriceLine.getClickPriceTextviewRight().setTranslationY(y);
//                    }
                if (xData >= xInterval * size * 3 / 5) {
                    clickPriceLine.setTextViewLeft(true);
                    clickPriceLine.setTranslationX(xData -
                            clickPriceLine.getClickPriceTextview().getRight()
                            - DensityUtil.dip2px(getContext(), 5));
                } else {
                    clickPriceLine.setTextViewLeft(false);
                    clickPriceLine.setTranslationX(xData);
                }
            } else {
                clickPriceLine.getClickPriceTextview().setVisibility(GONE);
            }
//            }
        }
        return true;
    }

    /**
     * 获取点击事件的最靠近点的数据
     *
     * @param x
     */
    private int getTouchData(int x) {
        int a = x;
        int b = x;
        for (; a <= getRight(); a++) {
            if (historyPosition.get(a) != null) {
                break;
            }
        }
        for (; b >= 0; b--) {
            if (historyPosition.get(b) != null) {
                break;
            }
        }
        if ((a - x) >= (x - b)) {//返回距离小的x值
            return b;
        } else {
            return a;
        }
    }
}
