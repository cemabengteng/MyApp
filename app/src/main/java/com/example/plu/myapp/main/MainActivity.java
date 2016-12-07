package com.example.plu.myapp.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.view.StripPagerTabLayout.GiftStripPagerTabLayout;
import com.example.view.StripPagerTabLayout.Tab;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> {

    @Inject
    MainPresenter mPresenter;
    @Bind(R.id.tab)
    GiftStripPagerTabLayout mTab;
    @Bind(R.id.list)
    RecyclerViewPager mGiftViewPager;

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initData(Bundle state) {
        final List<String> lists = new ArrayList<>();
        lists.add("标签一");
        lists.add("标签二");
        lists.add("标签三");
        lists.add("标签四");
        lists.add("标签五");
        lists.add("标签六");
        lists.add("标签七");
        lists.add("标签八");
        mTab.setData(lists);
        if (lists.size() <= 5) {
            mTab.setVisibleTabCount(lists.size());
        } else {
            mTab.setVisibleTabCount(5);
        }

        mTab.setOnTabClickListener(new GiftStripPagerTabLayout.OnTabClickListener() {
            @Override
            public void onTabClickListener(Tab tab, int pos) {
                Toast.makeText(mContext, "位置：" + pos, Toast.LENGTH_SHORT).show();
//                mGiftViewPager.smoothScrollToPosition(pos);
                mGiftViewPager.scrollToPosition(pos);
            }
        });

        LinearLayoutManager layout = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,
                false);
        mGiftViewPager.setLayoutManager(layout);
        mGiftViewPager.setAdapter(new RecyclerView.Adapter<MainActivity.ViewHolder>() {


            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textView = new TextView(mContext);
                return new ViewHolder(textView);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.tv.setText("位置是: " + position);
            }

            @Override
            public int getItemCount() {
                return lists.size();
            }
        });


        mGiftViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int i, int i1) {
                Log.i("test", "pageIndex: " + i + " " + i1);
                mTab.setCurrentItem(i1);
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tv;

        public ViewHolder(View view) {
            super(view);
            tv = (TextView) view;
        }
    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
    }
}
