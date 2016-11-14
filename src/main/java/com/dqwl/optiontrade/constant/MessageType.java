package com.dqwl.optiontrade.constant;

/**
 * @author xjunda
 * @date 2016-07-26
 */
public interface MessageType {
    int TYPE_BINARY_HEART_BEAT_REQUEST = 1;
    int TYPE_BINARY_HEART_BEAT_RESPONSE = 2;
    int TYPE_BINARY_LOGIN_RESULT = 11;
    int TYPE_BINARY_SERVER_TIME = 281;
    int TYPE_BINARY_HISTORY_RESULT = 290;
    int TYPE_BINARY_TRADE_NOTIFY = 300;//下单成功订单情况
    int TYPE_BINARY_ACTIVE_ORDER_LIST = 310;
    int TYPE_BINARY_SYMBOLE_SHOW = 320;
    int TYPE_BINARY_REAL_TIME_LIST = 1000;
    int TYPE_BINARY_HISTORY_LIST = 1101;
    int TYPE_BINARY_ORDER_RESPONSE = 211;
    int TYPE_BINARY_USER_INFO = 1300;
    int TYPE_BINARY_ALL_SYMBOL = 1201;
    String TYPE_ORDER_SREVER_TIME="{\"msg_type\":280}";
}
