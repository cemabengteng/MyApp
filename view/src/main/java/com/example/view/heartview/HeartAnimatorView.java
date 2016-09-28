package com.example.view.heartview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.view.R;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuj on 2015/12/7.
 * 爱心动画视图
 */
public class HeartAnimatorView extends View {

    public static final String TAG = HeartAnimatorView.class.getName();
    private static final int ANIM_DURATION = 100; //每个动画播放的间隔
    private static final int MAX_HEARTS_TOTAL_COUNT = 60; //最大可绘制的个数
    private static final int MAX_HEARTS_COUNT = 10; //最大一次添加的个数
    private static final int ANIM_RESUME_DURATION = 1000;//暂停恢复动画时间
    private static final int ANIM_TASK_DURATION = ANIM_DURATION;
    public static final int ANIM_WAVE_HEART_NUM = 16; // 爆心动画飘星数量
    public static final int ANIM_WAVE_HEART_DURING = 1600; // 爆心动画飘星持续时间1.6秒

    private LinkedList<PathAnimator> animators = new LinkedList<>(); //动画列表
    private CopyOnWriteArrayList<Heart> drawingHeartList = new CopyOnWriteArrayList<>(); //当前绘制的心列表

    private Config config;

    private int defStyleAttr;
    private AttributeSet attributeSet;

    private HeartPool heartPool;
    private Paint paint;
    private AnimHandler handler;
    private ExecutorService executors = Executors.newCachedThreadPool();
    private long lastAddTime;


