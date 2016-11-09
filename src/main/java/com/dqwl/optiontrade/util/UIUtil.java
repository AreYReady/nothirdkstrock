package com.dqwl.optiontrade.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * @author xjunda
 * @date 2016-08-03
 */
class UIUtil {
    /**
     * 设置listview没有数据时的显示界面
     *
     * @author xjunda
     * Created at 2015/12/17 9:11
     */
    public static void setEmptyViewForLv(Context context, ListView listView, View emptyView) {
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }


}
