package com.example.plu.myapp.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.FragmentLifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by chengXing on 2016/9/14.
 */
public class RxFragment extends Fragment implements FragmentLifecycleProvider {
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @NonNull
    @Override
    @CheckResult
    public Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        super.onPause();
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycleSubject.onNext(FragmentEvent.STOP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        lifecycleSubject.onNext(FragmentEvent.DETACH);
    }
}
