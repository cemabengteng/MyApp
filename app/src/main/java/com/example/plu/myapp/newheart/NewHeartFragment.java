package com.example.plu.myapp.newheart;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.plu.myapp.R;
import com.example.view.newheartview.GoodAnimationUtile;
import com.example.view.newheartview.GoodsInitUtile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chengXing on 2016/9/29.
 */

public class NewHeartFragment extends Fragment {
    private RelativeLayout mRlShowHeartView;
    private List<ImageView> imagesPool;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagesPool = initGoodImageViews();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_heart, container, false);
        mRlShowHeartView = (RelativeLayout) inflate.findViewById(R.id.rlShowHeartView);
        initListener();
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        showNum(10);
    }

    private Random mRandom = new Random();

    private void initListener() {
        mRlShowHeartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOne();
            }
        });
    }

    public void showOne(){
        setGoodsUiTouch(GoodsInitUtile.getGoodsType(mRandom.nextInt(21)));
    }

    public void showNum(int num){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                moveHandler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 0, 400);
    }


    private List<ImageView> initGoodImageViews() {
        List<ImageView> images = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            ImageView img = new ImageView(getActivity());
            RelativeLayout.LayoutParams rlPram = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlPram.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rlPram.addRule(RelativeLayout.CENTER_HORIZONTAL);
            img.setLayoutParams(rlPram);
            img.setImageResource(R.drawable.ic_qipao_zi_3);

            GoodHolder holderTag = new GoodHolder();
            holderTag.time = System.currentTimeMillis();
            img.setTag(holderTag);
            img.setScaleX(0.1f);
            img.setScaleY(0.1f);
            images.add(img);
        }
        return images;
    }

    public void setGoodsUiTouch(int resource) {
        final ImageView goodImgView = getGoodImageView();
        if (goodImgView == null) {
            return;
        }
        ((GoodHolder) goodImgView.getTag()).isAdd = true;
        ((GoodHolder) goodImgView.getTag()).time = System.currentTimeMillis();
        goodImgView.setImageResource(resource);
        mRlShowHeartView.addView(goodImgView);
        goodImgView.setVisibility(View.INVISIBLE);

        final Animation anim = GoodAnimationUtile.createAnimation(getActivity());
        anim.setAnimationListener(new GoodMsgAnimaionList(goodImgView));
        goodImgView.startAnimation(anim);
    }

    private ImageView getGoodImageView() {
        for (int i = 0; i < imagesPool.size(); i++) {
            ImageView image = imagesPool.get(i);
            GoodHolder tag = (GoodHolder) image.getTag();
            if (tag.isAdd == false) {
                return image;
            }
        }
        return null;
    }

    private Handler moveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                setGoodsUiTouch(GoodsInitUtile.getGoodsType(mRandom.nextInt(21)));
                return;
            }
            long before = (Long) msg.obj;
            for (int i = 0; i < mRlShowHeartView.getChildCount(); i++) {
                ImageView imgv = null;
                try {
                    imgv = (ImageView) mRlShowHeartView.getChildAt(i);
                } catch (Exception e) {
                    imgv = null;
                }
                if (imgv == null) {
                    continue;
                }

                long time = ((GoodHolder) imgv.getTag()).time;

                if (time == before) {
                    ((GoodHolder) imgv.getTag()).isAdd = false;
                    mRlShowHeartView.removeView(imgv);
                }
            }
        }

    };

    class GoodMsgAnimaionList implements Animation.AnimationListener {
        private ImageView imgv;

        public GoodMsgAnimaionList(ImageView imgv) {
            this.imgv = imgv;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            imgv.clearAnimation();
            imgv.setVisibility(View.INVISIBLE);

            Message msg = moveHandler.obtainMessage();
            msg.obj = ((GoodHolder) (imgv.getTag())).time;
            moveHandler.sendMessage(msg);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationStart(Animation arg0) {
        }

    }

    class GoodHolder {
        public boolean isAdd = false;
        public long time;
    }


}
