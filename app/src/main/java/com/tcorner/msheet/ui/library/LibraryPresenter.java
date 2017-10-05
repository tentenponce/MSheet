package com.tcorner.msheet.ui.library;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.ui.base.BasePresenter;

import javax.inject.Inject;

import rx.Subscription;

/**
 * library presenter
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryPresenter extends BasePresenter<LibraryMvpView> {

    private Subscription mSubscription;

    @Inject
    public LibraryPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void attachView(LibraryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }
}
