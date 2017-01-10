package com.example.plu.myapp.util;

import android.text.TextUtils;
import android.util.Log;


import java.lang.reflect.Field;

/**
 * 专门检测是否为空的util
 * 检测到Null会在log中打印出来，并提示相应的行数
 * 请及时解决。
 * author: liutao
 * date: 2016/7/26.
 */
public class NullUtil {
    public static String tagPrefix = "NullUtil";//log前缀

    //TODO 使用该方法时，请不要将一级数据和二级数据同时传入
    //TODO 例如:
    //TODO        NullUtil.isNull(beanA,beanA.id)
    //TODO       此时如果beanA是Null，那么beanA.id会直接崩溃
    public static boolean isNull(Object... objects) {
        return isNullDeubug(objects);
    }

    /*暂不可用
     */
    private static NullBean getInstance() {
        return new NullBean();
    }

    private static boolean isNullDeubug(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (int i = 0, len = objects.length; i < len; i++) {
            Object o = objects[i];
            if (null == o) {
                String tag = getTag(getCallerStackTraceElement());
                Log.e(tag, "index = " + i + " is Null");
                return true;
            }
        }
        return false;
    }

    private static boolean isNullRelease(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (Object o : objects) {
            if (null == o) {
                return true;
            }
        }
        return false;
    }

    private static String getTag(StackTraceElement element) {
        try {
            String tag = "%s.%s(Line:%d)"; // 占位符
            String callerClazzName = element.getClassName(); // 获取到类名
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
            tag = String.format(tag, callerClazzName, element.getMethodName(), element.getLineNumber()); // 替换
            tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
            return tag;
        } catch (Exception e) {
            return tagPrefix;
        }
    }

    /**
     * 获取线程状态
     *
     * @return
     */
    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[5];
    }

    /**
     * 可进行多段判断的类
     */
    public static class NullBean {
        private boolean isNull;

        public NullBean check(Object... o) {
            if (!isNull) {
                this.isNull = NullUtil.isNull(o);
            }
            return this;
        }

        public NullBean check(CheckAction action) {
            if (!isNull) {
                try {
                    Object[] o = action.onCheck();
                    isNull = NullUtil.isNull(o);
                } catch (Exception e) {
                    isNull = true;
                }
            }
            return this;
        }

        public NullBean check(Object o, String... names) {
            if (!isNull) {
                try {
                    isNull = inCheck(o, names);
                } catch (Exception e) {
                    isNull = true;
                }
            }
            return this;
        }

        private boolean inCheck(Object o, String... names) throws IllegalAccessException {
            if (NullUtil.isNull(o)) {
                return true;
            }
            for (Field f : o.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                for (String name : names) {
                    if (f.getName().equals(name)) {
                        return f.get(o) == null;
                    }
                }
            }
            return true;
        }

        public boolean isNull() {
            return isNull;
        }


    }

    public static interface CheckAction {
        Object[] onCheck();
    }
}
