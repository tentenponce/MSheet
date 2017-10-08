package com.tcorner.msheet.injection.component;

import com.tcorner.msheet.injection.PerActivity;
import com.tcorner.msheet.injection.module.ActivityModule;
import com.tcorner.msheet.ui.library.LibraryActivity;
import com.tcorner.msheet.ui.library.addgroup.AddGroupActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LibraryActivity libraryActivity);

    void inject(AddGroupActivity addGroupActivity);
}
