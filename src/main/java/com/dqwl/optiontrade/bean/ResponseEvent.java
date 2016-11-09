package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-07-23
 * 相应包
 * @link LoginActivity # onLoginResult
 */
public class ResponseEvent implements IResponseEvent{

    /**
     * msg_type : 11
     * result_code : 0
     */

    private int msg_type;
    private int result_code;

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getResult_code() {
        return result_code;
    }

    @Override
    public boolean checkLoginResult() {
        return false;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

}
