package com.dqwl.optiontrade.mvp.trade_record;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.adapter.TradeRecordAdapter;
import com.dqwl.optiontrade.adapter.TradeRecordNotCompelteAdapter;
import com.dqwl.optiontrade.adapter.TradeTypeAdapter;
import com.dqwl.optiontrade.application.OptionApplication;
import com.dqwl.optiontrade.base.BaseActivity;
import com.dqwl.optiontrade.bean.BeanOrderRecord;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.DataEvent;
import com.dqwl.optiontrade.constant.MessageType;
import com.dqwl.optiontrade.mvp.time_chart_show.MinaTimeChartActivity;
import com.dqwl.optiontrade.mvp.trade_record.presenter.TradeRecordPresenterCompl;
import com.dqwl.optiontrade.mvp.trade_record.view.TradeRecordView;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.dqwl.optiontrade.util.SystemUtil;
import com.dqwl.optiontrade.util.TimeUtils;
import com.dqwl.optiontrade.widget.CustomProgressBar;
import com.dqwl.optiontrade.widget.SmallProgressBar;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity.activeOrder;

public class TradeRecordActivity extends BaseActivity implements View.OnClickListener, TradeRecordView {
    private ViewPager vpTradeRecord;
    private LinearLayout llNotCompeleteOrder, llCompeleteOrder;
    private TextView tvRecordResult, tvTitle;
    private PullToRefreshListView lvTradeRecord;
    private BeanOrderRecord orderRecordCompele;
    private CustomProgressBar cpbNetworkError;
    /**
     * 未完成的订单
     */
    private List<SmallProgressBar> smallProgressBars = new ArrayList<>();
    private TradeRecordAdapter tradeRecorCompeletedAdapter;
    private TradeRecordNotCompelteAdapter tradeRecordNotComelteAdapter;
    private Runnable progressRunnable;//进度条定时器
    /**
     * 上一个页面的产品，不进行订阅和取消订阅
     */
    private String symbol;
    private TradeRecordPresenterCompl mTradeRecordPresenterCompl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initIntentData() {
        symbol = getIntent().getStringExtra(MinaTimeChartActivity.SYMBOL);
    }

    @Override
    protected void initViewAndLsnr() {
        setContentView(R.layout.activity_trade_history);
        tvTitle = (TextView) findViewById(R.id.actionTitle1);
        cpbNetworkError = (CustomProgressBar) findViewById(R.id.cpb_network_error);
        ImageButton ibLeftRoller = (ImageButton) findViewById(R.id.leftRollerButton);
        ImageButton ibRightRoller = (ImageButton) findViewById(R.id.rightRollerButton);
        vpTradeRecord = (ViewPager) findViewById(R.id.vp_trade_record);
        llNotCompeleteOrder = (LinearLayout) findViewById(R.id.openPositionsTabBt);
        llCompeleteOrder = (LinearLayout) findViewById(R.id.expiredPositionsTabBt);
        tvRecordResult = (TextView) findViewById(R.id.pa_posStatusTextView);
        lvTradeRecord = (PullToRefreshListView) findViewById(R.id.lv_trade_record);
        ibLeftRoller.setOnClickListener(this);
        ibRightRoller.setOnClickListener(this);
        llNotCompeleteOrder.setOnClickListener(this);
        llCompeleteOrder.setOnClickListener(this);
        llNotCompeleteOrder.setSelected(true);
    }

    @Override
    protected void initData() {
        if(TextUtils.isEmpty(symbol)){
           Set<Integer> tickets = activeOrder.keySet();
            List<Integer> deleteList=new ArrayList<>();
            for (Integer ticket : tickets){
                BeanOrderResult order = activeOrder.get(ticket);
                if (order != null) {
                    long timeLeft = (System.currentTimeMillis() -
                            TimeUtils.getOrderStartTimeNoTimeZone(order.getOpen_time()))/1000;//秒
                    if (timeLeft >= order.getTime_span()) {//当前时间大于到期时间，说明已经结账，要删除
//                        activeOrder.remove(order.getTicket());
                        deleteList.add(order.getTicket());
                    } else {
                        SmallProgressBar spb = new SmallProgressBar(this, -1, order);
                        smallProgressBars.add(spb);//没有到期则要显示
                    }
                }
            }

            for(Integer delete:deleteList){
                activeOrder.remove(delete);
            }
         }
    }

