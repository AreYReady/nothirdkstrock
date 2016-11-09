package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-08-06
 * 下单之后的响应结果
 * @link MinaTimeChartActivity # onOrderResponse
 */
public class BeanOrderResponse {

    /**
     * error_reason : 商品报价无效
     * msg_type : 211
     * result_code : 136
     */

    private String error_reason;
    private int msg_type;
    private int result_code;

    public String getError_reason() {
        return error_reason;
    }

    public void setError_reason(String error_reason) {
        this.error_reason = error_reason;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }
}
