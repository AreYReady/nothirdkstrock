package com.dqwl.optiontrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;

import java.util.List;

/**
 * @author xjunda
 * @date 2016-09-05
 */
public class EndTimeAdapter extends BaseAdapter {
    private Context mContext;
    private List<BeanSymbolConfig.SymbolsBean.CyclesBean> data;

    public EndTimeAdapter(Context context, List<BeanSymbolConfig.SymbolsBean.CyclesBean> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BeanSymbolConfig.SymbolsBean.CyclesBean getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BeanSymbolConfig.SymbolsBean.CyclesBean cyclesBean = getItem(position);
        ViewHolder viewHolder;
        if(convertView ==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_symbol_endtime, null);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_symbol_endtime);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(cyclesBean.getDesc());
        return convertView;
    }

    private class ViewHolder{
        private TextView mTextView;
    }
}
