package com.example.plu.myapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.SparseArray;

import com.example.view.StringUtil;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by plu on 2016/6/17.
 * <p/>
 * http://fresco-cn.org/docs/drawee-components.html#_
 */
public class FrescoUtil {
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    public static final String IMAGE_PIPELINE_CACHE_DIR = "suipai_image_cache"; // sd卡缓存目录文件名
    public static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;
    private static MemoryManager memoryManager = new MemoryManager();

    /**
     * 初始化Fresco，未对缓存做控制
     */
    public static void initDefultFresco(Context context) {
        Fresco.initialize(context);
    }

    /**
     * 初始化Fresco，默认使用HttpURLConnection下载图片
     */
    public static void initSimpleFresco(Context context) {
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
        configureCaches(configBuilder, context);
        configureLoggingListeners(configBuilder);
        Fresco.initialize(context, configBuilder.build());
    }

    /**
     * 请求进度
     */
    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        configBuilder.setRequestListeners(requestListeners);
    }

    /**
     * 清空Fresco，同时清理内存缓存和硬盘缓存
     */
    public static void clearCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //同时清理内存缓存和硬盘缓存
        imagePipeline.clearCaches();

    }

    public static void clearCache(String url) {
        try {
            PluLog.e(url);
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            imagePipeline.evictFromCache(Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空Fresco，清空内存缓存
     */
    public static void clearMemoryCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
        imagePipeline.clearMemoryCaches();

    }

    /**
     * 清空Fresco，清空硬盘缓存
     */
    public static void clearDiskCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //清空硬盘缓存，一般在设置界面供用户手动清理
        imagePipeline.clearDiskCaches();


    }

    private static SparseArray<List<Uri>> uriList = new SparseArray<>();

    /**
     * 移除某个key下的所有缓存
     * 单个页面设置
     * TODO 频繁释放可能导致内存不稳定
     */
    public synchronized static void evictFromCacheByKey(int key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上,不用管内存
            return;
        }
        List<Uri> list = uriList.get(key);
        if (list == null || list.size() == 0) {
            return;
        }
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        for (Uri uri : list) {
            PluLog.e("移除了--" + key + "--下的图片缓存--" + uri);
            imagePipeline.evictFromCache(uri);
        }
        uriList.remove(key);
    }

    public static long getCacheSize(Context context) {
        File file = context.getCacheDir();//获得cache缓存目录
        file = new File(file.getAbsolutePath() + "/" + IMAGE_PIPELINE_CACHE_DIR);
        if (!file.exists()) return 0;
        String path = file.getAbsolutePath();
//        PluLog.d("FRESCO:PATH" + path);
        long size = FileUtils.getFolderSize(file);
//        PluLog.d("FRESCO:SIZE" + size);
        return size;
    }

    /**
     * 快速释放换的方法：https://github.com/facebook/fresco/issues/213
     */
    static class MemoryManager implements MemoryTrimmableRegistry {
        List<MemoryTrimmable> trimmables = new ArrayList<>();

        @Override
        public void registerMemoryTrimmable(MemoryTrimmable trimmable) {
            if (trimmables == null) trimmables = new ArrayList<>();
            trimmables.add(trimmable);
        }

        @Override
        public void unregisterMemoryTrimmable(MemoryTrimmable trimmable) {
            PluLog.e(">>>-unregisterMemoryTrimmable");
            trim();
        }

        public void trim() {
            if (trimmables == null) return;
            PluLog.e(">>>-trim all");
            for (MemoryTrimmable mt : trimmables) {
                mt.trim(MemoryTrimType.OnSystemLowMemoryWhileAppInForeground);
            }
        }
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(
            ImagePipelineConfig.Builder configBuilder,
            Context context) {
        // Memory Cache Params
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size

        configBuilder
//                .setMemoryTrimmableRegistry(memoryManager)
                .setBitmapMemoryCacheParamsSupplier(
                        // Memory Cache
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(
                        // Disk Cache
                        DiskCacheConfig.newBuilder(context)
                                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                                .build());
    }


    /**
     * 仅加载本地图片，在有缩略图时，先展示缩略图
     */
    public static void loadLocalFileImage(SimpleDraweeView image, String picUrl) {
        if (StringUtil.isEmpty(picUrl)) {
            return;
        }
        if (!picUrl.startsWith("file://") && !picUrl.startsWith("content://")) {
            picUrl = "file://" + picUrl;
        }
        Uri uri = Uri.parse(picUrl);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setLocalThumbnailPreviewsEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(image.getController())
                .build();
        image.setController(controller);
    }


    /**
     * 加载res文件夹中的图片
     */
    public static void loadResImage(SimpleDraweeView image, int resId) {
        if (resId <= 0) {
            return;
        }
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(resId))
                .build();
        image.setImageURI(uri);
    }

    /**
     * 保存某个页面的所有链接,暂时保留此方法.
     *
     * @param key
     * @param uri
     */
    public static void saveUri(int key, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上,不用管内存
            return;
        }
        List<Uri> list = uriList.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(uri);
        uriList.put(key, list);
    }

    //不需要兼容gif調用次方法
    public static void setImageURI(SimpleDraweeView simpleDraweeView, String url, int key) {
        setImageURI(false, -1, simpleDraweeView, url, key, false);

    }

    //如果需要兼容gif图，则调用此方法
    public static void setImageURI(SimpleDraweeView simpleDraweeView, String url, int color, int key) {
        setImageURI(true, color, simpleDraweeView, url, key, false);
    }

    public static void setImageURI(SimpleDraweeView simpleDraweeView, String url, int color, int key, boolean retry) {
        setImageURI(true, color, simpleDraweeView, url, key, retry);
    }

    /**
     * @param igoneGif         是否忽略gif圖片，忽略表示不兼容gif图片
     * @param color            如果兼容gif图片，需要带次参数，颜色为simpleDraweeView所有布局的背景色
     * @param simpleDraweeView
     * @param url
     */
    public static void setImageURI(boolean igoneGif, int color, SimpleDraweeView simpleDraweeView, String url, int key, boolean retry) {
        try {
            Uri uri = Uri.parse(url);
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(false)//默认app中所有gif不播放
                    .setUri(uri)//设置uri
                    .setOldController(simpleDraweeView.getController())
                    .setTapToRetryEnabled(retry)//重试
                    .build();
            GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
            if (igoneGif) {//如果兼容gif图片
                RoundingParams roundingParams = hierarchy.getRoundingParams();
                roundingParams.setRoundingMethod(RoundingParams.RoundingMethod.OVERLAY_COLOR).setOverlayColor(color);
                hierarchy.setRoundingParams(roundingParams);
            }
            draweeController.setHierarchy(hierarchy);
            simpleDraweeView.setController(draweeController);
            saveUri(key, uri);
        } catch (Exception e) {
            Uri uri = Uri.parse("error");
            simpleDraweeView.setImageURI(uri);
            e.printStackTrace();
            PluLog.e(">>>-setImageURI--Exception:" + e.toString() + "   url:" + url);
//            if (memoryManager != null)
//                memoryManager.trim();
        }
    }

    public static void setImageURI(SimpleDraweeView simpleDraweeView, String url) {
        setImageURI(simpleDraweeView, url, -1);
    }

    public static void setImageURI(final SimpleDraweeView simpleDraweeView, final String url, int key, boolean retry) {
        setImageURI(false, -1, simpleDraweeView, url, key, retry);
    }

    public static void setImageURI(final SimpleDraweeView simpleDraweeView, final String url, boolean retry) {
        setImageURI(false, -1, simpleDraweeView, url, -1, retry);
//        try {
//            Uri uri = Uri.parse(url);
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setUri(uri)
//                    .setTapToRetryEnabled(retry)
//                    .setOldController(simpleDraweeView.getController())
//                    .build();
//            simpleDraweeView.setController(controller);
//        } catch (Exception e) {
//            PluLog.e(">>>SimpleDraweeView:" + e.toString() + "   url:" + url);
//        }
    }

    /**
     * 代码设置图片属性示例
     */
    public static void setupImageByCode(Resources resources, SimpleDraweeView simpleDraweeView, Drawable failureImage, String url) {
        GenericDraweeHierarchy hierarchy =
                new GenericDraweeHierarchyBuilder(resources).build();
        // 修改缩放类型:
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        // 修改占位图为资源id，也可以将它修改为一个Drawable
//        hierarchy.setPlaceholderImage(R.drawable.ic_launcher);

        // 其他图层也可以修改：
        hierarchy.setFailureImage(failureImage, ScalingUtils.ScaleType.CENTER);
        // 如果选择缩放类型为focusCrop，需要指定一个居中点:
        hierarchy.setActualImageFocusPoint(new PointF());
        // 为图像添加一个color filter:
        hierarchy.setActualImageColorFilter(new ColorFilter());

        // 设置DraweeHierarchy的圆角显示参数
        RoundingParams roundingParams = hierarchy.getRoundingParams();
        roundingParams.setCornersRadius(10);
        // 是否圆形图片
        roundingParams.setRoundAsCircle(true);
        hierarchy.setRoundingParams(roundingParams);
        // 设置淡出时间
        hierarchy.setFadeDuration(300);
        simpleDraweeView.setHierarchy(hierarchy);
    }

    /**
     * 判断图片是否已经缓存在内存或磁盘中
     *
     * @param
     * @return
     */
    public static boolean isExistInDisk(String url) {
        try {
            Uri uri = Uri.parse(url);
            return (Fresco.getImagePipeline().isInBitmapMemoryCache(uri)) || (Fresco.getImagePipeline().isInDiskCacheSync(uri));
        } catch (Exception e) {
            return false;
        }
    }


}