    @Override
    protected void initRegister() {
        EventBus.getDefault().register(this);
    }


    /**
     * 初始化vp标题
     */
    private void initViewPager() {
        List<TextView> tradeTypes = new ArrayList<>();
        for (String type : getResources().getStringArray(R.array.order_type)) {
            View view = LayoutInflater.from(this).inflate(R.layout.trade_type_textview, null);
            TextView tvType = (TextView) view.findViewById(R.id.tv_trade_type);
            tvType.setText(type);
            tradeTypes.add(tvType);
        }
        TradeTypeAdapter tradeTypeAdapter = new TradeTypeAdapter(tradeTypes);
        vpTradeRecord.setAdapter(tradeTypeAdapter);
    }

    /**
     * 获取未完成订单
     * @param smallProgressBars
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetSmallProgressbars(final LinkedList<SmallProgressBar> smallProgressBars) {
        for (SmallProgressBar smallProgressBar : smallProgressBars) {
            BeanOrderResult orderResult = smallProgressBar.getOrder();
            if(smallProgressBar.getmDegree()<=0){
                smallProgressBars.remove(smallProgressBar);
            }else {
                mTradeRecordPresenterCompl.subscribeNewSymbol(orderResult, symbol);
            }
        }
        this.smallProgressBars = smallProgressBars;
        initNotCompeleteOrders();
        EventBus.getDefault().removeStickyEvent(smallProgressBars);
    }

    /**
     * 初始化未完成的订单
     */
    private void initNotCompeleteOrders() {
        tradeRecordNotComelteAdapter = new TradeRecordNotCompelteAdapter(this, this.smallProgressBars, this);
        for(SmallProgressBar smallProgressBar : this.smallProgressBars){
            for(BeanSymbolConfig.SymbolsBean symbolsBean : OptionApplication.symbolShow.getSymbols()){
                if(smallProgressBar.getOrder().getSymbol().equalsIgnoreCase(symbolsBean.getSymbol())){
                    smallProgressBar.getOrder().setDesc(symbolsBean.getDesc());
                }
            }
        }
        lvTradeRecord.setAdapter(tradeRecordNotComelteAdapter);
        setRecordResult(this.smallProgressBars.size(), "没有未结算订单");
        progressRunnable = new Runnable() {//每秒刷新进度条
            @Override
            public void run() {
                tradeRecordNotComelteAdapter.notifyDataSetChanged();
                if (TradeRecordActivity.this.smallProgressBars.size() <= 0) {
                    setRecordResult(0, "没有未结算订单");
                    return;
                }
                llNotCompeleteOrder.postDelayed(this, 1000);
            }
        };
        llNotCompeleteOrder.post(progressRunnable);
    }

    @Subscribe(sticky = true)
    public void onGetTradeResult(DataEvent dataEvent) {//获取已结算订单
        if (dataEvent.getType() == MessageType.TYPE_BINARY_HISTORY_RESULT) {
            orderRecordCompele = new Gson().fromJson(dataEvent.getResult(), BeanOrderRecord.class);
            for(BeanOrderResult orderResult : orderRecordCompele.getItems()){
                for(BeanSymbolConfig.SymbolsBean symbolsBean : OptionApplication.symbolShow.getSymbols()){
                    if(orderResult.getSymbol().equalsIgnoreCase(symbolsBean.getSymbol())){
                        orderResult.setDesc(symbolsBean.getDesc());
                    }
                }
            }
            tradeRecorCompeletedAdapter = new TradeRecordAdapter(this, orderRecordCompele, this);
        }
    }

    @Subscribe(sticky = true, priority = 1)
    public void onGetHistoryData(Handler handlerRead) {//获取session操作和订阅历史数据
        if (!SystemUtil.isAvalidNetSetting(this)) {
            return;
        }
        if(mTradeRecordPresenterCompl==null){
            mTradeRecordPresenterCompl = new TradeRecordPresenterCompl(this, handlerRead);
        }else {
//            mTradeRecordPresenterCompl.setHandler(sslSocketChannel);
        }
//        if(mScreenReceiver==null){
//            startScreenBroadcastReceiver(handlerRead);
//        }else {
////            mScreenReceiver.setHandler(sslSocketChannel);
//        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getSocketChannel(Handler handlerRead){
        if(popupWindowLoading!=null){
            popupWindowLoading.dismiss();
        }
    }

