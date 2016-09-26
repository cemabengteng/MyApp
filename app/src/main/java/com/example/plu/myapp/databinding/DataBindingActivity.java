package com.example.plu.myapp.databinding;

import android.os.Bundle;

import com.example.plu.myapp.base.activity.DaggerActivity;

/**
 * Created by ccharp on 16/9/25.
 */

public class DataBindingActivity extends DaggerActivity {
    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void initView() {
        com.example.plu.myapp.databinding.ContentMainBinding binding = com.example.plu.myapp.databinding.ContentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
