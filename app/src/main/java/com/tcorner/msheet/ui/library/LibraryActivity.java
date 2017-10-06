package com.tcorner.msheet.ui.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.tcorner.msheet.R;
import com.tcorner.msheet.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * displays all of your music sheeeeeets
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryActivity extends BaseActivity implements LibraryMvpView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    LibraryPresenter libraryPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);

        activityComponent().inject(this);
        libraryPresenter.attachView(this);

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        libraryPresenter.detachView();
    }

    @Override
    public void showError() {
        /**/
    }

    @Override
    public void showMusicSheets() {

    }

    private void initViews() {
        /* init toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_library);
        }
    }
}
