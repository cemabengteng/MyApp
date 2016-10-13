package com.example.plu.myapp.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.dagger.component.ActivityComponent;
import com.example.view.bopengheartview.HeartAnimSurfaceView;
import com.example.view.heartview.NewHeartAnimSurfaceView;
import com.example.view.newheartview.GoodAnimationUtile;
import com.example.view.newheartview.GoodsInitUtile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> {
    public final static String TAG = MainActivity.class.getSimpleName();
    @Inject
    MainPresenter mPresenter;
    @Bind(R.id.heartView)
    NewHeartAnimSurfaceView heartView;
    @Bind(R.id.thumbContainer)
    RelativeLayout thumbContainer;

    @Bind(R.id.boPengHeartView)
    HeartAnimSurfaceView boPengHeartView;

    private Random mRandom;
    private List<ImageView> lsImgGoods = new ArrayList<>();

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    public MainComponent initComponent(@NonNull ActivityComponent component) {
        MainComponent mainComponent = component.provideMainComponent();
        mainComponent.inject(this);
        return mainComponent;
    }

    @Override
    protected void initData(Bundle state) {
        mRandom = new Random();
        initGoodImageViews();
    }

    private void initGoodImageViews() {
        for (int i = 0; i < 25; i++) {
            ImageView img = new ImageView(this);
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
            lsImgGoods.add(img);
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);

    }

    private final Handler msgReceivHandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (msg.arg1 == 1) {
                    setGoodsUiTouch(GoodsInitUtile.getGoodsType(msg.arg2));
                }
            }
        }

    };

    @OnClick({R.id.btStarHeartAni, R.id.btStart})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.btStarHeartAni:
//                heartView.addHearts(10);
//                heartView.addHeartNow();
                boPengHeartView.addHeartNow();
//                heartView.addHeartsNow(10,1000);
                break;
            case R.id.btStart:
                sendThumbs();
                break;
        }
    }

    private void sendThumbs() {
        Message msgMsg = msgReceivHandle.obtainMessage();
        msgMsg.what = 1;
        msgMsg.arg1 = 1;//点赞
        int random = (int) (Math.random() * 100 + 1) % 14;
        msgMsg.arg2 = random;
        msgReceivHandle.sendMessage(msgMsg);
    }

    /**
     * 点赞的话，开始设置赞的效果
     */
    public void setGoodsUiTouch(int resource) {
        ImageView goodImgView = getGoodImageView();
        if (goodImgView == null) {
            return;
        }
        ((GoodHolder) goodImgView.getTag()).isAdd = true;
        ((GoodHolder) goodImgView.getTag()).time = System.currentTimeMillis();
        goodImgView.setImageResource(resource);
        thumbContainer.addView(goodImgView);
        goodImgView.setVisibility(View.INVISIBLE);

        Animation anim = GoodAnimationUtile.createAnimation(this);
        anim.setAnimationListener(new GoodMsgAnimaionList(goodImgView));
        goodImgView.startAnimation(anim);
    }

    private ImageView getGoodImageView() {
        for (int i = 0; i < lsImgGoods.size(); i++) {
            ImageView imgtem = lsImgGoods.get(i);
            GoodHolder tag = (GoodHolder) imgtem.getTag();
            if (tag.isAdd == false) {
                return imgtem;
            }
        }
        return null;
    }

    class GoodHolder {
        public boolean isAdd = false;
        public long time;
    }

    private Handler moveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (int i = 0; i < thumbContainer.getChildCount(); i++) {
                ImageView imgv = null;
                try {
                    imgv = (ImageView) thumbContainer.getChildAt(i);
                } catch (Exception e) {
                    imgv = null;
                }
                if (imgv == null) {
                    continue;
                }

                long time = ((GoodHolder) imgv.getTag()).time;
                long before = (Long) msg.obj;

                if (time == before) {
                    ((GoodHolder) imgv.getTag()).isAdd = false;
                    thumbContainer.removeView(imgv);
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
            msg.what = 1;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose:
                Toast.makeText(this, "compose", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