    /**
     * 设置结果文本
     *
     * @param num
     * @param noData
     */
    private void setRecordResult(int num, String noData) {
        if (num <= 0) {
            tvRecordResult.setText(noData);
            tvRecordResult.setVisibility(View.VISIBLE);
            lvTradeRecord.setVisibility(View.GONE);
        } else {
            tvRecordResult.setVisibility(View.INVISIBLE);
            lvTradeRecord.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.someId:
                int position = (int) v.getTag();
                boolean showDetail;
                if (llCompeleteOrder.isSelected() && orderRecordCompele.getItems() != null) {
                    showDetail = orderRecordCompele.getItems().get(position).isShowDetail();
                    if (showDetail) {
                        orderRecordCompele.getItems().get(position).setShowDetail(false);
                    } else {
                        orderRecordCompele.getItems().get(position).setShowDetail(true);
                    }
                    tradeRecorCompeletedAdapter.notifyDataSetChanged();
                } else if (llNotCompeleteOrder.isSelected() && smallProgressBars.size() > 0) {
                    showDetail = tradeRecordNotComelteAdapter.getItem(position).getOrder().isShowDetail();
                    if (showDetail) {
                        tradeRecordNotComelteAdapter.getItem(position).getOrder().setShowDetail(false);
                    } else {
                        tradeRecordNotComelteAdapter.getItem(position).getOrder().setShowDetail(true);
                    }
                    tradeRecordNotComelteAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.expiredPositionsTabBt:
                if (llCompeleteOrder.isSelected())
                    return;
                resetButton();
                llCompeleteOrder.setSelected(true);
                if (tradeRecorCompeletedAdapter != null) {
                    lvTradeRecord.setAdapter(tradeRecorCompeletedAdapter);
                    if(orderRecordCompele.getItems()!=null){
                        setRecordResult(orderRecordCompele.getItems().size(), "没有已结算订单");
                    }else {
                        setRecordResult(0, "没有已结算订单");
                    }
                } else {
                    setRecordResult(0, "没有已结算订单");
                }
                tvTitle.setText("已结算订单");
                llNotCompeleteOrder.removeCallbacks(progressRunnable);
                break;
            case R.id.openPositionsTabBt:
                if (llNotCompeleteOrder.isSelected())
                    return;
                resetButton();
                llNotCompeleteOrder.setSelected(true);
                if (tradeRecordNotComelteAdapter != null) {
                    lvTradeRecord.setAdapter(tradeRecordNotComelteAdapter);
                    setRecordResult(smallProgressBars.size(), "没有未结算订单");
                } else {
                    setRecordResult(0, "没有未结算订单");
                }
                tvTitle.setText("未结算订单");
                llNotCompeleteOrder.post(progressRunnable);
                break;
            default:
                break;
        }
    }

    private void resetButton() {
        llNotCompeleteOrder.setSelected(false);
        llCompeleteOrder.setSelected(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeOut(String timeout){
        if(timeout.equalsIgnoreCase(SSLSocketChannel.TIMEOUT)){
            if(popupWindowLoading!=null)
                popupWindowLoading.dismiss();
            showToast(R.string.net_or_server_error);
//            ll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTradeRecordPresenterCompl!=null)
        mTradeRecordPresenterCompl.onDestroy();
        EventBus.getDefault().unregister(this);
//        if(mScreenReceiver!=null)
//        unregisterReceiver(mScreenReceiver);
    }

//    private ScreenBroadcastReceiver mScreenReceiver;
//
//    /**
//     * 接收锁屏广播
//     */
//    private void startScreenBroadcastReceiver(Handler handlerRead) {
//        mScreenReceiver = new ScreenBroadcastReceiver(this, handlerRead, cpbNetworkError);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(mScreenReceiver, filter);
//    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addSmallProgressBar(SmallProgressBar smallProgressBar) {
        smallProgressBars.add(smallProgressBar);
    }
}
