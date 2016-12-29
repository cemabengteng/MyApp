package com.example.plu.myapp.biggift.setjson;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.dagger.component.CommonActivityComponent;
import com.example.plu.myapp.util.PluLog;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/12/28.
 */

public class SetJsonActivity extends MvpActivity<CommonActivityComponent, SetJsonPersenter> implements SetJsonView {

    public static String PATH = "swf_path";

    @Inject
    SetJsonPersenter mPersenter;

    @Bind(R.id.et_text_img_name)
    EditText etTextImgName;
    @Bind(R.id.et_text_font_size)
    EditText etTextFontSize;
    @Bind(R.id.radio_random_true)
    RadioButton radioRandomTrue;
    @Bind(R.id.radio_random_false)
    RadioButton radioRandomFalse;
    @Bind(R.id.et_orign_fram_width)
    EditText etOrignFramWidth;
    @Bind(R.id.et_orign_fram_height)
    EditText etOrignFramHeight;
    @Bind(R.id.et_display_frame_height_multiby)
    EditText etDisplayFrameHeightMultiby;
    @Bind(R.id.et_display_frame_height_offset)
    EditText etDisplayFrameHeightOffset;
    @Bind(R.id.et_edges_centerX_landMultiby)
    EditText etDdgesCenterXLandMultiby;
    @Bind(R.id.et_edges_centerX_landOffset)
    EditText etEdgesCenterXLandOffset;
    @Bind(R.id.et_edges_centerX_portMultiby)
    EditText etEdgesCenterXPortMultiby;
    @Bind(R.id.et_edges_centerX_portOffset)
    EditText etEdgesCenterXPortOffset;
    @Bind(R.id.et_display_frame_width_multiby)
    EditText etDisplayFrameWidthMultiby;
    @Bind(R.id.et_display_frame_width_offset)
    EditText etDisplayFrameWidthOffset;

    @Override
    protected void initInject() {
        initCommon().inject(this);
    }

    @Override
    protected SetJsonPersenter createPresenter() {
        return mPersenter;
    }

    @Override
    protected void initData(Bundle state) {
        LargeGift largeGift = getIntent().getParcelableExtra(PATH);
        PluLog.d(largeGift);
        mPersenter.loadJson(largeGift);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_set_json);
    }

    @Override
    public void onLoadBigGiftJson(boolean isSuccess, BigGiftConfigBean bean) {
        if (isSuccess) {
            initJson(bean);
        } else {
            Toast.makeText(mContext, "json获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initJson(BigGiftConfigBean bean) {
        etTextImgName.setText(bean.getTextImgName());
        etTextFontSize.setText(String.valueOf(bean.getTextSize()));
        if (bean.isRandom()) {
            radioRandomTrue.setChecked(true);
        } else {
            radioRandomFalse.setChecked(true);
        }
        etOrignFramWidth.setText(String.valueOf(bean.getOrignFram().getWidth()));
        etOrignFramHeight.setText(String.valueOf(bean.getOrignFram().getHeight()));
        if (bean.getDisplayFrame().getHeight() != null) {
            etDisplayFrameHeightMultiby.setText(String.valueOf(bean.getDisplayFrame().getHeight().getMultiby()));
            etDisplayFrameHeightOffset.setText(String.valueOf(bean.getDisplayFrame().getHeight().getOffset()));
        } else {
            etDisplayFrameWidthMultiby.setText(String.valueOf(bean.getDisplayFrame().getWidth().getMultiby()));
            etDisplayFrameWidthOffset.setText(String.valueOf(bean.getDisplayFrame().getWidth().getOffset()));
        }
        etDdgesCenterXLandMultiby.setText(String.valueOf(bean.getEdges().getCenterX().getLandMultiby()));
        etEdgesCenterXLandOffset.setText(String.valueOf(bean.getEdges().getCenterX().getLandOffset()));
        etEdgesCenterXPortMultiby.setText(String.valueOf(bean.getEdges().getCenterX().getPortMultiby()));
        etEdgesCenterXPortOffset.setText(String.valueOf(bean.getEdges().getCenterX().getPortOffset()));
    }
}
