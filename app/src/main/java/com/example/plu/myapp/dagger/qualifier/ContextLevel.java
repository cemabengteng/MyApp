package com.example.plu.myapp.dagger.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by plu on 2016/8/29.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextLevel {
    String APPLICATION = "Application";
    String ACTIVITY = "Activity";
    String FRAGMENT = "Fragment";
    String value() default APPLICATION;
}
