package com.example.plu.myapp.dagger.moudle;

import android.support.v4.app.Fragment;

import dagger.Module;

/**
 * Created by plu on 2016/8/29.
 */
@Module
public class FragmentModule {

    private final Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        this.mFragment = fragment;
    }

}
