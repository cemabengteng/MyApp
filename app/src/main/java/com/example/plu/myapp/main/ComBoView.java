package com.example.plu.myapp.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.layout.BaseRelativelayout;
import com.example.plu.myapp.util.FrescoUtil;
import com.example.plu.myapp.util.NullUtil;
import com.example.view.cobo.CircleProgressBar;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by chengXing on 2017/1/9.
 */

public class ComBoView extends BaseRelativelayout {
    private static final int TOTAL_DEGREE = 110; // 各个btn的总角度
    private static final int DEFAULT_COMBO_DURING = 3000; // 默认倒计时长(ms)
    private static final int DEFAULT_COMBO_DELAY = 600; // 默认倒计误差时长(ms)

    private static final int TYPE_OF_IN_CHARGIFT = -1; // 用在快捷礼物下，第一点击时，展示连击动画，再次点击则赠送
    private static final int TYPE_OF_IN_GIFTLIST = -2; // 用在礼物列表下，每次点击都赠送，显示时展示动画

    private View mMainBar;
    private View mComboOutside;
    private CircleProgressBar mComboBar;
    private SimpleDraweeView mComboBarImg;
    private CircleProgressBar[] mGroupGifts;

    private Subscription comboOb; // 倒计时线程
    private int progress = 0; // 倒计时进度
    private int length = 0;
    private Gifts mGifts;
    private int mComboDuring = DEFAULT_COMBO_DURING; // 开始连击后的连击倒计时
    private int mFirstDuring = DEFAULT_COMBO_DURING; // 初次进入时使用固定的关闭倒计时
    private boolean mParentIsDismiss;   //当连击动画结束时，父布局是否需要隐藏
    private int currentType; //当前的模式
    private boolean isAnimatorRunning = false;  //动画是否还在播放

    private AnimatorSet showAnimatorSet;
    private AnimatorSet dismissAnimatorSet;
    private OnComboListener listener;
    private CircleProgressBar id1;
    private CircleProgressBar id2;
    private CircleProgressBar id3;
    private CircleProgressBar id4;

    public ComBoView(Context context) {
        this(context, null);
    }

