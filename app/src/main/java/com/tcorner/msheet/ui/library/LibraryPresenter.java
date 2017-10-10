package com.tcorner.msheet.ui.library;

import android.support.v4.util.Pair;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
import com.tcorner.msheet.ui.base.BasePresenter;
import com.tcorner.msheet.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
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

    void getGroups() {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.getGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<List<Group>, Iterable<Group>>() {
                    @Override
                    public Iterable<Group> apply(@NonNull List<Group> groups) throws Exception {
                        return groups;
                    }
                })
                .flatMap(new Function<Group, ObservableSource<Pair<Group, List<GroupTag>>>>() {
                    @Override
                    public ObservableSource<Pair<Group, List<GroupTag>>> apply(@NonNull Group group) throws Exception {
                        return Observable.zip(Observable.just(group),
                                dataManager.getGroupTagsByGroup(group.uuid())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()),
                                new BiFunction<Group, List<GroupTag>, Pair<Group, List<GroupTag>>>() {
                                    @Override
                                    public Pair<Group, List<GroupTag>> apply(@NonNull Group group, @NonNull List<GroupTag> groupTags) throws Exception {
                                        return new Pair<>(group, groupTags);
                                    }
                                });
                    }
                })
                .map(new Function<Pair<Group, List<GroupTag>>, Group>() {
                    @Override
                    public Group apply(@NonNull Pair<Group, List<GroupTag>> groupListPair) throws Exception {
                        groupListPair.first.tags().addAll(groupListPair.second);
                        return groupListPair.first;
                    }
                })
                .subscribe(new Observer<Group>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Group group) {
                        getMvpView().showGroup(group);
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

    void deleteGroup(String uuid) {
        checkViewAttached();
        RxUtil.dispose(disposable);

        dataManager.deleteGroup(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Group>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Group group) {
                        /**/
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {
                        getMvpView().showDeleteGroup();
                    }
                });
    }
}