    public HeartAnimatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public HeartAnimatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeartAnimatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        this.defStyleAttr = defStyleAttr;
        this.attributeSet = attrs;
        paint = new Paint();
        handler = new AnimHandler(this);
        heartPool = new HeartPool();
    }

    private void initConfig() {

        if (config == null) {
            final TypedArray a = getContext().obtainStyledAttributes(
                    attributeSet, R.styleable.HeartLayout, defStyleAttr, 0);

            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.btn_live_nice1, ops);
            double scale = 1.5;
            int dHeight = (int) (ops.outHeight * scale);
            int dWidth = (int) (ops.outWidth * scale);
            int pointx = dWidth;//随机上浮方向的x坐标
            int mWidth = getMeasuredWidth();
            int initX = mWidth / 2 - ops.outWidth / 4;
            int initY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            config = Config.fromTypeArray(a, initX, initY, pointx, dWidth, dHeight);
            a.recycle();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        executors.shutdownNow();
        handler.removeCallbacksAndMessages(null);
        clean();
    }

    /**
     * 添加多个心
     *
     * @param num
     */
    public void addHearts(int num) {

        long duration = 0;
        if (num <= 3 && num > 1) {
            duration = 1000;
        } else if (num <= 10) {
            duration = 5000 / 30;
        } else {
            duration = 10000 / 50;
            if (num > 50) {
                num = 50;
            }
        }

        for (int i = 0; i < num; i++) {
            addHeart(true);
        }
        if (handler.isPause()) {
            return;
        }
        sendStartAnim(num, duration);
    }

    /**
     * 添加心
     */
    public void addHeart(boolean isBatch) {
        initConfig();
        final Heart heart = heartPool.createHeart(getContext());
        final PathAnimator animator = new PathAnimator(config, heart, this);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                recycleHeart(heart);
                ViewCompat.postInvalidateOnAnimation(HeartAnimatorView.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ViewCompat.postInvalidateOnAnimation(HeartAnimatorView.this);
            }
        });
        animators.add(animator);

        if (handler.isEnd()) {
            handler.restart();
        }

        if (handler.isPause()) {
            return;
        }
        if (!isBatch) {
            // 单个心直接发送，不用通过线程
            handler.sendEmptyMessage(AnimHandler.MSG_START_NEXT);
//            sendStartAnim(1, 0);
        }

    }

    private void sendStartAnim(int num, long duration) {
        executors.execute(new AnimTask(num, duration, handler));
    }

    private int getRetainCount() {
        return animators == null ? 0 : animators.size();
    }

    /**
     * 回收对象
     *
     * @param heart
     */
    private void recycleHeart(Heart heart) {
        drawingHeartList.remove(heart);
        heartPool.recycleHeart(heart);
    }

    /**
     * 清理
     */
    public void clean() {
        handler.removeCallbacksAndMessages(null);
        for (PathAnimator animator : animators) {
            animator.cancel();
        }
        animators.clear();
        heartPool.clean();
    }

    /**
     * 重置绘制状态
     */
    public void resetDrawingState() {
        clean();
        if (drawingHeartList != null) {
            drawingHeartList.clear();
        }
    }

    /**
     * 清除剩余的动画
     */
    public void cleanRetainAnimations() {
        if (animators != null && animators.size() > 0) {
            animators.clear();
        }
        handler.end();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pauseAnimforAwhile();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        pauseAnimforAwhile(); //键盘弹出关闭时延时播放后续动画
    }

    private void pauseAnimforAwhile() {
        if (animators == null || animators.size() == 0) {
            return;
        }
        handler.pause(ANIM_RESUME_DURATION);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            return;
        }
        if (drawingHeartList == null || drawingHeartList.size() == 0) {
            return;
        }

        for (Heart heart : drawingHeartList) {
            Bitmap bitmap = heart.getBitmap();
            if (!bitmap.isRecycled()) {
                paint.setAlpha((int) (heart.getTransform().getAlpha() * 255));
                canvas.drawBitmap(bitmap, heart.getTransform().getMatrix(), paint);
            }
        }

        ViewCompat.postInvalidateOnAnimation(HeartAnimatorView.this);
    }

    /**
     * 开始下一个动画
     *
     * @return
     */
    private boolean startNextAnim() {
        if (animators == null || animators.size() == 0) {
            return false;
        }
        PathAnimator animator = animators.removeFirst();
        //对绘制的数量进行控制
        if (drawingHeartList.size() < MAX_HEARTS_TOTAL_COUNT) {
            if (animator != null) {
                drawingHeartList.add(animator.heart);
                animator.start();
            }
        }
        return animators.size() > 0;
    }


    private static class AnimTask implements Runnable {
        private int count;
        private long duration;
        private WeakReference<Handler> reference;

        public AnimTask(int count, long duration, Handler handler) {
            this.count = count;
            this.duration = duration;
            reference = new WeakReference<>(handler);
        }

        @Override
        public void run() {
            do {
                Handler handler = reference.get();
                if (handler == null) {
                    return;
                }
                handler.sendEmptyMessage(AnimHandler.MSG_START_NEXT);
                count--;
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (count > 0);
        }
    }

    private static class AnimHandler extends Handler {

        private static final int MSG_START_NEXT = 0;
        private static final int MSG_PAUSE_FOR_WHILE = 1;
        private static final int MSG_RESUME = 2;

        WeakReference<HeartAnimatorView> heartAnimatorViewWeakReference;
        boolean pause;
        boolean end;

        public AnimHandler(HeartAnimatorView view) {
            heartAnimatorViewWeakReference = new WeakReference<HeartAnimatorView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            HeartAnimatorView view = heartAnimatorViewWeakReference.get();
            if (view != null) {
                switch (what) {
                    case MSG_START_NEXT:
                        if (pause || end) {
                            return;
                        }
                        int num = msg.arg1;
                        if (num == 0) {
                            num = 1;
                        }
                        if (num > MAX_HEARTS_COUNT) {
                            num = MAX_HEARTS_COUNT;
                        }
                        //批量播放动画，防止数字停止，动画未停止
                        for (int i = 0; i < num; i++) {
                            view.startNextAnim();
                        }
                      /*
                      if (view.startNextAnim();) {
                            sendEmptyMessageDelayed(MSG_START_NEXT, ANIM_DURATION);
                        }*/
                        break;
                    case MSG_PAUSE_FOR_WHILE:
                        pause(ANIM_RESUME_DURATION);
                        break;
                    case MSG_RESUME:
                        pause = false;
                        removeMessages(MSG_START_NEXT);
                        sendEmptyMessageDelayed(MSG_START_NEXT, ANIM_DURATION);
                        break;
                }

            }
        }

        /**
         * 暂停
         *
         * @param pauseDuration 动画暂停时间间隔
         */
        public void pause(long pauseDuration) {
            pause = true;
            removeMessages(MSG_START_NEXT);
            if (pauseDuration > 0) {
                sendEmptyMessageDelayed(MSG_RESUME, pauseDuration);
            }
        }

        public boolean isPause() {
            return pause;
        }

        public void setPause(boolean isPause) {
            pause = isPause;
            if (isPause) {
                removeCallbacksAndMessages(null);
            }
        }


        public void end() {
            end = true;
            removeCallbacksAndMessages(null);
        }

        public boolean isEnd() {
            return end;
        }

        public void restart() {
            pause = false;
            end = false;
        }
    }


    /**
     * 动画的配置参数
     */
    public static class Config {
        public int initX;
        public int initY;
        public int xRand;
        public int animLengthRand;
        public int bezierFactor;
        public int xPointFactor;
        public int animLength;
        public int heartWidth;
        public int heartHeight;
        public int animDuration;

        static Config fromTypeArray(TypedArray typedArray, float x, float y, int pointx, int heartWidth, int heartHeight) {
            Config config = new Config();
            Resources res = typedArray.getResources();
            config.initX = (int) typedArray.getDimension(R.styleable.HeartLayout_initX,
                    x);
            config.initY = (int) typedArray.getDimension(R.styleable.HeartLayout_initY,
                    y);
            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout_xRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout_animLength,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length));//动画长度
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand));
            config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout_bezierFactor,
                    res.getInteger(R.integer.heart_anim_bezier_factor));
            config.xPointFactor = pointx;
            config.heartWidth = heartWidth;
            config.heartHeight = heartHeight;
            config.animDuration = typedArray.getInteger(R.styleable.HeartLayout_anim_duration,
                    res.getInteger(R.integer.anim_duration));//持续期
            return config;
        }
    }

    private static class PathAnimator extends ValueAnimator {

        private static final int DURATION = 3000; //动画时长
        private static AtomicInteger mCounter = new AtomicInteger(0);
        private static Random mRandom = new Random();

        private Config mConfig;
        private PathMeasure mPm;
        private float mDistance;
        private float mRotation;
        private View container;
        private Heart heart;

        public PathAnimator(Config config, final Heart heart, final View container) {
            this.mConfig = config;
            this.heart = heart;
            heart.animator = this;
            this.container = container;
            setupAnimator();
        }

        /**
         * 初始化动画路径
         */
        private void initPath() {
            mRotation = randomRotation();
            mPm = new PathMeasure(createPath(mCounter, container, 2), false);
            mDistance = mPm.getLength();
        }

        private void setupAnimator() {
            setFloatValues(0, 1f);
            setDuration(DURATION);
            setInterpolator(new LinearInterpolator());
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value == 0) {
                        initPath();//推迟初始化动画,防止切换键盘动画位置错乱
                    }
                    transform(value);
                }
            });
            addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mCounter.incrementAndGet();
                    float[] values = new float[9];
                    heart.getTransform().getMatrix().getValues(values);
                    // PluLog.i("startYPos" + values[5]);
                    ViewCompat.postInvalidateOnAnimation(container);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mCounter.decrementAndGet();
                }
            });
        }

        /**
         * 动画变换操作
         *
         * @param factor 动画的进度
         */
        private void transform(float factor) {
            Matrix matrix = heart.getTransform().getMatrix();
            Transform transform = heart.getTransform();
            float scale = 1F;
            float scaleFactor = factor * 0.6f;
            if (3000.0F * scaleFactor < 200.0F) {
                scale = scale(scaleFactor, 0.0D, 0.06666667014360428D, 0.40000000298023224D, 1.100000023841858D);
            } else if (3000.0F * scaleFactor < 300.0F) {
                scale = scale(scaleFactor, 0.06666667014360428D, 0.10000000149011612D, 1.100000023841858D, 1.0D);
            }
            if (mPm != null) {
                mPm.getMatrix(mDistance * (factor), matrix, PathMeasure.POSITION_MATRIX_FLAG);
            }
            matrix.preScale(scale, scale);
            matrix.postRotate(mRotation * factor);
            transform.setAlpha(1.0F - factor);
            //边界检查,防止左边界截断
            checkBound(matrix);
        }

        private void checkBound(Matrix matrix) {
            int bitmapWidth = heart.getBitmap().getWidth();
            int padding = bitmapWidth / 4;
            float[] values = new float[9];
            Matrix resultMatrix = heart.getTransform().getMatrix();
            resultMatrix.getValues(values);
            if (values[2] < padding) {
                values[2] = padding;
            }
            resultMatrix.setValues(values);
        }

        /**
         * 创建动画路径
         *
         * @param counter
         * @param view
         * @param factor
         * @return
         */
        public Path createPath(AtomicInteger counter, View view, int factor) {
            Random r = mRandom;
            int x = r.nextInt(mConfig.xRand);
            int x2 = r.nextInt(mConfig.xRand);

            int y = view.getHeight() - mConfig.initY;
            int y2 = counter.intValue() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand);
            factor = y2 / mConfig.bezierFactor;
            int y3 = y - y2;
            y2 = y - y2 / 2;
            Path p = new Path();
            p.moveTo(mConfig.initX, y);
            //上移一段距离后，开始曲线移动
            p.cubicTo(mConfig.initX, y, x, y2 + factor, x, y2);
            p.moveTo(x, y2);
            p.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);
            return p;
        }


        public static float randomRotation() {
            return mRandom.nextFloat() * 28.6F - 14.3F;
        }

        private static float scale(double a, double b, double c, double d, double e) {
            return (float) ((a - b) / (c - b) * (e - d) + d);
        }

    }

    /**
     * 心
     */
    private static class Heart {

        public ValueAnimator animator;
        public Transform transform;
        public Bitmap bitmap;

        public Heart(Bitmap bitmap) {
            this.bitmap = bitmap;
            transform = new Transform();
        }

        public Transform getTransform() {
            return transform;
        }

        public void setTransform(Transform transform) {
            this.transform = transform;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void reset() {
            if (animator != null) {
                animator.cancel();
            }
            if (transform != null) {
                transform.reset();
            }
        }
    }


    /**
     * 变换状态
     */
    private static class Transform {

        public float alpha;
        public Matrix matrix;//旋转，位置变换

        public Transform() {
            reset();
        }

        public Matrix getMatrix() {
            return matrix;
        }

        public void setMatrix(Matrix matrix) {
            this.matrix = matrix;
        }

        public float getAlpha() {
            return alpha;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        public void reset() {
            alpha = 1f;
            matrix = new Matrix();
        }
    }


    /**
     * 对象池
     */
    private static class HeartPool {

        private final int[] HEARTS = new int[]{R.drawable.heart0, R.drawable.heart1, R.drawable.heart2, R.drawable.heart3, R.drawable.heart4, R.drawable.heart5, R.drawable.heart6, R.drawable.heart7, R.drawable.heart8};

        private WeakHashMap<Integer, Bitmap> bitmapWeakHashMap = new WeakHashMap<>();//位图缓存

        private LinkedList<Heart> cacheHearts = new LinkedList<>();//缓存

        private Random random = new Random();

        /**
         * 创建对象
         *
         * @param context
         * @return
         */
        public Heart createHeart(Context context) {
            Bitmap bitmap = getRandomBitmap(context);
            Heart heart = null;
            if (cacheHearts.size() > 0) {
                heart = cacheHearts.removeFirst();
                if (heart.bitmap != bitmap) {
                    heart.setBitmap(bitmap);
                }
                heart.reset();
            } else {
                heart = new Heart(bitmap);
            }
            return heart;
        }

        /**
         * 获取随机的图片
         *
         * @param context
         * @return
         */
        public Bitmap getRandomBitmap(Context context) {
            int resPos = random.nextInt(HEARTS.length);
            Bitmap bitmap = bitmapWeakHashMap.get(resPos);
            if (bitmap == null || bitmap.isRecycled()) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), HEARTS[resPos]);
                bitmapWeakHashMap.put(resPos, bitmap);
            }
            return bitmap;
        }

        /**
         * 回收对象
         *
         * @param heart
         */
        public void recycleHeart(Heart heart) {
            if (heart == null) {
                return;
            }
            if (!cacheHearts.contains(heart)) {
                cacheHearts.add(heart);
            }
        }

        /**
         * 清理对象池
         */
        public void clean() {
            cacheHearts.clear();
            if (bitmapWeakHashMap.size() > 0) {
                for (Integer key : bitmapWeakHashMap.keySet()) {
                    bitmapWeakHashMap.get(key).recycle();
                }
            }
            bitmapWeakHashMap.clear();
        }
    }
}

