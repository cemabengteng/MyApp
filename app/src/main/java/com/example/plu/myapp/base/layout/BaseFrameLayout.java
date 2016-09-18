package com.example.plu.myapp.base.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by chengXing on 2016/9/18.
 */
public abstract class BaseFrameLayout extends RxFrameLayout {
    public Context mContext;
    private View mRootView;

    public BaseFrameLayout(Context context) {
        this(context, null);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttris(context, attrs, defStyleAttr);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        mContext = getContext();
        initData();
        initView();
        initListener();
    }

    protected abstract void initListener();

    protected void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(getLayout(), this, true);
        ButterKnife.bind(mRootView);
    }

    protected abstract int getLayout();

    protected abstract void initData();

    protected abstract void handleAttris(Context context, AttributeSet attrs, int attr);


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(mRootView);
    }
}
