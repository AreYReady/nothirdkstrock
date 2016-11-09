package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.util.ChartUtils;
import com.dqwl.optiontrade.util.DensityUtil;

/**
 * @author xjunda
 * @date 2016/7/17
 * 点击价格线
 */
public class ClickPriceLine extends FrameLayout {
    /**
     * 价格是显示在左边还是右边，false为右边，ture为左边
     */
    private boolean left;
    private TextView clickPriceTextview;
    private Paint paint;

    public ClickPriceLine(Context context) {
        this(context, null);
    }

    public ClickPriceLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        View view = LayoutInflater.from(context).inflate(R.layout.view_click_price_text, this);
        clickPriceTextview = (TextView) view.findViewById(R.id.tv_price);
        paint = new Paint();
        setVisibility(GONE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 0.5f));
        Path path = new Path();
        float startX = 0;
        int tvMargin = DensityUtil.dip2px(getContext(), 5);
        if(left){
            startX = clickPriceTextview.getRight() + DensityUtil.dip2px(getContext(), 5);
            tvMargin = 0;
        }
        path.moveTo(startX, 0);
        path.lineTo(startX, getBottom() - DensityUtil.dip2px(getContext(), ChartUtils.YOFFSET));
        canvas.drawPath(path, paint);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) clickPriceTextview.getLayoutParams();
        layoutParams.setMargins(tvMargin, 0, 0, 0);
        clickPriceTextview.setLayoutParams(layoutParams);
    }

    public TextView getClickPriceTextview() {
        return clickPriceTextview;
    }

    /**
     * 设置价格显示在左边还是右边
     * @param left
     */
    public void setTextViewLeft(boolean left) {
        if(this.left = left){
            return;
        }
        this.left = left;
        invalidate();
    }
}
