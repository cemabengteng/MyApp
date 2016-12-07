package com.example.view.StripPagerTabLayout;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuj on 2016/2/22.
 *
 * PagerAdapter with CustomTab
 */
public interface CustomTabPagerAdapter {

    View getCustomTab(int pos, ViewGroup tabContainer);

    int getCount();
}
