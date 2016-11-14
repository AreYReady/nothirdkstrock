package com.dqwl.optiontrade.mvp.time_chart_show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.adapter.EndTimeAdapter;
import com.dqwl.optiontrade.adapter.TradeTitleAdapter;
import com.dqwl.optiontrade.base.BaseActivity;
import com.dqwl.optiontrade.bean.BeanHistoryRequest;
import com.dqwl.optiontrade.bean.BeanOrderResponse;
import com.dqwl.optiontrade.bean.BeanOrderResult;
import com.dqwl.optiontrade.bean.BeanServerTime;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.BeanUserInfo;
import com.dqwl.optiontrade.bean.DataEvent;
import com.dqwl.optiontrade.bean.HistoryData;
import com.dqwl.optiontrade.bean.HistoryDataList;
import com.dqwl.optiontrade.bean.RealTimeDataList;
import com.dqwl.optiontrade.constant.MessageType;
import com.dqwl.optiontrade.handler.HandlerSend;
import com.dqwl.optiontrade.mvp.login.presenter.LoginPresenterCompl;
import com.dqwl.optiontrade.mvp.time_chart_show.presenter.ITimeChartShowPresenter;
import com.dqwl.optiontrade.mvp.time_chart_show.presenter.TimeChartShowPresenterCompl;
import com.dqwl.optiontrade.mvp.time_chart_show.view.ITimeChartShowView;
import com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity;
import com.dqwl.optiontrade.mvp.trade_record.TradeRecordActivity;
import com.dqwl.optiontrade.util.ChartUtils;
import com.dqwl.optiontrade.util.DensityUtil;
import com.dqwl.optiontrade.util.MoneyUtil;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.dqwl.optiontrade.util.SoundManager;
import com.dqwl.optiontrade.util.SystemUtil;
import com.dqwl.optiontrade.util.TimeUtils;
import com.dqwl.optiontrade.widget.BigProgressBar;
import com.dqwl.optiontrade.widget.CustomProgressBar;
import com.dqwl.optiontrade.widget.LinearLineWrapLayout;
import com.dqwl.optiontrade.widget.MultiDirectionSlidingDrawer;
import com.dqwl.optiontrade.widget.NowPriceLine;
import com.dqwl.optiontrade.widget.SmallProgressBar;
import com.dqwl.optiontrade.widget.TimeChart;
import com.dqwl.optiontrade.widget.TopActionBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity.activeOrder;
import static com.dqwl.optiontrade.util.MoneyUtil.addPrice;

public class MinaTimeChartActivity extends BaseActivity implements View.OnClickListener,ITimeChartShowView {
    /**
     * 进度条指示器高度
     */
    private static final int IMAGE_INDECATOR_HEIGHT = 24;
    private String TAG=SystemUtil.getTAG(this.getClass());
    /**
     * 从服务器获取多少个数据，默认为60个
     */
    private static final int COUNTS = 60;
    private static final int NOT_COMPLETE_ORDER = 999;
    public static final String SYMBOL = "symbol";
    private TimeChart timeChart;
    private RealTimeDataList preRealTimeDataList;//上一次实时数据，用来判断背景色
    private ArrayList<BeanSymbolConfig.SymbolsBean> allSubscribeSymbols;//当前的所有产品
    private int symbolPosition;//产品索引
    private LinearLayout llNetworkErrorTip;//网络连接失败提示布局
    private CustomProgressBar cpbNetworkError;//网络重新连接进图条
    private TopActionBar topActionBar;
    /**
     * 却换图表按钮
     */
    private ImageView ivSwichChart;
    private TextView tvServerTime;
    private TextView tvBailMoney;
    private TextView tvEndTime;
    private TextView tvRealTimePrice;
    private TextView tvMoneyWin;
    private CustomProgressBar cpbarForTimeChart;//中间时间图表的进度环
    private FrameLayout flUp;
    private RelativeLayout rlContain;//下单倒计时父级总容器
    private ScrollView svContain;//下单倒计时父容器
    private LinearLayout llContain;//下单倒计时的容器
    private LinearLineWrapLayout mLineWrapLayout;
    private TextView tvUserName, tvUserID, tvMoneyLeft, tvMoneyLeftHander;//用户名，用户ID， 剩余金额， 手柄显示的剩余金额
    private TextView tvRealTimePricePopProgress;
    private LinkedList<SmallProgressBar> smallProgressBars = new LinkedList<>();
    private TextView tvPopupTop;//顶部textview，无用
    private PopupWindow popupProgressbars;//大倒计时弹出菜单
    private LinearLayout popupcontain;//弹出窗口顶级容器
    /**
     * viewpager标题适配器
     */
    private TradeTitleAdapter tradeTitleAdapter;
    /**
     * 弹出框的小进度条容器
     */
    private LinearLayout llContainPopProgressbar;
    private BigProgressBar bigProgressBar;//大进度条
    private BigProgressbarRunnable bigProgressRunnable;//大进度条定时
    private ViewPager vpTradeSymbol;
    /**
     * 下单成功后，确认按钮和取消按钮
     */
    private Button btnConfirmOrder, btnCancellOrder;
    private String historyRequestStr;//订阅历史数据
    private TextView tvSymbolBigProgress, tvOpenPriceBigProgress, tvMoneyBigProgress, tvWinMoneyBigProgress;
    /**
     * 下单弹出的popupwindow
     */
    private PopupWindow popupOrder;
    private TextView tvExpiredTimePop;//弹出框到期时间
    private ImageView ivTradeSucessPop;//交易成功图标
    private CustomProgressBar customProgressBarPopOrder;//下单中弹出窗口进度条
    /**
     * 下单中确认、取消按钮布局容器,倒计时确认布局
     */
    private View btnPopup, viewOrderCountDownConfirmPopOrder;
    private TextView tvMoneyPopOrder, tvSymbolPopOrder, tvMoneyWinPopOrder, tvOpenPricePopOrder,
            tvOrderStatusPopOrder, tvOrderCofirmTimeLeftPopOrder;
    private ImageView ivOpenDirectionPopOrder;

    private TextView tvNowPrice;
    private MultiDirectionSlidingDrawer slidingDrawer;//侧拉账户信息
    /**
     * 下单确认倒计时
     */
    private int timeLeft = 3;//
    private TimeLeftPopOrder timeLeftPopOrder;//下单倒计时确认线程
    /**
     * 到期时间选择后的最大选择后的最大刻度，是历史数据period的60倍，单位秒
     */
    private int maxDegreeSecond = 60;

    /**
     * 收益率
     */
    private int percent;

    /**
     * 到期时间
     */
    private String endTime = "60秒";
    /**
     * 发送给服务器的到期时间字符串默认为1分钟
     */
    private String endTimeToServer = "m1";

    /**
     * 时间周期，暂定为60秒
     */
    private int period = 60;

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

    /**
     * 到期时间对话框
     */
    private AlertDialog dialogEndtime;
    /**
     * 保证金对话框
     */
    private AlertDialog dialogBailMoney;
    /**
     * 保证金列表
     */
//    private String bailMoneys[];

