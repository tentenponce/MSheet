package com.tcorner.msheet.ui.sheet;

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
 * sheet presenter
 * Created by Tenten Ponce on 10/20/2017.
 */

class SheetPresenter extends BasePresenter<SheetMvpView> {

    private final DataManager dataManager;
    private Disposable disposable;

    @Inject
    SheetPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(disposable);
    }

    void addSheet(Sheet sheet) {
        checkViewAttached();
        RxUtil.dispose(disposable);

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
                        getMvpView().showAddSheet(sheet);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        /**/
                    }
                });
    }

    void deleteSheet(String uuid) {
        checkViewAttached();
        RxUtil.dispose(disposable);

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

    void getGroupSheets(String groupUuid) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.getGroupSheets(groupUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Sheet>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Sheet> sheets) {
                        getMvpView().showSheets(sheets);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        /**/
                    }
                });
    }
}
