package com.example.view.heartview;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.view.R;
import com.example.view.StringUtil;

import java.lang.ref.WeakReference;

/**
 * Created by liuj on 2015/12/8.
 */
public class NewHeartAnimLayout extends RelativeLayout {

    private HeartWaveView wave_heart;
    private TextView tv_like;
    private NewHeartAnimSurfaceView heartAnimatorView;
    ValueAnimator mScaleValueAnim;
    //private HeartCheckHandler handler;

    public NewHeartAnimLayout(Context context) {
        super(context);
        initContent(context, null, 0);
    }

    public NewHeartAnimLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContent(context, attrs, 0);
    }

    public NewHeartAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContent(context, attrs, defStyleAttr);
    }

    private void initContent(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.ly_periscope, this);
        tv_like = (TextView) findViewById(R.id.player_tv_like);
        heartAnimatorView = (NewHeartAnimSurfaceView) findViewById(R.id.heartanimatiorview);
        wave_heart = (HeartWaveView) findViewById(R.id.wave_heart);
        //handler = new HeartCheckHandler(this);
    }


    public void scaleHeart() {
        mScaleValueAnim = ValueAnimator.ofObject(new FloatEvaluator(), 1f, 1.2f, 1f);
        mScaleValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                wave_heart.setScaleX(value);
                wave_heart.setScaleY(value);
            }
        });
        mScaleValueAnim.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // handler.removeCallbacksAndMessages(null);
    }

    /**
     * 获取点赞数量文本框
     *
     * @return
     */
    public TextView getLikeTv() {
        return tv_like;
    }

    /**
     * 添加多个心
     *
     * @param num
     */
    public void addHearts(int num) {
        if (heartAnimatorView == null) {
            return;
        }
        heartAnimatorView.addHearts(num);
        // handler.startCheck();
    }

    /**
     * 添加单个心
     */
    public void addWaveHearts() {
        if (heartAnimatorView == null) {
            return;
        }
        heartAnimatorView.addHeartNow();
        //handler.startCheck();
    }

    public int getLikeCount() {
        int count = 0;
        try {
            if (tv_like != null) {
                count = StringUtil.String2Integer(tv_like.getText() + "", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 清除所有
     */
    public void clear() {
        if (heartAnimatorView == null) {
            return;
        }
        if (mScaleValueAnim != null) {
            mScaleValueAnim.removeAllUpdateListeners();
        }
        heartAnimatorView.clean();
    }

    public void release() {
        if (heartAnimatorView != null) {
            heartAnimatorView.cleanWeakReference();
            heartAnimatorView.release();
        }
    }

    /**
     * 重置绘制状态
     */
    public void resetDrawingState() {
        if (heartAnimatorView != null) {
            heartAnimatorView.resetDrawingState();
        }
    }


    /**
     * 停止动画
     */
    public void pauseHeartAnim() {
        if (heartAnimatorView != null) {
            heartAnimatorView.pause();
        }
    }


    /**
     * 开始飘心动画
     */
    public void startHeartAnim() {
        if (heartAnimatorView != null) {
            heartAnimatorView.resume();
        }
    }

    /**
     * 清除未显示的心
     */
    @Deprecated
    public void clearRetainHearts() {
       /* if (heartAnimatorView == null) {
            return;
        }
        Log.e("heartanim", "clearRetainHearts");
        heartAnimatorView.cleanRetainAnimations();*/
    }


    /**
     * 检查点赞数量是否变化
     */
    private static class HeartCheckHandler extends Handler {
        private static final int MSG_CHECK = 1002;
        private static final int CHECK_DURATION = 5000;
        int lastCount;
        private WeakReference<NewHeartAnimLayout> heartAnimLayoutWeakReference;

        public HeartCheckHandler(NewHeartAnimLayout newHeartAnimLayout) {
            heartAnimLayoutWeakReference = new WeakReference<NewHeartAnimLayout>(newHeartAnimLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewHeartAnimLayout newHeartAnimLayout = heartAnimLayoutWeakReference.get();
            if (newHeartAnimLayout == null) {
                removeCallbacksAndMessages(null);
                return;
            }
            if (msg.what == MSG_CHECK) {
                int currentCount = newHeartAnimLayout.getLikeCount();
                if (lastCount == currentCount) {
                    newHeartAnimLayout.clearRetainHearts();
                }
                lastCount = newHeartAnimLayout.getLikeCount();
                sendEmptyMessageDelayed(MSG_CHECK, CHECK_DURATION);
            }
        }

        private void startCheck() {
            if (hasMessages(MSG_CHECK)) {
                return;
            }
            sendEmptyMessageDelayed(MSG_CHECK, CHECK_DURATION);
        }
    }
}
