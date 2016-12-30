package com.example.plu.myapp.biggift.show;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.BaseActivity;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;
import com.example.plu.myapp.util.PluLog;
import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.BaseStage;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFManager;
import com.funzio.pure2D.lwf.LWFObject;

import java.io.FileInputStream;
import java.io.InputStream;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/12/29.
 */

public class ShowActivity extends BaseActivity implements View.OnTouchListener, Scene.Listener {

    public static final String BEAN = "BigGiftConfigBean";
    @Bind(R.id.lwfSurface)
    BaseStage surface;

    private BaseScene mScene;
    private LWFObject mLWFObject;
    private LWFData mLWFData;
    private LWFManager mLWFManager;
    private LWF mLWF;

    @Override
    protected void initData(Bundle state) {
        final BigGiftConfigBean giftConfigBean = (BigGiftConfigBean) getIntent().getSerializableExtra(BEAN);

        if (!LWF.loadLibrary()) {
            PluLog.e("ERROR: loadLibrary");
        }

        // 创建manager
        mLWFManager = new LWFManager();

        // 创建一个动画容器
        mScene = new BaseScene();
        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);
        mScene.setColor(new GLColor(0f, 0f, 0f, 0f));
        mScene.setListener(this);

        // 将动画容器绑定到GLSurface中
        surface.setScene(mScene);
        surface.setOnTouchListener(this);

        //开始一个动画
        mScene.queueEvent(new Runnable() {
            @Override
            public void run() {
                //加载Files中的lwf动画的资源文件
                mLWFObject = new LWFObject();
                mScene.addChild(mLWFObject);
                try {
                    InputStream inputStream;
                    inputStream = new FileInputStream(giftConfigBean.getPath());
                    mLWFData = mLWFManager.createLWFData(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 加载出错，直接结束
                    return;
                }

                //TODO 此处还要设置字体

                attachLWF(giftConfigBean);

            }
        });
    }

    private void attachLWF(BigGiftConfigBean bean) {
        // attach lwf
        mLWF = mLWFObject.attachLWF(mLWFData);
        mLWF.setPlaying(true);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_show_lwf);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onSurfaceCreated(GLState glState, boolean firstTime) {

    }
}
