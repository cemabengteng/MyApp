package com.example.plu.myapp.base.mvp;

import com.example.plu.myapp.util.PluLog;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by liuj on 2016/10/10.
 * MvpPreseter的ViewHolder
 * <p/>
 * 注:在调用getView()时做一层保护，防止未判断isViewAttached()导致的空指针异常。
 * 如果是间接调用getView()的接口，还是需要判断isViewAttached()
 */
public class ViewHolder<V extends MvpView> {

    private WeakReference<V> viewRef;
    private Class clazz;
    private V viewProxy;

    public ViewHolder(V view) {
        viewRef = new WeakReference<V>(view);
        initViewClass(view);
    }

    private void initViewClass(V view) {
        Class viewClazz = view.getClass();
        findMvpViewClazz(viewClazz);
    }

    private void findMvpViewClazz(Class viewClazz) {
        Class[] classes = viewClazz.getInterfaces();
        for (Class itemClazz : classes) {
            if (MvpView.class.isAssignableFrom(itemClazz)) {
                clazz = itemClazz;
                return;
            }
        }
        //查找父类
        Class superClazz = viewClazz.getSuperclass();
        if (superClazz == null || superClazz.getName().contains("android.")) {
            return;
        }
        findMvpViewClazz(superClazz);
    }

    public boolean isViewAttached() {
        return getViewInternal() != null;
    }

    private V getViewInternal() {
        return viewRef != null ? viewRef.get() : null;
    }


    public V getView() {
        if (clazz != null) {
            PluLog.e("viewHolder" + clazz);
        }
        if (clazz == null) {
            return getViewInternal();
        }
        return getViewProxy();
    }

    /**
     * 通过动态代理MvpView的接口，判断view是否释放，如果释放则不做任何操作
     *
     * @return
     */
    private V getViewProxy() {
        if (viewProxy == null) {
            viewProxy = (V) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (isViewAttached()) {
                        try {
                            return method.invoke(viewRef.get(), args);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }
                    return null;
                }
            });
        }
        return viewProxy;
    }

    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }
}
