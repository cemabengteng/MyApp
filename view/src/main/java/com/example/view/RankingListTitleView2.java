package com.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by chengXing on 2016/8/31.
 */
public class RankingListTitleView2 extends TitleBarView {


    private RelativeLayout rlView;

    public RankingListTitleView2(Context context) {
        this(context, null);
    }

    public RankingListTitleView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankingListTitleView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        rlView = getViewById(R.id.rlView);
        getViewById(R.id.ctv_titlebar_title).setVisibility(GONE);
//        StripPagerTabLayout stripPagerTabLayout = new StripPagerTabLayout(getContext());
        TextView textView = new TextView(getContext());
        textView.setText("hahhah");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RIGHT_OF,R.id.ctv_titlebar_left);
        rlView.addView(textView,layoutParams);
    }
}
