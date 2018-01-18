package com.tcorner.msheet.ui.library.modifygroup;

import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.ui.base.MvpView;

/**
 * add group mvpview
 * Created by Tenten Ponce on 10/8/2017.
 */

public interface ModifyGroupMvpView extends MvpView {

    void showAddGroup(Group group);

    void showUpdateGroup(Group group);
}
