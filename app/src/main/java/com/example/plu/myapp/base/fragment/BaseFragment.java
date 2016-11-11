package com.example.plu.myapp.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plu.myapp.base.rx.RxFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by chengXing on 2016/9/14.
 */
public abstract class BaseFragment extends RxFragment {
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View v = getInflateView();
        if (v == null) {
            v = View.inflate(mContext, getLayout(), null);
        }
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
    }

    protected <T extends Fragment> T findFragment(Class<T> fragmentClazz, String tag, Bundle bundle) {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(getActivity(), fragmentClazz.getName(), bundle);
        }
        return (T) fragment;
    }

    protected void hideFragment(String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
        }
    }

    protected void removeFragment(String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    protected abstract void initListener();

    protected abstract void initData();

    public abstract View getInflateView();

    @LayoutRes
    protected abstract int getLayout();

}
