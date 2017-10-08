package com.tcorner.msheet.ui.library.addgroup;

import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.MvpView;

/**
 * add group mvpview
 * Created by Tenten Ponce on 10/8/2017.
 */

public interface AddGroupMvpView extends MvpView {

    void showAddGroup(Group group);
}
