package com.dqwl.optiontrade.mvp.trade_index;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.adapter.RealTimeAdapter;
import com.dqwl.optiontrade.base.BaseActivity;
import com.dqwl.optiontrade.bean.BeanChangePercent;
import com.dqwl.optiontrade.bean.BeanCurrentServerTime;
import com.dqwl.optiontrade.bean.BeanOrderRecord;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.bean.BeanServerTime;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.EventBusAllSymbol;
import com.dqwl.optiontrade.bean.EventBusEvent;
import com.dqwl.optiontrade.bean.MyFavorEvent;
import com.dqwl.optiontrade.bean.RealTimeDataList;
import com.dqwl.optiontrade.broadcastrecevier.ScreenBroadcastReceiver;
import com.dqwl.optiontrade.mvp.login.LoginActivity;
import com.dqwl.optiontrade.mvp.login.presenter.LoginPresenterCompl;
import com.dqwl.optiontrade.mvp.my_account.MyAccountActivity;
import com.dqwl.optiontrade.mvp.time_chart_show.MinaTimeChartActivity;
import com.dqwl.optiontrade.mvp.trade_index.presenter.ITradeIndexPresenter;
import com.dqwl.optiontrade.mvp.trade_index.presenter.TradeIndexPresenterCompl;
import com.dqwl.optiontrade.mvp.trade_index.view.ITradeIndexView;
import com.dqwl.optiontrade.mvp.trade_record.TradeRecordActivity;
import com.dqwl.optiontrade.util.CacheUtil;
import com.dqwl.optiontrade.util.NoCompleteActiveOrder;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLDecoderImp;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.dqwl.optiontrade.util.SystemUtil;
import com.dqwl.optiontrade.util.TimeUtils;
import com.dqwl.optiontrade.widget.CustomProgressBar;
import com.dqwl.optiontrade.widget.TopActionBar;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 登入之后的显示交易类型的页面
 */
public class TradeIndexActivity extends BaseActivity implements View.OnClickListener, ITradeIndexView {
    public static final String SYMBOL = "SYMBOL";
    public static final String POSITION = "POSITION";
    public static final String TIMEOFFSET = "TIMEOFFSET";
    public static final String VOL_MIN = "vol_min";
    public static final String VOL_MAX = "vol_max";
    public static final String STEP_MIN = "step_min";
    public static final String TZ_DELTA = "tz_delta";
    private RealTimeAdapter realTimeAdapter;//实时适配器
    private ListView lvRealTime;
    private TopActionBar topActionBar;
    private LinearLayout llNetworkErrorContainer;//没有网络时候的布局
    private CustomProgressBar cpbNetworkError;//网络重新连接进图条
    private LinearLayout llBottomTab;
    private String TAG = SystemUtil.getTAG(this);
    private BeanSymbolConfig symbolShow;
    /**
     * 所有symbole列表
     */
    private ArrayList<EventBusAllSymbol.ItemSymbol> allSymbol;
    //按两次返回键退出程序
    private long exitTime = 0;
    private ITradeIndexPresenter mTradeIndexPresenterCompl;
    /**
     * 进度条经历的时间差
     */
    private long timeOffset;
    /**
     * 未完成的订单
     */
    public static HashMap<Integer, BeanOrderResult> activeOrder = new LinkedHashMap<>();
    /**
     * 下一个页面的symbol
     */
    private String nextSymbole;

    /**
     * 最小交易量
     */
    private int vol_min;
    /**
     * 最大交易量
     */
    private int vol_max;
    /**
     * 最小步进价格
     */
    private int step_min;
    public static int tz_detla = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initIntentData() {
        allSymbol = getIntent().getParcelableArrayListExtra(LoginActivity.ALL_SYMBOL);
        timeOffset = System.currentTimeMillis();//记录当前时间
    }