    public ComBoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComBoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_combo_layout_only;
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(getLayout(), this, true);
        mGroupGifts = new CircleProgressBar[4];
        mComboOutside = rootView.findViewById(R.id.comboOutside);
        mMainBar = rootView.findViewById(R.id.mainBar);
        mComboBar = (CircleProgressBar) rootView.findViewById(R.id.comboBar);
        mComboBarImg = (SimpleDraweeView) rootView.findViewById(R.id.comboBarImg);
        id1 = (CircleProgressBar) rootView.findViewById(R.id.id_1);
        id2 = (CircleProgressBar) rootView.findViewById(R.id.id_2);
        id3 = (CircleProgressBar) rootView.findViewById(R.id.id_3);
        id4 = (CircleProgressBar) rootView.findViewById(R.id.id_4);
        mGroupGifts[0] = id1;
        mGroupGifts[1] = id2;
        mGroupGifts[2] = id3;
        mGroupGifts[3] = id4;
    }

    @Override
    protected void initData() {
        super.initData();
        // 计算按钮距离原点的长度 = comboW * 2 - groupW - comboP + groupW + comboP + 一些琐碎的padding
        length = mContext.getResources().getDimensionPixelOffset(R.dimen.combo_btn_width) * 2 +
                (int) (mContext.getResources().getDimensionPixelOffset(R.dimen.combo_btn_padding) * 0.1);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mComboBarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType == TYPE_OF_IN_GIFTLIST) {
                    sendGift();
                } else if (currentType == TYPE_OF_IN_CHARGIFT) {
                    //只显示id1,id2,id3,id4弹出动画
                    if (isAnimatorRunning) {
                        sendGift();
                    } else {
                        progress = 0;
                        initGroupGifts();
                        startShowNumAnim();
                        isAnimatorRunning = true;
                    }
                }
            }
        });

        id1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum(v);
            }
        });

        id2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum(v);
            }
        });

        id3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum(v);
            }
        });

        id4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum(v);
            }
        });

        mComboOutside.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType == TYPE_OF_IN_GIFTLIST) {
                    superDismiss();
                } else {
                    if (comboOb != null && !comboOb.isUnsubscribed()) {
                        comboOb.unsubscribe();
                    }
                    isAnimatorRunning = false;
                    numViewDismiss();
                    mComboBar.setProgress(0);
                }
            }
        });
    }

    public void setOnComboListener(ComBoView.OnComboListener listener) {
        this.listener = listener;
    }

    /**
     * 发送礼物
     * 1. 回调发送礼物
     * 2. 更新进度条
     * 3. 重置连击间隔时间
     */
    private void sendGift() {
        // 正在倒计时，点击后重置进度，并发送礼物
        if (comboOb != null && !comboOb.isUnsubscribed()) {
            progress = 0;
            // 发送连击礼物，getCombo判断是否是连击礼物
            if (listener != null && mGifts != null) {
                listener.sendGift(1, mGifts.getComboInteval() > 0);
            }
            // 重置连击间隔时间
            if (mGifts != null && mGifts.getComboInteval() > 0 &&
                    mFirstDuring > 0 && mFirstDuring != mComboDuring) {
                mFirstDuring = 0;
                progress = 0;
                if (mComboBar != null) {
                    mComboBar.setProgress(progress);
                }
                // 只重置progress，不再开启线程
                comboOb.unsubscribe();
                comboOb = null;
                startCombo();
            }
        }
    }

    /**
     * 设置连击时限
     *
     * @param during：连击时限
     */
    private void setComboDuring(int during) {
        if (during <= 0) {
            during = DEFAULT_COMBO_DURING;
        }
        // 如果连击时长不同，先关闭原线程
        if (during != mComboDuring) {
            if (comboOb != null && !comboOb.isUnsubscribed()) {
                comboOb.unsubscribe();
            }
        }
        // 重新设置连击时长
        this.mComboDuring = during - DEFAULT_COMBO_DELAY;
    }


    /**
     * 该方法包含两步
     * 1. 设置礼物数据
     * 2. 展示连击ui
     *
     * @param gifts
     */
    public void show(Gifts gifts) {
        if (mComboBar == null) return;
        if (gifts == null) return;

        //设置为赠送模式
        currentType = TYPE_OF_IN_GIFTLIST;

        // 进度条初始化
        progress = 0;
        // 初始化初次倒计时长
        mFirstDuring = DEFAULT_COMBO_DURING - DEFAULT_COMBO_DELAY;
        mComboBar.setProgress(progress);

        this.mGifts = gifts;
        setComboDuring(mGifts.getComboInteval() * 1000);

        initGiftImg();
        initGroupGifts();

//        this.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 全透明背景
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }

        mMainBar.post(new Runnable() {
            @Override
            public void run() {
                startShowAnim();
            }
        });
    }

    /**
     * 设置需要展示的礼物
     *
     * @param gifts
     */
    public void setData(Gifts gifts) {
        this.mGifts = gifts;
        currentType = ComBoView.TYPE_OF_IN_CHARGIFT;

        // 进度条初始化
        progress = 0;
        // 初始化初次倒计时长
        mFirstDuring = DEFAULT_COMBO_DURING - DEFAULT_COMBO_DELAY;
        mComboBar.setProgress(progress);

        setComboDuring(mGifts.getComboInteval() * 1000);
        initGiftImg();
    }

    /**
     * 设置礼物图标
     */
    private void initGiftImg() {
        if (mComboBarImg == null || mGifts == null) return;
        String url;
        if (TextUtils.isEmpty(mGifts.getImg())) {
            url = GlobalValue.GIFT_HEAD_URL + mGifts.getName() + GlobalValue.GIFT_MIDDLE_URL +
                    mGifts.getName() + mGifts.getNewBannerIcon() + GlobalValue.GIFT_END_URL;
        } else {
            url = mGifts.getImg();
        }
        FrescoUtil.setImageURI(mComboBarImg, url);
    }


    /**
     * 初始化组合礼物，最多取4个，45度对齐
     */
    private void initGroupGifts() {
        if (mGroupGifts == null || mGifts.getOptionses() == null) return;
        if (length == 0) {
            length = mContext.getResources().getDimensionPixelOffset(R.dimen.combo_btn_width) * 2 +
                    (int) (mContext.getResources().getDimensionPixelOffset(R.dimen.combo_btn_padding) * 0.1);
        }

        List<Options> optionsList = new ArrayList();

        int size = 0;
        for (int i = 0; i < mGifts.getOptionses().size(); i++) {
            if (mGifts.getOptionses().get(i) != null &&
                    mGifts.getOptionses().get(i).getNum() > 1) {
                size++;
                if (size > mGroupGifts.length) {
                    // 超过最大限制跳过
                    size = mGroupGifts.length;
                    break;
                }
                optionsList.add(mGifts.getOptionses().get(i));
            }
        }

        // 根据按钮总数计算每个按钮的夹角度数
        int degree = size <= 1 ? TOTAL_DEGREE / 2 : TOTAL_DEGREE / (size - 1);
        // 计算初始角度，45度对称
        int startDegree = -(TOTAL_DEGREE - 90) / 2;
        for (int i = 0; i < size; i++) {
            mGroupGifts[i].setVisibility(View.VISIBLE);
            mGroupGifts[i].setText(String.valueOf(optionsList.get(i).getNum()));

            // 计算正弦角度,起始角度startDegree度,依次增加degree度
            double curDegree = Math.toRadians(size <= 1 ? degree : i * degree + startDegree);

            // 计算坐标
            double width = length * Math.cos(curDegree);
            double height = length * Math.sin(curDegree);

            // 设置位置
            mGroupGifts[i].setTranslationX((float) -width);
            mGroupGifts[i].setTranslationY((float) -height);
        }
    }


    private void onClickNum(View view) {
        try {
            // 获取礼物数量,回调
            String numTxt = ((CircleProgressBar) view).getText();
            if (listener != null) {
                listener.sendGift(Integer.valueOf(numTxt), false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private synchronized void startShowAnim() {
        if (showAnimatorSet != null && showAnimatorSet.isRunning()) {
            return;
        }
        if (NullUtil.isNull(mMainBar, mGroupGifts)) {
            startCombo();
        }
        mMainBar.setAlpha(1);
        mMainBar.setPivotX(mMainBar.getWidth() / 2);
        mMainBar.setPivotY(mMainBar.getHeight() / 2);

        // 动画总时长
        showAnimatorSet = new AnimatorSet();//组合动画
        // 组合动画A，时长500ms
        ObjectAnimator barRotation = ObjectAnimator.ofFloat(mMainBar, "rotation", -180, 0).setDuration(300);
        ObjectAnimator barScaleX = ObjectAnimator.ofFloat(mMainBar, "scaleX", 0.2f, 1.0f).setDuration(200);
        ObjectAnimator barScaleY = ObjectAnimator.ofFloat(mMainBar, "scaleY", 0.2f, 1.0f).setDuration(200);
        showAnimatorSet.play(barRotation).with(barScaleX).with(barScaleY);
        for (int i = 0, length = mGroupGifts.length; i < length; i++) {
            mGroupGifts[i].setAlpha(1);
            mGroupGifts[i].setScaleX(0f);
            mGroupGifts[i].setScaleY(0f);
            mGroupGifts[i].setPivotX(mGroupGifts[i].getWidth() / 2);
            mGroupGifts[i].setPivotY(mGroupGifts[i].getHeight() / 2);
            // 组合动画C，时长delay：220ms + 40ms * 3，during：160ms
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mGroupGifts[i], "scaleX", 0.2f, 1.1f, 1.0f).setDuration(160);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mGroupGifts[i], "scaleY", 0.2f, 1.1f, 1.0f).setDuration(160);
            showAnimatorSet.play(scaleX).with(scaleY).after(250 + 40 * i);
        }
        showAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startCombo();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        showAnimatorSet.start();
    }

    /**
     * 仅仅展示连击数字的动画
     */
    private void startShowNumAnim() {
        if (showAnimatorSet != null && showAnimatorSet.isRunning()) {
            return;
        }
        if (NullUtil.isNull(mMainBar, mGroupGifts)) {
            startCombo();
        }

        // 动画总时长
        showAnimatorSet = new AnimatorSet();//组合动画
        for (int i = 0, length = mGroupGifts.length; i < length; i++) {
            mGroupGifts[i].setAlpha(1);
            mGroupGifts[i].setScaleX(0f);
            mGroupGifts[i].setScaleY(0f);
            mGroupGifts[i].setPivotX(mGroupGifts[i].getWidth() / 2);
            mGroupGifts[i].setPivotY(mGroupGifts[i].getHeight() / 2);
            // 组合动画C，时长delay：220ms + 40ms * 3，during：160ms
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mGroupGifts[i], "scaleX", 0.2f, 1.1f, 1.0f).setDuration(160);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mGroupGifts[i], "scaleY", 0.2f, 1.1f, 1.0f).setDuration(160);
            showAnimatorSet.play(scaleX).with(scaleY).after(250 + 40 * i);
        }
        showAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startCombo();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        showAnimatorSet.start();
    }

    /**
     * 开始倒计时/发送连击
     */
    private void startCombo() {
        // 线程未结束逻辑，为了减少不断重复创建线程
        if (comboOb != null && !comboOb.isUnsubscribed()) {
            // 只重置progress，不再开启线程
            return;
        }
        // 开始倒计时
        comboOb = Observable.interval((mFirstDuring <= 0 ? mComboDuring : mFirstDuring) / 100
                , TimeUnit.MILLISECONDS, Schedulers.io())
                .onBackpressureBuffer(1000)
                .doOnNext(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        progress++;
                        if (mComboBar != null) {
                            mComboBar.setProgress(progress);
                        }
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Long>>() {
                    @Override
                    public Observable<? extends Long> call(Throwable throwable) {
                        return Observable.just(0L);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        if (currentType == TYPE_OF_IN_GIFTLIST) {
                            setVisibility(GONE);
                        } else {
                            numViewDismiss();
                            mComboBar.setProgress(0);
                            isAnimatorRunning = false;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (currentType == TYPE_OF_IN_GIFTLIST) {
                            setVisibility(GONE);
                        } else {
                            numViewDismiss();
                            mComboBar.setProgress(0);
                            isAnimatorRunning = false;
                        }
                    }

                    @Override
                    public void onNext(Long b) {
                        // 完成100%，加一点点缓冲（5）后关闭
                        if (progress > 105) {
                            // 关闭线程
                            if (currentType == TYPE_OF_IN_GIFTLIST) {
                                setVisibility(GONE);
                            } else {
                                if (comboOb != null && !comboOb.isUnsubscribed()) {
                                    comboOb.unsubscribe();
                                }
                                numViewDismiss();
                                mComboBar.setProgress(0);
                                isAnimatorRunning = false;
                            }
                            return;
                        }
                    }
                });
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            if (comboOb != null && !comboOb.isUnsubscribed()) {
                comboOb.unsubscribe();
            }
            dismissAnim(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    superDismiss();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    superDismiss();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    public synchronized void dismissAnim(Animator.AnimatorListener animatorListener) {
        if (dismissAnimatorSet != null && dismissAnimatorSet.isRunning()) {
            return;
        }
        if (NullUtil.isNull(mMainBar, mGroupGifts)) {
            superDismiss();
        }
        mMainBar.setPivotX(mMainBar.getWidth() / 2);
        mMainBar.setPivotY(mMainBar.getHeight() / 2);

        // 动画总时长500ms，最长动画为groupRotation（delay：120ms，during：380ms）
        dismissAnimatorSet = new AnimatorSet();//组合动画
        ObjectAnimator barScaleX = ObjectAnimator.ofFloat(mMainBar, "scaleX", 1.0f, 1.4f).setDuration(300);
        ObjectAnimator barScaleY = ObjectAnimator.ofFloat(mMainBar, "scaleY", 1.0f, 1.4f).setDuration(300);
        ObjectAnimator barAlpha = ObjectAnimator.ofFloat(mMainBar, "alpha", 0.5f, 0.0f).setDuration(200);
        dismissAnimatorSet.play(barAlpha).with(barScaleX).with(barScaleY);
        for (int i = 0, length = mGroupGifts.length; i < length; i++) {
            mGroupGifts[i].setPivotX(mGroupGifts[i].getWidth() / 2);
            mGroupGifts[i].setPivotY(mGroupGifts[i].getHeight() / 2);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mGroupGifts[i], "scaleX", 1.0f, 2.0f).setDuration(300);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mGroupGifts[i], "scaleY", 1.0f, 2.0f).setDuration(300);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mGroupGifts[i], "alpha", 0.5f, 0.0f).setDuration(150);
            dismissAnimatorSet.play(alpha).with(scaleX).with(scaleY); // 倒序
        }
        dismissAnimatorSet.addListener(animatorListener);
        dismissAnimatorSet.start();
    }

    private void superDismiss() {
        if (mGroupGifts == null) return;
        // 隐藏按钮
        for (CircleProgressBar bar : mGroupGifts) {
            bar.setVisibility(View.GONE);
        }
        setVisibility(GONE);
    }

    /**
     * 仅仅消失数字组合view
     */
    private void numViewDismiss() {
        if (mGroupGifts == null) return;
        // 隐藏按钮
        for (CircleProgressBar bar : mGroupGifts) {
            bar.setVisibility(View.GONE);
        }
    }

    /**
     * 设置父布局是否需要隐藏
     *
     * @param parentIsDismiss
     */
    public void setParentIsDismiss(boolean parentIsDismiss) {
        mParentIsDismiss = parentIsDismiss;
    }

    public boolean isParentIsDismiss() {
        return mParentIsDismiss;
    }

    public interface OnComboListener {
        void sendGift(int num, boolean isCombo);
    }

}

