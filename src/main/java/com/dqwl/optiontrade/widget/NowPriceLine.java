package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.util.DensityUtil;

/**
 * @author xjunda
 * @date 2016/7/17
 * 当前价格虚线
 */
public class NowPriceLine extends FrameLayout {
    private TextView nowPriceTextview;
    private Paint paint;
    private PathEffect effects;
    private Runnable mRunnable;//textview延迟加载

    public NowPriceLine(Context context) {
        this(context, null);
    }

    public NowPriceLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        View view = LayoutInflater.from(context).inflate(R.layout.view_now_price_text, this);
        nowPriceTextview = (TextView) view.findViewById(R.id.tv_price);
        paint = new Paint();
        float offset = DensityUtil.dip2px(getContext(), 2);
        effects = new DashPathEffect(new float[]{offset, offset, offset, offset}, offset);
        paint.setPathEffect(effects);
        nowPriceTextview.setVisibility(INVISIBLE);
        setVisibility(INVISIBLE);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                nowPriceTextview.setVisibility(VISIBLE);
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 1));
        Path path = new Path();
        path.moveTo(0, nowPriceTextview.getBottom() / 2);
        path.lineTo(getRight() - nowPriceTextview.getWidth(), nowPriceTextview.getBottom() / 2);
        canvas.drawPath(path, paint);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nowPriceTextview.getLayoutParams();
        layoutParams.setMargins(getRight() - nowPriceTextview.getWidth(), 0, 0, 0);
        nowPriceTextview.setLayoutParams(layoutParams);
        postDelayed(mRunnable,1);
    }

    public TextView getNowPriceTextview() {
        return nowPriceTextview;
    }
}
