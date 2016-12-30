package com.example.plu.myapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.plu.myapp.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    /**
     * drawable转bitmap
     * <p>
     * 文字格式：水平居中，垂直靠底部显示，距底部5dp
     */
    public static Bitmap drawableTextToBitmap(Context context, String name, String giftName,
                                              int textSize, Bitmap image) {
        Paint mNamePaint = getColorPaint(context.getResources().
                getColor(R.color.suipai_username_color), textSize);
        Paint mGiftPaint = getColorPaint(context.getResources().
                getColor(R.color.rank_first_background), textSize);
        int mNameWidth = (int) mNamePaint.measureText(name, 0, name.length());
        int mGiftNameWidth = (int) mGiftPaint.measureText(giftName, 0, giftName.length());
        int total = mNameWidth + mGiftNameWidth + ScreenUtil.dip2px(context, 5);
        int padding = total >= image.getWidth() ? 0 : (image.getWidth() - total) / 2; // 居中

        //根据最大长度设置宽度
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        //要在这个图上画文字
        c.drawBitmap(image, 0, 0, null);
        //要在这个图上画文字
        c.drawText(name, padding, image.getHeight() -
                ScreenUtil.dip2px(context, 5), mNamePaint);
        c.drawText(giftName, padding + ScreenUtil.dip2px(context, 5) + mNameWidth,
                image.getHeight() - ScreenUtil.dip2px(context, 5), mGiftPaint);
        return bitmap;
    }

    private static Paint getColorPaint(int color, int size) {
        Typeface font = Typeface.create(
                Typeface.SANS_SERIF, Typeface.BOLD);
        if (size < 12) size = 12; // 字号最小值
        Paint mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true); // 防止边缘的锯齿
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mTextPaint.setColor(color);
        mTextPaint.setShadowLayer(1.5f, 1.5f, 1.5f, Color.argb(70, 0, 0, 0));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(size); // 文字size
        mTextPaint.setTypeface(font); // 字体+加粗
        return mTextPaint;
    }

    /**
     * 保存bitmap到本地
     *
     * @param b
     */
    public static void iconSave(Bitmap b, String path) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, out);
        FileOutputStream outfile = null;
        try {
            outfile = new FileOutputStream(path);
            outfile.write(out.toByteArray());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
