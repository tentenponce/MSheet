package com.tcorner.msheet.ui.collection;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Collection;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * collection presenter
 * Created by Tenten Ponce on 1/26/2018.
 */

public class CollectionPresenter extends BasePresenter<CollectionMvpView> {

    private final DataManager dataManager;
    private Disposable disposable;

    @Inject
    CollectionPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(disposable);
    }

    void getCollections() {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.getCollections()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Collection>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Collection> collections) {
                        getMvpView().showCollections(collections);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
