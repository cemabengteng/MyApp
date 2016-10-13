package com.example.view.bopengheartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import com.example.view.R;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuj on 2016/6/1.
 * 飘心动画
 */
public class HeartAnimSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static String TAG = "HeartAnimSurfaceView";
    private static final int ANIM_LOOP_DURATION = 100;
    private static final int RENDER_DURATION = 8; //渲染间隔
    private static final int ANIM_DURATION = 5000; //每个动画播放的间隔
    private static final long BATCH_ANIM_DURATION = 3000; //每个批量动画的间隔
    private static final long PAUSE_DURATION = 500; //暂停动画时间间隔

    private static final int LOW_LEVEL = 20;
    private static final int MIDDLE_LEVEL = 40;
    private static final int HIGH_LEVEL = 80; // 注：ANIM_DURATION/HIGH_LEVEL接近RENDER_DURATION有可能会导致绘制间隔过小

    private WeakReference<Context> mContextWf;
    private CopyOnWriteArrayList<AnimTransform> animList = new CopyOnWriteArrayList<>(); //动画列表
    private LinkedList<BatchHeartAnim> batchHeartAnimList = new LinkedList<>(); //批量动画列表

    private AnimTransform.Config config;
    private AnimTransform.HeartPool heartPool;
    private Paint paint;

    private long lastCheckTime; //上一次播放普通动画的时间
    private long lastBatchTime; //上一次批量动画的时间
    private volatile boolean isRun; // 结束绘制，关闭线程
    private boolean isRender; // 是否暂停绘制
    private int totalCount; // 当前飘星数
    private long sleep; // 每一祯的实际间隔 = 原本帧时间 + 操作耗时

    private final SurfaceHolder surfaceHolder;
    private HandlerThread handlerThread;
    private RenderThread renderThread;
    private AnimHandler animHandler;
    private ExecutorService executorService;
    private Canvas unlockCanvas;

    public HeartAnimSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContextWf = new WeakReference<Context>(context);
        init(attrs, 0);

        // 设置surfaceHolder
        setZOrderMediaOverlay(true);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setZOrderOnTop(true); // necessary
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }


    private void init(final AttributeSet attrs, final int defStyleAttr) {
        heartPool = new AnimTransform.HeartPool();
        paint = new Paint();

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
            final TypedArray a = mContextWf.get().obtainStyledAttributes(
                    attrs, R.styleable.HeartLayout, defStyleAttr, 0);
            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.heart0, ops);
            double scale = 1.5;
            int dHeight = (int) (ops.outHeight * scale);
            int dWidth = (int) (ops.outWidth * scale);
            int initX = mWidth / 2 - ops.outWidth / 3;
            int initY = ops.outHeight / 3;
            // 动画的配置参数
            config = AnimTransform.Config.fromTypeArray(a, initX, initY, dWidth, dHeight);
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
            animHandler = new AnimHandler(handlerThread.getLooper(), HeartAnimSurfaceView.this);
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
     * @param num：真实产生的心数，之后会做梯度处理
     */
    public void addHearts(int num) {
        if (isRender) {
            totalCount += num;
        }
    }


    /**
     * 添加立即播放的一组心
     *
     * @param num:个数
     * @param duration:播放时长
     */
    public void addHeartsNow(int num, long duration) {
        batchHeartAnimList.add(new BatchHeartAnim(num, duration));
    }


    /**
     * 添加一个立即播放的心动画
     */
    public void addHeartNow() {
        addHeartsNow(1, 0);
    }

    /**
     * 根据梯度调整固定的飘心数量，每秒均匀飘动
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
     * <p>
     * 单个或一组，自由控制数量
     */
    private synchronized void startNextBatchAnim() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBatchTime < BATCH_ANIM_DURATION) {
            return;
        }
        try {
            if (batchHeartAnimList != null && batchHeartAnimList.size() > 0
                    && batchHeartAnimList.getFirst() != null) {
                BatchHeartAnim batchHeartAnim = batchHeartAnimList.removeFirst();
                startAnimTogether(batchHeartAnim.count, batchHeartAnim.eachDuration());
                // 添加了单个动画时，没有播放间隔
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
     * <p>
     * 梯度飘星，固定数量
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
            int duration = ANIM_DURATION / animCount;
            startAnimTogether(animCount, duration);
        }
    }

    /**
     * 播放动画
     *
     * @param count    动画个数
     * @param duration 每个动画的时间间隔
     */
    private void startAnimTogether(int count, long duration) {
        CopyOnWriteArrayList<AnimTransform> animators = new CopyOnWriteArrayList();
        for (int i = 0; i < count; i++) {
            final AnimTransform.Heart heart = heartPool.createHeart(mContextWf.get());
            final AnimTransform pathAnimator = new AnimTransform(config, heart, this);
            animators.add(pathAnimator);
            // count越多，绘制时间越久，需要加一个间隔时间
            pathAnimator.setStartDelay(duration * i);
        }
        animList.addAll(animators);
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
        for (AnimTransform transform : animList) {
            if (transform == null) {
                continue;
            }
            transform.cancel();
        }
        animList.clear();
        batchHeartAnimList.clear();
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

        private WeakReference<HeartAnimSurfaceView> weakReference;
        private boolean isRun;

        private AnimHandler(Looper looper, HeartAnimSurfaceView heartAnimSurfaceView) {
            super(looper);
            weakReference = new WeakReference<HeartAnimSurfaceView>(heartAnimSurfaceView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HeartAnimSurfaceView heartAnimSurfaceView = weakReference.get();
            if (heartAnimSurfaceView == null) {
                return;
            }
            if (msg.what == MSG_ANIM) {
                isRun = true;
                if (heartAnimSurfaceView.isRun) {
                    heartAnimSurfaceView.setRender(true);
                    heartAnimSurfaceView.startNextBatchAnim();
                    heartAnimSurfaceView.startNextAnim();
                    sendEmptyMessageDelayed(MSG_ANIM, ANIM_LOOP_DURATION);
                }
            } else if (msg.what == MSG_CLEAR) {
                isRun = false;
                heartAnimSurfaceView.setRender(false);
                heartAnimSurfaceView.resetDrawingState();
                heartAnimSurfaceView.clearScreen();
            }
        }

        /**
         * 延时开始动画，
         *
         * @param delay:在一个延时后开启动画
         */
        private void startAnim(long delay) {
            removeCallbacksAndMessages(null);
            sendEmptyMessageDelayed(MSG_ANIM, delay);
        }

        /**
         * 立即开始动画
         */
        private void startAnim() {
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
     * 渲染线程，不断执行heart.draw()绘制飘星
     */
    private class RenderThread implements Runnable {
        private boolean isRun; // 控制线程开关
        private boolean isRender; // 暂停/开始

        public RenderThread() {
            isRun = true;
            isRender = true;
        }

        /**
         * 关闭绘制线程
         */
        public void stop() {
            isRun = false;
        }

        /**
         * @param isRender：false时暂停绘制
         */
        public void render(boolean isRender) {
            this.isRender = isRender;
        }

        @Override
        public void run() {
            while (isRun) {
                if (getWidth() == 0 || getHeight() == 0) {
                    // view未加载时，不绘制
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
                        if (HeartAnimSurfaceView.this.isRender) {
                            if (unlockCanvas != null) {
                                surfaceHolder.unlockCanvasAndPost(unlockCanvas);
                                unlockCanvas = null;
                            }
                            canvas = surfaceHolder.lockCanvas();
                            if (canvas != null) {
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清屏
                                if (isRender && animList != null) {
                                    for (AnimTransform transform : animList) {
                                        if (transform == null) {
                                            continue;
                                        }
                                        // 设置进度增量
                                        transform.getHeart().setIncrease(sleep);
                                        if (transform.isWanting()) {
                                            // 一组动画中每个动画间需要有间隔
                                            continue;
                                        }
                                        boolean isTransForming = transform.transform();
                                        if (isTransForming) {
                                            // 动画未结束，绘制
                                            transform.draw(canvas, paint);
                                        } else {
                                            // 动画结束，释放操作
                                            transform.recycle(heartPool);
                                            // 释放单个已完成动画
                                            animList.remove(transform);
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
                                surfaceHolder.unlockCanvasAndPost(canvas); // 结束锁定画图，并提交改变。
                            }
                        } catch (Exception e) {
//                            Log.e(TAG, "-----unlockException " + e.getMessage());
                            try {
                                java.lang.reflect.Field field = SurfaceView.class.getDeclaredField("mSurfaceLock");
                                field.setAccessible(true);
                                ReentrantLock lock = (ReentrantLock) field.get(HeartAnimSurfaceView.this);
                                lock.unlock();
//                                Log.e(TAG, "-----renderThread unlock");
                            } catch (Exception ex) {
                                unlockCanvas = canvas;
//                                Log.e(TAG, "-----reflation exception is " + ex.getMessage());
                            }
                        }
                    }
                }
                if (isRun) {
                    try {
                        // 绘制时间越久，绘制间隔越大，防止性能问题
                        sleep = (System.currentTimeMillis() - start) / 2
                                + RENDER_DURATION;
                        if (sleep > RENDER_DURATION * 2) sleep = RENDER_DURATION * 2;
                        // 渲染间隔
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, " run " + isRun);
                }
            }
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
         * 批量飘星动画中，每个动画的间隔
         */
        public long eachDuration() {
            if (duration == 0) {
                return 0;
            }
            return count / duration;
        }
    }

}
