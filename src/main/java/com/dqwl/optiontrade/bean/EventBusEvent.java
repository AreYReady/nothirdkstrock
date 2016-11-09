package com.dqwl.optiontrade.bean;

/**
 * @author xjunda
 * @date 2016-08-10
 * @link TradeIndexActivity # onRemoveAllFavors
 */
public class EventBusEvent {
    private String event;

    public EventBusEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
