package com.dqwl.optiontrade.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.dqwl.optiontrade.R;


public class CustomProgressBar
        extends ImageView {
    private Context context;
    private Animation rotation;

    @TargetApi(11)
    public CustomProgressBar(Context paramContext) {
        this(paramContext, null);
        this.context = paramContext;
        if (Build.VERSION.SDK_INT > 10) {
            setLayerType(2, null);
        }
    }

    @TargetApi(11)
    public CustomProgressBar(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.context = paramContext;
        if (Build.VERSION.SDK_INT > 10) {
            setLayerType(2, null);
        }
    }

    @TargetApi(11)
    public CustomProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.context = paramContext;
        if (Build.VERSION.SDK_INT > 10) {
            setLayerType(2, null);
        }
    }

    public void startLoadingAnimation() {
        if (this.rotation == null) {
            this.rotation = AnimationUtils.loadAnimation(this.context, R.anim.loading_dialog_rotation_animation);
        }
        setVisibility(VISIBLE);
        startAnimation(this.rotation);
    }

    public void startLoadingAnimation(int paramInt1, int paramInt2) {
        if (this.rotation == null) {
            this.rotation = new RotateAnimation(360.0F, 0.0F, paramInt1, paramInt2);
            this.rotation.setDuration(1000L);
            this.rotation.setRepeatCount(-1);
            this.rotation.setInterpolator(new LinearInterpolator());
        }
        startAnimation(this.rotation);
    }

    public void stopLoadingAnimation() {
        clearAnimation();
        setVisibility(GONE);
    }
}
