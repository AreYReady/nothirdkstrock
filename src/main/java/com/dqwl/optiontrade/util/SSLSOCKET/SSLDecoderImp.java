package com.dqwl.optiontrade.util.SSLSOCKET;

import android.util.Log;

import com.dqwl.optiontrade.bean.BeanOrderRecord;
import com.dqwl.optiontrade.bean.BeanOrderResponse;
import com.dqwl.optiontrade.bean.BeanServerTime;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.bean.BeanUserInfo;
import com.dqwl.optiontrade.bean.DataEvent;
import com.dqwl.optiontrade.bean.EventBusAllSymbol;
import com.dqwl.optiontrade.bean.RealTimeDataList;
import com.dqwl.optiontrade.bean.ResponseEvent;
import com.dqwl.optiontrade.constant.MessageType;
import com.dqwl.optiontrade.util.SocketUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;

/**
 * @author xjunda
 * @date 2016-09-01
 */
public class SSLDecoderImp implements Decoder<String> {

    public static final String HEART_BEAT = "HEART_BEAT";
    private String TAG="hsc";

    @Override
    public String decode(ByteBuffer in) throws DecoderException {
        String result = null;
        if (in.remaining() > 4) {//前4字节是包头
            //标记当前position的快照标记mark，以便后继的reset操作能恢复position位置
            in.mark();
            byte[] l = new byte[4];
            in.get(l);

            //包体数据长度
            int len = SocketUtil.byteArrayToInt(l);//将byte转成int
            if(len>300)
            Log.i("123", "lennnnnnnnnnnnnfffffff: " + len);
            //注意上面的get操作会导致下面的remaining()值发生变化
            if (in.remaining() < len) {
                //如果消息内容不够，则重置恢复position位置到操作前,进入下一轮, 接收新数据，以拼凑成完整数据
                Log.i("123", "remaininffffffff: " + in.remaining());
                in.reset();
                return null;
            } else {
                //消息内容足够
                in.reset();//重置恢复position位置到操作前
                int sumlen = 4 + len;//总长 = 包头+包体
                byte[] packArr = new byte[sumlen];
                in.get(packArr, 0, sumlen);
                Log.i("123", "decode: fffff" + in.remaining());
//                if (in.remaining() - packArr.length > 0) {//如果读取一个完整包内容后还粘了包，就让父类再调用一次，进行下一次解析
//                    result = SocketUtil.handReceviceData(packArr);
//                    Log.i("123", "decode:dddddd " + result);
//                    return null;
//                }else {
                    if(sumlen > 300)
                        Log.i("123", "decodefffff: " + result);
                    result = SocketUtil.handReceviceData(packArr);
                    handleResult(result);
                    Log.i("123", "decode: " + result);
//                }
            }
        }
        return result;//处理成功，让父类进行接收下个包
    }

    private void handleResult(String resultMessage){
        ResponseEvent responseEvent = new Gson().fromJson(resultMessage, ResponseEvent.class);
        if (responseEvent != null) {
            switch (responseEvent.getMsg_type()) {
                case MessageType.TYPE_BINARY_LOGIN_RESULT://登录结果信息
                    EventBus.getDefault().post(responseEvent);
                    Log.i(TAG, "handleResult: 登入"+resultMessage);
                    break;
                case MessageType.TYPE_BINARY_ALL_SYMBOL://所有产品列表
                    Log.i(TAG, "handleResult:所有产品列表=  "+resultMessage);
                    EventBusAllSymbol allSymbol = new Gson().fromJson(resultMessage, EventBusAllSymbol.class);
                    EventBus.getDefault().post(allSymbol);
                    break;
                case MessageType.TYPE_BINARY_SYMBOLE_SHOW://要展示的产品
                    BeanSymbolConfig symbolShow = new Gson().fromJson(resultMessage, BeanSymbolConfig.class);
                    Log.i(TAG, "handleResult:要展示的产品=  "+resultMessage);
                    EventBus.getDefault().postSticky(symbolShow);
                    break;
                case MessageType.TYPE_BINARY_ACTIVE_ORDER_LIST://进行中订单
                    Log.i(TAG, "handleResult:进行中订单=  "+resultMessage);
                    BeanOrderRecord orderRecord = new Gson().fromJson(resultMessage, BeanOrderRecord.class);
                    EventBus.getDefault().postSticky(orderRecord);
                    break;
                case MessageType.TYPE_BINARY_HISTORY_RESULT://历史已完成订单
                    EventBus.getDefault().postSticky(new DataEvent(resultMessage, MessageType.TYPE_BINARY_HISTORY_RESULT));
                    Log.i(TAG, "handleResult:历史已完成订单=  "+resultMessage);
                    break;
                case MessageType.TYPE_BINARY_TRADE_NOTIFY://订单通知结果
                    EventBus.getDefault().post(new DataEvent(resultMessage, MessageType.TYPE_BINARY_TRADE_NOTIFY));
                    Log.i(TAG, "handleResult:订单通知结果=  "+resultMessage);
                    break;
                case MessageType.TYPE_BINARY_REAL_TIME_LIST://发送实时数据
                    RealTimeDataList realTimeDataList = new Gson().fromJson(resultMessage, RealTimeDataList.class);
                    Log.i(TAG, "handleResult:发送实时数据=  "+resultMessage);
                    EventBus.getDefault().post(realTimeDataList);
                    break;
                case MessageType.TYPE_BINARY_HISTORY_LIST://发送历史数据，画图
                    Log.i(TAG, "handleResult:发送历史数据，画图=  "+resultMessage);
                    EventBus.getDefault().post(new DataEvent(resultMessage, MessageType.TYPE_BINARY_HISTORY_LIST));
                    Log.i("123", "handleResult: historyffffff");
                    break;
                case MessageType.TYPE_BINARY_ORDER_RESPONSE:
                    Log.i(TAG, "handleResult:TYPE_BINARY_ORDER_RESPONSE=  "+resultMessage);
                    BeanOrderResponse orderResponse = new Gson().fromJson(resultMessage, BeanOrderResponse.class);
                    EventBus.getDefault().post(orderResponse);
                    break;
                case MessageType.TYPE_BINARY_HEART_BEAT_REQUEST://心跳请求，每30秒服务器请求客服端一次
                    Log.i(TAG, "handleResult:心跳请求，每30秒服务器请求客服端一次=  "+resultMessage);
                    EventBus.getDefault().post(HEART_BEAT);
                    break;
                case MessageType.TYPE_BINARY_HEART_BEAT_RESPONSE://心跳响应
                    Log.i(TAG, "handleResult:心跳响应=  "+resultMessage);
//                    EventBus.getDefault().post(new BeanMinaSessionEvent(session));
                    break;
                case MessageType.TYPE_BINARY_USER_INFO://用户信息
                    Log.i(TAG, "handleResult:用户信息=  "+resultMessage);
                    BeanUserInfo userInfo = new Gson().fromJson(resultMessage, BeanUserInfo.class);
                    EventBus.getDefault().postSticky(userInfo);
                    break;
                case MessageType.TYPE_BINARY_SERVER_TIME://服务器时间
                    Log.i(TAG, "handleResult:服务器时间=  "+resultMessage);
                    BeanServerTime serverTime = new Gson().fromJson(resultMessage, BeanServerTime.class);
                    EventBus.getDefault().post(serverTime);
                    break;
            }
        }
    }
}
