package com.dqwl.optiontrade.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.application.SysApplication;
import com.dqwl.optiontrade.util.SystemUtil;


/**
 * 所有界面的基类，一些公共的属性、方法写在这里<br />
 *
 * @author xjunda
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected AlertDialog dialogLoading;
    protected PopupWindow popupWindowLoading;
    private Toast mToast;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 这里对Activity作统一设置，如设置标题、是否全屏等
        SysApplication.getInstance().addActivity(this);
        initIntentData();
        initViewAndLsnr();
        initRegister();
        initData();
    }

    /**
     * 初始化上个页面传过来的数据
     */
    protected abstract void initIntentData();

    /**
     * 设置布局id，并且初始化视图和监听事件
     */
    protected abstract void initViewAndLsnr();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化广播，监听
     */
    protected abstract void initRegister() ;


    /**
     * 显示一个Toast类型的消息
     *
     * @param msg 显示的消息
     */
    protected void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * 显示{@link Toast}通知
     *
     * @param strResId 字符串资源id
     */
    protected void showToast(int strResId) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(strResId);
        mToast.show();
    }

    /**
     * 显示{@link Toast}
     *
     * @param msg     消息
     * @param gravity 方位
     * @param xOffset x偏移量
     * @param yOffset y偏移量
     */
    public void showToast(String msg, int gravity, int xOffset, int yOffset) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * 显示{@link Toast}
     *
     * @param strResId 字符串资源id
     * @param gravity  方位
     * @param xOffset  x偏移量
     * @param yOffset  y偏移量
     */
    public void showToast(int strResId, int gravity, int xOffset, int yOffset) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(strResId);
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.show();
    }

    /**
     * 显示一个普通类型的对话框
     *
     * @param title 标题
     * @param msg   显示的消息
     */
    public void showNormalDialog(String title, String msg) {
        Dialog mDialog = new Dialog(this);
        mDialog.setTitle(title);
        TextView txtMsg = new TextView(this);
        txtMsg.setText(msg);
        txtMsg.setPadding(10, 10, 10, 10);
        mDialog.setContentView(txtMsg);
        mDialog.show();
    }


    /**
     * 隐藏输入键盘
     */
    public void hideSoftInput() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(
                        this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示加载中界面
     */
//    protected void showDialogLoading() {
//        View popView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
//        dialogLoading = new AlertDialog.Builder(this, R.style.LodingDialog)
//                .setView(popView)
//                .create();
//        dialogLoading.show();
//        dialogLoading.setCanceledOnTouchOutside(false);
//    }

    /**
     * 显示加载中界面
     *
     * @param view 相对view
     */
    public void showPopupLoading(View view) {
        if(popupWindowLoading==null){
            View popView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
            popupWindowLoading = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindowLoading.setOutsideTouchable(false);
            popupWindowLoading.setFocusable(false);
            popupWindowLoading.setBackgroundDrawable(new BitmapDrawable());
        }
        popupWindowLoading.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    protected boolean isNetWorkAvalid(){
        if(!SystemUtil.isAvalidNetSetting(this)){
            showToast(R.string.check_your_network);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().delActivity(this);
        if (popupWindowLoading != null)
            popupWindowLoading.dismiss();
    }
}
