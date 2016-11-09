package com.dqwl.optiontrade.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.EventBusEvent;
import com.dqwl.optiontrade.bean.MyFavorEvent;
import com.dqwl.optiontrade.util.CacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * @author xjunda
 * @date 2016-07-25
 * 实时数据列表适配器包括喜爱的产品列表
 */
public class RealTimeAdapter extends BaseAdapter implements View.OnClickListener {
    public static final String REMOVE_ALL = "REMOVE_ALL";
    private Context context;
    private List<BeanSymbolConfig.SymbolsBean> data;
    private Map<String, BeanSymbolConfig.SymbolsBean> favorsSymbol;
    private boolean editAble;//是否在编辑中
    private boolean isFavorShow;//是否在展示我的喜爱的产品

    public RealTimeAdapter(Context context, List<BeanSymbolConfig.SymbolsBean> realTimeDataList) {
        this.context = context;
        this.data = realTimeDataList;
        favorsSymbol = (Map<String, BeanSymbolConfig.SymbolsBean>) context.getSharedPreferences(CacheUtil.FAVOR_SYMBOL, Context.MODE_PRIVATE)
                .getAll();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BeanSymbolConfig.SymbolsBean getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trade_list_row, null);
            viewHolder.tvProductName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tvProductPrice = (TextView) convertView.findViewById(R.id.rate);
            viewHolder.cbStar = (CheckBox) convertView.findViewById(R.id.favoriteCheckBox);
            viewHolder.tvArrow = (TextView) convertView.findViewById(R.id.arrowView1);
            viewHolder.btnEdit = (Button) convertView.findViewById(R.id.favRowDelBt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setItemData(position, convertView, viewHolder);
        if (editAble) {
            viewHolder.btnEdit.setVisibility(View.VISIBLE);
            viewHolder.tvArrow.setVisibility(View.INVISIBLE);
            viewHolder.tvProductPrice.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.btnEdit.setVisibility(View.GONE);
            viewHolder.tvArrow.setVisibility(View.VISIBLE);
            viewHolder.tvProductPrice.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    /**
     * 设置viewHolder位置
     * @param position
     * @param convertView
     * @param viewHolder
     */
    private void setItemData(int position, View convertView, ViewHolder viewHolder) {
        BeanSymbolConfig.SymbolsBean realTimeData = getItem(position);
        viewHolder.tvProductName.setText(realTimeData.getDesc());
        viewHolder.tvProductPrice.setText(realTimeData.getProductPrice() + "");
        viewHolder.cbStar.setTag(realTimeData);
        viewHolder.cbStar.setOnClickListener(this);
        viewHolder.btnEdit.setTag(position);
        viewHolder.btnEdit.setOnClickListener(this);
        if (isFavorShow) {
            viewHolder.cbStar.setVisibility(View.GONE);
        } else {
            viewHolder.cbStar.setVisibility(View.VISIBLE);
        }
        if (favorsSymbol.containsKey(realTimeData.getSymbol())) {
            viewHolder.cbStar.setChecked(true);
        } else {
            viewHolder.cbStar.setChecked(false);
        }
        if (realTimeData.getFlag() > 0) {
            if (realTimeData.getFlag() == 1) {
                viewHolder.tvProductPrice.setBackgroundColor(Color.GREEN);
            } else if (realTimeData.getFlag() == 2) {
                viewHolder.tvProductPrice.setBackgroundColor(Color.RED);
            } else {
                viewHolder.tvProductPrice.setBackgroundColor(Color.GRAY);
            }
            final ViewHolder finalViewHolder = viewHolder;
            convertView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finalViewHolder.tvProductPrice.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 300);
        }
    }

    /**
     * 局部刷新
     * @param view
     * @param itemIndex
     */
    public void updateView(View view, int itemIndex) {
        if(view == null) {
            return;
        }
        //从view中取得holder
        ViewHolder holder = (ViewHolder) view.getTag();
        setItemData(itemIndex, view, holder);
    }


//
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.favoriteCheckBox) {
            CheckBox cb = (CheckBox) v;
            BeanSymbolConfig.SymbolsBean symbol = (BeanSymbolConfig.SymbolsBean) cb.getTag();
            if (cb.isChecked()) {
                EventBus.getDefault().post(new MyFavorEvent(1, symbol));
                favorsSymbol.put(symbol.getSymbol(), symbol);
            } else if (!cb.isChecked()) {
                EventBus.getDefault().post(new MyFavorEvent(-1, symbol));
                favorsSymbol.remove(symbol.getSymbol());
            }
        } else if (v.getId() == R.id.favRowDelBt) {
            Button btnDel = (Button) v;
            int position = (int) btnDel.getTag();
            EventBus.getDefault().post(new MyFavorEvent(-1, getItem(position)));
            favorsSymbol.remove(getItem(position).getSymbol());
            data.remove(position);
            notifyDataSetChanged();
            if (data.size() <= 0) {
                EventBus.getDefault().post(new EventBusEvent(REMOVE_ALL));
            }
        }
    }

    /**
     * 设置是否可编辑
     *
     * @param editAble
     */
    public void setEditAble(boolean editAble) {
        this.editAble = editAble;
        notifyDataSetChanged();
    }

    /**
     * 设置是否在展示我的喜爱产品
     *
     * @param isFavorShow
     */
    public void setIsFavorShow(boolean isFavorShow) {
        this.isFavorShow = isFavorShow;
    }

    private class ViewHolder {
        private TextView tvProductName;
        private TextView tvProductPrice;
        private CheckBox cbStar;
        private TextView tvArrow;
        private Button btnEdit;
    }
}
