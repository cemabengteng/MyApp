package com.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.view.utils.ViewUtils;

/**
 * 一个自定义的titlebar
 * author: liutao
 * date: 2016/6/13.
 */
public class TitleBarView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener {

    private AppCompatCheckedTextView mTitleCtv;
    private AppCompatCheckedTextView mLeftCtv;
    private AppCompatCheckedTextView mRightCtv;
    private RelativeLayout rlView;
    private TitleBarListener titleBarListener;

    //默认字体大小
    private static final int Titlebar_titlebar_leftAndRightTextSize = 12;
    private static final int Titlebar_titlebar_titleTextSize = 16;

    //默认间距
    private static final int Titlebar_titlebar_titleDrawablePadding = 3;
    private static final int Titlebar_titlebar_leftDrawablePadding = 3;
    private static final int Titlebar_titlebar_rightDrawablePadding = 3;
    private static final int Titlebar_titlebar_leftAndRightPadding = 10;

    //默认长度
    private static final int Titlebar_titlebar_leftMaxWidth = 85;
    private static final int Titlebar_titlebar_rightMaxWidth = 85;
    private static final int Titlebar_titlebar_titleMaxWidth = 144;

    //默认是否粗体
    private static final boolean Titlebar_titlebar_isTitleTextBold = true;
    private static final boolean Titlebar_titlebar_isLeftTextBold = false;
    private static final boolean Titlebar_titlebar_isRightTextBold = false;
    // 默认有分割线
     private static final boolean Titlebar_titlebar_divider = true;


