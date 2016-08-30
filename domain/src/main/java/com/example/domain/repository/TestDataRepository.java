package com.example.domain.repository;

import rx.Observable;

/**
 * Created by plu on 2016/8/30.
 */
public interface TestDataRepository extends DataRepository{
    Observable<String> test();
}
