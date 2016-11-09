package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.util.DensityUtil;

/**
 * 小进度条
 * @link TradeRecordActivity # onGetSmallProgressbars   sticky = true
 */
public class SmallProgressBar extends FrameLayout {
    private int widthPixels;
    private int heightPixels;
    private int centerX;
    private int centerY;
    /**
     * 外面空进度壳
     */
    private Bitmap bmpEmpty;
    private Bitmap bmpDegreeGreen;
    private Bitmap bmpDegreeRed;
    private Bitmap bmpDegreeGray;
    /**
     * 中间部分小圆
     */
    private Bitmap bmpCenterGreen;
    private Bitmap bmpCenterRed;
    private Bitmap bmpCenterGray;
    private PorterDuffXfermode mMode;
    private Paint paintDegree;//bitmap画笔
    private RectF mOval;
    private int paintColor;//画笔颜色1为绿亏，2为红赢，3为平灰
    private float mDegree = -1;//进度
    private float maxDegree = 60;//最大进度
    private Paint paintDegreeText;//进度文本
    private ProgressBar progressBar;
    private BeanOrderResult order;//序号ticket
    /**
     * @param context
     */
    public SmallProgressBar(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param maxDegree 最大刻度
     * @param order     进度条ticket
     */
    public SmallProgressBar(Context context, int maxDegree, BeanOrderResult order) {
        this(context, null);
        this.maxDegree = maxDegree;
        this.order = order;
        this.mDegree = maxDegree;
    }

    public SmallProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);

        bmpEmpty = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_empty_bg);
        bmpDegreeGreen = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_full_green_bg);
        bmpDegreeRed = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_full_red_bg);
        bmpDegreeGray = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_full_grey_bg);
        bmpCenterGreen = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_green_center_bg);
        bmpCenterRed = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_red_center_bg);
        bmpCenterGray = BitmapFactory.decodeResource(getResources(),
                R.mipmap.small_grey_center_bg);

        mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        paintDegree = new Paint();
//        paintDegree.setColor(Color.WHITE);
        paintDegree.setXfermode(mMode);
        mOval = new RectF();
        mOval.left = 0;
        mOval.top = 0;

        paintDegreeText = new Paint();
        paintDegreeText.setColor(Color.GREEN);