    /**
     * 保证金列表
     */
    private ArrayList<Integer> bailMoneyList;
    /**
     * 第一次进来，实时报价的前一个价格
     */
    private double firstPrice;
    /**
     * 2为绿色，1为红色,0为gray
     */
    private int flag = 0;//

    private ITimeChartShowPresenter mTimeChartShowPresenter;

    /**
     * 最后一个开盘价，最后一个收盘价，最后一个最小价，最后一个最大价
     */
    private Map<String, String> lastOpenPrice = new Hashtable<>(),
            lastClosePrice = new HashMap<>(),
            lastMinPrice = new HashMap<>(),
            lastMaxPrice = new HashMap<>();
    /**
     * 最后一个价格的时间，毫秒
     */
    private long lastTime;

    /**
     * 保存第一次的最后一个价格的时间，毫秒，用来算经历了几次
     */
    private long firstLastTime;

    /**
     * 历史数据
     */
    private Map<String,HistoryDataList> historyDataList = new Hashtable<>();
    /**
     * 下单方向0，看跌，1看涨，-1取消
     */
    private int direction;

    /**
     * 进度条经历的时间差
     */
    private long timeOffset;

//    private HistoryRunable mHistoryRunable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(SystemUtil.getTAG(this.getClass()), "onCreate: 执行了");
    }

    @Override
    protected void initIntentData() {
        timeOffset = getIntent().getLongExtra(TradeIndexActivity.TIMEOFFSET, 0);
        allSubscribeSymbols = (ArrayList<BeanSymbolConfig.SymbolsBean>) getIntent().getSerializableExtra(TradeIndexActivity.SYMBOL);
        symbolPosition = getIntent().getIntExtra(TradeIndexActivity.POSITION, 0);
//        getIntent().getSerializableExtra(LoginActivity.ACTIVEORDER);
//        bailMoneys = getResources().getStringArray(R.array.bail_money);
        vol_max = getIntent().getIntExtra(TradeIndexActivity.VOL_MAX, 0);
        vol_min = getIntent().getIntExtra(TradeIndexActivity.VOL_MIN, 0);
        step_min = getIntent().getIntExtra(TradeIndexActivity.STEP_MIN, 0);
        bailMoneyList = new ArrayList<>();
        SoundManager.initSoundManager(this);
    }

    @Override
    protected void initViewAndLsnr() {
        setContentView(R.layout.activity_mina_time_chart);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置屏幕常亮
        topActionBar = (TopActionBar) findViewById(R.id.top_action_bar);
        tvServerTime = (TextView) findViewById(R.id.tv_server_time);
        ivSwichChart = (ImageView) findViewById(R.id.iv_switch_chart);
        llNetworkErrorTip = (LinearLayout) findViewById(R.id.networkErrorContainer1);
        ImageButton ivReConnet = (ImageButton) findViewById(R.id.networkErrorRefreshButton1);
        cpbNetworkError = (CustomProgressBar) findViewById(R.id.cpb_network_error);
        tvPopupTop = (TextView) findViewById(R.id.tv_popup_top);
        timeChart = (TimeChart) findViewById(R.id.view_time_chart);
        ImageButton ivSubMoney = (ImageButton) findViewById(R.id.iv_sub_money);
        ImageButton ivAddMoney = (ImageButton) findViewById(R.id.iv_add_money);
        ImageButton ivEndTime = (ImageButton) findViewById(R.id.iv_end_time);
        tvBailMoney = (TextView) findViewById(R.id.tv_money);
        tvEndTime = (TextView) findViewById(R.id.tv_time_end);
        tvRealTimePrice = (TextView) findViewById(R.id.tv_real_time_price);
        tvMoneyWin = (TextView) findViewById(R.id.tv_money_win);
        TextView tvMoneyLose = (TextView) findViewById(R.id.tv_money_lose);
        cpbarForTimeChart = (CustomProgressBar) findViewById(R.id.cpbar);
        flUp = (FrameLayout) findViewById(R.id.fl_down_order);
        FrameLayout flDown = (FrameLayout) findViewById(R.id.fl_up_order);
        svContain = (ScrollView) findViewById(R.id.sv_contain);
        rlContain = (RelativeLayout) findViewById(R.id.rl_contain);
        mLineWrapLayout = (LinearLineWrapLayout) findViewById(R.id.ll_wrap_layout);
        vpTradeSymbol = (ViewPager) findViewById(R.id.productsPager);
        tvUserName = (TextView) findViewById(R.id.sliderUserName);
        tvUserID = (TextView) findViewById(R.id.userSliderAccountNumber1);
        tvMoneyLeft = (TextView) findViewById(R.id.userSliderAccountBalance1);
        tvMoneyLeftHander = (TextView) findViewById(R.id.sliderHandleBalance);
        slidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.myUserInfoSlider);
        ivSubMoney.setOnClickListener(this);
        ivAddMoney.setOnClickListener(this);
        ivEndTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        flUp.setOnClickListener(this);
        flDown.setOnClickListener(this);
        slidingDrawer.setOnDrawerCloseListener(new MultiDirectionSlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                tvMoneyLeftHander.setVisibility(View.VISIBLE);
            }
        });
        slidingDrawer.setOnDrawerOpenListener(new MultiDirectionSlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                tvMoneyLeftHander.setVisibility(View.INVISIBLE);
            }
        });
        ivReConnet.setOnClickListener(this);
        if (!SystemUtil.isAvalidNetSetting(this)) {
            llNetworkErrorTip.setVisibility(View.VISIBLE);
        }
        topActionBar.setBtnClickLsnr(this);
        ivSwichChart.setOnClickListener(this);
        cpbNetworkError.stopLoadingAnimation();
        cpbarForTimeChart.stopLoadingAnimation();
        tvBailMoney.setTag(0);//初始化保证金位置
        tvBailMoney.setOnClickListener(this);
        tvBailMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String bailMoneyStr = tvBailMoney.getText().toString().substring(1);
                double bailMoney = Double.valueOf(bailMoneyStr);
                tvMoneyWin.setText((bailMoney*percent/100) +"");
            }
        });
        tvNowPrice = ((NowPriceLine)timeChart.getChildAt(0)).getNowPriceTextview();
        initOrerPopup();
    }

    @Override
    protected void initData() {
//        mTimeChartShowPresenter
    }

    @Override
    protected void initRegister() {
        EventBus.getDefault().register(this);
        initViewPager();
    }

    @Subscribe(sticky = true)
    public void onGetAllSymbol(ArrayList<BeanSymbolConfig.SymbolsBean> symbolsBean){
        allSubscribeSymbols = symbolsBean;
        if(tradeTitleAdapter!=null){
            tradeTitleAdapter.notifyDataSetChanged();
        }
        initCycle();
    }
    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        tradeTitleAdapter = new TradeTitleAdapter(this, allSubscribeSymbols);
        vpTradeSymbol.setAdapter(tradeTitleAdapter);
        vpTradeSymbol.setCurrentItem(symbolPosition, false);
        vpTradeSymbol.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tradeTitleAdapter.initPercnet();
            }

            @Override
            public void onPageSelected(int position) {
                String symbol = allSubscribeSymbols.get(position).getSymbol();
                tvPopupTop.setText(symbol);
                for (SmallProgressBar smallProgressBar : smallProgressBars) {
                    if (smallProgressBar.getOrder().getSymbol().equalsIgnoreCase(symbol)) {
                        ((View) smallProgressBar.getParent()).setVisibility(View.VISIBLE);
                    } else {
                        ((View) smallProgressBar.getParent()).setVisibility(View.GONE);
                    }
                }
                checkChartCache(position, false);
                symbolPosition = position;
                initCycle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class HistoryRunable implements Runnable{
        private String histroyRequest;

        public HistoryRunable(String histroyRequest) {
            this.histroyRequest = histroyRequest;
        }

        @Override
        public void run() {
            mTimeChartShowPresenter.writeHistroyRequestToServer(histroyRequest);
            vpTradeSymbol.postDelayed(this, period*1000);
        }
    }

    /**
     * 初始化到期时间，派息比例，最大刻度
     */
    private void initCycle() {
        BeanSymbolConfig.SymbolsBean.CyclesBean cyclesBean
                = allSubscribeSymbols.get(symbolPosition).getCycles().get(0);
        maxDegreeSecond = cyclesBean.getCycle();
        percent = cyclesBean.getPercent();
        endTime = cyclesBean.getDesc();
        tvEndTime.setText(endTime);
        String bailMoneyStr = tvBailMoney.getText().toString().substring(1);
        double bailMoney = Double.valueOf(bailMoneyStr);
        tvMoneyWin.setText((bailMoney*percent/100) +"");

        BeanSymbolConfig.SymbolsBean symbolsBean = allSubscribeSymbols.get(symbolPosition);
        if(symbolsBean.getVol_min()>0){
            vol_min = symbolsBean.getVol_min();
        }else if(vol_min<=0){
            vol_min = 10;//如果服务端全局和局部都没有配置，就用10
        }
        if(symbolsBean.getVol_max()>0){
            vol_max = symbolsBean.getVol_max();
        }else if(vol_max<=0){//服务端全局和局部都没有配置
            vol_max = 1000;
        }
        if(symbolsBean.getStep_min()>0){
            step_min = symbolsBean.getStep_min();
        }else if(step_min<=0){//服务端全局和局部都没有配置
            step_min = 10;
        }
        bailMoneyList.clear();
        for (int i = vol_min; i <= vol_max; i+=step_min) {
            bailMoneyList.add(i);
        }
        if(bailMoneyList.get(bailMoneyList.size()-1)<vol_max){
            bailMoneyList.add(vol_max);
        }
        tvBailMoney.setText("$" + vol_min);
        tvBailMoney.setTag(0);
    }

    /**
     * 检查是否有缓存，并且加载，没有则发送请求给服务器
     * @param position
     * @param flag     是否最大值，最小值变化
     */
    public void checkChartCache(int position, boolean flag) {
        BeanHistoryRequest cache = new BeanHistoryRequest(allSubscribeSymbols.get(position).getSymbol());
        HistoryDataList nowDataList = null;
        if(mTimeChartShowPresenter!=null)
        nowDataList = mTimeChartShowPresenter.isHistoryCache(cache, maxDegreeSecond);
        BeanHistoryRequest historyRequest = new BeanHistoryRequest
                (allSubscribeSymbols.get(position).getSymbol(), COUNTS);
        historyRequestStr = new Gson().toJson(historyRequest, BeanHistoryRequest.class);
        String symbol = allSubscribeSymbols.get(symbolPosition).getSymbol();
        if (nowDataList != null) {
                mTimeChartShowPresenter.subscribeSymbols(this, symbol,
                        allSubscribeSymbols.get(position).getSymbol());
            cpbarForTimeChart.stopLoadingAnimation();
            drawTimeChart(nowDataList);
            if (Double.valueOf(nowDataList.getNowPrice()) <= 0) {
                setFirstNowPrice(nowDataList);
            } else {
                timeChart.showNowPrice(Double.valueOf(nowDataList.getNowPrice()), nowDataList.getPrice(), nowDataList.getFlag());
                setRealTimeText();
                setRealTimeTvBackground(nowDataList.getFlag());
                if(flag){
                    timeChart.postInvalidate();
                }
            }
        } else {
            timeChart.getChildAt(0).setVisibility(View.INVISIBLE);
            timeChart.setData(null, null, ivSwichChart.isSelected());
                mTimeChartShowPresenter.subscribeSymbols(this, symbol,
                        allSubscribeSymbols.get(position).getSymbol());
                cpbarForTimeChart.startLoadingAnimation();
            mTimeChartShowPresenter.writeHistroyRequestToServer(historyRequestStr);
//            if(mHistoryRunable!=null) {
//                vpTradeSymbol.removeCallbacks(mHistoryRunable);
//            }
//            mHistoryRunable = new HistoryRunable(historyRequestStr);
//            vpTradeSymbol.post(mHistoryRunable);
        }
    }

    /**
     * 根据历史数据算出最大最小值，并且重绘
     * @param nowDataList
     */
    private void drawTimeChart(HistoryDataList nowDataList) {
        double[] price;//最高价[1]，最低价[0]
        int digits = nowDataList.getDigits();//当前页面产品的小数位
        String[] data = nowDataList.getItems()
                .get(nowDataList.getCount() - 1)
                .getO().split("\\|");
        String symbol = nowDataList.getSymbol();
        lastOpenPrice.put(symbol, new BigDecimal(data[0]).movePointLeft(digits).toString());
        lastClosePrice.put(symbol, MoneyUtil.addPriceString(data[0], data[3], digits));
        lastMinPrice.put(symbol, MoneyUtil.addPriceString(data[0], data[2], digits));
        lastMaxPrice.put(symbol, MoneyUtil.addPriceString(data[0], data[1], digits));
//        lastTime = (nowDataList.getItems().get(0).getT()
//                + nowDataList.getItems().get(nowDataList.getCount()-1).getT())*1000;

        price = ChartUtils.calcMaxMinPrice(nowDataList, digits);
        timeChart.setData(nowDataList, price, ivSwichChart.isSelected());
        nowDataList.setPrice(price);
        mTimeChartShowPresenter.saveCache(nowDataList);
    }

    /**
     * 设置实时文本价格，限制长度和实时价格一样
     */
    private void setRealTimeText(){
//        int maxLenth = SystemUtil.getMaxLength(tvNowPrice);
        String price = tvNowPrice.getText().toString();
//        if(price.length()>maxLenth){//超过7位则截取前7位
//            tvRealTimePrice.setText(MoneyUtil.getRealTimePriceTextBig(this,price.substring(0,maxLenth)));
//        }else {
            tvRealTimePrice.setText(MoneyUtil.getRealTimePriceTextBig(this,price));
//        }
    }

    /**
     * nowprice还没有实时数据时，先计算最近一次的nowprice
     *
     * @param nowDataList
     */
    private void setFirstNowPrice(HistoryDataList nowDataList) {
        double[] tempNowPrcie = mTimeChartShowPresenter.getTempNowPrice(nowDataList);
        setRealTimeTextBackGround(tempNowPrcie[0], tempNowPrcie[1], nowDataList.getPrice());
    }


    /**
     * 如果没有实时价格，则获取历史价格中的最后两个，并且比较
     * @return 0最后一个价格，1倒数第二个价格
     */
    public double[] getTempNowPrice(double[] nowPrice) {
        return nowPrice;
    }

    @Override
    public void setBadgeNum(int num) {
        topActionBar.setBadgeNum(num);
    }

    /**
     * 新下单增加，下单结果出现减少角标
     *
     * @param orderAction
     */
    @Subscribe
    public void onOrderAction(Integer orderAction) {
        topActionBar.addBadgeNum(orderAction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeOut(String timeout){
        if(timeout.equalsIgnoreCase(SSLSocketChannel.TIMEOUT)){
            if(popupWindowLoading!=null)
                popupWindowLoading.dismiss();
            showToast(R.string.net_or_server_error);
            llNetworkErrorTip.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(sticky = true, priority = 1, threadMode = ThreadMode.MAIN)
    public void onGetSSLSocketChannel(Handler handlerRead) {//获取session操作和检查缓存订阅历史数据
        Log.i(TAG, "onGetSSLSocketChannel: ccccccccc");
        if (!SystemUtil.isAvalidNetSetting(this)) {
            return;
        }
        if(((HandlerSend)handlerRead).getSSLSocketChannel()==null){
            return;
        }
        llNetworkErrorTip.setVisibility(View.INVISIBLE);
        cpbNetworkError.stopLoadingAnimation();
//        if(mScreenReceiver==null){
//            startScreenBroadcastReceiver(handlerRead);
//        }else {
//            mScreenReceiver.setHandler(handlerRead);//原来的session已经关掉，要指向新的session
//        }
        if (mTimeChartShowPresenter == null) {
            mTimeChartShowPresenter = new TimeChartShowPresenterCompl(this, handlerRead);
        } else {
            mTimeChartShowPresenter.setHandler(handlerRead);
        }

        mTimeChartShowPresenter.getServerTime();

//        mTimeChartShowPresenter.getHistoyCache(this);
        mTimeChartShowPresenter.getUsrInfo(this);
        checkChartCache(symbolPosition,false);
        mTimeChartShowPresenter.getActiveOrder(
                activeOrder, timeOffset, smallProgressBars.size()>0);//大于0的话，说明已经处理过了。或者没有退出该页面
        if(popupWindowLoading!=null){
            popupWindowLoading.dismiss();
        }
    }
    private CountDownTimer countDownTimer;
    private long time;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getServerTime(BeanServerTime serverTime){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        tvServerTime.setVisibility(View.VISIBLE);
        time = TimeUtils.getOrderStartTime(serverTime.getTime());
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long l) {
                time += 1000l;
                tvServerTime.setText(TimeUtils.getXTime(time/1000));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawRealTimeData(RealTimeDataList realTimeData) {//绘制实时数据
        String symbol = allSubscribeSymbols.get(symbolPosition).getSymbol();
        if (realTimeData != null && realTimeData.getQuotes() != null){
            for (RealTimeDataList.BeanRealTime realTime : realTimeData.getQuotes()){
                if(symbol.equalsIgnoreCase(realTime.getSymbol())){
                    String realTimeSymbol =  realTime.getSymbol();
                    double nowPrice = realTime.getBid();
                    long nowTime = TimeUtils
                            .getOrderStartTime(realTime.getTime());//和last一样标准时区
                    Log.i("123", "onDrawRealTimeData: now" + realTime.getTime() + realTimeSymbol);
                    String o;
                    HistoryDataList dataList = historyDataList.get(realTimeSymbol);//当前的历史数据
                    if(dataList==null)
                        return;
                    int digits = dataList.getDigits();
                    long firstTime = dataList.getItems().get(0).getT();//秒
                    long lastTimeHistory = (dataList.getItems().get(0).getT()
                            + dataList.getItems().get(dataList.getCount()-1).getT())*1000;
                    Log.i("123", "onDrawRealTimeData: history" + TimeUtils.getShowTime(lastTimeHistory) + realTimeSymbol);
                    if(nowTime - lastTimeHistory >= period*1000){//说明当前实时价格超过最后一个时间点，暂时定为一分钟
                        //最后一个的时间，有可能不止一个周期，中间有可能没有数据
                        int timeDelay = (int) ((nowTime - lastTimeHistory)/period/1000);
                        Log.i("123", "onDrawRealTimeData: " + timeDelay);
                        long lastTime = dataList.getItems().get(dataList.getCount()-1).getT() + period*timeDelay;
                        dataList.getItems().remove(0);//移除第一个数据，即整个view向左移动
                        dataList.getItems().get(0).setT(firstTime + period*timeDelay);
//                        lastTime += period*1000;//暂时为60秒
//                        long t = lastTime/1000 - dataList.getItems().get(0).getT();//秒
                        lastOpenPrice.put(realTimeSymbol, nowPrice+"");
                        lastMaxPrice.put(realTimeSymbol, nowPrice+"");
                        lastMinPrice.put(realTimeSymbol, nowPrice+"");
                        lastClosePrice.put(realTimeSymbol, nowPrice+"");
                        o = new BigDecimal(nowPrice+"").movePointRight(digits).intValue()
                                + "|0|0|0";
                        HistoryData data = new HistoryData(o, lastTime);
                        dataList.getItems().add(data);
                        HistoryData historyData1 = dataList.getItems().get(1);//判断第二个数据和第一个的间隔时间是否在一个周期内
                        long time1 = historyData1.getT() - period;
                        if (time1 > period) {
                            for (int i = 1; i < dataList.getCount(); i++) {
                                HistoryData historyData = dataList.getItems().get(i);
                                historyData.setT(period * i);
                            }
                        } else {
                            for (int i = 1; i < dataList.getCount(); i++) {
                                HistoryData historyData = dataList.getItems().get(i);
                                long time = historyData.getT() - period;
                                if (time > period * i) {
                                    historyData.setT(time);
                                } else {
                                    historyData.setT(period * i);
                                }
                            }
                        }

                        Log.i("123", "lastData: " + nowPrice);
                        Log.i("123", "lastData: " + new Gson().toJson(data, HistoryData.class));
                        timeChart.invalidate();
                    }else {//绘制最后一个数据
                        lastClosePrice.put(realTimeSymbol, nowPrice+"");
                        String openPrice = lastOpenPrice.get(realTimeSymbol);
                        String maxPrice = lastMaxPrice.get(realTimeSymbol);
                        String minPrice = lastMinPrice.get(realTimeSymbol);
                        String closePrice = lastClosePrice.get(realTimeSymbol);
                        BigDecimal nowDecimal = new BigDecimal(nowPrice+"");
                        BigDecimal maxDecimal = new BigDecimal(maxPrice+"");
                        BigDecimal minDecimal = new BigDecimal(minPrice + "");
                        if(nowDecimal.compareTo(maxDecimal)==1){
                            lastMaxPrice.put(realTimeSymbol, nowPrice+"");
                        }else if(nowDecimal.compareTo(minDecimal)==-1){
                            lastMinPrice.put(realTimeSymbol, nowPrice+"");
                        }
                        long lastTime = dataList.getItems().get(dataList.getCount()-1).getT();
                        dataList.getItems()//完整的写入最后一个数据
                                .remove(dataList.getItems().get(dataList.getCount()-1));
                        o = new BigDecimal(openPrice+"").movePointRight(digits).intValue()
                                + "|" + new BigDecimal(MoneyUtil.subPrice(lastMaxPrice.get(realTimeSymbol),openPrice))
                                .movePointRight(digits).intValue()
                                + "|" + new BigDecimal(MoneyUtil.subPrice(lastMinPrice.get(realTimeSymbol), openPrice))
                                .movePointRight(digits).intValue()
                                + "|" + new BigDecimal(MoneyUtil.subPrice(lastClosePrice.get(realTimeSymbol),openPrice))
                                .movePointRight(digits).intValue();
                        HistoryData data = new HistoryData(o, lastTime);
                        dataList.getItems().add(data);
                        Log.i("123", "lastData: " + new Gson().toJson(data, HistoryData.class));
                    }
                    if (preRealTimeDataList != null && preRealTimeDataList.getQuotes() != null) {
                        firstPrice = preRealTimeDataList.getQuotes().get(0).getBid();
                        for (SmallProgressBar progressBar : smallProgressBars) {//进度条绘制，指定颜色
                            if (realTimeData.getQuotes().get(0).getSymbol()
                                    .equalsIgnoreCase(progressBar.getOrder().getSymbol())) {
                                double orderPrice = progressBar.getOrder().getOpen_price();
                                progressBar.getOrder().setRealTime(true);
                                progressBar.getOrder().setOpen_price(orderPrice);
                                progressBar.getOrder().setClose_price(nowPrice);
                                if (nowPrice == orderPrice) {
                                    progressBar.setPaintColor(3);
                                } else if (nowPrice > orderPrice) {
                                    progressBar.setPaintColor(2);
                                } else if (nowPrice < orderPrice) {
                                    progressBar.setPaintColor(1);
                                }
                            }
                        }
                    }

                    setRealTimeText();
                    if (popupProgressbars != null && popupProgressbars.isShowing()) {
                        tvRealTimePricePopProgress.setText(MoneyUtil
                                .getRealTimePriceTextBig(this, tvNowPrice.getText().toString()));
                    }
                    preRealTimeDataList = realTimeData;
                    BeanHistoryRequest cache = new BeanHistoryRequest(
                            allSubscribeSymbols.get(symbolPosition).getSymbol());
                    historyDataList.put(realTimeSymbol, dataList);
                    mTimeChartShowPresenter.saveRealTimeCache(cache, tvNowPrice.getText().toString(), flag
                            ,nowPrice, symbolPosition, firstPrice);
                }
            }
        }

    }

    /**
     * 比较前后价格，算出背景色
     *
     * @param nowPrice 当前价格
     * @param prePrice 前一个价格
     */
    public void setRealTimeTextBackGround(double nowPrice, double prePrice, double[] price) {
        if (nowPrice > prePrice) {
            flag = 2;
        } else if (nowPrice < prePrice) {
            flag = 1;
        } else {
            flag = 3;
        }
        setRealTimeTvBackground(flag);
        timeChart.showNowPrice(nowPrice, price, flag);
        setRealTimeText();
    }


    /**
     * 设置实时文本的背景
     * @param flag
     */
    private void setRealTimeTvBackground(int flag) {
        switch (flag) {
            case 1:
                tvRealTimePrice.setBackgroundResource(R.drawable.bg_green_price);
                if (popupProgressbars != null && popupProgressbars.isShowing()) {
                    tvRealTimePricePopProgress.setBackgroundResource(R.drawable.bg_green_price);
                }
                break;
            case 2:
                tvRealTimePrice.setBackgroundResource(R.drawable.bg_red_price);
                if (popupProgressbars != null && popupProgressbars.isShowing()) {
                    tvRealTimePricePopProgress.setBackgroundResource(R.drawable.bg_red_price);
                }
                break;
            case 3:
                tvRealTimePrice.setBackgroundResource(R.drawable.bg_gray_price);
                if (popupProgressbars != null && popupProgressbars.isShowing()) {
                    tvRealTimePricePopProgress.setBackgroundResource(R.drawable.bg_gray_price);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawHistroyData(DataEvent dataEvent) {
        Log.i(TAG, "onDrawHistroyData: fffff");
        HistoryDataList dataList;
        if (dataEvent.getType() == MessageType.TYPE_BINARY_HISTORY_LIST) {//绘制历史数据
            dataList = new Gson().fromJson(dataEvent.getResult(), new TypeToken<HistoryDataList>() {
            }.getType());
            historyDataList.put(dataList.getSymbol(), dataList);
            drawTimeChart(dataList);

            String[] data = dataList.getItems()
                    .get(dataList.getCount() - 1)
                    .getO().split("\\|");

            firstPrice = addPrice(data[0], data[3], dataList.getDigits());
            tvPopupTop.setText(dataList.getSymbol());

            setFirstNowPrice(dataList);

            cpbarForTimeChart.stopLoadingAnimation();

            //画完历史数据后才画实时数据，并且隐藏刷新环
            mTimeChartShowPresenter.writeRealTimeRequstToServer(allSubscribeSymbols.get(symbolPosition).getSymbol());
            Log.i("123", "onDrawHistroyData: " + symbolPosition);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetUserInfo(BeanUserInfo userInfo) {
        tvUserName.setText(userInfo.getName());
        tvUserID.setText("账户ID " + userInfo.getLogin());
        tvMoneyLeft.setText("$" + userInfo.getBalance());
        tvMoneyLeftHander.setText("$" + userInfo.getBalance());
        tvMoneyLeftHander.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisconnectFromServer(String result) {
        if (result.equalsIgnoreCase(LoginPresenterCompl.DISCONNECT_FROM_SERVER)) {
            llNetworkErrorTip.setVisibility(View.VISIBLE);
            cpbNetworkError.stopLoadingAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.i(TAG, "onDestroy: activeOrder.size"+TradeIndexActivity.activeOrder.size());
        if(mTimeChartShowPresenter!=null) {
            mTimeChartShowPresenter.onDestroy(this);
        }
        SoundManager.releaseResources();
//        if(mScreenReceiver!=null)
//        unregisterReceiver(mScreenReceiver);
        if (popupOrder != null) {
            popupOrder.dismiss();
        }
        if (popupProgressbars != null) {
            popupProgressbars.dismiss();
        }
        if(popupWindowLoading!=null){
            popupWindowLoading.dismiss();
        }
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
//        if(mHistoryRunable!=null){
//            vpTradeSymbol.removeCallbacks(mHistoryRunable);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topActionButton1:
                startActivityForResult(new Intent(this, TradeRecordActivity.class)
                        .putExtra(SYMBOL, allSubscribeSymbols.get(symbolPosition).getSymbol()), NOT_COMPLETE_ORDER);
                activeOrder.size();
                EventBus.getDefault().postSticky(smallProgressBars);//传递当前的进度条
                break;
            case R.id.iv_switch_chart:
                if (ivSwichChart.isSelected()) {
                    ivSwichChart.setSelected(false);
                } else {
                    ivSwichChart.setSelected(true);
                }
                checkChartCache(symbolPosition, false);
                break;
            case R.id.networkErrorRefreshButton1:
                if(!isNetWorkAvalid())
                    return;
                if(mTimeChartShowPresenter==null){
                    mTimeChartShowPresenter = new TimeChartShowPresenterCompl(this);
                }
                showPopupLoading(cpbNetworkError);
                mTimeChartShowPresenter.connectToMinaServer();
                cpbNetworkError.startLoadingAnimation();
                break;
            case R.id.tv_money:
                showBailMoneyDialog();
                break;
            case R.id.iv_sub_money:
            case R.id.iv_add_money:
                int position = (int) tvBailMoney.getTag();
                if(v.getId()==R.id.iv_sub_money){
                    position--;
                }else {
                    position++;
                }
                if(position>bailMoneyList.size()-1){
                    position = bailMoneyList.size()-1;
                }else if(position<0){
                    position = 0;
                }
                tvBailMoney.setText("$" + bailMoneyList.get(position));
                tvBailMoney.setTag(position);
                break;
            case R.id.tv_time_end:
            case R.id.iv_end_time:
                showEndTimeListDialog();
                break;
            case R.id.fl_up_order:
                direction = 1;
                ivOpenDirectionPopOrder.setImageResource(R.mipmap.call_small_icon);
                showPopupOrder();
                break;
            case R.id.fl_down_order:
                direction = 0;
                ivOpenDirectionPopOrder.setImageResource(R.mipmap.put_small_icon);
                showPopupOrder();
                break;
            case R.id.cancelButton1:
                direction = -1;
                popupOrder.dismiss();
//                btnConfirmOrder.setTag(null);
//                EventBus.getDefault().post(-1);
//                llContain.removeViewAt(0);`
//                smallProgressBars.remove(smallProgressBars.size()-1);
            case R.id.approveButton1:
//                confirmOrder();
                popupOrder.dismiss();
                break;
            default:
                break;
        }
    }

    private void showBailMoneyDialog() {
        if(dialogBailMoney == null){
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_bail_money_pickup, null);
            ListView lvBailMoney = (ListView) view.findViewById(R.id.lv_bail_money);
            ArrayAdapter<Integer> lvBailMoneyAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, bailMoneyList);
            lvBailMoney.setAdapter(lvBailMoneyAdapter);
            dialogBailMoney = new AlertDialog.Builder(this)
                    .setView(view).create();
            dialogBailMoney.setCanceledOnTouchOutside(true);
            lvBailMoney.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tvBailMoney.setText("$" + bailMoneyList.get(position));
                    tvBailMoney.setTag(position);
                    dialogBailMoney.dismiss();
                }
            });
        }
        dialogBailMoney.show();
    }

    /**
     * 显示到期时间选择对话框
     */
    private void showEndTimeListDialog() {
//            final String[] endTimes = getResources().getStringArray(R.array.end_time);
//            final String[] endTimeToServers = getResources().getStringArray(R.array.end_time_to_server);
//            final int[] maxDegrees = getResources().getIntArray(R.array.end_time_second);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_end_time_pickup, null);
            ListView lvEndTime = (ListView) view.findViewById(R.id.lv_end_time_select);
            final List<BeanSymbolConfig.SymbolsBean.CyclesBean> data = allSubscribeSymbols.get(symbolPosition).getCycles();
            EndTimeAdapter endTimeAdapter =
                    new EndTimeAdapter(this, data);
            lvEndTime.setAdapter(endTimeAdapter);
            dialogEndtime = new AlertDialog.Builder(this)
                    .setView(view).create();
            dialogEndtime.setCanceledOnTouchOutside(true);
            lvEndTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BeanSymbolConfig.SymbolsBean.CyclesBean cyclesBean = data.get(position);
//                    endTimeToServer = cyclesBean.get;
                    percent = cyclesBean.getPercent();
                    maxDegreeSecond =cyclesBean.getCycle();
                    endTime = cyclesBean.getDesc();
                    tvEndTime.setText(endTime);
//                    checkChartCache(symbolPosition, fals
                    dialogEndtime.dismiss();
                    tradeTitleAdapter.setPercentPostion(symbolPosition, position);
                }
            });
        dialogEndtime.show();
    }

    /**
     * 下单，弹出窗口按确定后下单，或者3秒后自动下单
     */
    private void placeOrder() {
        if(direction<0){
            return;
        }
        if (!SystemUtil.isAvalidNetSetting(this)) {
            showToast(R.string.check_your_network);
            return;
        }

        double money = Double.valueOf(tvBailMoney.getText().toString().substring(1));
        if(mTimeChartShowPresenter!=null){
            mTimeChartShowPresenter.writeOrderToServer(allSubscribeSymbols.get(symbolPosition).getSymbol(),
                    direction, money, maxDegreeSecond, percent);
            cpbarForTimeChart.startLoadingAnimation();
        }
    }

    /**
     * 显示订单对话框
     */
    private void showPopupOrder() {
        SoundManager.playCallPutSound();
        setConfirmOrderMessage();
//        customProgressBarPopOrder.startLoadingAnimation();
        tvOpenPricePopOrder.setText(tvRealTimePrice.getText().toString());
        tvMoneyPopOrder.setText(tvBailMoney.getText().toString());
        tvSymbolPopOrder.setText(allSubscribeSymbols.get(symbolPosition).getSymbol());
        tvMoneyWinPopOrder.setText("$" + tvMoneyWin.getText().toString());
        tvOrderStatusPopOrder.setText(R.string.confirm_order);
        popupOrder.showAtLocation(flUp, Gravity.CENTER, 0, 0);
    }

    /**
     * 初始化下单弹出框
     */
    private void initOrerPopup() {
        View view = LayoutInflater.from(this).inflate(R.layout.confirmation_dialog_layout, null);
        tvExpiredTimePop = (TextView) view.findViewById(R.id.titleValueView1);
        tvMoneyPopOrder = (TextView) view.findViewById(R.id.titleValueView2);
        tvSymbolPopOrder = (TextView) view.findViewById(R.id.titleValueView3);
        tvMoneyWinPopOrder = (TextView) view.findViewById(R.id.titleValueView4);
        tvOpenPricePopOrder = (TextView) view.findViewById(R.id.titleValueView5);
        tvOrderStatusPopOrder = (TextView) view.findViewById(R.id.messageView1);
        tvOrderCofirmTimeLeftPopOrder = (TextView) view.findViewById(R.id.counterView1);
        viewOrderCountDownConfirmPopOrder = view.findViewById(R.id.countDownTimerContainer);
        customProgressBarPopOrder = (CustomProgressBar) view.findViewById(R.id.customProgressBar3);
        ivOpenDirectionPopOrder = (ImageView) view.findViewById(R.id.optionIconView1);
        ivTradeSucessPop = (ImageView) view.findViewById(R.id.messageIconView1);
        btnConfirmOrder = (Button) view.findViewById(R.id.approveButton1);
        btnCancellOrder = (Button) view.findViewById(R.id.cancelButton1);
        btnPopup = view.findViewById(R.id.bottomContainer);
        popupOrder = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupOrder.setOutsideTouchable(false);
        popupOrder.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupOrder.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                confirmOrder();
                viewOrderCountDownConfirmPopOrder.setVisibility(View.INVISIBLE);
                tvExpiredTimePop.setText("");
                tvOrderStatusPopOrder.setText(R.string.in_processing);
                btnPopup.setVisibility(View.INVISIBLE);
                rlContain.removeCallbacks(timeLeftPopOrder);
                timeLeft = 3;
                placeOrder();
            }
        });
        btnConfirmOrder.setOnClickListener(this);
        btnCancellOrder.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderResult(DataEvent dataEvent) {
        if (dataEvent.getType() == MessageType.TYPE_BINARY_TRADE_NOTIFY) {
            BeanOrderResult orderResult = new Gson().fromJson(dataEvent.getResult(), BeanOrderResult.class);
            if (orderResult.getResult() > 0) {
                //有结果数据，下单结果
                for (final SmallProgressBar progressBar : smallProgressBars) {
                    BeanOrderResult order = progressBar.getOrder();
                    if (order.getTicket() == orderResult.getTicket()) {
                        switch (orderResult.getResult()) {// TODO: 2016-08-09 0,1,2代表含义
                            case 3:
                                break;
                            case 2:
                                SoundManager.playWonSound();
                                break;
                            case 1:
                                SoundManager.playLostSound();
                                break;
                            default:
                                break;
                        }
                        progressBar.setDegress(-1, orderResult.getResult());
                        mLineWrapLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLineWrapLayout.removeView((View) progressBar.getParent());
                                smallProgressBars.remove(progressBar);
                            }
                        }, 1000);
                        activeOrder.remove(order.getTicket());
                        EventBus.getDefault().post(-1);//下单结果后角标-1
                    }
                }
            } else {//下单成功后添加进度条，并且写入sp，没有result就是刚下单成功后返回的下单详情
                activeOrder.put(orderResult.getTicket(), orderResult);
                EventBus.getDefault().post(1);//下单结果后角标+1
                addProgressBar(orderResult, orderResult.getTime_span() - (3 - timeLeft));
                String orderMoney = MoneyUtil.addPrice(tvMoneyLeft.getText().toString().substring(1)
                        , tvBailMoney.getText().toString().substring(1));
            }
        }
    }

    /**
     * 添加进度圆环
     *
     * @param orderResult
     */
    public void addProgressBar(BeanOrderResult orderResult, final int progeress) {
        final SmallProgressBar smallProgressBar = new SmallProgressBar(this, orderResult.getTime_span(), orderResult);
        smallProgressBar.setDegress(progeress, smallProgressBar.getPaintColor());
        smallProgressBar.post(new Runnable() {
            @Override
            public void run() {
                float progress = smallProgressBar.getmDegree();
                if (progress <= 0) {
                    return;
                }
                smallProgressBar.setDegress(progress - 1, smallProgressBar.getPaintColor());
                smallProgressBar.postDelayed(this, 1000);
            }
        });
        clearAllFocus();
        final LinearLayout llProgressbarContainer = new LinearLayout(this);
        llProgressbarContainer.setOrientation(LinearLayout.VERTICAL);
        llProgressbarContainer.addView(smallProgressBar);
        final ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.sixty_seconds_white_arrow);
        llProgressbarContainer.addView(imageView, 0);
        int position = 0;
        for (int i = 0; i < smallProgressBars.size(); i++) {
            if(orderResult.getTime_span()>=smallProgressBars.get(i).getOrder().getTime_span()){
                position = i;
                break;
            }
            position = smallProgressBars.size();
        }
        mLineWrapLayout.addView(llProgressbarContainer,position);
        if (popupProgressbars != null && popupProgressbars.isShowing()) {
            tvSymbolBigProgress.setText(smallProgressBar.getOrder().getSymbol());
            tvOpenPriceBigProgress.setText(smallProgressBar.getOrder().getOpen_price() + "");
            tvMoneyBigProgress.setText("$" + smallProgressBar.getOrder().getMoney() + "");
            tvWinMoneyBigProgress.setText("$" + smallProgressBar.getOrder().getMoney() * smallProgressBar.getOrder().getCommision_level() / 100);
            bigProgressBar.setMaxDegree(smallProgressBar.getMaxDegree());
            bigProgressBar.setmDegree(smallProgressBar.getmDegree());
            bigProgressBar.removeCallbacks(bigProgressRunnable);
            bigProgressRunnable = new BigProgressbarRunnable(smallProgressBar);
            bigProgressBar.post(bigProgressRunnable);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
        smallProgressBars.add(position,smallProgressBar);
        llProgressbarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFocus();
                if (popupProgressbars != null && popupProgressbars.isShowing()) {
                    popupShowingProgress(smallProgressBar);
                } else {
                    showPopupProgressBar(smallProgressBar);
                }
                imageView.setVisibility(View.VISIBLE);
            }
        });
        String symbol = allSubscribeSymbols.get(symbolPosition).getSymbol();
        if (smallProgressBar.getOrder().getSymbol().equalsIgnoreCase(symbol)) {
            ((View) smallProgressBar.getParent()).setVisibility(View.VISIBLE);
        } else {
            ((View) smallProgressBar.getParent()).setVisibility(View.GONE);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 下单成功后设置倒计时3s
     */
    private void setConfirmOrderMessage() {
        if (timeLeftPopOrder == null) {//倒计时线程
            timeLeftPopOrder = new TimeLeftPopOrder();
        }
        timeLeft = 3;
//        long startTime = TimeUtils.getOrderStartTimeNoTimeZone(orderResult.getOpen_time());
//        tvExpiredTimePop.setText(TimeUtils.getShowTimeNoTimeZone(startTime + orderResult.getTime_span() * 1000));
//        tvOpenPricePopOrder.setText(orderResult.getOpen_price() + "");

        tvExpiredTimePop.setText(endTime);
        tvOpenPricePopOrder.setText(tvNowPrice.getText().toString());
//        customProgressBarPopOrder.stopLoadingAnimation();
        ivTradeSucessPop.setVisibility(View.VISIBLE);
        btnPopup.setVisibility(View.VISIBLE);
        tvOrderStatusPopOrder.setText(R.string.order_sucess);
        viewOrderCountDownConfirmPopOrder.setVisibility(View.VISIBLE);
        rlContain.post(timeLeftPopOrder);
    }

    /**
     * 重置，清除所有焦点
     */
    private void clearAllFocus() {
        for (int i = 0; i < mLineWrapLayout.getChildCount(); i++) {
            ((LinearLayout) mLineWrapLayout.getChildAt(i)).getChildAt(0).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 下单反馈 0为成功，其他为失败
     * @param orderResponse
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderResponse(BeanOrderResponse orderResponse) {//下单响应结果
        if (orderResponse.getResult_code() == 0) {
        } else {
            showToast(orderResponse.getError_reason());
        }
        cpbarForTimeChart.stopLoadingAnimation();
    }

    private void popupShowingProgress(SmallProgressBar smallProgressBar) {
        if (bigProgressRunnable != null &&//点击不同的才再重新设置大进度条
                bigProgressRunnable.getProgressBar().getOrder().getTicket() !=
                        smallProgressBar.getOrder().getTicket()) {
            setBigProgress(smallProgressBar);
        }
    }

    private void showPopupProgressBar(final SmallProgressBar progressBar) {
        if (popupProgressbars == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.popup_progress_bars, null);
            popupcontain = (LinearLayout) view.findViewById(R.id.ll_popup_contain);
            llContainPopProgressbar = (LinearLayout) view.findViewById(R.id.ll_contain);
            bigProgressBar = (BigProgressBar) popupcontain.findViewById(R.id.bigprogressbar);
            tvRealTimePricePopProgress = (TextView) popupcontain.findViewById(R.id.tv_real_time_price);
            FrameLayout flUp, flDown;
            flUp = (FrameLayout) popupcontain.findViewById(R.id.fl_down_order);
            flDown = (FrameLayout) popupcontain.findViewById(R.id.fl_up_order);
            flUp.setOnClickListener(this);
            flDown.setOnClickListener(this);
            popupProgressbars = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            popupProgressbars.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                    llContainPopProgressbar.removeView(svContain);
                    bigProgressBar.removeCallbacks(bigProgressRunnable);//关闭完之后要删除定时
                    rlContain.addView(svContain, 1);//要加在timechart後面，networkerror前面
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) svContain.getLayoutParams();
                    lp.setMargins(DensityUtil.dip2px(MinaTimeChartActivity.this, 30), 0,0,0);
                    svContain.setLayoutParams(lp);
//                    llContain.setTranslationY(-IMAGE_INDECATOR_HEIGHT);//窗口关闭时候，也要移动
                }
            });
        }

        tvRealTimePricePopProgress.setText(MoneyUtil.getRealTimePriceTextBig(this, tvRealTimePrice.getText().toString()));
        setBigProgress(progressBar);
        rlContain.removeView(svContain);
