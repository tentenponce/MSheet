package com.tcorner.msheet.ui.library.addgroup;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * add group presenter
 * Created by Tenten Ponce on 10/8/2017.
 */

class AddGroupPresenter extends BasePresenter<AddGroupMvpView> {

    private final DataManager dataManager;
    private Disposable disposable;

    @Inject
    AddGroupPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (disposable != null) disposable.dispose();
    }

    void addGroup(final Group group) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.addGroup(group)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Group, ObservableSource<List<GroupTag>>>() {
                    @Override
                    public ObservableSource<List<GroupTag>> apply(@NonNull Group group) throws Exception {
                        return dataManager.addGroupTags(group.tags());
                    }
                })
                .subscribe(new Observer<List<GroupTag>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<GroupTag> groupTags) {
                        getMvpView().showAddGroup(group);
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
