package com.tcorner.msheet.ui.sheet;

import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.ui.base.MvpView;

/**
 * sheet mvp view
 * Created by Tenten Ponce on 10/20/2017.
 */

interface SheetMvpView extends MvpView {

    void showSheet(Sheet sheet);
}