    //逻辑
    //判断双击事件
    private static final int DoubleClickTime = 500;
    long[] mClicks = new long[2];
    boolean canDoubleClick = true;//单击事件与双击事件冲突,只启用一个
    private static int DOUBLECLICK_INTERVAL=1000;//两次双击事件的间隔时间

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_titlebar, this);
        initView();
        setListener();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Titlebar);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }


    protected void initView() {
        rlView = getViewById(R.id.rlView);
        mLeftCtv = getViewById(R.id.ctv_titlebar_left);
        mRightCtv = getViewById(R.id.ctv_titlebar_right);
        mTitleCtv = getViewById(R.id.ctv_titlebar_title);
    }

    protected void setListener() {
        mLeftCtv.setOnClickListener(this);
        mTitleCtv.setOnClickListener(this);
        mRightCtv.setOnClickListener(this);
        mTitleCtv.setOnTouchListener(this);
    }

    protected void initAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.Titlebar_titlebar_leftText) {
            setLeftText(typedArray.getText(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_titleText) {
            setTitleText(typedArray.getText(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_rightText) {
            setRightText(typedArray.getText(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_leftDrawable) {
            setLeftDrawable(typedArray.getDrawable(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_titleDrawable) {
            setTitleDrawable(typedArray.getDrawable(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_rightDrawable) {
            setRightDrawable(typedArray.getDrawable(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_leftAndRightTextSize) {
            int textSize = typedArray.getDimensionPixelSize(attr, ViewUtils.sp2px(getContext(), Titlebar_titlebar_leftAndRightTextSize));
            mLeftCtv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            mRightCtv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        } else if (attr == R.styleable.Titlebar_titlebar_titleTextSize) {
            mTitleCtv.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimensionPixelSize(attr, ViewUtils.sp2px(getContext(), Titlebar_titlebar_titleTextSize)));
        } else if (attr == R.styleable.Titlebar_titlebar_leftAndRightTextColor) {
            mLeftCtv.setTextColor(typedArray.getColorStateList(attr));
            mRightCtv.setTextColor(typedArray.getColorStateList(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_titleTextColor) {
            mTitleCtv.setTextColor(typedArray.getColorStateList(attr));
        } else if (attr == R.styleable.Titlebar_titlebar_titleDrawablePadding) {
            mTitleCtv.setCompoundDrawablePadding(typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_titleDrawablePadding)));
        } else if (attr == R.styleable.Titlebar_titlebar_leftDrawablePadding) {
            mLeftCtv.setCompoundDrawablePadding(typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_leftDrawablePadding)));
        } else if (attr == R.styleable.Titlebar_titlebar_rightDrawablePadding) {
            mRightCtv.setCompoundDrawablePadding(typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_rightDrawablePadding)));
        } else if (attr == R.styleable.Titlebar_titlebar_leftAndRightPadding) {
            int leftAndRightPadding = typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_leftAndRightPadding));
            mLeftCtv.setPadding(leftAndRightPadding, 0, leftAndRightPadding, 0);
            mRightCtv.setPadding(leftAndRightPadding, 0, leftAndRightPadding, 0);
        } else if (attr == R.styleable.Titlebar_titlebar_leftMaxWidth) {
            setLeftCtvMaxWidth(typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_leftMaxWidth)));
        } else if (attr == R.styleable.Titlebar_titlebar_rightMaxWidth) {
            setRightCtvMaxWidth(typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_rightMaxWidth)));
        } else if (attr == R.styleable.Titlebar_titlebar_titleMaxWidth) {
            setTitleCtvMaxWidth(typedArray.getDimensionPixelSize(attr, ViewUtils.dp2px(getContext(), Titlebar_titlebar_titleMaxWidth)));
        } else if (attr == R.styleable.Titlebar_titlebar_isTitleTextBold) {
            mTitleCtv.getPaint().setFakeBoldText(typedArray.getBoolean(attr, Titlebar_titlebar_isTitleTextBold));
        } else if (attr == R.styleable.Titlebar_titlebar_isLeftTextBold) {
            mLeftCtv.getPaint().setFakeBoldText(typedArray.getBoolean(attr, Titlebar_titlebar_isLeftTextBold));
        } else if (attr == R.styleable.Titlebar_titlebar_isRightTextBold) {
            mRightCtv.getPaint().setFakeBoldText(typedArray.getBoolean(attr, Titlebar_titlebar_isRightTextBold));
        }else if(attr==R.styleable.Titlebar_titlebar_divider){//分割线

        }
    }

    public void setLeftCtvMaxWidth(int maxWidth) {
        mLeftCtv.setMaxWidth(maxWidth);
    }

    public void setRightCtvMaxWidth(int maxWidth) {
        mRightCtv.setMaxWidth(maxWidth);
    }

    public void setTitleCtvMaxWidth(int maxWidth) {
        mTitleCtv.setMaxWidth(maxWidth);
    }

    public void hiddenLeftCtv() {
        mLeftCtv.setVisibility(GONE);
    }

    public void showLeftCtv() {
        mLeftCtv.setVisibility(VISIBLE);
    }

    public void setLeftText(@StringRes int resid) {
        setLeftText(getResources().getString(resid));
    }

    public void setLeftText(CharSequence text) {
        mLeftCtv.setText(text);
        showLeftCtv();
    }

    public void setLeftDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mLeftCtv.setCompoundDrawables(drawable, null, null, null);
        showLeftCtv();
    }

    public void hiddenTitleCtv() {
        mTitleCtv.setVisibility(GONE);
    }

    public void showTitleCtv() {
        mTitleCtv.setVisibility(VISIBLE);
    }

    public void setTitleText(CharSequence text) {
        mTitleCtv.setText(text);
        showTitleCtv();
    }

    public void setTitleText(@StringRes int resid) {
        setTitleText(getResources().getString(resid));
    }

    public void setTitleDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mTitleCtv.setCompoundDrawables(null, null, drawable, null);
        showTitleCtv();
    }

    public void hiddenRightCtv() {
        mRightCtv.setVisibility(GONE);
    }

    public void showRightCtv() {
        mRightCtv.setVisibility(VISIBLE);
    }

    public void setRightText(CharSequence text) {
        mRightCtv.setText(text);
        showRightCtv();
    }

    public void setRightText(@StringRes int resid) {
        setRightText(getResources().getString(resid));
    }

    public void setRightDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mRightCtv.setCompoundDrawables(null, null, drawable, null);
        showRightCtv();
    }

    public void setLeftCtvChecked(boolean checked) {
        mLeftCtv.setChecked(checked);
    }

    public void setTitleCtvChecked(boolean checked) {
        mTitleCtv.setChecked(checked);
    }

    public void setRightCtvChecked(boolean checked) {
        mRightCtv.setChecked(checked);
    }

    public AppCompatCheckedTextView getLeftCtv() {
        return mLeftCtv;
    }

    public AppCompatCheckedTextView getRightCtv() {
        return mRightCtv;
    }

    public AppCompatCheckedTextView getTitleCtv() {
        return mTitleCtv;
    }

    public void setTitleBarListener(TitleBarListener titleBarListener) {
        this.titleBarListener = titleBarListener;
    }

    public void setCanDoubleClick(boolean canDoubleClick) {
        this.canDoubleClick = canDoubleClick;
    }

    @Override
    public void onClick(View v) {
        if (titleBarListener != null) {
            int id = v.getId();
            if (id == R.id.ctv_titlebar_left) {
                titleBarListener.onClickLeft();
            } else if (id == R.id.ctv_titlebar_title) {
                titleBarListener.onClickTitle();
            } else if (id == R.id.ctv_titlebar_right) {
                titleBarListener.onClickRight();
            }
        }
    }

    long lastDoubleClickTime=0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!canDoubleClick) {
            return false;
        }
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            System.arraycopy(mClicks, 1, mClicks, 0, mClicks.length - 1);
            mClicks[mClicks.length - 1] = System.currentTimeMillis();
            if (mClicks[0] >= (System.currentTimeMillis() - DoubleClickTime)&&mClicks[0]>lastDoubleClickTime+DOUBLECLICK_INTERVAL) {
                if (titleBarListener != null) {
                    //防止重复响应双击事件
                    lastDoubleClickTime= System.currentTimeMillis();
                    titleBarListener.onDoubleClickTitle();
                }
            }
        }
        return true;
    }


    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    /**
     * 根据实际业务重写相应地方法
     */
    public interface TitleBarListener {
        void onClickLeft();

        void onClickTitle();

        void onClickRight();

        void onDoubleClickTitle();
    }
}
