package com.example.view.bopengheartview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.example.view.R;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * Created by plu on 2016/10/11.
 */
public class AnimTransform {
    private static final String TAG = AnimTransform.class.getSimpleName();

    private Random mRandom;
    private Config mConfig;
    private PathMeasure mPm;
    private float mRotation;
    private WeakReference<View> container;
    private Heart heart;
    private long mStartDelay = 0; // 动画开始的延迟时间
    private float mOverFactor = 1.0f; // 触发加速透明时的进度%
    private float mAlpha = 1.0f; // 触发加速透明标识时的透明度

    public AnimTransform(Config config, final Heart heart, final View container) {
        this.mConfig = config;
        this.heart = heart;
        this.container = new WeakReference<>(container);
        this.mRandom = new Random();
        this.mOverFactor = 1.0f;
        this.mAlpha = 1.0f;
        this.hasRecycled = false;

        initPath(); // 创建运动路径
    }

    /**
     * 初始化动画路径
     */
    private void initPath() {
        mRotation = randomRotation();
        View view = container.get();
        if (view == null) return;
        mPm = new PathMeasure(createPath(view), false);
    }

    /**
     * 创建动画路径，获取飘动的随机位置点
     */
    private Path createPath(View view) {
        int x = mRandom.nextInt(mConfig.xRand); // X轴随机位移点
        int d2 = mConfig.animLength * 2/*+ r.nextInt(mConfig.animLengthRand)*/; // Y轴移动距离
        int factor = d2 / mConfig.bezierFactor; // 贝塞尔变换速度
        int y = view.getHeight() - mConfig.initY; // 总高度-初始高度=Y初始点
        int y2 = y - d2; // 第二阶段移动初始点

        Path p = new Path();
        //设置Path的初始点
        p.moveTo(mConfig.initX, y);
        //曲线移动一段距离至点(x, y2)，之后为直线运动
        p.cubicTo(mConfig.initX, y, x, y2 + factor, x, y2);
        return p;
    }

