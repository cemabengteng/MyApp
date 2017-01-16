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

import java.math.BigDecimal;

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
    @Bind(R.id.et_display_frame_width_multiby)
    EditText etDisplayFrameWidthMultiby;
    @Bind(R.id.et_display_frame_width_offset)
    EditText etDisplayFrameWidthOffset;

    @Bind(R.id.et_edges_centerX_landMultiby)
    EditText etDdgesCenterXLandMultiby;
    @Bind(R.id.et_edges_centerX_landOffset)
    EditText etEdgesCenterXLandOffset;
    @Bind(R.id.et_edges_centerX_portMultiby)
    EditText etEdgesCenterXPortMultiby;
    @Bind(R.id.et_edges_centerX_portOffset)
    EditText etEdgesCenterXPortOffset;

    @Bind(R.id.et_edges_centerY_landMultiby)
    EditText etDdgesCenterYLandMultiby;
    @Bind(R.id.et_edges_centerY_landOffset)
    EditText etEdgesCenterYLandOffset;
    @Bind(R.id.et_edges_centerY_portMultiby)
    EditText etEdgesCenterYPortMultiby;
    @Bind(R.id.et_edges_centerY_portOffset)
    EditText etEdgesCenterYPortOffset;

    @Bind(R.id.edges_bottom_landMultiby)
    EditText etEdgesBottomLandMultiby;
    @Bind(R.id.edges_bottom_landOffset)
    EditText etEdgesBottomLandOffset;
    @Bind(R.id.edges_bottom_portMultiby)
    EditText etEdgesBottomPortMultiby;
    @Bind(R.id.edges_bottom_portOffset)
    EditText etEdgesBottomPortOffset;

    @Bind(R.id.edges_right_landMultiby)
    EditText etEdgesRightLandMultiby;
    @Bind(R.id.edges_right_landOffset)
    EditText etEdgesRightLandOffset;
    @Bind(R.id.edges_right_portMultiby)
    EditText etEdgesRightPortMultiby;
    @Bind(R.id.edges_right_portOffset)
    EditText etEdgesRightPortOffset;

    @Bind(R.id.edges_left_landMultiby)
    EditText etEdgesLeftLandMultiby;
    @Bind(R.id.edges_left_landOffset)
    EditText etEdgesLeftLandOffset;
    @Bind(R.id.edges_left_portMultiby)
    EditText etEdgesLeftPortMultiby;
    @Bind(R.id.edges_left_portOffset)
    EditText etEdgesLeftPortOffset;

    @Bind(R.id.edges_top_landMultiby)
    EditText etEdgesTopLandMultiby;
    @Bind(R.id.edges_top_landOffset)
    EditText etEdgesTopLandOffset;
    @Bind(R.id.edges_top_portMultiby)
    EditText etEdgesTopPortMultiby;
    @Bind(R.id.edges_top_portOffset)
    EditText etEdgesTopPortOffset;

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
            ((View) etDisplayFrameHeightMultiby.getParent()).setVisibility(View.GONE);
            ((View) etDisplayFrameHeightOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getDisplayFrame().getWidth() != null) {
            etDisplayFrameWidthMultiby.setText(String.valueOf(bean.getDisplayFrame().getWidth().getMultiby()));
            etDisplayFrameWidthOffset.setText(String.valueOf(bean.getDisplayFrame().getWidth().getOffset()));
        } else {
            ((View) etDisplayFrameWidthMultiby.getParent()).setVisibility(View.GONE);
            ((View) etDisplayFrameWidthOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getEdges().getCenterX() != null) {
            etDdgesCenterXLandMultiby.setText(String.valueOf(bean.getEdges().getCenterX().getLandMultiby()));
            etEdgesCenterXLandOffset.setText(String.valueOf(bean.getEdges().getCenterX().getLandOffset()));
            etEdgesCenterXPortMultiby.setText(String.valueOf(bean.getEdges().getCenterX().getPortMultiby()));
            etEdgesCenterXPortOffset.setText(String.valueOf(bean.getEdges().getCenterX().getPortOffset()));
        } else {
            ((View) etDdgesCenterXLandMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesCenterXLandOffset.getParent()).setVisibility(View.GONE);
            ((View) etEdgesCenterXPortMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesCenterXPortOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getEdges().getCenterY() != null) {
            etDdgesCenterYLandMultiby.setText(String.valueOf(bean.getEdges().getCenterY().getLandMultiby()));
            etEdgesCenterYLandOffset.setText(String.valueOf(bean.getEdges().getCenterY().getLandOffset()));
            etEdgesCenterYPortMultiby.setText(String.valueOf(bean.getEdges().getCenterY().getPortMultiby()));
            etEdgesCenterYPortOffset.setText(String.valueOf(bean.getEdges().getCenterY().getPortOffset()));
        } else {
            ((View) etDdgesCenterYLandMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesCenterYLandOffset.getParent()).setVisibility(View.GONE);
            ((View) etEdgesCenterYPortMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesCenterYPortOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getEdges().getBottom() != null) {
            etEdgesBottomLandMultiby.setText(String.valueOf(bean.getEdges().getBottom().getLandMultiby()));
            etEdgesBottomLandOffset.setText(String.valueOf(bean.getEdges().getBottom().getLandOffset()));
            etEdgesBottomPortMultiby.setText(String.valueOf(bean.getEdges().getBottom().getPortMultiby()));
            etEdgesBottomPortOffset.setText(String.valueOf(bean.getEdges().getBottom().getPortOffset()));
        } else {
            ((View) etEdgesBottomLandMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesBottomLandOffset.getParent()).setVisibility(View.GONE);
            ((View) etEdgesBottomPortMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesBottomPortOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getEdges().getRight() != null) {
            etEdgesRightLandMultiby.setText(String.valueOf(bean.getEdges().getRight().getLandMultiby()));
            etEdgesRightLandOffset.setText(String.valueOf(bean.getEdges().getRight().getLandOffset()));
            etEdgesRightPortMultiby.setText(String.valueOf(bean.getEdges().getRight().getPortMultiby()));
            etEdgesRightPortOffset.setText(String.valueOf(bean.getEdges().getRight().getPortOffset()));
        } else {
            ((View) etEdgesRightLandMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesRightLandOffset.getParent()).setVisibility(View.GONE);
            ((View) etEdgesRightPortMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesRightPortOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getEdges().getLeft() != null) {
            etEdgesLeftLandMultiby.setText(String.valueOf(bean.getEdges().getLeft().getLandMultiby()));
            etEdgesLeftLandOffset.setText(String.valueOf(bean.getEdges().getLeft().getLandOffset()));
            etEdgesLeftPortMultiby.setText(String.valueOf(bean.getEdges().getLeft().getPortMultiby()));
            etEdgesLeftPortOffset.setText(String.valueOf(bean.getEdges().getLeft().getPortOffset()));
        } else {
            ((View) etEdgesLeftLandMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesLeftLandOffset.getParent()).setVisibility(View.GONE);
            ((View) etEdgesLeftPortMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesLeftPortOffset.getParent()).setVisibility(View.GONE);
        }

        if (bean.getEdges().getTop() != null) {
            etEdgesTopLandMultiby.setText(String.valueOf(bean.getEdges().getTop().getLandMultiby()));
            etEdgesTopLandOffset.setText(String.valueOf(bean.getEdges().getTop().getLandOffset()));
            etEdgesTopPortMultiby.setText(String.valueOf(bean.getEdges().getTop().getPortMultiby()));
            etEdgesTopPortOffset.setText(String.valueOf(bean.getEdges().getTop().getPortOffset()));
        } else {
            ((View) etEdgesTopLandMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesTopLandOffset.getParent()).setVisibility(View.GONE);
            ((View) etEdgesTopPortMultiby.getParent()).setVisibility(View.GONE);
            ((View) etEdgesTopPortOffset.getParent()).setVisibility(View.GONE);
        }
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
            if (!TextUtils.isEmpty(bigGiftConfigBean.getTextImgName())) {
                int intOrignFramWidth = Integer.valueOf(orignFramWidth);
                if (intOrignFramWidth != (bigGiftConfigBean.getOrignFram().getWidth())) {
                    bigGiftConfigBean.getOrignFram().setWidth(intOrignFramWidth);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "OrignFramWidth数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etOrignFramHeight.getText().toString())) {
                int orignFramHeight = Integer.valueOf(etOrignFramHeight.getText().toString());
                if (orignFramHeight != (bigGiftConfigBean.getOrignFram().getHeight())) {
                    bigGiftConfigBean.getOrignFram().setHeight(orignFramHeight);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "OrignFramHeight数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etDisplayFrameHeightMultiby.getText().toString())) {
                double displayFrameHeight = Double.valueOf(etDisplayFrameHeightMultiby.getText().toString());
                double doubleValue = new BigDecimal(displayFrameHeight).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                displayFrameHeight = doubleValue;
                if (displayFrameHeight != (bigGiftConfigBean.getDisplayFrame().getHeight().getMultiby())) {
                    bigGiftConfigBean.getDisplayFrame().getHeight().setMultiby(displayFrameHeight);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameHeight数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etDisplayFrameHeightOffset.getText().toString())) {
                int displayFrameHeightOffset = Integer.valueOf(etDisplayFrameHeightOffset.getText().toString());
                if (displayFrameHeightOffset != (bigGiftConfigBean.getDisplayFrame().getHeight().getOffset())) {
                    bigGiftConfigBean.getDisplayFrame().getHeight().setOffset(displayFrameHeightOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameHeightOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etDisplayFrameWidthMultiby.getText().toString())) {
                double displayFrameWidthMultiby = Double.valueOf(etDisplayFrameWidthMultiby.getText().toString());
                double doubleValue = new BigDecimal(displayFrameWidthMultiby).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                displayFrameWidthMultiby = doubleValue;
                if (displayFrameWidthMultiby != (bigGiftConfigBean.getDisplayFrame().getWidth().getMultiby())) {
                    bigGiftConfigBean.getDisplayFrame().getWidth().setMultiby(displayFrameWidthMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameWidthMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etDisplayFrameWidthOffset.getText().toString())) {
                Integer displayFrameWidthOffset = Integer.valueOf(etDisplayFrameWidthOffset.getText().toString());
                if (displayFrameWidthOffset != (bigGiftConfigBean.getDisplayFrame().getWidth().getOffset())) {
                    bigGiftConfigBean.getDisplayFrame().getWidth().setOffset(displayFrameWidthOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "displayFrameWidthOffset数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (!TextUtils.isEmpty(etDdgesCenterXLandMultiby.getText().toString())) {
                double ddgesCenterXLandMultiby = Double.valueOf(etDdgesCenterXLandMultiby.getText().toString());
                double doubleValue = new BigDecimal(ddgesCenterXLandMultiby).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                ddgesCenterXLandMultiby = doubleValue;
                if (ddgesCenterXLandMultiby != (bigGiftConfigBean.getEdges().getCenterX().getLandMultiby())) {
                    bigGiftConfigBean.getEdges().getCenterX().setLandMultiby(ddgesCenterXLandMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "ddgesCenterXLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesCenterXLandOffset.getText().toString())) {
                Integer edgesCenterXLandOffset = Integer.valueOf(etEdgesCenterXLandOffset.getText().toString());
                if (edgesCenterXLandOffset != (bigGiftConfigBean.getEdges().getCenterX().getLandOffset())) {
                    bigGiftConfigBean.getEdges().getCenterX().setLandOffset(edgesCenterXLandOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterXLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (!TextUtils.isEmpty(etEdgesCenterXPortMultiby.getText().toString())) {
                double edgesCenterXPortMultiby = Double.valueOf(etEdgesCenterXPortMultiby.getText().toString());
                double doubleValue = new BigDecimal(edgesCenterXPortMultiby).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                edgesCenterXPortMultiby = doubleValue;
                if (edgesCenterXPortMultiby != (bigGiftConfigBean.getEdges().getCenterX().getPortMultiby())) {
                    bigGiftConfigBean.getEdges().getCenterX().setPortMultiby(edgesCenterXPortMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterXPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (!TextUtils.isEmpty(etEdgesCenterXPortOffset.getText().toString())) {
                Integer edgesCenterXPortOffset = Integer.valueOf(etEdgesCenterXPortOffset.getText().toString());
                if (edgesCenterXPortOffset != (bigGiftConfigBean.getEdges().getCenterX().getPortOffset())) {
                    bigGiftConfigBean.getEdges().getCenterX().setPortOffset(edgesCenterXPortOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterXPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etDdgesCenterYLandMultiby.getText().toString())) {
                double ddgesCenterYLandMultiby = Double.valueOf(etDdgesCenterYLandMultiby.getText().toString());
                double doubleValue = new BigDecimal(ddgesCenterYLandMultiby).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                ddgesCenterYLandMultiby = doubleValue;
                if (ddgesCenterYLandMultiby != (bigGiftConfigBean.getEdges().getCenterY().getLandMultiby())) {
                    bigGiftConfigBean.getEdges().getCenterY().setLandMultiby(ddgesCenterYLandMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "ddgesCenterYLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesCenterYLandOffset.getText().toString())) {
                Integer edgesCenterYLandOffset = Integer.valueOf(etEdgesCenterYLandOffset.getText().toString());
                if (edgesCenterYLandOffset != (bigGiftConfigBean.getEdges().getCenterY().getLandOffset())) {
                    bigGiftConfigBean.getEdges().getCenterY().setLandOffset(edgesCenterYLandOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterYLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (!TextUtils.isEmpty(etEdgesCenterYPortMultiby.getText().toString())) {
                double edgesCenterYPortMultiby = Double.valueOf(etEdgesCenterYPortMultiby.getText().toString());
                double doubleValue = new BigDecimal(edgesCenterYPortMultiby).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                edgesCenterYPortMultiby = doubleValue;
                if (edgesCenterYPortMultiby != (bigGiftConfigBean.getEdges().getCenterY().getPortMultiby())) {
                    bigGiftConfigBean.getEdges().getCenterY().setPortMultiby(edgesCenterYPortMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterYPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (!TextUtils.isEmpty(etEdgesCenterYPortOffset.getText().toString())) {
                Integer edgesCenterYPortOffset = Integer.valueOf(etEdgesCenterYPortOffset.getText().toString());
                if (edgesCenterYPortOffset != (bigGiftConfigBean.getEdges().getCenterY().getPortOffset())) {
                    bigGiftConfigBean.getEdges().getCenterY().setPortOffset(edgesCenterYPortOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesCenterYPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }


        try {
            if (!TextUtils.isEmpty(etEdgesBottomLandMultiby.getText().toString())) {
                Double edgesBottomLandMultiby = Double.valueOf(etEdgesBottomLandMultiby.getText().toString());
                if (edgesBottomLandMultiby != (bigGiftConfigBean.getEdges().getBottom().getLandMultiby())) {
                    bigGiftConfigBean.getEdges().getBottom().setLandMultiby(edgesBottomLandMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesBottomLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesBottomLandOffset.getText().toString())) {
                Integer edgesBottomLandOffset = Integer.valueOf(etEdgesBottomLandOffset.getText().toString());
                if (edgesBottomLandOffset != (bigGiftConfigBean.getEdges().getBottom().getLandOffset())) {
                    bigGiftConfigBean.getEdges().getBottom().setLandOffset(edgesBottomLandOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesBottomLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesBottomPortMultiby.getText().toString())) {
                Double edgesBottomPortMultiby = Double.valueOf(etEdgesBottomPortMultiby.getText().toString());
                if (edgesBottomPortMultiby != (bigGiftConfigBean.getEdges().getBottom().getPortMultiby())) {
                    bigGiftConfigBean.getEdges().getBottom().setPortMultiby(edgesBottomPortMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesBottomPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesBottomPortOffset.getText().toString())) {
                Integer edgesBottomPortOffset = Integer.valueOf(etEdgesBottomPortOffset.getText().toString());
                if (edgesBottomPortOffset != (bigGiftConfigBean.getEdges().getBottom().getPortOffset())) {
                    bigGiftConfigBean.getEdges().getBottom().setPortOffset(edgesBottomPortOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesBottomPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesRightLandMultiby.getText().toString())) {
                Double edgesRightLandMultiby = Double.valueOf(etEdgesRightLandMultiby.getText().toString());
                if (edgesRightLandMultiby != (bigGiftConfigBean.getEdges().getRight().getLandMultiby())) {
                    bigGiftConfigBean.getEdges().getRight().setLandMultiby(edgesRightLandMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesRightLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesRightLandOffset.getText().toString())) {
                Integer edgesRightLandOffset = Integer.valueOf(etEdgesRightLandOffset.getText().toString());
                if (edgesRightLandOffset != (bigGiftConfigBean.getEdges().getRight().getLandOffset())) {
                    bigGiftConfigBean.getEdges().getRight().setLandOffset(edgesRightLandOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesRightLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesRightPortMultiby.getText().toString())) {
                Double edgesRightPortMultiby = Double.valueOf(etEdgesRightPortMultiby.getText().toString());
                if (edgesRightPortMultiby != (bigGiftConfigBean.getEdges().getRight().getPortMultiby())) {
                    bigGiftConfigBean.getEdges().getRight().setPortMultiby(edgesRightPortMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesRightPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesRightPortOffset.getText().toString())) {
                Integer edgesRightPortOffset = Integer.valueOf(etEdgesRightPortOffset.getText().toString());
                if (edgesRightPortOffset != (bigGiftConfigBean.getEdges().getRight().getPortOffset())) {
                    bigGiftConfigBean.getEdges().getRight().setPortOffset(edgesRightPortOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesRightPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesLeftLandMultiby.getText().toString())) {
                Double edgesLeftLandMultiby = Double.valueOf(etEdgesLeftLandMultiby.getText().toString());
                if (edgesLeftLandMultiby != (bigGiftConfigBean.getEdges().getLeft().getLandMultiby())) {
                    bigGiftConfigBean.getEdges().getLeft().setLandMultiby(edgesLeftLandMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesLeftLandOffset.getText().toString())) {
                Integer edgesLeftLandOffset = Integer.valueOf(etEdgesLeftLandOffset.getText().toString());
                if (edgesLeftLandOffset != (bigGiftConfigBean.getEdges().getLeft().getLandOffset())) {
                    bigGiftConfigBean.getEdges().getLeft().setLandOffset(edgesLeftLandOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesLeftPortMultiby.getText().toString())) {
                Double edgesLeftPortMultiby = Double.valueOf(etEdgesLeftPortMultiby.getText().toString());
                if (edgesLeftPortMultiby != (bigGiftConfigBean.getEdges().getLeft().getPortMultiby())) {
                    bigGiftConfigBean.getEdges().getLeft().setPortMultiby(edgesLeftPortMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesLeftPortOffset.getText().toString())) {
                Integer edgesLeftPortOffset = Integer.valueOf(etEdgesLeftPortOffset.getText().toString());
                if (edgesLeftPortOffset != (bigGiftConfigBean.getEdges().getLeft().getPortOffset())) {
                    bigGiftConfigBean.getEdges().getLeft().setPortOffset(edgesLeftPortOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        //
        try {
            if (!TextUtils.isEmpty(etEdgesTopLandMultiby.getText().toString())) {
                Double edgesTopLandMultiby = Double.valueOf(etEdgesTopLandMultiby.getText().toString());
                if (edgesTopLandMultiby != (bigGiftConfigBean.getEdges().getTop().getLandMultiby())) {
                    bigGiftConfigBean.getEdges().getTop().setLandMultiby(edgesTopLandMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesTopLandMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesTopLandOffset.getText().toString())) {
                Integer edgesTopLandOffset = Integer.valueOf(etEdgesTopLandOffset.getText().toString());
                if (edgesTopLandOffset != (bigGiftConfigBean.getEdges().getTop().getLandOffset())) {
                    bigGiftConfigBean.getEdges().getTop().setLandOffset(edgesTopLandOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftLandOffset数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesTopPortMultiby.getText().toString())) {
                Double edgesTopPortMultiby = Double.valueOf(etEdgesTopPortMultiby.getText().toString());
                if (edgesTopPortMultiby != (bigGiftConfigBean.getEdges().getTop().getPortMultiby())) {
                    bigGiftConfigBean.getEdges().getTop().setPortMultiby(edgesTopPortMultiby);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftPortMultiby数据有误", Toast.LENGTH_SHORT).show();
        }

        try {
            if (!TextUtils.isEmpty(etEdgesTopPortOffset.getText().toString())) {
                Integer edgesTopPortOffset = Integer.valueOf(etEdgesTopPortOffset.getText().toString());
                if (edgesTopPortOffset != (bigGiftConfigBean.getEdges().getTop().getPortOffset())) {
                    bigGiftConfigBean.getEdges().getTop().setPortOffset(edgesTopPortOffset);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "edgesLeftPortOffset数据有误", Toast.LENGTH_SHORT).show();
        }
    }
}
