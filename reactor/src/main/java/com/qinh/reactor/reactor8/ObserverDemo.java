package com.qinh.reactor.reactor8;

import java.util.Observable;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-19-21:21
 */
public class ObserverDemo extends Observable {

    public static void main(String[] args) {
        ObserverDemo observer = new ObserverDemo();
        //添加观察者
        observer.addObserver((o,arg) -> {
            System.out.println("发送变化");
        });
        observer.addObserver((o,arg) -> {
            System.out.println("收到被观察者通知，准备改变");
        });
        //数据变化
        observer.setChanged();
        //通知
        observer.notifyObservers();
    }
}
