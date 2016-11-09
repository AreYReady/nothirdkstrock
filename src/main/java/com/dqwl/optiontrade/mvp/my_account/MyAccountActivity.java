package com.dqwl.optiontrade.mvp.my_account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.base.BaseActivity;
import com.dqwl.optiontrade.bean.BeanUserInfo;
import com.dqwl.optiontrade.mvp.trade_record.TradeRecordActivity;
import com.dqwl.optiontrade.widget.ItemListRow;
import com.dqwl.optiontrade.widget.TopActionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyAccountActivity extends BaseActivity implements View.OnClickListener {
    public static final String LOGIN_OUT = "LOGIN_OUT";
    private TextView tvUserName;
    private TextView tvUserId;
    private TextView tvMoneyLef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initIntentData() {

    }

    @Override
    protected void initViewAndLsnr() {
        setContentView(R.layout.activity_my_account);
        TopActionBar topActionBar = (TopActionBar) findViewById(R.id.top_action_bar);
        tvUserName = (TextView) findViewById(R.id.customerNameView1);
        tvUserId = (TextView) findViewById(R.id.accountNumView1);
        tvMoneyLef = (TextView) findViewById(R.id.balanceView1);
        Button btnBack = (Button) findViewById(R.id.backToTraidingBt1);
        ItemListRow itemTradeRecord = (ItemListRow) findViewById(R.id.item_trade_record);
        ItemListRow itemSetting = (ItemListRow) findViewById(R.id.item_setting);
        topActionBar.setBtnClickLsnr(this);
        itemTradeRecord.setOnClickListener(this);
        itemSetting.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initRegister() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true)
    public void onGetUserInfo(BeanUserInfo userInfo) {
        tvUserName.setText(userInfo.getName());
        tvUserId.setText("账户ID " + userInfo.getLogin());
        tvMoneyLef.setText("$" + userInfo.getBalance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topActionButton1:
                EventBus.getDefault().post(LOGIN_OUT);
                this.finish();
                break;
            case R.id.backToTraidingBt1:
                finish();
                break;
            case R.id.item_trade_record:
                startActivity(new Intent(this, TradeRecordActivity.class));
                break;
            case R.id.item_setting:
                showToast("功能开发中");
                break;
        }
    }
}
