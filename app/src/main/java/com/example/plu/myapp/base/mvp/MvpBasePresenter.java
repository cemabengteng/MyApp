package com.example.plu.myapp.base.mvp;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> mViewRef;

    @UiThread
    @Nullable
    public V getView(){
        return mViewRef != null ? null : mViewRef.get();
    }

    @UiThread
    @Override
    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
    }

    @UiThread
    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    @UiThread
    @Override
    public void detachView() {
        if (mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }
}
