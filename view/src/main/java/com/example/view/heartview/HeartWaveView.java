package com.example.view.heartview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.example.view.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * 点赞View：有血槽，爆心，满血槽三组动画
 * <p/>
 * Created by plu on 16-5-17.
 */
public class HeartWaveView extends View {
    public static final int TYPE_WAVE = 1; // 普通血槽，水波
    public static final int TYPE_LOADING = 2; // 爆心动画
    public static final int TYPE_PROGRESS = 3; // 满血槽动画

    private static final int SLEEP_DURING = 50; // 间隔50毫秒
    private static final int MAX_HEART = 10;

    private Paint mPaintWater = null, mRingPaint = null, mTextPaint = null, mHeartPaint = null;

    private int mRingColor, mRingBgColor, mWaterColor, mWaterBgColor,
            mFontSize, mTextColor;
    private float mProgressPadding;

    private int[] mLoadingBgColor, mLoadingColor;
    private int mCurLoadingBgColor, mCurLoadingColor;
    private float crestCount = 1.5f;

    private float mProgress, mTime; // 20秒积累一颗心
    private float mLoadingProgress = 0.0f, mLoadingTime = HeartAnimatorView.ANIM_WAVE_HEART_DURING; // 3.2秒爆心动画时间
    private int mLoadingPadding; // Loading水波纹的padding最大值
    private int mViewPadding; // View的padding值

    private Point mCenterPoint;
    private float mRingWidth, mProgress2WaterWidth;
    private int mShowType; // 1.水纹；2.loading+波纹；3.进度条
    private int mHeartNum = 0; // 积攒的爱心数量，最多10个

    private long mWaveFactor = 0L;
    private float mAmplitude = 20.0F; // 20F 振幅
    private float mWaveSpeed = 0.03F; // 0.020F
    private int mWaterAlpha = 255; // 255

    private PaintFlagsDrawFilter pfd; // 抗锯齿

    private Subscription subscriptionProgress; // 满血槽
    private Subscription subscriptionLoad; // 爆心
    private Subscription subscriptionMain; // 血槽
    private AnimatorSet animatorSet;

    public HeartWaveView(Context paramContext) {
        super(paramContext);
    }

    public HeartWaveView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HeartWaveView(Context context, AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWaterWaveAttr(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("Recycle")
    private void initWaterWaveAttr(Context context, AttributeSet attrs, int defStyle) {
        // 初始化颜色，背景，进度，大小等值
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HeartWaveView, defStyle, 0);
        mRingColor = typedArray.getColor(
                R.styleable.HeartWaveView_progressColor, 0xFF33B5E5);
        mRingBgColor = typedArray.getColor(
                R.styleable.HeartWaveView_progressBgColor, 0xFFBEBEBE);
        mWaterColor = typedArray.getColor(
                R.styleable.HeartWaveView_waterWaveColor, 0XFFFF6B00);
        mWaterBgColor = typedArray.getColor(
                R.styleable.HeartWaveView_waterWaveBgColor, 0xFF000000);
        mRingWidth = typedArray.getDimensionPixelOffset(
                R.styleable.HeartWaveView_progressWidth, 0);
        mProgress2WaterWidth = typedArray.getDimensionPixelOffset(
                R.styleable.HeartWaveView_progress2WaterWidth, 0);
        mFontSize = typedArray.getDimensionPixelOffset(
                R.styleable.HeartWaveView_fontSize, 0);
        mProgressPadding = typedArray.getDimensionPixelOffset(
                R.styleable.HeartWaveView_progress_padding, 5);
        mTextColor = typedArray.getColor(
                R.styleable.HeartWaveView_textColor, 0xFFFFFFFF);
        mProgress = typedArray.getInteger(
                R.styleable.HeartWaveView_progress, 15);
        mTime = typedArray.getInteger(
                R.styleable.HeartWaveView_waveTime, 10000); // 10秒积累一颗库存
        //        typedArray.getColor(R.styleable.WaterWaveProgress_waterWaveColor, 0XFF4FFDFE);
        typedArray.recycle();

        // 爆心的loading颜色数组赋值
        mLoadingBgColor = new int[]{context.getResources().getColor(R.color.heart_wave_color6),
                context.getResources().getColor(R.color.heart_wave_color8),
                context.getResources().getColor(R.color.heart_wave_color10),
                context.getResources().getColor(R.color.heart_wave_color12)};
        mLoadingColor = new int[]{context.getResources().getColor(R.color.heart_wave_color5),
                context.getResources().getColor(R.color.heart_wave_color7),
                context.getResources().getColor(R.color.heart_wave_color9),
                context.getResources().getColor(R.color.heart_wave_color11)};

        Log.e("initWaterWaveAttr", ">>>initWaterWaveAttr:" + mProgressPadding);
        mLoadingPadding = (int) mProgressPadding;
        mViewPadding = (int) (mProgressPadding * 2);
    }

    @SuppressLint("NewApi")
    private void init(Context context) {
        // 抗锯齿
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
//        canvas.setDrawFilter(pfd);

        mCenterPoint = new Point();
        mCurLoadingBgColor = mLoadingBgColor[0];
        mCurLoadingColor = mLoadingColor[0];

        // 使用硬件加速在有些机型上会出现错误，http://blog.csdn.net/niu_gao/article/details/7464320
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 进度条画笔，外层栏，内层白
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true); // 防止边缘的锯齿
        mRingPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingWidth);

