package com.example.plu.myapp.biggift.show;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.BaseActivity;
import com.example.plu.myapp.biggift.bean.BigGiftConfigBean;
import com.example.plu.myapp.util.PluLog;
import com.example.plu.myapp.util.Utils;
import com.example.view.StringUtil;
import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.BaseStage;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFManager;
import com.funzio.pure2D.lwf.LWFObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;

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

        // 获取屏幕宽高
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDisplaySize.x = metrics.widthPixels;
        mDisplaySize.y = metrics.heightPixels;

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
        surface.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScene.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        clearLwf(); // 先清除上一次的动画
                        addOneLwf(giftConfigBean);
                    }
                });
            }
        }, 100);

    }

    private void addOneLwf(BigGiftConfigBean giftConfigBean) {
        boolean isFile = !giftConfigBean.isDefault();
        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);
        try {
            InputStream inputStream;
            if (isFile) {
                inputStream = new FileInputStream(giftConfigBean.getPath() + "/" + giftConfigBean.getName());
            } else {
                inputStream = mContext.getAssets().open(
                        giftConfigBean.getPath() + giftConfigBean.getName());
            }
            mLWFData = mLWFManager.createLWFData(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            // 加载出错，直接结束
//            onLwfComplete();
            return;
        }

        int textureNum = mLWFData.getTextureNum();
        Texture[] textures = new Texture[textureNum];
        for (int i = 0; i < textureNum; i++) {
            // 动画帧图片路径
            String fileName = giftConfigBean.getPath() + "/" + mLWFData.getTextureName(i);
            if (fileName.endsWith(giftConfigBean.getTextImgName())) {
                String cachePath = giftConfigBean.getPath()
                        + File.separator + mLWFData.getTextureName(i);
                int size = StringUtil.numStrToInt(giftConfigBean.getTextSize());
                boolean isSuc = createTextImageFile(fileName, cachePath, size, isFile);
                // 创建失败
                textures[i] = mScene.getTextureManager().createFileTexture(
                        isSuc ? cachePath : fileName, null);
            } else {
                if (isFile) {
                    textures[i] = mScene.getTextureManager().createFileTexture(fileName, null);
                } else {
                    textures[i] = mScene.getTextureManager().createAssetTexture(fileName, null);
                }
            }
        }
        mLWFData.setTextures(textures);
        attachLWF(giftConfigBean);
    }

    private void clearLwf() {
        if (mLWFManager != null) {
            mLWFManager.dispose();
        }
        if (mScene != null) {
            mScene.removeChild(mLWFObject);
            // 非GL线程调用这个方法会导致应用内字体变方块
            if (mScene.getTextureManager() != null) {
                mScene.getTextureManager().removeAllTextures();
            }
        }
    }

    private boolean createTextImageFile(String fileName, String cachePath, int textSize, boolean isFile) {
        try {
            InputStream is;
            if (isFile) {
                is = new FileInputStream(fileName);
            } else {
                is = mContext.getAssets().open(fileName);
            }
            Bitmap image = BitmapFactory.decodeStream(is);
            is.close();

            int num = Math.max(10, 11);
            String name = "王尼玛";
            if (name.length() > 5) {
                name = name.substring(0, 5) + ".."; // 名字过长
            }
            String giftContent = new StringBuilder().append("送出")
                    .append("某道具").append("*")
                    .append(num).toString();
            Bitmap bitmap = Utils.drawableTextToBitmap(mContext, name, giftContent, textSize, image);
            Utils.iconSave(bitmap, cachePath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Point mDisplaySize = new Point();
    private float maxScale = 0;     //lwf动画缩放比率

    private void attachLWF(BigGiftConfigBean giftConfig) {
        // attach lwf
        mLWF = mLWFObject.attachLWF(mLWFData);
        mLWF.setPlaying(true);

        //需要根据横竖屏状态计算scale值
        if (giftConfig.getDisplayFrame() != null && giftConfig.getDisplayFrame().getHeight() != null && giftConfig.getDisplayFrame().getHeight().getMultiby() != 0) {
            maxScale = (float) (Math.max(mDisplaySize.y, mDisplaySize.x) * giftConfig.getDisplayFrame().getHeight().getMultiby()) / (float) mLWF.getSize().y;
        } else if (giftConfig.getDisplayFrame() != null && giftConfig.getDisplayFrame().getWidth() != null && giftConfig.getDisplayFrame().getWidth().getMultiby() != 0) {
            maxScale = (float) (Math.min(mDisplaySize.y, mDisplaySize.x) * giftConfig.getDisplayFrame().getWidth().getMultiby()) / (float) mLWF.getSize().x;
        } else {
            //没有下发scale配置，使用默认配置
            maxScale = Math.min(mLWF.getSize().x, mLWF.getSize().y) / Math.max(mDisplaySize.y, mDisplaySize.x);
        }

        if (maxScale != 0) {
            mLWF.scale("_root", maxScale, maxScale);
        }

        changeLwfPosition(giftConfig, mLWF);

        mLWF.addEventHandler("complete", new LWF.Handler() {
            @Override
            public void call() {
                // 这个回调本身在GL线程中
                PluLog.d("complete");
            }
        });
    }


    private float moveX = 0;        //lwf动画x轴偏移量     movex :  0 >> movex 从左往右
    private float moveY = 0;        //lwf动画y轴偏移量     movey :  movey >> 0 从上往下
    private int mScreenOrientation = Configuration.ORIENTATION_UNDEFINED;   //默认屏幕方向
    private boolean isPort = true;
    private final static float PADDING = 0.04f;
    private double top;
    private double left;
    private double right;
    private double bottom;

    private void changeLwfPosition(BigGiftConfigBean giftConfig, LWF lwf) {

        float lwfWidth = lwf.getSize().x;
        float lwfHeight = lwf.getSize().y;
        if (maxScale != 0) {
            lwfWidth = mLWF.getSize().x * maxScale;
            lwfHeight = mLWF.getSize().y * maxScale;
        }

        float screenWidth = 0;
        float screenHeight = 0;
        if (mScreenOrientation != Configuration.ORIENTATION_UNDEFINED) {
            isPort = mScreenOrientation == Configuration.ORIENTATION_PORTRAIT;
            float width = Math.min(mDisplaySize.x, mDisplaySize.y);
            float height = Math.max(mDisplaySize.x, mDisplaySize.y);
            screenWidth = isPort ? width : height;
            screenHeight = isPort ? height : width;
        } else {
            isPort = mDisplaySize.x < mDisplaySize.y;
            screenWidth = mDisplaySize.x;
            screenHeight = mDisplaySize.y;
        }
        moveX = (screenWidth - lwfWidth) / 2;
        moveY = (screenHeight - lwfHeight) / 2;

        BigGiftConfigBean.EdgesBean edges = giftConfig.getEdges();
        if (giftConfig.isRandom() && moveX > 0 && moveY > 0) {
            double tPadding = PADDING * 2, bPadding = PADDING * 2, lPadding = PADDING, rPadding = PADDING;
            if (edges != null && edges.getTop() != null) {
                tPadding = isPort ? edges.getTop().getPortMultiby() : edges.getTop().getLandMultiby();
            }
            if (edges != null && edges.getBottom() != null) {
                bPadding = isPort ? edges.getBottom().getPortMultiby() : edges.getBottom().getLandMultiby();
            }
            if (edges != null && edges.getLeft() != null) {
                lPadding = isPort ? edges.getLeft().getPortMultiby() : edges.getLeft().getLandMultiby();
            }
            if (edges != null && edges.getRight() != null) {
                rPadding = isPort ? edges.getRight().getPortMultiby() : edges.getRight().getLandMultiby();
            }
            //随机位置动画
            moveX = (float) (screenWidth - screenWidth * lPadding - screenWidth * rPadding - lwfWidth);
            moveY = (float) (screenHeight - screenHeight * tPadding - screenHeight * bPadding - lwfHeight);
            moveX = new Random().nextInt(moveX < 0 ? 0 : (int) moveX);
            moveY = new Random().nextInt(moveY < 0 ? 0 : (int) moveY);
            moveX += screenWidth * lPadding; // padding值
            moveY += lwfHeight + screenHeight * tPadding;
        } else {                              //固定位置动画
            if (edges.getCenterX() != null) { //垂直居中
                top = isPort ? edges.getCenterX().getPortMultiby() : edges.getCenterX().getLandMultiby();
                moveX = (screenWidth - lwfWidth) / 2;
                moveY = (float) (1 - top) * screenHeight;
            } else if (edges.getCenterY() != null) {//水平居中
                left = isPort ? edges.getCenterY().getPortMultiby() : edges.getCenterY().getLandMultiby();
                moveX = (float) left * screenWidth;
                moveY = lwfHeight + (screenHeight - lwfHeight) / 2;
            } else if (edges.getTop() != null && edges.getLeft() != null) {
                top = isPort ? edges.getTop().getPortMultiby() : edges.getTop().getLandMultiby();
                left = isPort ? edges.getLeft().getPortMultiby() : edges.getLeft().getLandMultiby();
                //左上
                moveX = (float) left * screenWidth;
                moveY = (float) (1 - top) * screenHeight;
            } else if (edges.getRight() != null && edges.getBottom() != null) {
                right = isPort ? edges.getRight().getPortMultiby() : edges.getRight().getLandMultiby();
                bottom = isPort ? edges.getBottom().getPortMultiby() : edges.getBottom().getLandMultiby();
                //右下
                //right(先将view移到最右边，然后往左移屏幕宽度的百分比)
                moveX = (float) ((screenWidth - lwfWidth) - right * screenWidth);
                //bottom
                moveY = (float) (lwfHeight + bottom * screenHeight);
            }
        }

        lwf.moveTo("_root", moveX, -moveY);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 获取屏幕宽高
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDisplaySize.x = metrics.widthPixels;
        mDisplaySize.y = metrics.heightPixels;

//        if (mScreenOrientation == Configuration.ORIENTATION_UNDEFINED) {
//            changeLwfPosition(configBean, mLWF);
//        }
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_lwf);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mLWF != null) {
                mLWF.gotoAndPlay("_root", "start");
            }
        }
        return false;
    }

    @Override
    public void onSurfaceCreated(GLState glState, boolean firstTime) {
        PluLog.d("onSurfaceCreated");
    }
}
