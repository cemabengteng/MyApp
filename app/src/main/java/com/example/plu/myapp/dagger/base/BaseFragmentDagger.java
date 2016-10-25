package com.example.plu.myapp.dagger.base;

import android.support.annotation.NonNull;

import com.example.plu.myapp.dagger.component.FragmentComponent;

/**
 * Created by chengXing on 2016/10/25.
 */

public interface BaseFragmentDagger<C extends BaseComponent> {
    C initComponent(@NonNull FragmentComponent component);
}
