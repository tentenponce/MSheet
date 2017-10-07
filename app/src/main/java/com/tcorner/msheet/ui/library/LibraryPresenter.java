package com.tcorner.msheet.ui.library;

import android.util.Log;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * library presenter
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryPresenter extends BasePresenter<LibraryMvpView> {

    private final DataManager dataManager;
    private Disposable disposable;

    @Inject
    LibraryPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (disposable != null) disposable.dispose();
    }

    void addSheet(Sheet sheet) {
        checkViewAttached();
        RxUtil.unsubscribe(disposable);

        dataManager.addSheet(sheet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Sheet>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Sheet sheet) {
                        /**/
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        getMvpView().showAddSheet();
                    }
                });
    }

    void getSheets() {
        checkViewAttached();
        RxUtil.unsubscribe(disposable);

        dataManager.getSheets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Sheet>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Sheet> sheets) {
                        getMvpView().showMusicSheets(sheets);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("androidruntime", "Errorrrr: " + e.getMessage());
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        /**/
                    }
                });
    }

    public void deleteSheet(String uuid) {
        checkViewAttached();
        RxUtil.unsubscribe(disposable);

        dataManager.deleteSheet(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Sheet>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Sheet sheet) {
                        /**/
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        getMvpView().showDeleteSheet();
                    }
                });
    }
}
