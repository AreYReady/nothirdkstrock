package com.dqwl.optiontrade.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dqwl.optiontrade.R;


/**
 * @author xjunda
 * @date 2016-08-05
 */
public class TopActionBar extends FrameLayout {
    private TextView tvTitle, tvBadge;
    private ImageButton ivLeft;
    private Button btnRight;
    private int badgeNum;

    public TopActionBar(Context context) {
        this(context, null);
    }

    public TopActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.top_action_bar, this);
        tvTitle = (TextView) view.findViewById(R.id.actionTitle1);
        tvBadge = (TextView) view.findViewById(R.id.openPosCounter);
        ivLeft = (ImageButton) view.findViewById(R.id.refreshMainDataButton1);
        btnRight = (Button) view.findViewById(R.id.topActionButton1);
        String title = attrs.getAttributeValue(null, "title");
        int ivLeftResource = attrs.getAttributeResourceValue(null, "ivLeft", 0);
        String btnText = attrs.getAttributeValue(null, "btnText");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (ivLeftResource > 0) {
            ivLeft.setVisibility(VISIBLE);
            ivLeft.setImageResource(ivLeftResource);
        }
        if (!TextUtils.isEmpty(btnText)) {
            btnRight.setVisibility(VISIBLE);
            btnRight.setText(btnText);
        }
    }

    public void addBadgeNum(int badgeNum) {
        this.badgeNum = this.badgeNum + badgeNum;
        if (this.badgeNum > 0) {
            tvBadge.setVisibility(VISIBLE);
            tvBadge.setText(this.badgeNum + "");
            btnRight.setEnabled(true);
        } else {
            tvBadge.setVisibility(GONE);
            btnRight.setEnabled(false);
        }
    }

    public void setBadgeNum(int badgeNum) {
        this.badgeNum = badgeNum;
        if (this.badgeNum > 0) {
            tvBadge.setVisibility(VISIBLE);
            tvBadge.setText(this.badgeNum + "");
            btnRight.setEnabled(true);
        } else {
            tvBadge.setVisibility(GONE);
            btnRight.setEnabled(false);
        }
    }


    /**
     * 设置右边按钮监听器
     *
     * @param lsnr
     */
    public void setBtnClickLsnr(OnClickListener lsnr) {
        btnRight.setOnClickListener(lsnr);
    }


    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置左边icon的可见情况
     *
     * @param visiblility
     */
    public void setIvLeftVisiblility(int visiblility) {
        ivLeft.setVisibility(visiblility);
    }

    public String getBtnRightText() {
        return btnRight.getText().toString();
    }

    /**
     * 设置右边按钮的文本
     *
     * @param text
     */
    public void setBtnRightText(String text) {
        btnRight.setText(text);
    }
}