//        llContain.setTranslationY(0);//移动回原来位置
        llContainPopProgressbar.addView(svContain, 0);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);
        popupProgressbars.showAsDropDown(tvPopupTop, 0, 0);
    }

    private void setBigProgress(final SmallProgressBar progressBar) {
        if (tvSymbolBigProgress == null) {
            tvSymbolBigProgress = (TextView) popupcontain.findViewById(R.id.tv_symbol);
            tvOpenPriceBigProgress = (TextView) popupcontain.findViewById(R.id.tv_open_price);
            tvMoneyBigProgress = (TextView) popupcontain.findViewById(R.id.tv_money);
            tvWinMoneyBigProgress = (TextView) popupcontain.findViewById(R.id.tv_win_money);
        }
        tvSymbolBigProgress.setText(progressBar.getOrder().getSymbol());
        tvOpenPriceBigProgress.setText(progressBar.getOrder().getOpen_price() + "");
        tvMoneyBigProgress.setText("$" + progressBar.getOrder().getMoney() + "");
        tvWinMoneyBigProgress.setText("$" + progressBar.getOrder().getMoney() * progressBar.getOrder().getCommision_level() / 100);
        bigProgressBar.setMaxDegree(progressBar.getMaxDegree());
        bigProgressBar.setmDegree(progressBar.getmDegree());
        if (bigProgressRunnable != null &&
                bigProgressRunnable.getProgressBar().getOrder().getTicket() !=
                        progressBar.getOrder().getTicket()) {
            bigProgressBar.removeCallbacks(bigProgressRunnable);
            bigProgressRunnable = new BigProgressbarRunnable(progressBar);
            bigProgressBar.post(bigProgressRunnable);
        } else if (bigProgressRunnable == null) {
            bigProgressRunnable = new BigProgressbarRunnable(progressBar);
            bigProgressBar.post(bigProgressRunnable);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (popupProgressbars != null && popupProgressbars.isShowing()) {
                popupProgressbars.dismiss();
                return false;
            }
            if (popupOrder != null && popupOrder.isShowing()) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onDestroy: activeOrder.size"+TradeIndexActivity.activeOrder.size());
        if (requestCode == NOT_COMPLETE_ORDER) {//返回要重新设置角标，并且移除已经完成的订单
            for (final SmallProgressBar smallProgressBar : smallProgressBars) {
                if (smallProgressBar.getmDegree()
                        <= 0) {
                    mLineWrapLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activeOrder.remove(smallProgressBar.getOrder().getTicket());
                            mLineWrapLayout.removeView((View) smallProgressBar.getParent());
                            smallProgressBars.remove(smallProgressBar);
                            topActionBar.setBadgeNum(activeOrder.size());
                        }
                    }, 200);
                }
            }
        }
    }

    private class TimeLeftPopOrder implements Runnable {

        @Override
        public void run() {
            if (timeLeft > 0) {
                tvOrderCofirmTimeLeftPopOrder.setText("00:00:0" + timeLeft--);
                rlContain.postDelayed(this, 1000);
            } else {
                popupOrder.dismiss();
                rlContain.removeCallbacks(this);
                timeLeft = 3;
            }
        }
    }

    private class BigProgressbarRunnable implements Runnable {
        private SmallProgressBar progressBar;

        public BigProgressbarRunnable(SmallProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        public SmallProgressBar getProgressBar() {
            return progressBar;
        }

        @Override
        public void run() {
            float progress = bigProgressBar.getmDegree();
            bigProgressBar.setDegress(progressBar.getmDegree(), progressBar.getPaintColor());
            if (progress >= 0) {
                bigProgressBar.post(this);
            } else if (progress < 0) {
                bigProgressBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final int smallBarsNum = smallProgressBars.size();
                        SmallProgressBar smallProgressBarShow;
                        if (smallBarsNum > 0) {//如果还有订单，就从第一单开始再计时
                            if ((smallProgressBarShow = hasVisibleProgressBar()) != null) {
                                bigProgressBar.removeCallbacks(bigProgressRunnable);
                                ((LinearLayout) (smallProgressBarShow.getParent())).getChildAt(0).setVisibility(View.VISIBLE);
                                bigProgressBar.setMaxDegree(smallProgressBarShow.getMaxDegree());
                                bigProgressBar.setmDegree(smallProgressBarShow.getmDegree());
                                bigProgressRunnable = new BigProgressbarRunnable(smallProgressBarShow);
                                bigProgressBar.post(bigProgressRunnable);
                            } else {
                                popupProgressbars.dismiss();
                            }
                        } else {
                            popupProgressbars.dismiss();
                        }
                    }
                }, 1000);
            }
        }
    }

    /**
     * 判断是否还有可见的进度条，就是当前页面的进度条
     *
     * @return
     */
    private SmallProgressBar hasVisibleProgressBar() {
        for (SmallProgressBar smallProgressBar : smallProgressBars) {
            if (smallProgressBar.getOrder().getSymbol()
                    .equalsIgnoreCase(allSubscribeSymbols.get(symbolPosition).getSymbol())) {
                return smallProgressBar;
            }
        }
        return null;
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
}
