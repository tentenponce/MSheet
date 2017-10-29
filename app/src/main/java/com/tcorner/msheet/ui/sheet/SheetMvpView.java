package com.tcorner.msheet.ui.sheet;

import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.MvpView;

import java.util.List;

/**
 * sheet mvp view
 * Created by Tenten Ponce on 10/20/2017.
 */

interface SheetMvpView extends MvpView {

    void showSheets(List<Sheet> sheets);

    void showAddSheet(Sheet sheet);

    void showDeleteSheet();
}
