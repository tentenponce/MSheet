package com.tcorner.msheet.ui.library;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tcorner.msheet.ui.base.BaseActivity;

import javax.inject.Inject;

/**
 * displays all of your music sheeeeeets
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class LibraryActivity extends BaseActivity {

    @Inject
    LibraryPresenter libraryPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showError() {

    }
}