//        paintDegreeText.setTypeface(Typeface.DEFAULT_BOLD);
        paintDegreeText.setTextSize(DensityUtil.dip2px(getContext(), 14));

        View view = inflate(context, R.layout.progress_circle_center, this);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        setWillNotDraw(false);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        widthPixels = w;
        heightPixels = h;
        centerX = widthPixels / 2;
        centerY = heightPixels / 2;
    }

    /**
     *
     */


    @Override
    protected void onDraw(final Canvas canvas) {
        Bitmap bmpDegree;
        Bitmap bmpCenter;
        String result;
        paintDegreeText.setColor(Color.WHITE);
        switch (paintColor) {
            case 1:
                bmpDegree = bmpDegreeGreen;
                bmpCenter = bmpCenterGreen;
    //                paintDegreeText.setColor(Color.RED);
                result = "亏";
                break;
            case 2:
                bmpDegree = bmpDegreeRed;
                bmpCenter = bmpCenterRed;
//                paintDegreeText.setColor(Color.GRAY);
                result = "赢";
                break;
            case 3:
                bmpDegree = bmpDegreeGray;
                bmpCenter = bmpCenterGray;
//                paintDegreeText.setColor(Color.GREEN);
                result = "平";
                break;
            default:
                bmpDegree = bmpDegreeGray;
                bmpCenter = bmpCenterGray;
//                paintDegreeText.setColor(Color.GRAY);
                result = "平";
                break;
        }
        paintDegree.setXfermode(null);
        canvas.drawBitmap(bmpCenter, centerX - bmpCenterGreen.getWidth() / 2, centerY - bmpCenterGreen.getHeight() / 2, paintDegree);
        canvas.drawBitmap(bmpEmpty, centerX - bmpEmpty.getWidth() / 2, centerY - bmpEmpty.getHeight() / 2, paintDegree);
        int saveCount = canvas.saveLayer(centerX - bmpEmpty.getWidth() / 2, centerY - bmpEmpty.getHeight() / 2,
                centerX + bmpEmpty.getWidth() / 2,
                centerY + bmpEmpty.getWidth() / 2, null, Canvas.MATRIX_SAVE_FLAG
                        | Canvas.CLIP_SAVE_FLAG
                        | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                        | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                        | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        mOval.left = centerX - bmpDegree.getWidth() / 2;
        mOval.top = centerY - bmpDegree.getHeight() / 2;
        mOval.right = centerX + bmpDegree.getWidth() / 2;
        mOval.bottom = centerY + bmpDegree.getWidth() / 2;
        paintDegree.setXfermode(null);
        if (mDegree >= 0)
            canvas.drawArc(mOval, -90, 360 * mDegree / maxDegree, true, paintDegree);

        paintDegree.setXfermode(mMode);


        canvas.drawBitmap(bmpDegree, centerX - bmpDegreeGreen.getWidth() / 2, centerY - bmpDegreeGreen.getHeight() / 2, paintDegree);
        canvas.restoreToCount(saveCount);

        paintDegreeText.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetricsInt fontMetrics = paintDegreeText.getFontMetricsInt();
        float baseline = (mOval.bottom + mOval.top - fontMetrics.bottom - fontMetrics.top) / 2;//垂直居中

        if (mDegree > 0) {
            if(mDegree<=3600){
                canvas.drawText((int) mDegree + "", centerX, baseline, paintDegreeText);
            }else {
                canvas.drawText(">1小时", centerX, baseline, paintDegreeText);
            }
            progressBar.setVisibility(INVISIBLE);
        } else if (mDegree == 0) {
            float scale = bmpCenter.getWidth() / (float) progressBar.getWidth();
            progressBar.setScaleX(scale);
            progressBar.setScaleY(scale);
            progressBar.setVisibility(VISIBLE);
        } else if (mDegree < 0) {
            canvas.drawText(result, centerX, baseline, paintDegreeText);
            progressBar.setVisibility(INVISIBLE);
        }
    }

    public float getMaxDegree() {
        return maxDegree;
    }

    /**
     * 设置最大刻度
     *
     * @param maxDegree
     */
    public void setMaxDegree(float maxDegree) {
        this.maxDegree = maxDegree;
    }

    /**
     * 设置进度
     *
     * @param percent
     * @param paintColor
     */
    public void setDegress(float percent, int paintColor) {
        mDegree = percent;
        this.paintColor = paintColor;
        invalidate();
    }

    public float getmDegree() {
        return mDegree;
    }

    public BeanOrderResult getOrder() {
        return order;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 这里要计算一下控件的实际大小，然后调用setMeasuredDimension来设置
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = this.getMeasuredSize(widthMeasureSpec, true);
        int height = this.getMeasuredSize(heightMeasureSpec, false);
        setMeasuredDimension(width, height);
    }

    /**
     * 计算控件的实际大小
     *
     * @param length  onMeasure方法的参数，widthMeasureSpec或者heightMeasureSpec
     * @param isWidth 是宽度还是高度
     * @return int 计算后的实际大小
     */
    private int getMeasuredSize(int length, boolean isWidth) {
        // 模式
        int specMode = MeasureSpec.getMode(length);
        // 尺寸
        int specSize = MeasureSpec.getSize(length);
        // 计算所得的实际尺寸，要被返回
        int retSize = 0;
        // 得到两侧的padding（留边）
        int padding = (isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom());

        // 对不同的指定模式进行判断
        if (specMode == MeasureSpec.EXACTLY) {  // 显式指定大小，如40dp或fill_parent
            retSize = specSize;
        } else {                              // 如使用wrap_content
            retSize = (isWidth ? bmpEmpty.getWidth() + padding : bmpEmpty.getHeight() + padding);
//            if (specMode == MeasureSpec.UNSPECIFIED) {
//                retSize = Math.min(retSize, specSize);
//            }
        }

        return retSize;
    }
}
