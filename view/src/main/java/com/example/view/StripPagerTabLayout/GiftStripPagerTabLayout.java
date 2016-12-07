package com.example.view.StripPagerTabLayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.view.R;

import java.util.List;

/**
 * Created by liuj on 2016/2/22.
 */
public class GiftStripPagerTabLayout extends HorizontalScrollView implements PagerTabLayout {

    private static final int DEFAULT_VISIBLE_COUNT = 4;

    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;


    private OnTabClickListener onTabClickListener;
    private ViewPager.OnPageChangeListener mPageChangeListener;
    private ViewPager mViewPager;
    private IndicatorLinearLayout indicatorLinearLayout;

    private int visibleCount;
    private int mCurrentPos;
    private int mTabLayoutWidth;
    private int titleTextSize;
    private ColorStateList titleColors;
    private int tabBackgroundResourceId;
    private int gravity;
    private int pagerState;
    private boolean isDrag;

    public GiftStripPagerTabLayout(Context context) {
        this(context, null);
    }

    public GiftStripPagerTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftStripPagerTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContent(context, attrs, defStyleAttr);
    }

    private void initContent(Context context, AttributeSet attrs, int defStyleAttr) {
        handleAttrs(context, attrs, defStyleAttr);
        if (gravity == GRAVITY_CENTER) {
            setFillViewport(true);
        }
        indicatorLinearLayout = new IndicatorLinearLayout(context, attrs, defStyleAttr);
        addView(indicatorLinearLayout, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    }

    private void handleAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GiftStripPagerTabLayout);
        visibleCount = typedArray.getInt(R.styleable.GiftStripPagerTabLayout_StripPager_visible_count, DEFAULT_VISIBLE_COUNT);
        titleColors = typedArray.getColorStateList(R.styleable.GiftStripPagerTabLayout_StripPager_text_color);
        if (titleColors == null) {
            int titleSelectedColor = typedArray.getColor(R.styleable.GiftStripPagerTabLayout_StripPager_text_selected_color, getResources().getColor(R.color.default_title_selected_color));
            int titleUnselectedColor = typedArray.getColor(R.styleable.GiftStripPagerTabLayout_StripPager_text_default_color, getResources().getColor(R.color.default_title_unselected_color));
            titleColors = createColorStateList(titleUnselectedColor, titleSelectedColor);
        }
        titleTextSize = typedArray.getDimensionPixelSize(R.styleable.GiftStripPagerTabLayout_StripPager_text_size, 0);
        tabBackgroundResourceId = typedArray.getResourceId(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_tab_background, -1);
        gravity = typedArray.getInt(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_gravity, GRAVITY_LEFT);
        typedArray.recycle();
    }


    @Override
    public void setOnPageChangedListener(ViewPager.OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    @Override
    public void setCurrentItem(int pos) {
        mCurrentPos = pos;
        View childTab = pos == 0 ? indicatorLinearLayout.getChildAt(0) : indicatorLinearLayout.getChildAt(pos - 1);
        if (childTab.getLeft() != getScrollX()) {
            smoothScrollTo(childTab.getLeft(), 0);
        }
        indicatorLinearLayout.setSelectedTab(pos);
    }

    @Override
    public void notifyDataSetChanged() {
        indicatorLinearLayout.removeAllViews();
        if (mViewPager != null) {
            PagerAdapter pagerAdapter = mViewPager.getAdapter();
            if (pagerAdapter != null) {
                for (int i = 0; i < pagerAdapter.getCount(); i++) {
                    Tab tab = TabBuilder.createTab(i, pagerAdapter, indicatorLinearLayout);
                    addTab(tab);
                }
            }
        }
        scrollTo(0, 0);
        mCurrentPos = 0;
        indicatorLinearLayout.reset();
        requestLayout();
    }

    public void notifyDataSetChangedWithoutAdapter() {
        indicatorLinearLayout.removeAllViews();
        if (mDataLists != null && mDataLists.size() > 0) {
            for (int i = 0; i < mDataLists.size(); i++) {
                Tab tab = TabBuilder.createTabByDataList(i, mDataLists);
                addTab(tab);
            }
        }

        scrollTo(0, 0);
        mCurrentPos = 0;
        indicatorLinearLayout.reset();
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            return;
        }
        this.mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    private List<String> mDataLists;

    public void setData(List<String> list) {
        mDataLists = list;
        notifyDataSetChangedWithoutAdapter();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
        indicatorLinearLayout.updateIndicatorByTabPos(position, positionOffset);
    }


    @Override
    public void onPageSelected(int position) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageSelected(position);
        }
        setCurrentItem(position);
        indicatorLinearLayout.setSelectedTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrollStateChanged(state);
        }
        pagerState = state;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        adjustTabViewWidth(w);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        adjustTabViewWidth(getMeasuredWidth());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        super.onRestoreInstanceState(state);
    }

    private boolean isDrag() {
        return isDrag;
    }

    /**
     * 调整tabView的宽度
     *
     * @param newTabLayoutWidth
     */
    private void adjustTabViewWidth(int newTabLayoutWidth) {
        int newTabViewWidth = newTabLayoutWidth / visibleCount;
        if (mTabLayoutWidth != newTabViewWidth && newTabViewWidth > 0) {
            mTabLayoutWidth = newTabViewWidth;
            for (int i = 0; i < indicatorLinearLayout.getChildCount(); i++) {
                View tabView = indicatorLinearLayout.getChildAt(i);
                if (tabView != null) {
                    tabView.getLayoutParams().width = mTabLayoutWidth;
                }
            }
        }
    }

    private void addTab(final Tab tab) {
//        Log.d("pager", "addTab");
        if (tab == null || !tab.isValid()) {
            return;
        }
        View tabView = tab.customView;
        if (tabView == null) {
            tabView = new TabView(getContext(), tab);
        }
//        Log.d("pager", "mTabLayoutWidth" + mTabLayoutWidth);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mTabLayoutWidth > 0 ? mTabLayoutWidth : -2, -1);
        indicatorLinearLayout.addView(tabView, params);
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = indicatorLinearLayout.indexOfChild(v);
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClickListener(tab, pos);
                }
                mCurrentPos = pos;
                setCurrentItem(mCurrentPos);
                indicatorLinearLayout.setSelectedTab(mCurrentPos);

            }
        });
    }

    public void setVisibleTabCount(int count) {
        this.visibleCount = count;
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        states[i] = PRESSED_ENABLED_SELECTED_STATE_SET;
        colors[i] = selectedColor;

        return new ColorStateList(states, colors);
    }


    private class IndicatorLinearLayout extends LinearLayout {

        private static final int INDICATOR_FLEX_WIDTH = 0;
        private static final int INDICATOR_FIX_WIDTH = 1;
        private static final int INDICATOR_FILL_WIDTH = 2;

        private static final int INDICATOR_EXTRA_DISTANCE = 2;

        private int indicatorMode;
        private int indicatorPadding;
        private boolean hideIndicator;
        private int selectedPos;
        private float selectedOffset;
        private int left;
        private int height;
        private int width;
        private int color;
        private Paint paint;

        private View currentTabView;

        public IndicatorLinearLayout(Context context) {
            this(context, null);
        }

        public IndicatorLinearLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public IndicatorLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            Resources resources = context.getResources();
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GiftStripPagerTabLayout);
            color = typedArray.getColor(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_color, resources.getColor(R.color.default_strip_indicator_color));
            hideIndicator = typedArray.getBoolean(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_hide_indicator, false);
            indicatorPadding = typedArray.getDimensionPixelSize(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_padding, 0);
            if (!hideIndicator) {
                indicatorMode = typedArray.getInt(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_width_mode, INDICATOR_FLEX_WIDTH);
                height = typedArray.getDimensionPixelOffset(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_height, resources.getDimensionPixelOffset(R.dimen.default_strip_indicator_height));
                width = typedArray.getDimensionPixelOffset(R.styleable.GiftStripPagerTabLayout_StripPagerIndicator_width, 0);
                if (width == 0) {
                    if (indicatorMode != INDICATOR_FILL_WIDTH) {
                        indicatorMode = INDICATOR_FLEX_WIDTH;
                    }
                } else {
                    indicatorMode = INDICATOR_FIX_WIDTH;
                }

            }
            typedArray.recycle();
            init();
        }

        private void init() {
            setGravity(Gravity.CENTER_HORIZONTAL);
            setOrientation(LinearLayout.HORIZONTAL);
            setWillNotDraw(false);
            paint = new Paint();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (getChildCount() > 0) {
                updateIndicator();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (hideIndicator) {
                return;
            }
            if (getChildCount() > 0) {
                int top = getHeight() - height;
                canvas.drawRect(left, top, left + width, top + getHeight(), paint);
            }

        }

        private void reset() {
            selectedPos = 0;
            selectedOffset = 0;
            left = width == 0 ? 0 : indicatorPadding;
            if (indicatorMode == INDICATOR_FLEX_WIDTH) {
                width = 0;
            }
            currentTabView = null;
            if (getChildCount() > 0) {
                setSelectedTab(getChildAt(0));
            }
        }

        private View lastView = null;

        private void setSelectedTab(View tabView) {
            if (currentTabView != tabView) {
                if (null != currentTabView) {
                    currentTabView.setSelected(false);
                }
                tabView.setSelected(true);
                currentTabView = tabView;
            }

            if (lastView != null) {
                lastView.setBackgroundColor(Color.WHITE);
            }
            tabView.setBackgroundColor(Color.BLACK);
            lastView = tabView;
        }

        private void setSelectedTab(int selectedPos) {
            View tabView = getChildAt(selectedPos);
            setSelectedTab(tabView);
        }

        private void updateIndicatorByTabPos(int selectedPos, float offset) {
            this.selectedPos = selectedPos;
            this.selectedOffset = offset;
            updateIndicator();
        }


        private void updateIndicator() {
            if (hideIndicator) {
                return;
            }
            if (getChildCount() < selectedPos + 1) {
                return;
            }
            View child = getChildAt(selectedPos);
            if (indicatorMode == INDICATOR_FLEX_WIDTH) {
                if (selectedPos == getChildCount() - 1) {
                    width = getIndicatorLengthFromTabTitle(child);
                } else {
                    View nextChild = getChildAt(selectedPos + 1);
                    if (child instanceof TabView) {
                        int currentlength = getIndicatorLengthFromTabTitle(child);
                        int nextlength = getIndicatorLengthFromTabTitle(nextChild);
                        width = (int) ((nextlength - currentlength) * selectedOffset + currentlength);
                    }
                }
            }

            int padding = 0;
            if (width == 0) {
                padding = indicatorPadding;
                width = child.getRight() - child.getLeft() - 2 * padding;
            }

            left = (int) (padding + child.getLeft() + child.getMeasuredWidth() * selectedOffset + child.getMeasuredWidth() / 2 - width / 2);

            ViewCompat.postInvalidateOnAnimation(this);
        }

        private int getIndicatorLengthFromTabTitle(View tabView) {
            if (tabView instanceof TabView) {
                int titleLength = ((TabView) tabView).getTextLength();
                int extraDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_EXTRA_DISTANCE, getResources().getDisplayMetrics());
                return titleLength == 0 ? 0 : titleLength + extraDistance;
            }
            return 0;
        }
    }


    private class TabView extends LinearLayout {

        private Tab tab;
        private TextView textView;
        private LayoutInflater layoutInflater;

        public TabView(Context context, Tab tab) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.default_strip_tablayout_padding));
            if (tabBackgroundResourceId != -1) {
                setBackgroundResource(tabBackgroundResourceId);
            }
            this.tab = tab;
            layoutInflater = LayoutInflater.from(context);
            update();
        }

        private void update() {
            if (tab != null) {
                if (tab.iconResId != -1) {
                    ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.strip_tab_item_img, this, false);
                    imageView.setImageResource(tab.iconResId);
                    addView(imageView);
                }
                if (!TextUtils.isEmpty(tab.title)) {
                    textView = (TextView) layoutInflater.inflate(R.layout.strip_tab_item_title, this, false);
                    textView.setText(tab.title);
                    textView.setTextColor(titleColors);
                    if (titleTextSize != 0) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
                    }
                    addView(textView);
                }
            }
            if (tab.selected) {
                setSelected(true);
            }
        }

        private int getTextLength() {
            if (textView == null) {
                return 0;
            }
            return (int) textView.getPaint().measureText(textView.getText() + "");
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            tab.setSelected(selected);
        }
    }

    /**
     * tab点击回调
     */
    public interface OnTabClickListener {

        void onTabClickListener(Tab tab, int pos);

    }
}

