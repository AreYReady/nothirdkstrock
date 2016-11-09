package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class AutofitTextView
        extends TextView {
    private static final int DEFAULT_MIN_TEXT_SIZE = 8;
    private static final float PRECISION = 0.5F;
    private static final boolean SPEW = false;
    private static final String TAG = "me.grantland.widget.AutoFitTextView";
    private float mMaxTextSize;
    private float mMinTextSize;
    private Paint mPaint;
    private float mPrecision;

    public AutofitTextView(Context paramContext) {
        super(paramContext);
        init();
    }

    public AutofitTextView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    private float getTextSize(Resources paramResources, String paramString, float paramFloat1, float paramFloat2, float paramFloat3) {
        float f1 = (paramFloat2 + paramFloat3) / 2.0F;
        this.mPaint.setTextSize(TypedValue.applyDimension(0, f1, paramResources.getDisplayMetrics()));
        float f2 = this.mPaint.measureText(paramString);
        if (paramFloat3 - paramFloat2 < this.mPrecision) {
            return paramFloat2;
        }
        if (f2 > paramFloat1) {
            return getTextSize(paramResources, paramString, paramFloat1, paramFloat2, f1);
        }
        if (f2 < paramFloat1) {
            return getTextSize(paramResources, paramString, paramFloat1, f1, paramFloat3);
        }
        return f1;
    }

    private void init() {
        this.mMinTextSize = 8.0F;
        this.mMaxTextSize = getTextSize();
        this.mPrecision = 0.5F;
        this.mPaint = new Paint();
    }

    private void refitText(String paramString, int paramInt) {
        if (paramInt > 0) {
            Context localContext = getContext();
            Resources localResources = Resources.getSystem();
            paramInt = paramInt - getPaddingLeft() - getPaddingRight();
            float f1 = this.mMaxTextSize;
            float f2 = this.mMaxTextSize;
            if (localContext != null) {
                localResources = localContext.getResources();
            }
            this.mPaint.set(getPaint());
            this.mPaint.setTextSize(f1);
            if (this.mPaint.measureText(paramString) > paramInt) {
                f2 = getTextSize(localResources, paramString, paramInt, 0.0F, f2);
                f1 = f2;
                if (f2 < this.mMinTextSize) {
                    f1 = this.mMinTextSize;
                }
            }
            setTextSize(0, f1);
        }
    }

    public float getMaxTextSize() {
        return this.mMaxTextSize;
    }

    public void setMaxTextSize(int paramInt) {
        this.mMaxTextSize = paramInt;
    }

    public float getMinTextSize() {
        return this.mMinTextSize;
    }

    public void setMinTextSize(int paramInt) {
        this.mMinTextSize = paramInt;
    }

    public float getPrecision() {
        return this.mPrecision;
    }

    public void setPrecision(float paramFloat) {
        this.mPrecision = paramFloat;
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        paramInt1 = View.MeasureSpec.getSize(paramInt1);
        refitText(getText().toString(), paramInt1);
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        if (paramInt1 != paramInt3) {
            refitText(getText().toString(), paramInt1);
        }
    }

    protected void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
        super.onTextChanged(paramCharSequence, paramInt1, paramInt2, paramInt3);
        refitText(paramCharSequence.toString(), getWidth());
    }
}


