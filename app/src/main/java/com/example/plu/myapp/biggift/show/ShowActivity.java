package com.example.plu.myapp.biggift.show;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.BaseActivity;
import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.BaseStage;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.lwf.LWFObject;

import butterknife.Bind;

/**
 * Created by chengXing on 2016/12/29.
 */

public class ShowActivity extends BaseActivity implements View.OnTouchListener, Scene.Listener {

    @Bind(R.id.lwfSurface)
    BaseStage surface;

    private BaseScene mScene;
    private LWFObject mLWFObject;

    @Override
    protected void initData(Bundle state) {
        // 创建一个动画容器
        mScene = new BaseScene();
        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);
        mScene.setColor(new GLColor(0f, 0f, 0f, 0f));
        mScene.setListener(this);

        // 将动画容器绑定到GLSurface中
        surface.setScene(mScene);
        surface.setOnTouchListener(this);
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
