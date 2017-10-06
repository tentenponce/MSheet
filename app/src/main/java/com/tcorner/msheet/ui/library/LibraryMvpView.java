package com.tcorner.msheet.ui.library;

import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.MvpView;

import java.util.List;

/**
 * library mvp view
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public interface LibraryMvpView extends MvpView {

    void showAddSheet(Sheet sheet);

    void showMusicSheets(List<Sheet> sheets);
}
