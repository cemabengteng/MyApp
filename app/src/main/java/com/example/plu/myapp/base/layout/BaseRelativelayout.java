package com.example.plu.myapp.base.layout;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by chengXing on 2016/11/11.
 */

public abstract class BaseRelativelayout extends RxRelativeLayout {
    protected Context mContext;
    protected View rootView;

    private boolean selfRelease;
    private boolean isRootView;

    public BaseRelativelayout(Context context) {
        super(context, null);
    }

    public BaseRelativelayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public BaseRelativelayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttrs(context, attrs, defStyleAttr);
        init();
    }

    protected void handleAttrs(Context context, AttributeSet attributeSet, int defStyleAttr) {

    }

    private void init() {
        mContext = getContext();
        initView();
        initData();
        initListener();
    }

    protected void initListener() {

    }

    protected void initData() {
    }

    protected void initView() {
        int resLayoutId = getLayout();
        if (resLayoutId != 0) {
            rootView = LayoutInflater.from(mContext).inflate(getLayout(), this, false);
        }
        if (rootView == null) {
            isRootView = true;
            ButterKnife.bind(this);
        } else if (rootView.getClass() == RelativeLayout.class) {
            //减少层级
            RelativeLayout relativeLayout = (RelativeLayout) rootView;
            List<View> childViewList = new ArrayList<>();
            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                View childView = relativeLayout.getChildAt(i);
                childViewList.add(childView);
            }
            ((RelativeLayout) rootView).removeAllViews();
            for (int i = 0; i < childViewList.size(); i++) {
                addView(childViewList.get(i));
            }
            isRootView = true;
            ButterKnife.bind(this);
        } else {
            addView(rootView);
            ButterKnife.bind(this, rootView);
        }
        EventBus.getDefault().register(this);
    }

    @LayoutRes
    protected abstract int getLayout();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isRootView) {
            ButterKnife.bind(this);
        } else {
            ButterKnife.bind(this, rootView);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!selfRelease) {
            release();
        }
        super.onDetachedFromWindow();
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    /**
     * 设置自己控制释放时机
     *
     * @param selfRelease
     */
    public void setSelfRelease(boolean selfRelease) {
        this.selfRelease = selfRelease;
    }
}
