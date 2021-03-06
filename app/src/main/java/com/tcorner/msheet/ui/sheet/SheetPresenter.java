package com.tcorner.msheet.ui.sheet;

import android.util.Log;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
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

    void addSheet(final String imagePath, final String groupUuid) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.getLastSheetCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Integer, Observable<Sheet>>() {
                    @Override
                    public Observable<Sheet> apply(@NonNull Integer integer) throws Exception {
                        return dataManager.addSheet(Sheet.create(integer + 1, imagePath, groupUuid));
                    }
                })
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
                        Log.e("androidruntime", e.getMessage());
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
                .map(new Function<List<Sheet>, List<Sheet>>() {
                    @Override
                    public List<Sheet> apply(@NonNull List<Sheet> sheets) throws Exception {
                        Collections.sort(sheets, new Comparator<Sheet>() {
                            @Override
                            public int compare(Sheet sheet, Sheet sheet2) {
                                return Integer.valueOf(sheet.sheetOrder()).compareTo(sheet2.sheetOrder());
                            }
                        });

                        return sheets;
                    }
                })
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

    void swapSheet(Sheet sheet1, final Sheet sheet2) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.updateSheet(sheet1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Sheet, Observable<Sheet>>() {
                    @Override
                    public Observable<Sheet> apply(@NonNull Sheet sheet) throws Exception {
                        return dataManager.updateSheet(sheet2);
                    }
                })
                .subscribe(new Observer<Sheet>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Sheet sheet) {
                        getMvpView().showUpdateSheet(sheet);
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

    void swapSheet(List<Sheet> sheets) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        Observable.fromIterable(sheets)
                .flatMap(new Function<Sheet, Observable<Sheet>>() {
                    @Override
                    public Observable<Sheet> apply(@NonNull Sheet sheet) throws Exception {
                        return dataManager.updateSheet(sheet);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Sheet>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Sheet sheet) {
                        getMvpView().showUpdateSheet(sheet);
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
