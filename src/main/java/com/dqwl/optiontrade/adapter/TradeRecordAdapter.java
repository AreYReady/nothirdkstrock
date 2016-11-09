package com.dqwl.optiontrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanOrderRecord;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.widget.SmallProgressBar;

/**
 * @author xjunda
 *         交易历史记录
 * @date 2016-08-03
 */
public class TradeRecordAdapter extends BaseAdapter {
    private Context context;
    private BeanOrderRecord orderRecord;
    private View.OnClickListener lsnr;

    public TradeRecordAdapter(Context context, BeanOrderRecord orderRecord, View.OnClickListener lsnr) {
        this.context = context;
        this.orderRecord = orderRecord;
        this.lsnr = lsnr;
    }

    @Override
    public int getCount() {
        if (orderRecord.getItems() != null) {
            return orderRecord.getItems().size();
        } else if (orderRecord.getOrders() != null)
            return orderRecord.getOrders().size();
        return 0;
    }

    @Override
    public BeanOrderResult getItem(int position) {
        if (orderRecord.getItems() != null) {
            return orderRecord.getItems().get(getCount()-position-1);
        } else if (orderRecord.getOrders() != null)
            return orderRecord.getOrders().get(getCount()-position-1);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        BeanOrderResult record = null;
        if (orderRecord.getItems() != null) {
            record = getItem(position);
        } else if (orderRecord.getOrders() != null) {
            record = getItem(position);
        }
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trade_record, null);
            viewHolder.tvSymbolName = (TextView) convertView.findViewById(R.id.pAssetName1);
            viewHolder.tvGolePrice = (TextView) convertView.findViewById(R.id.goalRateViewTitle);
            viewHolder.tvResultPrice = (TextView) convertView.findViewById(R.id.currentRate1);
            viewHolder.tvWinMoney = (TextView) convertView.findViewById(R.id.payoutVal1);
            viewHolder.tvEndTime = (TextView) convertView.findViewById(R.id.expDateAndTime1);
            viewHolder.tvTicket = (TextView) convertView.findViewById(R.id.positionId1);
            viewHolder.tvOrderPrice = (TextView) convertView.findViewById(R.id.strikeRate1);
            viewHolder.tvOrerTime = (TextView) convertView.findViewById(R.id.time1);
            viewHolder.tvOrderMoney = (TextView) convertView.findViewById(R.id.investmentAmmount1);
            viewHolder.ivOrderDirection = (ImageView) convertView.findViewById(R.id.assetTypeImageView);
            viewHolder.rlTradeRecord = (RelativeLayout) convertView.findViewById(R.id.someId);
            viewHolder.llTradeContent = (LinearLayout) convertView.findViewById(R.id.ll_trade_contain);
            viewHolder.llDetail = (LinearLayout) convertView.findViewById(R.id.extendedRowView);
            viewHolder.spbTradeRecord = (SmallProgressBar) convertView.findViewById(R.id.spb_trade_record);
            viewHolder.llNotCompeleTip = (LinearLayout) convertView.findViewById(R.id.expiredValuesLay);
            viewHolder.llCompeleteTip = (LinearLayout) convertView.findViewById(R.id.ll_complete_tip);
            viewHolder.tvWinMoney2 = (TextView) convertView.findViewById(R.id.positionAmountLeft);
            viewHolder.tvLoseMoney = (TextView) convertView.findViewById(R.id.positionAmountRight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        assert record != null;
        viewHolder.tvSymbolName.setText(record.getDesc());
        viewHolder.tvResultPrice.setText(record.getClose_price() + "");
        viewHolder.tvEndTime.setText(record.getClose_time());
        viewHolder.tvTicket.setText(record.getTicket() + "");
        viewHolder.tvOrderPrice.setText(record.getOpen_price() + "");
        viewHolder.tvOrerTime.setText(record.getOpen_time());
        viewHolder.tvOrderMoney.setText("$" + record.getMoney());
        viewHolder.tvWinMoney2.setText("$" + record.getMoney() * record.getCommision_level() / 100);
        viewHolder.tvLoseMoney.setText("$0");

        switch (record.getResult()) {
            case 1:
                viewHolder.tvWinMoney.setText("-$" + record.getMoney());
                break;
            case 2:
                viewHolder.tvWinMoney.setText("$" + record.getMoney() * record.getCommision_level() / 100);
                break;
            case 3:
                viewHolder.tvWinMoney.setText("$0");
                break;
            default:
                break;
        }
        switch (record.getDirection()) {
            case 0:
                viewHolder.ivOrderDirection.setImageResource(R.mipmap.put_small_icon);
                break;
            case 1:
                viewHolder.ivOrderDirection.setImageResource(R.mipmap.call_small_icon);
                break;
            default:
                viewHolder.ivOrderDirection.setImageResource(R.mipmap.call_small_icon);
                break;
        }

        boolean flag = false;
        if (orderRecord.getItems() != null) {
            flag = orderRecord.getItems().get(position).isShowDetail();
            viewHolder.tvGolePrice.setVisibility(View.VISIBLE);
            viewHolder.spbTradeRecord.setDegress(-1, record.getResult());
        }
//        else if (orderRecord.getOrders() != null) {
//            flag = orderRecord.getOrders().get(position).isShowDetail();
//            viewHolder.tvGolePrice.setVisibility(View.INVISIBLE);
//            viewHolder.spbTradeRecord.setMaxDegree(record.getTime_span());
//            long timeLeft = record.getTime_span() - (System.currentTimeMillis() - TimeUtils.getOrderStartTime(record.getOpen_time())) / 1000;
//            if (timeLeft >= 0) {
//                viewHolder.spbTradeRecord.setDegress(timeLeft, record.getResult());
//            } else {
//                orderRecord.getOrders().remove(position);
//                notifyDataSetChanged();
//            }
//        }

        if (flag) {
            viewHolder.llDetail.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llDetail.setVisibility(View.GONE);
        }
        viewHolder.rlTradeRecord.setTag(position);
        viewHolder.rlTradeRecord.setOnClickListener(lsnr);
        return convertView;
    }

    private class ViewHolder {
        private TextView tvSymbolName, tvResultPrice, tvWinMoney, tvEndTime, tvGolePrice,
                tvTicket, tvOrderPrice, tvOrerTime, tvOrderMoney, tvWinMoney2, tvLoseMoney;
        private ImageView ivOrderDirection;
        private LinearLayout llTradeContent, llNotCompeleTip, llCompeleteTip;
        private LinearLayout llDetail;
        private RelativeLayout rlTradeRecord;
        private SmallProgressBar spbTradeRecord;
    }
}
