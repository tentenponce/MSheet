package com.tcorner.msheet.injection.component;

import android.app.Application;
import android.content.Context;

import com.tcorner.msheet.data.DataManager;
import com.tcorner.msheet.injection.ApplicationContext;
import com.tcorner.msheet.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context context();

    Application application();

    DataManager dataManager();
}
