package com.example.plu.myapp.base.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.plu.myapp.rx.rxlifecycle.LayoutEvent;
import com.example.plu.myapp.rx.rxlifecycle.LayoutLifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.subjects.BehaviorSubject;

/**
 * Created by chengXing on 2016/9/18.
 */
public class RxFrameLayout extends FrameLayout implements LayoutLifecycleProvider {

    private BehaviorSubject<LayoutEvent> lifecycleSubject = BehaviorSubject.create();

    public RxFrameLayout(Context context) {
        super(context);
    }

    public RxFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RxFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LayoutEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject,event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        lifecycleSubject.onNext(LayoutEvent.ON_ATTACHED_TO_WINDOW);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        lifecycleSubject.onNext(LayoutEvent.ON_DETACHED_FROM_WINDOW);
    }
}
