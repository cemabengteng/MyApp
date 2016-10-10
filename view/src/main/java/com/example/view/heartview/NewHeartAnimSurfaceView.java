package com.example.view.heartview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.example.view.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuj on 2016/6/1.
 * 飘心动画
 */
public class NewHeartAnimSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static String TAG = "HeartAnimSurfaceView";
    private static final int ANIM_LOOP_DURATION = 100;
    private static final int RENDER_DURATION = 10; //渲染间隔
    private static final int ANIM_DURATION = 5000; //每个动画播放的间隔
    private static final long BATCH_ANIM_DURATION = 3000; //每个批量动画的间隔
    private static final long PAUSE_DURATION = 500; //暂停动画时间间隔

    private static final int LOW_LEVEL = 20;
    private static final int MIDDLE_LEVEL = 40;
    private static final int HIGH_LEVEL = 80;

    private Context mContext;
    WeakReference<Context> mContextWf;
    private CopyOnWriteArrayList<Animator> animList = new CopyOnWriteArrayList<>(); //动画列表
    private LinkedList<BatchHeartAnim> batchHeartAnims = new LinkedList<>(); //批量动画列表
    private CopyOnWriteArrayList<Heart> drawingHeartList = new CopyOnWriteArrayList<>(); //当前绘制的心列表

    private Config config;
    private int defStyleAttr;
    private AttributeSet attributeSet;
    private Paint paint;
    private Paint clearPaint;
    private HeartPool heartPool;

    private long lastCheckTime; //上一次播放普通动画的时间
    private long lastBatchTime; //上一次批量动画的时间
    private volatile boolean isRun;
    private boolean isRender;
    private int totalCount;

    private final SurfaceHolder surfaceHolder;
    private HandlerThread handlerThread;
    private RenderThread renderThread;
    private AnimHandler animHandler;
    private ExecutorService executorService;

    public NewHeartAnimSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContextWf = new WeakReference<Context>(context);
        this.mContext = mContextWf.get();
        init(attrs, 0);

        heartPool = new HeartPool();

        //surfaceHolder
        setZOrderMediaOverlay(true);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setZOrderOnTop(true); // necessary
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }


    private void init(final AttributeSet attrs, final int defStyleAttr) {
        paint = new Paint();
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.attributeSet = attrs;
        this.defStyleAttr = defStyleAttr;
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                initConfig(attrs, defStyleAttr);
                if (config != null) {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }
        });
    }

    private void initConfig(AttributeSet attrs, int defStyleAttr) {
        int mWidth = getMeasuredWidth();
        if (config == null && mWidth != 0) {
            final TypedArray a = mContext.obtainStyledAttributes(
                    attributeSet, R.styleable.HeartLayout, defStyleAttr, 0);
            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.ic_qipao_fen_1, ops);
            double scale = 1.5;
            int dHeight = (int) (ops.outHeight * scale);
            int dWidth = (int) (ops.outWidth * scale);
            int pointx = dWidth;//随机上浮方向的x坐标
            int initX = mWidth / 2 - ops.outWidth / 3;
            int initY = ops.outHeight / 3;
            config = Config.fromTypeArray(a, initX, initY, pointx, dWidth, dHeight);
            a.recycle();
        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRun = isRender = true;
        lastCheckTime = System.currentTimeMillis();

        if (handlerThread == null) {
            handlerThread = new HandlerThread("animThread");
            handlerThread.start();
            animHandler = new AnimHandler(handlerThread.getLooper(), NewHeartAnimSurfaceView.this);
            animHandler.startAnim(ANIM_LOOP_DURATION);
        }
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
            renderThread = new RenderThread();
            executorService.submit(renderThread);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // isRender = false;  //此处将isRender 不要置为false ,是按举报页的时候,页走到这里了,导致unlockCanvas 没有调到导致anr

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRender = false;
        release();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (animHandler != null) {
            animHandler.clearAnimForAwhile(PAUSE_DURATION);
        }
    }

    /**
     * 添加飘心个数
     *
     * @param num
     */
    public void addHearts(int num) {
        if (isRender) {
            totalCount += num;
        }
    }


    /**
     * 添加立即播放的心
     *
     * @param num      个数
     * @param duration 播放时长
     */
    public void addHeartsNow(int num, long duration) {
        batchHeartAnims.add(new BatchHeartAnim(num, duration));
    }


    /**
     * 添加一个立即播放的心动画
     */
    public void addHeartNow() {
        addHeartsNow(1, 0);
    }

    /**
     * 根据级别调整飘心数量
     *
     * @return
     */
    private synchronized int adjustAnimCount() {
        int avgCount = totalCount / (ANIM_DURATION / 1000);
        int animCount = 0;
        if (totalCount == 0) {
            animCount = 0;
        } else if (avgCount < 2) {
            animCount = LOW_LEVEL;
        } else if (avgCount < 3) {
            animCount = MIDDLE_LEVEL;
        } else {
            animCount = HIGH_LEVEL;
        }
        totalCount = 0; //总数清空
        return animCount;
    }


    /**
     * 播放下一个批量动画
     */
    private void startNextBatchAnim() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBatchTime < BATCH_ANIM_DURATION) {
            return;
        }
        try {
            if (batchHeartAnims != null && batchHeartAnims.size() > 0 && batchHeartAnims.getFirst() != null) {
                BatchHeartAnim batchHeartAnim = batchHeartAnims.removeFirst();
                startAnimTogether(batchHeartAnim.count, batchHeartAnim.eachDuration());
                if (batchHeartAnim.count > 1) {
                    lastBatchTime = currentTime;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始下一个动画
     *
     * @return
     */
    private synchronized void startNextAnim() {

        //每5秒播放一次动画，检查时间间隔是否大于5秒
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckTime < ANIM_DURATION) {
            return;
        }
        lastCheckTime = currentTime;

        //本地点赞个数大于0，则开始播放
        if (totalCount > 0) {
            int animCount = adjustAnimCount();
            int duraion = ANIM_DURATION / animCount;
            startAnimTogether(animCount, duraion);
        }

    }


    /**
     * 播放动画
     *
     * @param count    动画个数
     * @param duration 每个动画的时间间隔
     */
    private void startAnimTogether(int count, long duration) {
        List<Animator> animators = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final Heart heart = heartPool.createHeart(mContext.getApplicationContext());
            final PathAnimator pathAnimator = new PathAnimator(config, heart, this);
            animators.add(pathAnimator);
            pathAnimator.setStartDelay(i * duration);
            pathAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    drawingHeartList.add(heart);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    recycleHeart(heart);
                    pathAnimator.removeAllListeners();
                }
            });
        }

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animList.remove(animatorSet);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animList.remove(animatorSet);
                animatorSet.removeAllListeners();
            }
        });
        animatorSet.playTogether(animators);
        animatorSet.start();

        animList.add(animatorSet);
    }

    /**
     * 回收对象
     *
     * @param heart
     */
    private synchronized void recycleHeart(Heart heart) {
        drawingHeartList.remove(heart);
        heartPool.recycleHeart(heart);
    }


    /**
     * 是否渲染
     *
     * @param isRender
     */
    public void setRender(boolean isRender) {
        this.isRender = isRender;
        if (renderThread != null) {
            renderThread.render(isRender);
        }
        if (!isRender) {
            resetDrawingState();
        }
    }

    /**
     * 清理
     */
    public void clean() {
        isRun = false;
        if (renderThread != null) {
            renderThread.stop();
        }
        resetDrawingState();
    }

    public void cleanWeakReference() {
        mContextWf.clear();
    }

    public void release() {
        isRun = false;
        mContextWf.clear();
        isRender = false;

        if (renderThread != null) {
            renderThread.stop();
        }

        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }

        if (animHandler != null) {
            animHandler.removeCallbacksAndMessages(null);
        }

        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }
    }

    /**
     * 重置绘制状态
     */
    public void resetDrawingState() {
        for (Animator animator : animList) {
            if (animator == null) {
                continue;
            }
            animator.cancel();
        }
//        mContextWf.clear();
        animList.clear();
        batchHeartAnims.clear();
        drawingHeartList.clear();
        totalCount = 0;
        lastBatchTime = 0;
        lastCheckTime = 0;
    }


    public void pause() {
        if (animHandler != null) {
            animHandler.clearAnim();
        }
    }

    public void resume() {
        if (animHandler != null) {
            animHandler.startAnim();
        }
    }


    /**
     * 清屏
     */
    public void clearScreen() {
        synchronized (surfaceHolder) {
            Canvas canvas = null;
            try {
                Context context = mContextWf.get();
                if (context == null) return;

                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清屏
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static class AnimHandler extends Handler {

        private static final int MSG_ANIM = 1;
        private static final int MSG_CLEAR = 2;

        private WeakReference<NewHeartAnimSurfaceView> weakReference;
        private boolean isRun;

        private AnimHandler(Looper looper, NewHeartAnimSurfaceView newHeartAnimSurfaceView) {
            super(looper);
            weakReference = new WeakReference<NewHeartAnimSurfaceView>(newHeartAnimSurfaceView);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewHeartAnimSurfaceView newHeartAnimSurfaceView = weakReference.get();
            if (newHeartAnimSurfaceView == null) {
                return;
            }
            if (msg.what == MSG_ANIM) {
                isRun = true;
                if (newHeartAnimSurfaceView.isRun) {
                    newHeartAnimSurfaceView.setRender(true);
                    newHeartAnimSurfaceView.startNextBatchAnim();
                    newHeartAnimSurfaceView.startNextAnim();
                    sendEmptyMessageDelayed(MSG_ANIM, ANIM_LOOP_DURATION);
                }
            } else if (msg.what == MSG_CLEAR) {
                isRun = false;
                newHeartAnimSurfaceView.setRender(false);
                newHeartAnimSurfaceView.resetDrawingState();
                newHeartAnimSurfaceView.clearScreen();
            }

        }

        /**
         * 开始动画
         */
        private void startAnim(long delay) {
            Log.d(TAG, "startAnim");
            removeCallbacksAndMessages(null);
            sendEmptyMessageDelayed(MSG_ANIM, delay);
        }

        /**
         * 开始动画
         */
        private void startAnim() {
            Log.d(TAG, "startAnim");
            startAnim(0);
        }

        /**
         * 清屏
         */
        private void clearAnim() {
            Log.d(TAG, "clearAnim");
            if (isRun) {
                removeCallbacksAndMessages(null);
                sendEmptyMessageDelayed(MSG_CLEAR, 0);
            }
        }


        /**
         * 停止动画
         *
         * @param duration 多少时间后恢复动画
         */
        private void clearAnimForAwhile(long duration) {
            clearAnim();
            startAnim(duration);
        }

    }

    /**
     * 渲染线程
     */
    private class RenderThread implements Runnable {

        private boolean isRun;

        private boolean isRender;

        private Canvas unlockCanvas;

        public RenderThread() {
            isRun = true;
            isRender = true;
        }

        public void stop() {
            isRun = false;
        }


        public void render(boolean isRender) {
            this.isRender = isRender;
        }

        @Override
        public void run() {
            while (isRun) {

                if (getWidth() == 0 || getHeight() == 0) {
                    continue;
                }

                Canvas canvas = null;
                long start = System.currentTimeMillis();
                Context context = mContextWf.get();
                if (context == null) {
                    isRun = false;
                    return;
                }
                if (surfaceHolder == null) {
                    isRun = false;
                    return;
                }

                synchronized (surfaceHolder) {
                    try {
                        if (NewHeartAnimSurfaceView.this.isRender) {
                            if (unlockCanvas != null) {
                                surfaceHolder.unlockCanvasAndPost(unlockCanvas);
                                unlockCanvas = null;
                            }
                            canvas = surfaceHolder.lockCanvas();
                            // Log.v("PLU","-----render lock canvas");
                            if (canvas != null) {
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清屏
                                if (isRender) {
                                    if (drawingHeartList != null) {
                                        for (Heart heart : drawingHeartList) {
                                            heart.draw(canvas, paint);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        unlockCanvas = null;
                        e.printStackTrace();
                    } finally {
                        try {
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                            }
                        } catch (Exception e) {
                            // Log.v("PLU","-----unlockException "+e.getMessage());
                            try {
                                java.lang.reflect.Field field = SurfaceView.class.getDeclaredField("mSurfaceLock");
                                field.setAccessible(true);
                                ReentrantLock lock = (ReentrantLock) field.get(NewHeartAnimSurfaceView.this);
                                lock.unlock();
                                //Log.v("PLU","-----renderThread unlock");
                            } catch (Exception ex) {
                                unlockCanvas = canvas;
                                // Log.v("PLU","-----reflation exception is "+ex.getMessage());

                            }
                        }

                    }
                }
                if (isRun) {
                    long end = System.currentTimeMillis();
                    try {
                        long sleep = (end - start) + RENDER_DURATION;
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
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


    private class PathAnimator extends ValueAnimator {

        private final int DURATION = 3000; //动画时长
        private AtomicInteger mCounter = new AtomicInteger(0);
        private Random mRandom = new Random();

        private Config mConfig;
        private PathMeasure mPm;
        private float mDistance;
        private float mRotation;
        private WeakReference<View> container;
        private Heart heart;

        public PathAnimator(Config config, final Heart heart, final View container) {
            this.mConfig = config;
            this.heart = heart;
            heart.animator = this;
            this.container = new WeakReference<>(container);
            setupAnimator();
        }

        /**
         * 初始化动画路径
         */
        private void initPath() {
            mRotation = randomRotation();
            View view = container.get();
            if (view == null) return;
            mPm = new PathMeasure(createPath(mCounter, view, 2), false);
            mDistance = mPm.getLength();
        }

        private void setupAnimator() {
            initPath();
            setFloatValues(0, 1f);
            setDuration(DURATION);
            setInterpolator(new LinearInterpolator());
            addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    transform(value);
                }
            });
            addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mCounter.incrementAndGet();
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


            factor = (float) (Math.round(factor * 10000)) / 10000;
            Log.i("test", "factor: " + factor);
            //锁住渲染，防止丢帧
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
//            matrix.preScale(scale, scale);
//            matrix.postRotate(mRotation * factor);
//            transform.setAlpha(1.0F - factor);
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


        public float randomRotation() {
            return mRandom.nextFloat() * 28.6F - 14.3F;
        }

        private float scale(double a, double b, double c, double d, double e) {
            return (float) ((a - b) / (c - b) * (e - d) + d);
        }

    }


    /**
     * 批量动画
     */
    private static class BatchHeartAnim {

        private int count;
        private long duration;

        public BatchHeartAnim(int count, long duration) {
            this.count = count;
            this.duration = duration;
        }

        /**
         * 获取每个动画的间隔
         *
         * @return
         */
        public long eachDuration() {
            if (duration == 0) {
                return 0;
            }
            return count / duration;
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


        /**
         * 绘制自己
         *
         * @param canvas
         * @param paint
         */
        public void draw(Canvas canvas, Paint paint) {
            if (transform.hasTransform()) {
                if (!bitmap.isRecycled()) {
                    paint.setAlpha((int) (transform.getAlpha() * 255));
                    canvas.drawBitmap(bitmap, transform.getMatrix(), paint);
                }
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

        /**
         * 是否已开始变化
         *
         * @return
         */
        public boolean hasTransform() {
            RectF rectF = new RectF();
            matrix.mapRect(rectF);
            return rectF.top != 0;
        }
    }


    /**
     * 对象池
     */
    private static class HeartPool {

        private static final int[] HEARTS = new int[]{
                R.drawable.ic_qipao_fen_1, R.drawable.ic_qipao_fen_2, R.drawable.ic_qipao_fen_3,
                R.drawable.ic_qipao_hong_1, R.drawable.ic_qipao_hong_2, R.drawable.ic_qipao_hong_3,
                R.drawable.ic_qipao_huang_1, R.drawable.ic_qipao_huang_2, R.drawable.ic_qipao_huang_3,
                R.drawable.ic_qipao_ju_1, R.drawable.ic_qipao_ju_2, R.drawable.ic_qipao_ju_3,
                R.drawable.ic_qipao_lan_1, R.drawable.ic_qipao_lan_2, R.drawable.ic_qipao_lan_3,
                R.drawable.ic_qipao_lv_1, R.drawable.ic_qipao_lv_2, R.drawable.ic_qipao_lv_3,
                R.drawable.ic_qipao_zi_1, R.drawable.ic_qipao_zi_2, R.drawable.ic_qipao_zi_3};

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
