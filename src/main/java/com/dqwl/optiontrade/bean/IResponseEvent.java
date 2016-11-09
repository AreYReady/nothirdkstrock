package com.dqwl.optiontrade.bean;

/**
 * Created by jimdaxu on 16/8/21.
 */
public interface IResponseEvent{
    public int getMsg_type();
    public int getResult_code();
    public boolean checkLoginResult();
    }
