package com.tcorner.msheet.ui.library;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * library presenter
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryPresenter extends BasePresenter<LibraryMvpView> {

    private final DataManager dataManager;
    private CompositeDisposable disposable;

    @Inject
    public LibraryPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
        disposable = new CompositeDisposable();
    }

    @Override
    public void attachView(LibraryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (disposable != null) disposable.dispose();
    }

    public void addSheet(Sheet sheet) {
        checkViewAttached();
        RxUtil.unsubscribe(disposable);

        disposable.add(dataManager.addSheet(sheet)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribeWith(new TestObserver<Sheet>() {
                    @Override
                    public void onNext(Sheet sheet) {
                        super.onNext(sheet);

                        getMvpView().showAddSheet(sheet);
                    }
                }));
    }

    public void getSheets() {
        checkViewAttached();
        RxUtil.unsubscribe(disposable);

        disposable.add(dataManager.getSheets()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribeWith(new TestObserver<List<Sheet>>() {
                    @Override
                    public void onNext(List<Sheet> sheets) {
                        super.onNext(sheets);

                        getMvpView().showMusicSheets(sheets);
                    }
                }));
    }
}
