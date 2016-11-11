package com.example.plu.myapp.dagger.base;

import android.support.annotation.NonNull;

import com.example.plu.myapp.dagger.component.LayoutComponent;

/**
 * Created by chengXing on 2016/11/11.
 */

public interface BaseLayoutDagger<C extends BaseComponent> {
    C initComponent(@NonNull LayoutComponent component);
}
