package com.tcorner.msheet.ui.library;

import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.MvpView;

import java.util.List;

/**
 * library mvp view
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public interface LibraryMvpView extends MvpView {

    void showDeleteGroup();

    void showGroups(List<Group> groups);
}
