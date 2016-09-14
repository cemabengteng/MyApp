package com.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;

/**
 * Created by liuj on 2016/6/13.
 * 通用的内容布局
 */
public class CommonContainer extends RelativeLayout implements View.OnClickListener {

    private Status currentStatus = Status.DEFAULT;

    private View contentView;
    private View emptyView;
    private View errorView;
    private View loadingView;

    public CommonContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContent(context, attrs);
    }

    /**
     * 初始化内容
     *
     * @param context
     * @param attrs
     */
    private void initContent(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonContainer);
        attachViewStub(context, R.id.empty_content, R.styleable.CommonContainer_cc_layout_empty, typedArray); //空
        attachViewStub(context, R.id.error_content, R.styleable.CommonContainer_cc_layout_error, typedArray); //错误
        attachViewStub(context, R.id.loading_content, R.styleable.CommonContainer_cc_layout_loading, typedArray); //加载
        typedArray.recycle();
    }


    /**
     * 添加viewStub
     *
     * @param context
     * @param layoutId
     * @param attValue
     * @param typedArray
     */
    private void attachViewStub(Context context, int layoutId, int attValue, TypedArray typedArray) {
        //防止过度绘制，减少层次，故处理比较复杂
        int resourceId = typedArray.getResourceId(attValue, 0);
        if (resourceId != 0) {
            ViewStub viewStub = new ViewStub(context);
            viewStub.setLayoutResource(resourceId);
            viewStub.setId(layoutId);
            addView(viewStub);
        }
    }

    private void attachViewStub(View v, int layoutId, int resourceId) {
        if (v != null) {
            removeView(v);
        }
        ViewStub view = (ViewStub) findViewById(resourceId);
        view.setLayoutResource(layoutId);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = findViewById(R.id.content);
        for (int i=0,len=getChildCount();i<len;i++){
            View childView=getChildAt(i);
            if(childView!=contentView){
                childView.bringToFront();
            }
        }
    }

    /**
     * 添加内容
     *
     * @param view
     */
    public void addContentView(View view) {
        if (contentView != null && view != contentView) {
            removeView(contentView);
        }
        addView(view);
        this.contentView = view;
    }

    /**
     * 设置当前状态
     *
     * @param status
     */
    public void setStatus(Status status) {
        setStatus(true, status);
    }

    public void setStatus(boolean hideAll, Status status) {
        currentStatus = status;
        if (hideAll) {
            hideAllView();
        }
        switch (status) {
            case DEFAULT:
                showContentView();
                break;
            case LOADING:
                showLoadingView();
                break;
            case ERROR:
                showErrorView();
                break;
            case EMPTY:
                showEmptyView();
                break;
        }
    }

    /**
     * 初始化某个状态，后可设置其值
     *
     * @param status
     */
    public void inflateStatus(Status status) {
        switch (status) {
            case ERROR:
                if (errorView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.error_content);
                    errorView = viewStub.inflate();
                }
                break;
            case EMPTY:
                if (emptyView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.empty_content);
                    emptyView = viewStub.inflate();
                }
                break;
            case LOADING:
                if (loadingView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.loading_content);
                    loadingView = viewStub.inflate();
                }
        }
    }
    public View getStatusView(Status status){
        inflateStatus(status);
        switch (status) {
            case ERROR:
                return  errorView;
            case EMPTY:
                return emptyView;
            case LOADING:
                return loadingView;
        }
        return null;
    }

    /**
     * 隐藏所有内容
     */
    public void hideAllView() {
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示内容
     */
    public void showContentView() {
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示加载内容
     */
    public void showLoadingView() {
        if (loadingView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.loading_content);
            loadingView = viewStub.inflate();
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示空内容
     */
    public void showEmptyView() {
        if (emptyView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.empty_content);
            emptyView = viewStub.inflate();
        }
        if (emptyView != null) {
            Log.e("sss","显示空布局");
            emptyView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 显示错误内容
     */
    public void showErrorView() {
        if (errorView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.error_content);
            errorView = viewStub.inflate();
        }
        if (errorView != null) {
            View errorBtn = errorView.findViewById(R.id.error_btn);
            if (null != errorBtn) {
                errorBtn.setOnClickListener(this);
            } else {
                errorView.setOnClickListener(this);
            }
            errorView.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    public void setEmptyView(@LayoutRes int layout) {
        attachViewStub(emptyView, layout, R.id.empty_content);
    }

    public void setLoadingView(@LayoutRes int layout) {
        attachViewStub(loadingView, layout, R.id.loading_content);
    }

    public void setErrorView(@LayoutRes int layout) {
        attachViewStub(errorView, layout, R.id.error_content);
    }

    @Override
    public void onClick(View v) {
        if (null == commonView) {
            return;
        }
        commonView.onErrorClick(v);
    }

    CommonView commonView;

    public void setCommonView(CommonView commonView) {
        this.commonView = commonView;
    }

    public interface CommonView {
        void setErrorView(@LayoutRes int res);

        void setLoadingView(@LayoutRes int res);

        void setEmptyView(@LayoutRes int res);

        void onErrorClick(View view);
    }

    public static class SimpleCommonView implements CommonView {

        @Override
        public void setErrorView(@LayoutRes int res) {

        }

        @Override
        public void setLoadingView(@LayoutRes int res) {

        }

        @Override
        public void setEmptyView(@LayoutRes int res) {

        }

        @Override
        public void onErrorClick(View view) {

        }
    }

    /**
     * 状态
     */
    public enum Status {
        DEFAULT,
        EMPTY,
        LOADING,
        ERROR
    }

}
