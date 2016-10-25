package com.example.plu.myapp.dagger.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by chengXing on 2016/10/25.
 */
@Scope
@Retention(RUNTIME)
public @interface LayoutScope {
}
