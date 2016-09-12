package com.example.data.net.repository;

import com.example.domain.repository.TestDataRepository;

import rx.Observable;

/**
 * Created by plu on 2016/8/30.
 */
public class TestDataRepositoryImpl extends DataRepositoryImpl implements TestDataRepository {


    @Override
    public Observable<String> test() {
        return null;
    }
}
