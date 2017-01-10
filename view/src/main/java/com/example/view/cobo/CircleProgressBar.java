package com.example.view.cobo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.view.R;


/**
 * Created by plu on 2016/10/8.
 */
public class CircleProgressBar extends View {
    // 画圆环的画笔
    private Paint ringPaint;
    // 背景园的画笔
    private Paint bgPaint;
    // 文字的画笔
    private Paint textPaint;
    // 圆环颜色
    private int ringColor;
    // 圆环背景颜色
    private int ringBgColor;
    // 背景颜色
    private int bgColor;
    // 半径
    private float radius;
    // 圆环宽度
    private float strokeWidth;
    // 总进度
    private int totalProgress = 100;
    // 当前进度
    private int currentProgress;
    // 透明度
//    private int alpha = 25;
    // 圆心
    private RectF oval;
    // 字的长度
    private float txtWidth;
    // 字的高度
    private float txtHeight;
    // 字
    private String txtContent = "";

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressbar, 0, 0);
        radius = typeArray.getDimension(R.styleable.CircleProgressbar_circleRadius, 80);
        strokeWidth = typeArray.getDimension(R.styleable.CircleProgressbar_ringWidth, 10);
        ringColor = typeArray.getColor(R.styleable.CircleProgressbar_ringColor, 0xFF0000);
        ringBgColor = typeArray.getColor(R.styleable.CircleProgressbar_ringBgColor, 0xFFFFFF);
        bgColor = typeArray.getColor(R.styleable.CircleProgressbar_bgColor, 0xFFFFFF);
        currentProgress = typeArray.getColor(R.styleable.CircleProgressbar_comboProgress, 0);
    }

    private void initVariable() {
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setDither(true);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setStrokeWidth(strokeWidth);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        ringPaint.setDither(true);
        ringPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(ringColor);
        textPaint.setTextSize(radius / 5 * 4);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        txtHeight = fm.descent + Math.abs(fm.ascent);
    }

    public void setProgress(int progress) {
        currentProgress = progress;
        postInvalidate();
    }

    public void setText(String text) {
        txtContent = text;
        postInvalidate();
    }

    public String getText() {
        return txtContent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 设置圆心实心背景
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius + strokeWidth / 3, bgPaint);

        txtWidth = textPaint.measureText(txtContent, 0, txtContent.length());
        canvas.drawText(txtContent, getWidth() / 2 - txtWidth / 2, getHeight() / 2 + txtHeight / 4, textPaint);

        if (currentProgress >= 0) {
            oval = new RectF(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);

            // 进度条的背景色
            ringPaint.setColor(ringBgColor);
            canvas.drawArc(oval, 0, 360, false, ringPaint);

            // 进度条，第三个参数（正/负）代表旋转方向（顺时针/逆时针）
            ringPaint.setColor(ringColor);
            canvas.drawArc(oval, -90, ((float) currentProgress / totalProgress) * 360, false, ringPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int length = (int) (radius * 2 + strokeWidth * 3); // 总宽度

        //获取宽和高的SpecMode和SpecSize
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // 重新测量宽高，解决wrap_content属性获取不到宽高的问题
        if (wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST) {
            //宽高都为wrap_content,直接指定为400
            setMeasuredDimension(length, length);

        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            //只有宽为wrap_content,宽直接指定为400，高为获取的SpecSize
            setMeasuredDimension(length, hSpecSize);

        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            //只有高为wrap_content,高直接指定为400，宽为获取的SpecSize
            setMeasuredDimension(wSpecSize, length);
        }
    }

    @Override
    public boolean callOnClick() {
        //TODO: 点击后的背景状态
        return super.callOnClick();
    }

}