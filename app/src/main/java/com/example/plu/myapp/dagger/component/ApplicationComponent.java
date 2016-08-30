package com.example.plu.myapp.dagger.component;

import com.example.plu.myapp.App;
import com.example.plu.myapp.dagger.base.BaseComponent;
import com.example.plu.myapp.dagger.moudle.ActivityModule;
import com.example.plu.myapp.dagger.moudle.ApplicationMoudle;
import com.example.plu.myapp.dagger.moudle.FragmentModule;
import com.example.plu.myapp.dagger.moudle.LayoutModule;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by plu on 2016/8/29.
 */
@Component
@Subcomponent(modules = ApplicationMoudle.class)
public interface ApplicationComponent extends BaseComponent {
    ActivityComponent provideActivityComponent(ActivityModule activityModule);

    FragmentComponent provideFragmentComponent(FragmentModule fragmentModule);

    LayoutComponent provideLayoutComponent(LayoutModule fragmentModule);

    void inject(App app);
}
