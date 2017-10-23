package com.tcorner.msheet.ui.sheet;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

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
        if (disposable != null) disposable.dispose();
    }

    void addSheet(Sheet sheet) {
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

    void getGroupSheets(Group group) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.getGroupSheets(group)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<List<Sheet>, Iterable<Sheet>>() {
                    @Override
                    public Iterable<Sheet> apply(@NonNull List<Sheet> sheets) throws Exception {
                        return sheets;
                    }
                })
                .subscribe(new Observer<Sheet>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Sheet sheet) {
                        getMvpView().showSheet(sheet);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        getMvpView().showCompleteLoadingSheet();
                    }
                });
    }
}
