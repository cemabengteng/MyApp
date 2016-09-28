package com.example.plu.myapp.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.plu.myapp.R;
import com.example.plu.myapp.base.activity.MvpActivity;
import com.example.plu.myapp.dagger.component.ActivityComponent;
import com.example.view.heartview.NewHeartAnimSurfaceView;

import java.util.Random;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chengXing on 2016/9/13.
 */
public class MainActivity extends MvpActivity<MainComponent, MainPresenter> {
    public final static String TAG = MainActivity.class.getSimpleName();
    @Inject
    MainPresenter mPresenter;
    @Bind(R.id.heartView)
    NewHeartAnimSurfaceView heartView;
    private Random mRandom;

    @Override
    protected MainPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    public MainComponent initComponent(@NonNull ActivityComponent component) {
        MainComponent mainComponent = component.provideMainComponent();
        mainComponent.inject(this);
        return mainComponent;
    }

    @Override
    protected void initData(Bundle state) {
        mRandom = new Random();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);

    }

    @OnClick(R.id.btStarHeartAni)
    public void click(View v) {
        switch (v.getId()) {
            case R.id.btStarHeartAni:
                heartView.addHeartsNow(mRandom.nextInt(10), 500);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose:
                Toast.makeText(this, "compose", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