        // 水波画笔
        mPaintWater = new Paint();
        mPaintWater.setStrokeWidth(1F);
//        mPaintWater.setAntiAlias(true); // 防止边缘的锯齿
//        mPaintWater.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mPaintWater.setColor(mWaterColor);
        mPaintWater.setAlpha(mWaterAlpha);

        // 绘制画心形线条
        mHeartPaint = new Paint();
        mHeartPaint.setAntiAlias(true); // 防止边缘的锯齿
        mHeartPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mHeartPaint.setStyle(Paint.Style.FILL);


        // 文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true); // 防止边缘的锯齿
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mFontSize);
    }

    /**
     * 水纹绘制线程
     */
    private void initAnimateWave() {
        int during = SLEEP_DURING * 4; // 每次更新的间隔
        final float add = (100 / (mTime / during)); // 每次更新的进度
        mWaveFactor = 0L;
        subscriptionMain = Observable.interval(during, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(10000) // 如果观察者线程阻塞了，先将接受的数据缓存下来，
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long number) {
                        // 150毫秒绘制一次
                        mProgress += add;
                        if (mProgress > 100) {
                            mHeartNum++;

                            // 大于10停止绘制
                            if (mHeartNum >= MAX_HEART) {
                                mProgress = 100;
                                release(TYPE_WAVE);
                            } else {
                                mProgress = 0;
                            }
                        }
                        invalidate();
                    }
                });
    }

    /**
     * 爆心的动画线程
     */
    private void setupAnimLoadingProgress() {
        int during = SLEEP_DURING * 3; // 每次更新的间隔
        final float add = (100 / (mLoadingTime / during)); // 每次更新的进度
        mLoadingProgress = 0;
        subscriptionLoad = Observable.interval(during, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(10000) // 如果观察者线程阻塞了，先将接受的数据缓存下来，
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在 IO线程
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long number) {
                        mLoadingProgress += add;
                        if (mLoadingProgress > 100) {
                            // 爆心结束，重新返回普通血槽模式
                            mLoadingProgress = 0;
                            // 如果当前是MAX状态，不仅要mHeartNum-1，还要把mProgress置0
                            mProgress = (mHeartNum == MAX_HEART) ? 0 : mProgress;
                            mHeartNum--;

                            setShowWave(true);
                            invalidate();
                            return;
                        }

                        mLoadingPadding++;
                        mLoadingPadding = (mLoadingPadding > mProgressPadding * 2) ? (int) (mLoadingPadding - mProgressPadding) : mLoadingPadding;

                        // 控制颜色变换的频率，并对应到指定颜色上去
                        int i = (int) (mLoadingProgress / 10) % mLoadingBgColor.length;
                        mCurLoadingBgColor = mLoadingBgColor[i];
                        mCurLoadingColor = mLoadingColor[i];
                        invalidate();
                    }
                });
    }

    float extraAnim;

    /**
     * 满血槽Progress动画，progress速度较快
     */
    private void setupMaxProgress() {
        int during = SLEEP_DURING; // 每次更新的间隔
        final float add = (100 / (mLoadingTime / 120)); // 每次更新的进度
        mLoadingProgress = 0;
        extraAnim = 0;
        subscriptionProgress = Observable.interval(during, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(10000) // 如果观察者线程阻塞了，先将接受的数据缓存下来，
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在 IO线程
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long number) {
                        if (extraAnim == 0) {
                            if (mLoadingProgress > 100) {
                                // 确保progress跑完一整圈
                                mLoadingProgress = 100;
                            } else if (mLoadingProgress == 100) {
                                mLoadingProgress = 0;
                                extraAnim = add;
                                setupHeartAnim();
                            } else {
                                mLoadingProgress += add;
                            }
                            invalidate();
                        } else {
                            extraAnim += add;
                            if (extraAnim > 100) {
                                extraAnim = 0;
                                releaseHeartAnim();
                            }
                        }
                    }
                });
    }

    @SuppressLint({"DrawAllocation", "NewApi"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            // 编辑器下的样式
            return;
        }

        int width = getWidth();
        int height = getHeight();
        width = height = (width < height) ? width : height;
        mAmplitude = width / 20f;
        mCenterPoint.x = width / 2;
        mCenterPoint.y = height / 2;
        mRingWidth = mRingWidth == 0 ? width / 20 : mRingWidth;
        mProgress2WaterWidth = mProgress2WaterWidth == 0 ? mRingWidth * 0.6f
                : mProgress2WaterWidth;
        mRingPaint.setStrokeWidth(mRingWidth);

        float waterPadding = mRingWidth + mProgress2WaterWidth;
        int waterHeightCount = (int) (height - waterPadding * 2);
        // 波纹高度处理，要去掉上下边的padding距离
        float waterHeight = waterHeightCount * (1 - (mProgress * (1f - waterPadding * 3 / waterHeightCount)) / 100) - waterPadding;
        int staticHeight = (int) (waterHeight + mAmplitude); // 最大波纹高度（实心+波峰）

        if (mShowType == TYPE_LOADING) {
            drawHeart(canvas, width, height, mLoadingPadding, mCurLoadingBgColor, 55);
            drawHeart(canvas, width, height, mLoadingPadding / 2, mCurLoadingBgColor, 88);
        }

        // 绘制并裁剪爱心，使超过爱心图案的部分都被裁剪掉
        drawHeartPath(canvas, width, height);

        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true); // 防止边缘的锯齿
        bgPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        bgPaint.setColor((mShowType == TYPE_LOADING) ? mCurLoadingBgColor : mWaterBgColor);
        bgPaint.setAlpha((mShowType == TYPE_PROGRESS) ? 0 : 120); // 满血槽时，背景透明，其他50%透明度
        // 方形容器背景
        canvas.drawRect(waterPadding - 5, waterPadding - 5, waterHeightCount
                + waterPadding + 5, waterHeightCount + waterPadding - 10, bgPaint);

        if (mShowType == TYPE_LOADING) {
            Paint progressPaint = new Paint();
            progressPaint.setAntiAlias(true); // 防止边缘的锯齿
            progressPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
            progressPaint.setColor(mCurLoadingColor);
            // 设置个新的长方形，扫描测量
            RectF oval = new RectF(0, -15, width, height);
            // 绘制扇形，从-90度开始，true时画扇形，false画弧线
            canvas.drawArc(oval, -90, mLoadingProgress * 360f / 100, true, progressPaint);

        } else {
            if (mProgress == 100) {
                // 满了之后设置高度，未满时预留了振幅，所以此时需要减掉振幅
                staticHeight -= mAmplitude + 5;
            }
            // 水纹下的实心部分的颜色
            LinearGradient lg = new LinearGradient(waterPadding - 5, staticHeight,
                    waterPadding - 5, waterHeightCount + waterPadding, mWaterColor,
                    getResources().getColor(R.color.heart_wave_color2), Shader.TileMode.CLAMP);
            mPaintWater.setShader(lg);
            mPaintWater.setAlpha((mShowType == TYPE_PROGRESS) ? 0 : 255); // 满血槽时，血槽透明
            canvas.drawRect(waterPadding - 5, staticHeight, waterHeightCount
                    + waterPadding + 5, waterHeightCount + waterPadding, mPaintWater);

            mWaveFactor++;
            if (mWaveFactor >= Integer.MAX_VALUE) {
                mWaveFactor = 0L;
            }
            int xToBeDrawed = (int) waterPadding; // 波纹X点
            int waveHeight = (int) (waterHeight - mAmplitude
                    * Math.sin(Math.PI
                    * (2.0F * (xToBeDrawed + (mWaveFactor * width)
                    * mWaveSpeed)) / width)); // 波纹Y点
            int newWaveHeight; // 波纹下一个Y点
            while (true) {
                // 绘制水波
                if (xToBeDrawed >= waterHeightCount + waterPadding) {
                    // X点超过边界的过滤
                    break;
                }
                if (mProgress == 100 && waveHeight == staticHeight) {
                    // 100%满了之后不再绘制波浪
                    if (mHeartNum == MAX_HEART) {
                        if (mShowType != TYPE_PROGRESS) {
                            mShowType = TYPE_PROGRESS;
                            releaseHeartWave();
                            setupMaxProgress();
                        }
                        Paint progressPaint = new Paint();
                        progressPaint.setAntiAlias(true); // 防止边缘的锯齿
                        progressPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
                        progressPaint.setColor(getResources().getColor(R.color.heart_wave_color13));// progress条颜色
                        // 设置个新的长方形，扫描测量
                        RectF oval = new RectF(0, 0, width, height);
                        // 绘制扇形，从-90度开始，true时画扇形，false画弧线
                        canvas.drawArc(oval, -90, mLoadingProgress * 360f / 100, true, progressPaint);

                        mHeartPaint.setShader(lg);
                        drawHeart(canvas, width, height, -3, mWaterColor, 255);
                    }
                    break;
                }
                if (mProgress == 100) {
                    // 100%满了之后，设置成最高高度
                    newWaveHeight = staticHeight;
                } else {
                    newWaveHeight = (int) (waterHeight - mAmplitude
                            * Math.sin(Math.PI
                            * (crestCount * (xToBeDrawed + (mWaveFactor * waterHeightCount)
                            * mWaveSpeed)) / waterHeightCount));
                }
                // 水纹上的波动部分的颜色
                canvas.drawLine(xToBeDrawed, newWaveHeight, xToBeDrawed + 1,
                        staticHeight, mPaintWater);
                xToBeDrawed++;
                waveHeight = newWaveHeight;
            }
        }

        if (mHeartNum > 0) {
            // 设置文字
            String progressTxt = String.valueOf(mHeartNum);
            if (mHeartNum == MAX_HEART) {
                mTextPaint.setTextSize(mFontSize == 0 ? (width - mViewPadding) / 6 : mFontSize); // 文字size
                mTextPaint.setTypeface(Typeface.DEFAULT); // 加粗
                progressTxt = "MAX";
            } else {
                mTextPaint.setTextSize(mFontSize == 0 ? (width - mViewPadding) / 4 : mFontSize); // 文字size
                mTextPaint.setTypeface(Typeface.DEFAULT_BOLD); // 加粗
            }
            float mTxtWidth = mTextPaint.measureText(progressTxt, 0,
                    progressTxt.length());
            canvas.drawText(progressTxt, mCenterPoint.x - mTxtWidth / 2,
                    mCenterPoint.y * 1.00f - mFontSize / 2, mTextPaint);
        }
    }

    /**
     * 绘制爱心
     */
    private void drawHeart(Canvas canvas, int width, int height, int padding, int color, int alpha) {
        // 绘制画心形线条
        mHeartPaint.setColor(color); //设置画笔颜色
        mHeartPaint.setAlpha(alpha);
        if (mShowType != TYPE_PROGRESS) {
            mHeartPaint.setShader(null);
        }

        int length = width / 4 - mViewPadding + padding; // 心半径
        RectF ovalRect = new RectF(width / 2 - length, height / 2 - length * 2 - 2,
                width / 2 + length, height / 2 + length * 2 - 2);// 设置个新的长方形

        // 绘制心形左半边形状
        Path path = new Path();
        Matrix matrix = new Matrix();
        Region region = new Region();
        // 长方形设置圆角，半斤为宽度/2
        path.addRoundRect(ovalRect, length, length, Path.Direction.CW);
        // 旋转45度
        matrix.postRotate(-45, width / 2, height / 2);
        path.transform(matrix, path);
        // 从正中间裁剪掉多余部分
        region.setPath(path, new Region(0, 0, width / 2, height));

        // 绘制心形右半边形状
        Path path1 = new Path();
        Matrix matrix1 = new Matrix();
        Region region1 = new Region();
        path1.addRoundRect(ovalRect, length, length, Path.Direction.CW);
        matrix1.postRotate(45, width / 2, height / 2);
        path1.transform(matrix1, path1);
        region1.setPath(path1, new Region(width / 2, 0, width, height));

        // 通过op方法，将两个半边的心形拼接，XOR（取并集）
        region.op(region1, Region.Op.XOR);

        Path path2 = new Path();
        region.getBoundaryPath(path2);
        path2.close();

        // 水纹下的实心部分的颜色
        canvas.drawPath(path2, mHeartPaint);
    }

    /**
     * 绘制并裁剪爱心，使超过爱心图案的部分都被裁剪掉
     */
    private void drawHeartPath(Canvas canvas, int width, int height) {
        int length = width / 4 - mViewPadding; // 心半径
        RectF ovalRect = new RectF(width / 2 - length, height / 2 - length * 2,
                width / 2 + length, height / 2 + length * 2);// 设置个新的长方形

        // 绘制心形左半边形状
        Path path = new Path();
        Matrix matrix = new Matrix();
        Region region = new Region();
        // 长方形设置圆角，半斤为宽度/2
        path.addRoundRect(ovalRect, length, length, Path.Direction.CW);
        // 旋转45度
        matrix.postRotate(-45, width / 2, height / 2);
        path.transform(matrix, path);
        // 从正中间裁剪掉多余部分
        region.setPath(path, new Region(0, 0, width / 2, height));

        // 绘制心形右半边形状
        Path path1 = new Path();
        Matrix matrix1 = new Matrix();
        Region region1 = new Region();
        path1.addRoundRect(ovalRect, length, length, Path.Direction.CW);
        matrix1.postRotate(45, width / 2, height / 2);
        path1.transform(matrix1, path1);
        region1.setPath(path1, new Region(width / 2, 0, width, height));

        // 通过op方法，将两个半边的心形拼接，XOR（取并集）
        region.op(region1, Region.Op.XOR);
        // 通过clipPath方法，裁剪出指定的心形
        canvas.clipPath(region.getBoundaryPath(), Op.REPLACE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = widthMeasureSpec;
//        int height = heightMeasureSpec;
//        width = height = (width < height) ? width : height;
//        setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //        setMeasuredDimension(openBitmap.getWidth(), openBitmap.getHeight());
    }

    public void setProgress(int progress) {
        progress = progress > 100 ? 100 : progress < 0 ? 0 : progress;
        mProgress = progress;
        invalidate();
    }

    public void setFontSize(int mFontSize) {
        this.mFontSize = mFontSize;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public int getHeartNum() {
        return mHeartNum;
    }

    /**
     * 设置初始心心数，并开启积攒血槽
     */
    public void setHeartNum(int heartNum) {
        this.mHeartNum = (heartNum < 0) ? 0 : heartNum;
        if (mHeartNum < MAX_HEART) {
            mProgress = 0;
            setShowWave(true);
            invalidate();
        } else {
            // Max满血槽状态，会在draw中开启动画
            mHeartNum = MAX_HEART;
            mProgress = 100;
            mShowType = TYPE_WAVE;
            invalidate();
        }
    }

    /**
     * 开启积攒血槽
     */
    public void setShowWave(boolean b) {
        mShowType = TYPE_WAVE;
        if (b) {
            release(TYPE_WAVE, TYPE_LOADING, TYPE_PROGRESS);
            initAnimateWave();
        } else {
            release(mShowType);
        }
    }

    /**
     * 开启爆心模式
     */
    public void setShowLoading(boolean b) {
        if (mShowType == TYPE_LOADING || mHeartNum < 1) {
            return;
        }
        mShowType = TYPE_LOADING;
        if (b) {
            release(TYPE_WAVE, TYPE_LOADING, TYPE_PROGRESS);

            setupAnimLoadingProgress();
        } else {
            mShowType = TYPE_WAVE;
            release(TYPE_WAVE, TYPE_LOADING, TYPE_PROGRESS);
            initAnimateWave();
        }
    }

    /**
     * 是否正在爆心，爆心时不可在此点击
     */
    public boolean canShowLoading() {
        return mShowType != TYPE_LOADING && mHeartNum > 0;
    }

    /**
     * 是否正在爆心，爆心时可点击
     */
    public boolean canClick() {
        return mShowType == TYPE_LOADING;
    }

    /**
     * 重置为波浪动画，在切换房间时需要调用
     */
    public void resetWave() {
        mShowType = TYPE_WAVE;
        mProgress = 0;
        invalidate();
    }

    public void releaseHeartWave() {
        release(TYPE_WAVE, TYPE_LOADING, TYPE_PROGRESS);
    }

    private void release(int... type) {
        for (int i = 0; i < type.length; i++) {
            switch (type[i]) {
                case TYPE_WAVE:
                    if (subscriptionMain != null) {
                        subscriptionMain.unsubscribe();
                        subscriptionMain = null;
                    }
                    break;
                case TYPE_LOADING:
                    if (subscriptionLoad != null) {
                        // 注：正在爆心时，触发release操作，实际心数应该减一
                        if (mLoadingProgress > 0) {
                            mHeartNum--;
                        }
                        subscriptionLoad.unsubscribe();
                        subscriptionLoad = null;
                    }
                    break;
                case TYPE_PROGRESS:
                    if (subscriptionProgress != null) {
                        subscriptionProgress.unsubscribe();
                        subscriptionProgress = null;
                    }
                    releaseHeartAnim();
                    break;
            }
        }
    }

    /**
     * 心跳动画，对view做放大效果，待使用
     */
    private void setupHeartAnim() {
        // 动画过程中，对控件使用硬件缓存
       // setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        animatorSet = new AnimatorSet();
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.2f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.2f);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(Animation.REVERSE);

        animatorSet.play(animation).with(animation1);
//        animatorSet.setInterpolator(AnimationUtils.loadInterpolator(getContext(),
//                android.R.anim.anticipate_overshoot_interpolator));
        // bounce_interpolator 停止前来回振几下
        animatorSet.setDuration(400);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时，清除缓存。
            //    setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        animatorSet.start();
    }

    private void releaseHeartAnim() {
        setScaleX(1);
        setScaleY(1);
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }
    }

}