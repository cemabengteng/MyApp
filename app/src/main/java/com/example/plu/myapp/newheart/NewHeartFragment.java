package com.example.plu.myapp.newheart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.plu.myapp.R;
import com.example.view.newheartview.GoodAnimationUtile;
import com.example.view.newheartview.GoodsInitUtile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chengXing on 2016/9/29.
 */

public class NewHeartFragment extends Fragment {
    private RelativeLayout mRlShowHeartView;
    private List<ImageView> imagesPool;
    private Button mBt;
    private Observable<Long> mInterval;

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
        mBt = (Button) inflate.findViewById(R.id.bt);
        initListener();
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInterval = Observable.interval(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
//        showNum(10);
    }

    private Random mRandom = new Random();

    private void initListener() {
        mRlShowHeartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRlShowHeartView.post(new Runnable() {
                    @Override
                    public void run() {
                        showOne();
                    }
                });
            }
        });
        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < mRandom.nextInt(20); i++) {
                    mRlShowHeartView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOne();
                        }
                    }, i * 110);
                }
            }
        });
    }

    public void showOne() {
        setGoodsUiTouch(GoodsInitUtile.getGoodsType(mRandom.nextInt(21)));
    }


    public void showNum(int num) {
        mInterval
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        showOne();
                    }
                });
    }


    private List<ImageView> initGoodImageViews() {
        List<ImageView> images = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            ImageView img = new ImageView(getActivity());
            RelativeLayout.LayoutParams rlPram = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlPram.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rlPram.addRule(RelativeLayout.CENTER_HORIZONTAL);
            img.setLayoutParams(rlPram);
//            img.setImageResource(R.drawable.ic_qipao_zi_3);

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
        mRlShowHeartView.removeView(goodImgView);
        mRlShowHeartView.addView(goodImgView);
        goodImgView.setVisibility(View.INVISIBLE);
        final Animation anim = GoodAnimationUtile.createAnimation(getActivity());
        anim.setAnimationListener(new GoodAnimaionListener(goodImgView));
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


    class GoodAnimaionListener implements Animation.AnimationListener {
        private ImageView imgv;

        public GoodAnimaionListener(ImageView imgv) {
            this.imgv = imgv;
        }

        public void setImageView(ImageView imag) {
            this.imgv = imag;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            Log.i("test", "onAnimationEnd");
            imgv.clearAnimation();
            imgv.setVisibility(View.INVISIBLE);

            for (int i = 0; i < mRlShowHeartView.getChildCount(); i++) {
                final ImageView view;
                try {
                    view = (ImageView) mRlShowHeartView.getChildAt(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                long time = ((GoodHolder) view.getTag()).time;
                if (time == ((GoodHolder) imgv.getTag()).time) {
                    ((GoodHolder) view.getTag()).isAdd = false;
                    mRlShowHeartView.post(new Runnable() {
                        @Override
                        public void run() {
                            mRlShowHeartView.removeView(view);
                        }
                    });
                }
            }
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
