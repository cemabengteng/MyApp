package com.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * Created by chengXing on 2016/8/31.
 */
public class RankingListTitleView extends RelativeLayout {
    public RankingListTitleView(Context context) {
        this(context, null);
    }

    public RankingListTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankingListTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.ranking_list_title_view,this);
    }
}