    /**
     * 设置开始绘制前的间隔时间
     * <p>
     * 增加到总持续时间中，计算进度时先减去delayTime
     */
    public void setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
    }

    /**
     * @return true: 未达到动画开始的延迟时间
     */
    public boolean isWanting() {
        Log.e(TAG, ">>>isTransForming!!!---wanting:" + this.heart.getIncrease() +
                "---mStartDelay:" + mStartDelay);
        return this.heart.getIncrease() - mStartDelay < 0;
    }

    public void draw(Canvas canvas, Paint paint) {
        this.heart.draw(canvas, paint);
    }

    public Heart getHeart() {
        return heart;
    }

    /**
     * 动画变换操作
     *
     * @return false：动画完成
     */
    public boolean transform() {
        long increase = this.heart.getIncrease();
        // increase * 渲染间隔 = 动画已进行时长
        if (increase > mStartDelay + mConfig.animDuration) {
            // 或动画持续时间已结束
            return false;
        }
        // 动画总进度
        float factor = (float) (increase - mStartDelay) / mConfig.animDuration;
        if (factor < 0) factor = 0; // 边界处理

        Matrix matrix = heart.getTransform().getMatrix();
        float scale = 0.6f; // view的正常大小值
        float step0 = 0.02f; // 第一阶段总比例2%
        float step1 = 0.12f; // 快速变大阶段总比例12%
        float step2 = 1.0f; // 第三阶段总比例100%
        float distanceFactor = 1.0f; // view正常飘动的速度

        if (factor < step0) {
            // [0, step0)时间区间里，完成[0.0D, 0.1D]的放大变化
            scale = scale(factor, 0.0D, step0 + 0.0006D, 0.0D, 0.1D);
            // [0, step0)时间区间里，完成[0, step0 + 0.02D]的运动区间
            distanceFactor = scale(factor, 0.0D, step0 + 0.0006D, 0.0D, step0 + 0.02D);
        } else if (factor < step1) {
            // [step0, step1)时间区间里，完成[0.1D, scale]的放大变化
            scale = scale(factor, step0 - 0.0006D, step1 + 0.0006D, 0.1D, scale);
            // [step0, step1)时间区间里，完成[step0 + 0.02D, step1]的运动区间
            distanceFactor = scale(factor, step0 - 0.0006D, step1 + 0.0006D, step0 + 0.02D, step1);
        } else if (factor <= step2) {
            // [step1, step2]时间区间里，完成[step1, step2]的运动区间
            distanceFactor = scale(factor, step1 - 0.0006D, step2 + 0.0006D, step1, step2);
        }
        if (mPm != null) {
            // 获取distance上的点配置的matrix位置信息（POSITION_MATRIX_FLAG:位置信息）
            float distance = mPm.getLength() * (distanceFactor);
            mPm.getMatrix(distance, matrix, PathMeasure.POSITION_MATRIX_FLAG);
        }
        // 设置view的缩放
        matrix.preScale(scale, scale);
        // 设置view旋转度数
        matrix.postRotate(mRotation * factor);
        //边界检查,防止左边界截断
        return !checkBound(factor);
    }

    /**
     * 边缘处理，接近边缘时，加速透明，不可逆
     *
     * @param factor：当前进度
     * @return true：超过边界，完全透明
     */
    private boolean checkBound(float factor) {
        float alpha; // view的初始透明度
        Transform transform = heart.getTransform();
        // 当接近边缘距离为width/2时，触发加速透明
        int padding = heart.getBitmap().getWidth() / 2;
        float[] values = new float[9];
        transform.getMatrix().getValues(values);
        if (values[2] < padding) {
            // 触发加速透明
            if (mOverFactor == 1.0f) {
                mOverFactor = factor;
                mAlpha = transform.getAlpha();
            }
        }

        if (mOverFactor != 1.0f) {
            // 加速透明，用总进度10%的时间完成全透明变换
            factor = factor > mOverFactor + 0.1D ? mOverFactor + 0.1f : factor;
            Log.e(TAG, ">>>factor:" + factor);
            alpha = scale(factor, mOverFactor - 0.00006, mOverFactor + 0.10006D, mAlpha, 0.0D);
        } else {
            // 透明度只会越来越小
            alpha = scale(factor, 0.0D, 1.00006D, 1.0D, 0.0D);
        }
        Log.e(TAG, ">>>alpha---before:" + alpha);
        // 透明度不可逆
        alpha = Math.min(Math.abs(alpha), heart.getTransform().getAlpha());
        // 设置view的透明度
        heart.getTransform().setAlpha(alpha);
        return alpha == 0;
    }
    private boolean hasRecycled = false;

    /**
     * release
     */
    public void recycle(HeartPool heartPool) {
        if (!hasRecycled) {
            heartPool.recycleHeart(heart);
            hasRecycled = true;
        }
    }

    /**
     * 关闭
     */
    public void cancel() {
        // TODO: 16/10/12 释放

    }

    /**
     * 心形随机旋转度数
     */
    private float randomRotation() {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }

    /**
     * 计算出一个顺滑的scale值
     * <p>
     * 注：要防止a超过了b和c的区间，返回值会为负数
     *
     * @param a：当前interpolated_Time
     * @param b：interpolated_Time可取的最小值
     * @param c：interpolated_Time可取的最大值
     * @param d：scale变化的最小值
     * @param e：scale变化的最大值
     */
    private float scale(double a, double b, double c, double d, double e) {
        return (float) ((a - b) / (c - b) * (e - d) + d);
    }

    /**
     * 动画的配置参数
     */
    public static class Config {
        public int initX; //x坐标
        public int initY; //y坐标
        public int xRand; // X轴变换范围
        public int animLengthRand;//动画边缘高度
        public int bezierFactor; //贝塞尔参数
        public int animLength; //动画运行高度
        public int heartWidth; //星的宽度
        public int heartHeight; //星的长度
        public int animDuration; //动画时间

        static Config fromTypeArray(TypedArray typedArray, float x, float y, int heartWidth, int heartHeight) {
            Config config = new Config();
            Resources res = typedArray.getResources();
            config.heartWidth = heartWidth;
            config.heartHeight = heartHeight;
            config.initX = (int) typedArray.getDimension(R.styleable.HeartLayout_initX,
                    x);
            config.initY = (int) typedArray.getDimension(R.styleable.HeartLayout_initY,
                    y);

            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout_xRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout_animLength,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length));
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand));
            config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout_bezierFactor,
                    res.getInteger(R.integer.heart_anim_bezier_factor));
            config.animDuration = typedArray.getInteger(R.styleable.HeartLayout_anim_duration,
                    res.getInteger(R.integer.anim_duration));
            return config;
        }
    }

    /**
     * 飘心动画中的对象
     * <p>
     * 注：可以自定义图片，传bitmap即可
     */
    public static class Heart {
        public Transform transform; // 动画轨迹属性
        public Bitmap bitmap; // 动画图片
        public long increase; // 动画进度，每绘制一次加1，直至increase*渲染间隔=动画时长

        public Heart(Bitmap bitmap) {
            this.bitmap = bitmap;
            transform = new Transform();
        }

        public long getIncrease() {
            return increase;
        }

        public void setIncrease(long increase) {
            this.increase += increase;
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

        /**
         * 停止动画，重置属性
         */
        public void reset() {
            increase = 0;
            if (transform != null) {
                transform.reset();
            }
        }

        /**
         * 绘制自己
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
     * 变换状态，包含位移、旋转及透明度
     */
    public static class Transform {
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
    public static class HeartPool {
        private static final int[] HEARTS
                = new int[]{
                R.drawable.ic_qipao_fen_1, R.drawable.ic_qipao_fen_2, R.drawable.ic_qipao_fen_3,
                R.drawable.ic_qipao_hong_1, R.drawable.ic_qipao_hong_2, R.drawable.ic_qipao_hong_3,
                R.drawable.ic_qipao_huang_1, R.drawable.ic_qipao_huang_2, R.drawable.ic_qipao_huang_3,
                R.drawable.ic_qipao_ju_1, R.drawable.ic_qipao_ju_2, R.drawable.ic_qipao_ju_3,
                R.drawable.ic_qipao_lan_1, R.drawable.ic_qipao_lan_2, R.drawable.ic_qipao_lan_3,
                R.drawable.ic_qipao_lv_1, R.drawable.ic_qipao_lv_2, R.drawable.ic_qipao_lv_3,
                R.drawable.ic_qipao_zi_1, R.drawable.ic_qipao_zi_2, R.drawable.ic_qipao_zi_3
        };

        private WeakHashMap<Integer, Bitmap> bitmapWeakHashMap = new WeakHashMap<>();//位图缓存
        private LinkedList<Heart> cacheHearts = new LinkedList<>();//缓存
        private Random random = new Random();

        /**
         * 创建自定义漂浮物
         */
        public Heart createHeart(Context context, Bitmap bitmap) {
            return create(bitmap);
        }

        /**
         * 创建随机默认飘星
         */
        public Heart createHeart(Context context) {
            Bitmap bitmap = getRandomBitmap(context);
            return create(bitmap);
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
         * 创建漂浮对象，管理对象池
         */
        public Heart create(Bitmap bitmap) {
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
         * 回收对象
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