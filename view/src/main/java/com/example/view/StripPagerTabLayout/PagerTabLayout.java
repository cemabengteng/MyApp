package com.example.view.StripPagerTabLayout;

import android.support.v4.view.ViewPager;

/**
 * Created by liuj on 2016/2/22.
 */
public interface PagerTabLayout extends ViewPager.OnPageChangeListener {

    void setOnPageChangedListener(ViewPager.OnPageChangeListener listener);

    void setCurrentItem(int pos);

    void notifyDataSetChanged();

    void setViewPager(ViewPager viewPager);

}
