package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dqwl.optiontrade.R;


/**
 * @author xjunda
 * @date 2016-07-27
 */
public class Titlebar extends FrameLayout {
    private TextView tvTitleName, tvRight;
    private ImageView ivRefresh;
    private int ivSrc;
    private String title, right;

    public Titlebar(Context context) {
        this(context, null);
    }

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        tvTitleName = (TextView) view.findViewById(R.id.tv_title_name);
        tvRight = (TextView) view.findViewById(R.id.tv_title_right);
        ivRefresh = (ImageView) view.findViewById(R.id.iv_title_icon);
        ivSrc = attrs.getAttributeResourceValue(null, "icon", 0);
        if (ivSrc > 0) {
            ivRefresh.setVisibility(VISIBLE);
            ivRefresh.setImageResource(ivSrc);
        }
        title = attrs.getAttributeValue(null, "title");
        if (!TextUtils.isEmpty(title)) {
            tvTitleName.setText(title);
        }
        right = attrs.getAttributeValue(null, "right_title");
        if (!TextUtils.isEmpty(right)) {
            tvRight.setText(right);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitleName.setText(title);
    }

    public TextView getTvRight() {
        return tvRight;
    }
}
