package com.example.domain.repository.usercase;

import com.example.domain.repository.DataRepository;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chengXing on 2016/9/13.
 */
public abstract class BaseUserCase<T extends DataRepository> implements UserCase {
    private final T mDataRepository;
    private CompositeSubscription mCompositeSubscription;

    public BaseUserCase(T t) {
        this.mDataRepository = t;
        mCompositeSubscription = new CompositeSubscription();
    }

    protected void addSubscription(Subscription subscriptions){
        if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()){
            mCompositeSubscription = new CompositeSubscription();
            return;
        }
        mCompositeSubscription.add(subscriptions);
    }

    public void release(){
        if (mCompositeSubscription == null && !mCompositeSubscription.isUnsubscribed()){
            return;
        }
        mCompositeSubscription.unsubscribe();
        mCompositeSubscription = null;
    }
}
