package com.example.plu.myapp.base.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.plu.myapp.rx.rxlifecycle.LayoutEvent;
import com.example.plu.myapp.rx.rxlifecycle.LayoutLifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.subjects.BehaviorSubject;

/**
 * Created by chengXing on 2016/11/11.
 */

public class RxRelativeLayout extends RelativeLayout implements LayoutLifecycleProvider {
    private final BehaviorSubject<LayoutEvent> lifecycleSubject = BehaviorSubject.create();

    public RxRelativeLayout(Context context) {
        super(context);
    }

    public RxRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RxRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LayoutEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
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
