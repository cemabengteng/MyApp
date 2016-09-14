package com.example.plu.myapp.base.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.plu.myapp.App;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.component.CommonFragmentComponent;
import com.example.plu.myapp.dagger.component.FragmentComponent;
import com.example.plu.myapp.dagger.moudle.FragmentModule;

/**
 * Created by chengXing on 2016/9/14.
 */
public class DaggerFragment<C extends BaseComponent> extends RxFragment {

    private C mComponent;
    private CommonFragmentComponent mCommonFragmentComponent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInject();
    }

    private void initInject() {
        FragmentComponent fragmentComponent = App.getInstance().getApplicationComponent().provideFragmentComponent(new FragmentModule(this));
        mComponent = initComponent(fragmentComponent);
    }

    public CommonFragmentComponent initCommon(){
        mCommonFragmentComponent = App.getInstance().getApplicationComponent().provideFragmentComponent(new FragmentModule(this)).provideCommonComponent();
        return mCommonFragmentComponent;
    }

    protected  C initComponent(@NonNull FragmentComponent component){
        return null;
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mComponent = null;
        mCommonFragmentComponent = null;
    }
}
