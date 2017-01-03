package com.example.plu.myapp.biggift.setjson;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.biggift.Const;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;
import com.example.plu.myapp.biggift.bean.LargeGift;
import com.example.plu.myapp.biggift.show.ShowActivity;
import com.example.plu.myapp.dagger.component.CommonActivityComponent;
import com.example.plu.myapp.util.FileUtils;
import com.example.plu.myapp.util.PluLog;
import com.google.gson.Gson;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

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

    private LargeGift largeGift;
    private BigGiftConfigBean bigGiftConfigBean;

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
        largeGift = getIntent().getParcelableExtra(PATH);
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
        this.bigGiftConfigBean = bean;
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

    @OnClick({R.id.bt_start, R.id.bt_save})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start:
                boolean isHaveAni = mPersenter.checkAniFile();
                if (isHaveAni) {
                    savaJsonData();
                    Intent intent = new Intent(SetJsonActivity.this, ShowActivity.class);
                    intent.putExtra(ShowActivity.BEAN, bigGiftConfigBean);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "没找到ani文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_save:
                saveToLocal(bigGiftConfigBean);
                break;
        }
    }

    private void saveToLocal(BigGiftConfigBean bean) {
        savaJsonData();
        BigGiftConfigBean temp = null;
        try {
            temp = (BigGiftConfigBean) bean.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (temp != null) {
            temp.setName(null);
            temp.setPath(null);
            Gson gson = new Gson();
            String sjson = gson.toJson(temp);
            PluLog.d(sjson);
            FileUtils.contentToTxt(Const.JSONFILEPAHT, sjson);
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void savaJsonData() {
        String textImgName = etTextImgName.getText().toString();
        if (!TextUtils.isEmpty(textImgName) && !textImgName.equals(bigGiftConfigBean.getTextImgName())) {
            bigGiftConfigBean.setTextImgName(textImgName);
        }

        String textFontSize = etTextFontSize.getText().toString();
        if (!TextUtils.isEmpty(textFontSize) && !textImgName.equals(bigGiftConfigBean.getTextSize())) {
            bigGiftConfigBean.setTextSize(textFontSize);
        }

        if (radioRandomTrue.isChecked()) {
            bigGiftConfigBean.setRandom(true);
        } else {
            bigGiftConfigBean.setRandom(false);
        }

        try {
            String orignFramWidth = etOrignFramWidth.getText().toString();
            if (TextUtils.isEmpty(bigGiftConfigBean.getTextImgName())) return;
            int intOrignFramWidth = Integer.valueOf(orignFramWidth);
            if (intOrignFramWidth != 0 && intOrignFramWidth != (bigGiftConfigBean.getOrignFram().getWidth())) {
                bigGiftConfigBean.getOrignFram().setWidth(intOrignFramWidth);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "OrignFramWidth数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (TextUtils.isEmpty(etOrignFramHeight.getText().toString())) return;
            int orignFramHeight = Integer.valueOf(etOrignFramHeight.getText().toString());
            if (orignFramHeight != 0 && orignFramHeight != (bigGiftConfigBean.getOrignFram().getHeight())) {
                bigGiftConfigBean.getOrignFram().setHeight(orignFramHeight);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "OrignFramHeight数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (TextUtils.isEmpty(etDisplayFrameHeightMultiby.getText().toString())) return;
            float displayFrameHeight = Float.valueOf(etDisplayFrameHeightMultiby.getText().toString());
            if (displayFrameHeight != 0 && displayFrameHeight != (bigGiftConfigBean.getDisplayFrame().getHeight().getMultiby())) {
                bigGiftConfigBean.getDisplayFrame().getHeight().setMultiby(displayFrameHeight);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameHeight数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (TextUtils.isEmpty(etDisplayFrameHeightOffset.getText().toString())) return;
            int displayFrameHeightOffset = Integer.valueOf(etDisplayFrameHeightOffset.getText().toString());
            if (displayFrameHeightOffset != 0 && displayFrameHeightOffset != (bigGiftConfigBean.getDisplayFrame().getHeight().getOffset())) {
                bigGiftConfigBean.getDisplayFrame().getHeight().setOffset(displayFrameHeightOffset);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameHeightOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (TextUtils.isEmpty(etDisplayFrameWidthMultiby.getText().toString())) return;
            Float displayFrameWidthMultiby = Float.valueOf(etDisplayFrameWidthMultiby.getText().toString());
            if (displayFrameWidthMultiby != 0 && displayFrameWidthMultiby != (bigGiftConfigBean.getDisplayFrame().getWidth().getMultiby())) {
                bigGiftConfigBean.getDisplayFrame().getWidth().setMultiby(displayFrameWidthMultiby);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameWidthMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (TextUtils.isEmpty(etDisplayFrameWidthOffset.getText().toString())) return;
            Integer displayFrameWidthOffset = Integer.valueOf(etDisplayFrameWidthOffset.getText().toString());
            if (displayFrameWidthOffset != 0 && displayFrameWidthOffset != (bigGiftConfigBean.getDisplayFrame().getWidth().getOffset())) {
                bigGiftConfigBean.getDisplayFrame().getWidth().setOffset(displayFrameWidthOffset);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameWidthOffset数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (TextUtils.isEmpty(etDdgesCenterXLandMultiby.getText().toString())) return;
            Float ddgesCenterXLandMultiby = Float.valueOf(etDdgesCenterXLandMultiby.getText().toString());
            if (ddgesCenterXLandMultiby != 0 && ddgesCenterXLandMultiby != (bigGiftConfigBean.getEdges().getCenterX().getLandMultiby())) {
                bigGiftConfigBean.getEdges().getCenterX().setLandMultiby(ddgesCenterXLandMultiby);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "ddgesCenterXLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (TextUtils.isEmpty(etEdgesCenterXLandOffset.getText().toString())) return;
            Integer edgesCenterXLandOffset = Integer.valueOf(etEdgesCenterXLandOffset.getText().toString());
            if (edgesCenterXLandOffset != 0 && edgesCenterXLandOffset != (bigGiftConfigBean.getEdges().getCenterX().getLandOffset())) {
                bigGiftConfigBean.getEdges().getCenterX().setLandOffset(edgesCenterXLandOffset);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterXLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (TextUtils.isEmpty(etEdgesCenterXPortMultiby.getText().toString())) return;
            Float edgesCenterXPortMultiby = Float.valueOf(etEdgesCenterXPortMultiby.getText().toString());
            if (edgesCenterXPortMultiby != 0 && edgesCenterXPortMultiby != (bigGiftConfigBean.getEdges().getCenterX().getPortMultiby())) {
                bigGiftConfigBean.getEdges().getCenterX().setPortMultiby(edgesCenterXPortMultiby);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterXPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (TextUtils.isEmpty(etEdgesCenterXPortOffset.getText().toString())) return;
            Integer edgesCenterXPortOffset = Integer.valueOf(etEdgesCenterXPortOffset.getText().toString());
            if (edgesCenterXPortOffset != 0 && edgesCenterXPortOffset != (bigGiftConfigBean.getEdges().getCenterX().getPortOffset())) {
                bigGiftConfigBean.getEdges().getCenterX().setPortOffset(edgesCenterXPortOffset);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterXPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }
    }
}
