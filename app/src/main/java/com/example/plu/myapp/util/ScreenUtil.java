package com.example.plu.myapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**屏幕的一个帮助类，单例
 * 使用前必须init
 * 提供对px,sp,dip等等操作
 * 提供屏幕尺寸
 * author: liutao
 * date: 2016/6/21.
 */
public class ScreenUtil {
    private int appHeight = 0;
    private int appWidth = 0;
    private Context context;
    private static ScreenUtil screenUtil;

    public static ScreenUtil getInstance(){
        return  screenUtil;

    }
    public static  void init(Context context){
        screenUtil=new ScreenUtil(context);
    }
    public ScreenUtil(Context context) {
        this.context = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int appHeight = dm.heightPixels;
        int appWidth = dm.widthPixels;
        if(appWidth>appHeight){//部分情况,app在横屏下崩溃会导致宽高颠倒。
            this.appWidth=appHeight;
            this.appHeight=appWidth;
        }else {
            this.appWidth=appWidth;
            this.appHeight=appHeight;
        }

    }
    public int getAppHeight() {
        return appHeight;
    }

    public int getAppWidth() {
        return appWidth;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public int px2dip(float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public int dip2px(float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public  int px2sp(float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public  int sp2px(float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取一个view的宽度
     * @param view
     * @return
     */
    public static int getMeasureWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }
    /**
     * 获取view的高度
     * @param view
     * @return
     */
    public static int getMeasureHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }
    /**
     * 根据listview最大高度动态调整可完整显示的item个数
     */
    public static void setListViewHeightBasedOnMaxHeght(ListView listView, int maxHeight) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int itemCount = 0;
        for (int i = listAdapter.getCount() - 1; i >= 0; i--) {
            itemCount++;
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 使列表长度刚好可以完整展示所有item，且不超过高度参数maxHeight
            if (totalHeight + listItem.getMeasuredHeight() > maxHeight) {
                break;
            } else {
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        // 高度有溢出，需要滑动时，才动态调整高度
        if (itemCount < listAdapter.getCount()) {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (itemCount - 1));
            listView.setLayoutParams(params);
        }
    }

    /**
     * 获得当前系统的亮度值： 0~255
     */
    public static int getSysScreenBrightness(Context context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    /**
     * 设置当前系统的亮度值:0~255
     */
    public static void setSysScreenBrightness(Context context,int brightness) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(uri, null); // 实时通知改变
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取手机屏幕亮度模式
     */

    public static int getScreenBrightnessMode(Context context){
        int screenMode=0;
        try{
            screenMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        }
        catch (Exception localException){
        }
        return screenMode;
    }

    /**
     * 判断屏幕亮度是否为自动模式
     * @param context
     * @return
     */
    public static boolean isAutoBrightnessMode(Context context){
        return getScreenBrightnessMode(context) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    }
    /**
     * 停止屏幕亮度为自动模式
     * @param context
     */
    public static void stopAutoBrightness(Context context){
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue fontScale（DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px2sp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float scaled = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scaled + 0.5f);
    }

    public static final int getHeightInPx(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final int getWidthInPx(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断是否为横屏
     * @param context
     * @return
     */
    public static boolean isPortrait(Context context){
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 判断是否为横屏
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context){
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

}
