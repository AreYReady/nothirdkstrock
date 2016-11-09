package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-07-29
 * 我的收藏事件
 * @link TradeIndexActivity # onFavorEvent
 */
public class MyFavorEvent {
    private int plus;
    private BeanSymbolConfig.SymbolsBean symbol;

    public MyFavorEvent(int plus, BeanSymbolConfig.SymbolsBean symbol) {
        this.plus = plus;
        this.symbol = symbol;
    }

    public BeanSymbolConfig.SymbolsBean getSymbol() {
        return symbol;
    }

    public int getPlus() {
        return plus;
    }
}
