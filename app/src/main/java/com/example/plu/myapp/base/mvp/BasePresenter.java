package com.example.plu.myapp.base.mvp;

import android.content.Context;

import com.example.domain.repository.usercase.BaseUserCase;
import com.example.plu.myapp.dagger.provider.PresenterProvider;
import com.example.plu.myapp.rx.rxlifecycle.LayoutLifecycleProvider;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

/**
 * Created by chengXing on 2016/9/13.
 */
public class BasePresenter<V extends MvpView> extends MvpBasePresenter<V> {

    private final Object[] mObjects;
    private final Context mContext;
    private final ActivityLifecycleProvider mActivityLifecycleProvider;
    private final FragmentLifecycleProvider mFragmentLifecycleProvider;
    private final LayoutLifecycleProvider mLayoutLifecycleProvider;

    public BasePresenter(PresenterProvider presenterProvider, Object ...objects) {
        this.mObjects = objects;
        mContext = presenterProvider.getContext();
        mActivityLifecycleProvider = presenterProvider.getActivityLifecycleProvider();
        mFragmentLifecycleProvider = presenterProvider.getFragmentLifecycleProvider();
        mLayoutLifecycleProvider = presenterProvider.getLayoutLifecycleProvider();
        if (isViewAttached()){
            return;
        }
        if (mActivityLifecycleProvider != null){
            attachView((V)mActivityLifecycleProvider);
        }else if (mFragmentLifecycleProvider != null){
            attachView((V)mFragmentLifecycleProvider);
        }else if (mLayoutLifecycleProvider != null){
            attachView((V)mLayoutLifecycleProvider);
        }
    }


    public Object getProvide(){
        if (mActivityLifecycleProvider != null){
            return mActivityLifecycleProvider;
        }else if (mFragmentLifecycleProvider != null){
            return mFragmentLifecycleProvider;
        }else if (mLayoutLifecycleProvider != null){
            return mLayoutLifecycleProvider;
        }
        return null;
    }

    @Override
    public void detachView() {
        releaseUserCase();
        unsubscribeSubscription();
        super.detachView();
    }

    private void unsubscribeSubscription() {

    }

    private void releaseUserCase() {
        if (mObjects == null || mObjects.length == 0){
            return;
        }
        for (Object o : mObjects){
            if (o == null){
                continue;
            }
            if (o instanceof BaseUserCase){
                ((BaseUserCase) o).release();
            }
            o = null;
        }
    }

}
