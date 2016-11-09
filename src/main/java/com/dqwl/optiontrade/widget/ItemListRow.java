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
 * @date 2016-08-04
 */
public class ItemListRow extends FrameLayout {
    private TextView tvMain, tvSub;
    private ImageView ivItem;

    public ItemListRow(Context context) {
        this(context, null);
    }

    public ItemListRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_row, this);
        tvMain = (TextView) view.findViewById(R.id.assetType);
        tvSub = (TextView) view.findViewById(R.id.numOfAssets);
        ivItem = (ImageView) view.findViewById(R.id.assetTypeImageView);
        String strMain = attrs.getAttributeValue(null, "mainText");
        String strSub = attrs.getAttributeValue(null, "subText");
        int ivResource = attrs.getAttributeResourceValue(null, "icon", 0);
        if (!TextUtils.isEmpty(strSub)) {
            tvSub.setVisibility(VISIBLE);
            tvSub.setText(strSub);
        }
        tvMain.setText(strMain);
        if (ivResource > 0) {
            ivItem.setImageResource(ivResource);
        }
    }
}
