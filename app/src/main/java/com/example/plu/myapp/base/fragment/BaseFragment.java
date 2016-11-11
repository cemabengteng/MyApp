package com.example.plu.myapp.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plu.myapp.base.rx.RxFragment;

import butterknife.ButterKnife;

/**
 * Created by chengXing on 2016/9/14.
 */
public abstract class BaseFragment extends RxFragment {
    public Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View v = getInflateView();
        ButterKnife.bind(v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected abstract void initListener();

    protected abstract void initData();

    public abstract View getInflateView();

}
