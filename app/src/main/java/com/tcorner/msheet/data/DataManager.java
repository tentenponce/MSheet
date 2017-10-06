package com.tcorner.msheet.data;

import com.tcorner.msheet.data.local.DatabaseHelper;
import com.tcorner.msheet.data.model.Sheet;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * data manager
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

@Singleton
public class DataManager {

    private final DatabaseHelper databaseHelper;

    @Inject
    public DataManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public Observable<Sheet> addSheet(Sheet sheet) {
        return databaseHelper.addSheet(sheet);
    }

    public Observable<List<Sheet>> getSheets() {
        return databaseHelper.getSheets();
    }
}
