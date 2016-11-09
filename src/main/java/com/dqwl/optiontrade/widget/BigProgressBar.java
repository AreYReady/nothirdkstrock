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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.util.DensityUtil;

/**
 * 大进度条
 */
public class BigProgressBar extends FrameLayout {
    private int widthPixels;
    private int heightPixels;
    private int centerX;
    private int centerY;
    private Bitmap bmpEmptyGreen;
    private Bitmap bmpEmptyRed;
    private Bitmap bmpEmptyGray;
    private Bitmap bmpFullGreen;
    private Bitmap bmpFullRed;
    private Bitmap bmpFullGray;
    private PorterDuffXfermode mMode;
    private Paint paintdegree;
    private RectF mOval;
    private Paint paintDegreeText;
    private int paintColor;//画笔颜色1为红亏，2为绿赢，3为平灰
    private float mDegree = -1;//进度
    private float maxDegree = 60;//最大进度
    private ProgressBar progressBar;
    private BeanOrderResult order;//序号ticket
    /**
     * @param context
     */
    public BigProgressBar(Context context) {
        super(context);
    }
    /**
     * @param context
     * @param maxDegree 最大刻度
     * @param order     进度条ticket
     */
    public BigProgressBar(Context context, int maxDegree, BeanOrderResult order) {
        this(context, null);
        this.maxDegree = maxDegree;
        this.order = order;
        this.mDegree = maxDegree;
    }

    public BigProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);

        bmpEmptyGreen = BitmapFactory.decodeResource(getResources(),
                R.mipmap.big_empty_green_bg);
        bmpEmptyRed = BitmapFactory.decodeResource(getResources(),
                R.mipmap.big_empty_red_bg);
        bmpEmptyGray = BitmapFactory.decodeResource(getResources(),
                R.mipmap.big_empty_grey_bg);
        bmpFullGreen = BitmapFactory.decodeResource(getResources(),
                R.mipmap.big_full_green_bg);
        bmpFullRed = BitmapFactory.decodeResource(getResources(),
                R.mipmap.big_full_red_bg);
        bmpFullGray = BitmapFactory.decodeResource(getResources(),
                R.mipmap.big_full_grey_bg);

        mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        paintdegree = new Paint();
//        paintdegree.setColor(Color.BLUE);
        paintdegree.setXfermode(mMode);
        mOval = new RectF();
        mOval.left = 0;
        mOval.top = 0;

        mDegree = 0;
        paintDegreeText = new Paint();
        paintDegreeText.setColor(Color.GREEN);
        paintDegreeText.setTypeface(Typeface.DEFAULT_BOLD);
        paintDegreeText.setTextSize(DensityUtil.dip2px(getContext(), 60));


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
    protected void onDraw(Canvas canvas) {
        Bitmap bmpEmpty;
        Bitmap bmpFull;
        String result;
        paintDegreeText.setColor(Color.WHITE);
        switch (paintColor) {
            case 1:
                bmpEmpty = bmpEmptyGreen;
                bmpFull = bmpFullGreen;
//                paintDegreeText.setColor(Color.GREEN);
                result = "亏";
                break;
            case 2:
                bmpEmpty = bmpEmptyRed;
                bmpFull = bmpFullRed;
//                paintDegreeText.setColor(Color.RED);
                result = "赢";
                break;
            case 3:
                bmpEmpty = bmpEmptyGray;
                bmpFull = bmpFullGray;
//                paintDegreeText.setColor(Color.GRAY);
                result = "平";
                break;
            default:
                bmpEmpty = bmpEmptyGray;
                bmpFull = bmpFullGray;
//                paintDegreeText.setColor(Color.GRAY);
                result = "平";
                break;
        }

        paintdegree.setXfermode(null);
        canvas.drawBitmap(bmpEmpty, centerX - bmpEmpty.getWidth() / 2, centerY - bmpEmpty.getHeight() / 2, paintdegree);
        int saveCount = canvas.saveLayer(centerX - bmpEmpty.getWidth() / 2, centerY - bmpEmpty.getHeight() / 2,
                centerX + bmpEmptyGreen.getWidth() / 2,
                centerY + bmpEmptyGreen.getWidth() / 2, null, Canvas.MATRIX_SAVE_FLAG
                        | Canvas.CLIP_SAVE_FLAG
                        | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                        | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                        | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        mOval.left = centerX - bmpFull.getWidth() / 2;
        mOval.top = centerY - bmpFull.getHeight() / 2;
        mOval.right = centerX + bmpFull.getWidth() / 2;
        mOval.bottom = centerY + bmpFull.getWidth() / 2;
        paintdegree.setXfermode(null);
        if (mDegree >= 0)
            canvas.drawArc(mOval, -90, 360 * mDegree / maxDegree, true, paintdegree);

        paintdegree.setXfermode(mMode);

        canvas.drawBitmap(bmpFull, centerX - bmpFull.getWidth() / 2, centerY - bmpFull.getHeight() / 2, paintdegree);
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
            progressBar.setVisibility(VISIBLE);
        } else if (mDegree < 0) {
            canvas.drawText(result, centerX, baseline, paintDegreeText);
            progressBar.setVisibility(INVISIBLE);
        }
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
            retSize = (isWidth ? bmpFullGreen.getWidth() + padding : bmpFullGreen.getHeight() + padding);
            if (specMode == MeasureSpec.UNSPECIFIED) {
                retSize = Math.min(retSize, specSize);
            }
        }

        return retSize;
    }

    public BeanOrderResult getOrder() {
        return order;
    }

    public float getmDegree() {
        return mDegree;
    }

    public void setmDegree(float mDegree) {
        this.mDegree = mDegree;
    }
}