    @Override
    protected void initViewAndLsnr() {
        setContentView(R.layout.activity_trade_index);
        topActionBar = (TopActionBar) findViewById(R.id.top_action_bar);
        llBottomTab = (LinearLayout) findViewById(R.id.bottomTabHolder);
        llNetworkErrorContainer = (LinearLayout) findViewById(R.id.networkErrorContainer1);
        cpbNetworkError = (CustomProgressBar) findViewById(R.id.cpb_network_error);
        ImageButton ivReConnet = (ImageButton) findViewById(R.id.networkErrorRefreshButton1);
        lvRealTime = (ListView) findViewById(R.id.tradeListView);
        lvRealTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isNetWorkAvalid())
                    return;
                if (llNetworkErrorContainer.isShown()) {
                    showToast(getString(R.string.reconnect_please));
                    return;
                }
                startActivity(new Intent(TradeIndexActivity.this, MinaTimeChartActivity.class)
//                        .putExtra(SYMBOL, mTradeIndexPresenterCompl.getRealTimeD
                        .putExtra(LoginActivity.ACTIVEORDER, activeOrder)
                        .putExtra(POSITION, position)
                        .putExtra(TIMEOFFSET, timeOffset)
                        .putExtra(VOL_MIN, vol_min)
                        .putExtra(VOL_MAX, vol_max)
                        .putExtra(TZ_DELTA, symbolShow.getTz_delta())
                        .putExtra(STEP_MIN, step_min));
                EventBus.getDefault().postSticky(mTradeIndexPresenterCompl.getRealTimeData());
                nextSymbole = mTradeIndexPresenterCompl.getRealTimeData().get(position).getSymbol();
            }
        });
        ivReConnet.setOnClickListener(this);


        LinearLayout llTradeType = (LinearLayout) findViewById(R.id.ll_trade_type);
        LinearLayout llTradeProduct = (LinearLayout) findViewById(R.id.ll_trade_product);
        LinearLayout llTrade = (LinearLayout) findViewById(R.id.ll_trade);
        LinearLayout llMyAccount = (LinearLayout) findViewById(R.id.ll_my_account);
        LinearLayout llTradeRecord = (LinearLayout) findViewById(R.id.ll_trade_record);
        llTradeType.setOnClickListener(this);
        llTradeProduct.setOnClickListener(this);
        llMyAccount.setOnClickListener(this);
        llTrade.setOnClickListener(this);
        llTradeRecord.setOnClickListener(this);
        topActionBar.setBadgeNum(getSharedPreferences(CacheUtil.FAVOR_SYMBOL, Context.MODE_PRIVATE).getAll().size());
        topActionBar.setBtnClickLsnr(this);
        cpbNetworkError.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopupLoading(cpbNetworkError);
            }
        }, 30);
    }

    @Override
    protected void initData() {
        NoCompleteActiveOrder.getSingleton().saveActiveOrder(activeOrder);
    }

    @Override
    protected void initRegister() {
        EventBus.getDefault().register(this);
    }

    /**
     * 设置实时数据
     *
     * @param realTimeDataList
     */
    private void showRealTimeData(RealTimeDataList realTimeDataList) {
//       mTradeIndexPresenterCompl.showRealTimeData(realTimeDataList);
//        realTimeAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onFavorEvent(MyFavorEvent myFavorEvent) {
        topActionBar.addBadgeNum(myFavorEvent.getPlus());
        String symbolDetail = new Gson().toJson(myFavorEvent.getSymbol(), BeanSymbolConfig.SymbolsBean.class);
        if (myFavorEvent.getPlus() > 0) {
            CacheUtil.saveFavor(this, myFavorEvent.getSymbol().getSymbol(), symbolDetail, true);
        } else {
            CacheUtil.saveFavor(this, myFavorEvent.getSymbol().getSymbol(), symbolDetail, false);
        }
    }

    @Subscribe(sticky = true, priority = 1, threadMode = ThreadMode.MAIN)
    public void getSocketChannel(Handler handlerRead) {
        if (mTradeIndexPresenterCompl == null) {
            mTradeIndexPresenterCompl = new TradeIndexPresenterCompl(this, handlerRead);
        } else {
            mTradeIndexPresenterCompl.setHandler(handlerRead);
        }
        if (mScreenReceiver == null) {
            startScreenBroadcastReceiver(handlerRead);
        } else {
            mScreenReceiver.setHandler(handlerRead);//原来的session已经关掉，要指向新的session
        }
        if (SystemUtil.isTopActivity(this, getLocalClassName())) {
            mTradeIndexPresenterCompl.subscribeSymbol("");
        }
    }

    @Subscribe
    public void onHeartHeat(String heatHeat) {
        if (heatHeat.equalsIgnoreCase(SSLDecoderImp.HEART_BEAT)) {
            mTradeIndexPresenterCompl.responseHeartBeat();
        }
    }

    /**
     * 获取到实时数据
     *
     * @param realTimeDataList
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRealTimeData(RealTimeDataList realTimeDataList) {
//        showRealTimeData(realTimeDataList);
        mTradeIndexPresenterCompl.showRealTimeData(realTimeDataList);
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisconnectFromServerOrLoginOut(String result) {
        if (result.equalsIgnoreCase(LoginPresenterCompl.DISCONNECT_FROM_SERVER)) {
            llNetworkErrorContainer.setVisibility(View.VISIBLE);
            cpbNetworkError.stopLoadingAnimation();
        } else if (result.equalsIgnoreCase(MyAccountActivity.LOGIN_OUT)) {
            startActivity(new Intent(this, LoginActivity.class));
            showToast("请重新登录");
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeOut(String timeout) {
        if (timeout.equalsIgnoreCase(SSLSocketChannel.TIMEOUT)) {
            if (popupWindowLoading != null)
                popupWindowLoading.dismiss();

            showToast(R.string.net_or_server_error);
            llNetworkErrorContainer.setVisibility(View.VISIBLE);
        } else if (timeout.equalsIgnoreCase(ScreenBroadcastReceiver.CONNECT)) {
            mTradeIndexPresenterCompl.connectToMinaServer();
        }
    }

    /**
     * 获取进行中，还没有完成的订单
     *
     * @param orderRecord
     */
    @Subscribe(sticky = true)
    public void onGetActiviteOrder(BeanOrderRecord orderRecord) {
        if (orderRecord != null && orderRecord.getOrders() != null) {
            for (BeanOrderResult orderResult : orderRecord.getOrders()) {
                activeOrder.put(orderResult.getTicket(), orderResult);
            }
        }
    }

    /**
     * 封装服务器时间
     *
     * @param beanServerTime
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getServerTime(BeanServerTime beanServerTime) {
        BeanCurrentServerTime.getInstance(TimeUtils
                .getOrderStartTimeNoTimeZone(beanServerTime.getTime()))
                .getCurrentServerTime();

        Log.i(TAG, "getServerTime: " + TimeUtils.getOrderStartTimeNoTimeZone(beanServerTime.getTime()));
    }

    /**
     * 获取要展示的产品
     *
     * @param symbolShow
     */
    private String nameHander;
    private Handler mHandler;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetSymbolShow(BeanSymbolConfig symbolShow) {
        this.symbolShow = symbolShow;
        this.tz_detla = symbolShow.getTz_delta();
        ArrayList<BeanSymbolConfig.SymbolsBean> realTimeDatas
                = mTradeIndexPresenterCompl.writeRealTimeData(symbolShow.getSymbols(), allSymbol);
        final Map<String, BeanChangePercent> mBeanChangePercentMap = mTradeIndexPresenterCompl.writePercentTimeData(symbolShow.getSymbols());
        HandlerThread handlerThread = new HandlerThread("nameHander");
        handlerThread.start();


        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //注意,偏移量的理解可能有误,如果换服务器后台的话,到时候要测试下
                //修正,treemap不能存储重复的值,所以,map<String,List<BeanChangePercent>>
                List<String> deletekey=new ArrayList<>();
                Map.Entry<String, BeanChangePercent> next;
                long currentTime;
                long keyTime;
                Iterator<Map.Entry<String, BeanChangePercent>> iterator = mBeanChangePercentMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    next = iterator.next();
                    try {
                    int   time = (int)(TimeUtils.stringToLong(TimeUtils.getCurrentTimeNoS(), "hh:mm")/1000 - TimeUtils.stringToLong(next.getKey(), "hh:mm")/1000);
                        if (time == 0) {
                            Log.i(TAG, "handleMessage: eventBus time"+time);
                            EventBus.getDefault().post(next);
                        } else if(time<0) {
                            //获取本地时间和第一个集合对比计算出相差的时间,休眠.
                            Log.i(TAG, "handleMessage: 休息了time"+Math.abs(time));
                            mHandler.sendEmptyMessageDelayed(0,Math.abs(time));
                            break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        mHandler.sendEmptyMessage(0);
//        //定时器,轮训这个数组,做操作.将数据发送到MinaTimeChartActivity里面区做操作
//        EventBus.getDefault().post(mBeanChangePercentMap);
        vol_max = symbolShow.getVol_max();
        vol_min = symbolShow.getVol_min();
        step_min = symbolShow.getStep_min();
        if (realTimeAdapter == null) {
            realTimeAdapter = new RealTimeAdapter(this, realTimeDatas);
            lvRealTime.setAdapter(realTimeAdapter);
        } else {
            EventBus.getDefault().post(realTimeDatas);
        }


        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
        llNetworkErrorContainer.setVisibility(View.GONE);
        cpbNetworkError.stopLoadingAnimation();
        EventBus.getDefault().removeStickyEvent(symbolShow);
    }

    @Subscribe
    public void onRemoveAllFavors(EventBusEvent eventBusEvent) {
        if (eventBusEvent.getEvent().equalsIgnoreCase(RealTimeAdapter.REMOVE_ALL)) {
            switchRealtimeSurface();
        }
    }

//    private void subsribeSymbol(List<RealTimeData> realTimeDatas) {
//            mTradeIndexPresenterCompl.subscribeSymbol("");
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
        if (mScreenReceiver != null)
            unregisterReceiver(mScreenReceiver);
        if (mTradeIndexPresenterCompl != null)
            mTradeIndexPresenterCompl.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mTradeIndexPresenterCompl != null) {
            mTradeIndexPresenterCompl.subscribeSymbol(nextSymbole);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.networkErrorRefreshButton1:
                if (!isNetWorkAvalid()) {
                    return;
                }
                if (mTradeIndexPresenterCompl == null) {
                    mTradeIndexPresenterCompl = new TradeIndexPresenterCompl(this);
                }
                showPopupLoading(cpbNetworkError);
                mTradeIndexPresenterCompl.connectToMinaServer();
                cpbNetworkError.startLoadingAnimation();
                break;
            case R.id.ll_trade_type:
                break;
            case R.id.ll_trade_product:
                break;
            case R.id.ll_trade:
                break;
            case R.id.ll_my_account:
                startActivity(new Intent(this, MyAccountActivity.class));
                break;
            case R.id.ll_trade_record:
                startActivity(new Intent(this, TradeRecordActivity.class));
                break;
            case R.id.topActionButton1:
                if (llBottomTab.isShown()) {
                    llBottomTab.setVisibility(View.GONE);
                    topActionBar.setIvLeftVisiblility(View.GONE);
                    topActionBar.setBtnRightText("编辑");
                    topActionBar.setTitle("我喜爱的产品");

                    mTradeIndexPresenterCompl.subscribleFavorSymbole(this);

                    realTimeAdapter.setIsFavorShow(true);
                    realTimeAdapter.notifyDataSetChanged();
                } else {
                    if (topActionBar.getBtnRightText().equals("编辑")) {
                        realTimeAdapter.setEditAble(true);
                        topActionBar.setBtnRightText("完成");
                    } else if (topActionBar.getBtnRightText().equals("完成")) {
                        realTimeAdapter.setEditAble(false);
                        topActionBar.setBtnRightText("编辑");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!llBottomTab.isShown()) {
                switchRealtimeSurface();
                return false;
            }

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 却换到实时界面
     */
    private void switchRealtimeSurface() {
//        topActionBar.setIvLeftVisiblility(View.VISIBLE);
        topActionBar.setTitle("交易");
        topActionBar.setBtnRightText("我喜爱的产品");
        llBottomTab.setVisibility(View.VISIBLE);

        mTradeIndexPresenterCompl.subscribleAllSymbole();

        realTimeAdapter.setIsFavorShow(false);
        realTimeAdapter.setEditAble(false);
    }

    private ScreenBroadcastReceiver mScreenReceiver;

    /**
     * 接收锁屏广播
     */
    private void startScreenBroadcastReceiver(Handler handlerRead) {
        mScreenReceiver = new ScreenBroadcastReceiver(this, handlerRead, cpbNetworkError);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setRealTimeData(int position) {
        //得到第一个可显示控件的位置，
        int visiblePosition = lvRealTime.getFirstVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (position - visiblePosition >= 0) {
            //得到要更新的item的view
            View view = lvRealTime.getChildAt(position - visiblePosition);
            //调用adapter更新界面
            realTimeAdapter.updateView(view, position);
        }
    }
}
