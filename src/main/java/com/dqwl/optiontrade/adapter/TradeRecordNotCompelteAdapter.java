package com.dqwl.optiontrade.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.util.TimeUtils;
import com.dqwl.optiontrade.widget.SmallProgressBar;

import java.util.List;

/**
 * @author xjunda
 *         交易历史记录
 * @date 2016-08-03
 */
public class TradeRecordNotCompelteAdapter extends BaseAdapter {
    private Context context;
    private List<SmallProgressBar> smallProgressBars;
    private View.OnClickListener lsnr;

    public TradeRecordNotCompelteAdapter(Context context, List<SmallProgressBar> smallProgressBars, View.OnClickListener lsnr) {
        this.context = context;
        this.smallProgressBars = smallProgressBars;
        this.lsnr = lsnr;
    }

    @Override
    public int getCount() {
        return smallProgressBars.size();
    }

    @Override
    public SmallProgressBar getItem(int position) {
        return smallProgressBars.get(getCount()-position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
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
        SmallProgressBar smallProgressBar = getItem(position);
        BeanOrderResult orderResult = smallProgressBar.getOrder();
        viewHolder.tvSymbolName.setText(orderResult.getDesc());
        setRealTimeBg(convertView, viewHolder, orderResult);

        if (smallProgressBar.getMaxDegree() < 0) {
            viewHolder.llNotCompeleTip.setVisibility(View.VISIBLE);
            viewHolder.spbTradeRecord.setVisibility(View.GONE);
            viewHolder.llCompeleteTip.setVisibility(View.GONE);
        } else {
            viewHolder.llNotCompeleTip.setVisibility(View.GONE);
            viewHolder.spbTradeRecord.setVisibility(View.VISIBLE);
            viewHolder.llCompeleteTip.setVisibility(View.VISIBLE);
        }

        long timeStart = TimeUtils.getOrderStartTime(orderResult.getOpen_time());
        viewHolder.tvEndTime.setText(TimeUtils.getShowTime(timeStart + orderResult.getTime_span() * 1000));
        viewHolder.tvTicket.setText(orderResult.getTicket() + "");
        viewHolder.tvOrderPrice.setText(orderResult.getOpen_price() + "");
        viewHolder.tvOrerTime.setText(orderResult.getOpen_time());
        viewHolder.tvOrderMoney.setText("$" + orderResult.getMoney());
        viewHolder.tvWinMoney2.setText("$" + orderResult.getMoney() * orderResult.getCommision_level() / 100);
        viewHolder.tvLoseMoney.setText("$0");

//        switch (orderResult.getResult()) {
//            case 0:
//                viewHolder.tvWinMoney.setText("$0");
//                break;
//            case 1:
                viewHolder.tvWinMoney.setText("$" + orderResult.getMoney() * orderResult.getCommision_level() / 100);
//                break;
//            default:
//                break;
//        }
        switch (orderResult.getDirection()) {
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

        viewHolder.tvGolePrice.setVisibility(View.INVISIBLE);
        viewHolder.spbTradeRecord.setMaxDegree(orderResult.getTime_span());
        if (smallProgressBar.getmDegree() >= -1) {
            viewHolder.spbTradeRecord.setDegress(smallProgressBar.getmDegree(), smallProgressBar.getPaintColor());
        } else {
            smallProgressBars.remove(position);
            notifyDataSetChanged();
        }
        boolean flag;
        flag = orderResult.isShowDetail();
        if (flag) {
            viewHolder.llDetail.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llDetail.setVisibility(View.GONE);
        }
        viewHolder.rlTradeRecord.setTag(position);
        viewHolder.rlTradeRecord.setOnClickListener(lsnr);
        return convertView;
    }

    /**
     * 设置实时文本背景颜色
     * @param convertView
     * @param viewHolder
     * @param orderResult
     */
    private void setRealTimeBg(View convertView, final ViewHolder viewHolder, BeanOrderResult orderResult) {
        if (TextUtils.isEmpty(orderResult.getClose_price() + "") || orderResult.getClose_price() <= 0) {
            viewHolder.tvResultPrice.setText(orderResult.getOpen_price() + "");
        } else {
            viewHolder.tvResultPrice.setText(orderResult.getClose_price() + "");
            if (orderResult.isRealTime()) {
                if (orderResult.getClose_price() > orderResult.getOpen_price()) {
                    viewHolder.tvResultPrice.setBackgroundColor(Color.RED);
                } else if (orderResult.getClose_price() < orderResult.getOpen_price()) {
                    viewHolder.tvResultPrice.setBackgroundColor(Color.GREEN);
                } else {
                    viewHolder.tvResultPrice.setBackgroundColor(Color.GRAY);
                }
                convertView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.tvResultPrice.setBackgroundColor(Color.TRANSPARENT);
                    }
                }, 300);
                orderResult.setRealTime(false);
            }
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
        SmallProgressBar smallProgressBar = getItem(itemIndex);
        BeanOrderResult orderResult = smallProgressBar.getOrder();
        setRealTimeBg(view,holder,orderResult);
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
