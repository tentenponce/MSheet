package com.tcorner.msheet.injection.component;

import com.tcorner.msheet.injection.PerActivity;
import com.tcorner.msheet.injection.module.ActivityModule;
import com.tcorner.msheet.ui.collection.CollectionFragment;
import com.tcorner.msheet.ui.collection.modifycollection.ModifyCollectionActivity;
import com.tcorner.msheet.ui.library.LibraryFragment;
import com.tcorner.msheet.ui.library.modifygroup.ModifyGroupActivity;
import com.tcorner.msheet.ui.sheet.SheetActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LibraryFragment libraryFragment);

    void inject(ModifyGroupActivity modifyGroupActivity);

    void inject(SheetActivity sheetActivity);

    void inject(CollectionFragment collectionFragment);

    void inject(ModifyCollectionActivity modifyCollectionActivity);
}
