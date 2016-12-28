package com.example.plu.myapp.biggift;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.component.ActivityComponent;
import com.example.plu.myapp.util.PluLog;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/12/28.
 */

public class LargeGiftMainActivity extends MvpActivity<LargeGiftMainComponent, LargeGiftMainPresenter> implements LargeGiftMainView {

    @Inject
    LargeGiftMainPresenter mPresenter;
    @Bind(R.id.recy_all_gifts)
    RecyclerView recyAllGifts;

    LargeGiftListAdapter adapter;
    private List<LargeGift> mData;

    @Override
    protected LargeGiftMainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    public LargeGiftMainComponent initComponent(@NonNull ActivityComponent component) {
        LargeGiftMainComponent largeGiftMainComponent = component.provideLargeGiftMainComponent();
        largeGiftMainComponent.inject(this);
        return largeGiftMainComponent;
    }

    @Override
    protected void initData(Bundle state) {
        mPresenter.getAllLargeGifts();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_large_gift_main);
    }

    @Override
    public void onGetLargeGiftFile(boolean isSuccess, List<LargeGift> list) {
        if (isSuccess) {
            if (list.size() > 0) {
                showLargeGift(list);
            } else {
                Toast.makeText(this, "请检查是否有文件", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请检查是否有文件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 大额礼物列表展示
     *
     * @param list
     */
    private void showLargeGift(List<LargeGift> list) {
        mData = list;
        recyAllGifts.setLayoutManager(new LinearLayoutManager(this));
        recyAllGifts.setAdapter(adapter = new LargeGiftListAdapter());
        adapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                PluLog.d("position: " + mData.get(position).getPath());
            }
        });
    }

    class LargeGiftListAdapter extends RecyclerView.Adapter<LargeGiftListAdapter.LargeGiftListViewHolder> {


        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        @Override
        public LargeGiftListAdapter.LargeGiftListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LargeGiftListViewHolder(
                    LayoutInflater.from(LargeGiftMainActivity.this).inflate(R.layout.item_large_gift_list, parent, false));
        }

        @Override
        public void onBindViewHolder(LargeGiftListAdapter.LargeGiftListViewHolder holder, final int position) {
            holder.tv.setText(mData.get(position).getName());
            if (mOnItemClickLitener != null) {
                holder.llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(v, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        class LargeGiftListViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            LinearLayout llItem;

            public LargeGiftListViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv_large_gift_name);
                llItem = (LinearLayout) view.findViewById(R.id.ll_item);
            }
        }
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }


}
