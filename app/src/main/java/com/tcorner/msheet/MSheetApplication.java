package com.tcorner.msheet;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.tcorner.msheet.injection.component.ApplicationComponent;
import com.tcorner.msheet.injection.component.DaggerApplicationComponent;
import com.tcorner.msheet.injection.module.ApplicationModule;

/**
 * application of the app
 * Created by Exequiel Egbert V. Ponce on 9/14/2017.
 */

public class MSheetApplication extends Application {

    ApplicationComponent mApplicationComponent;

    public static MSheetApplication get(Context context) {
        return (MSheetApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }
}
