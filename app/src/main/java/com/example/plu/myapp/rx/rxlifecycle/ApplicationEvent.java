package com.example.plu.myapp.rx.rxlifecycle;

/**
 * Created by chengXing on 2016/9/12.
 */
public enum  ApplicationEvent {
    ONCREATE,//创建的时候
    ONTERMINATE,//程序终止的时候,貌似无反应
    ONLOWMEMORY,//低内存的时候
    ONTRIMMENORY  //程序内存清理的时候
}
