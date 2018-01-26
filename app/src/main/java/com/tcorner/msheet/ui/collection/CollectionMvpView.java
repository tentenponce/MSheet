package com.tcorner.msheet.ui.collection;

import com.tcorner.msheet.data.model.Collection;
import com.tcorner.msheet.ui.base.MvpView;

import java.util.List;

/**
 * collection mvp view
 * Created by Tenten Ponce on 1/26/2018.
 */

public interface CollectionMvpView extends MvpView {

    void showCollections(List<Collection> collections);
}
