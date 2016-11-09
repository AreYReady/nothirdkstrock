package com.dqwl.optiontrade.application;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public class SysApplication {
    private List<Activity> mList = new LinkedList();
    private static SysApplication instance;

    private SysApplication() {
    }
    public synchronized static SysApplication getInstance() { 
        if (null == instance) { 
            instance = new SysApplication(); 
        } 
        return instance; 
    } 
    // add Activity  
    public void addActivity(Activity activity) {
        mList.add(activity); 
    }
    public void delActivity(Activity activity) {
        mList.remove(activity);
    }
 
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null) 
                    activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    }
}
