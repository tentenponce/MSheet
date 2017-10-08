package com.tcorner.msheet.data.local;

import android.database.Cursor;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.Sheet;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class DatabaseHelper {

    private final BriteDatabase mDb;

    @Inject
    DatabaseHelper(DbOpenHelper dbOpenHelper) {
        SqlBrite.Builder briteBuilder = new SqlBrite.Builder();
        mDb = briteBuilder.build().wrapDatabaseHelper(dbOpenHelper, Schedulers.computation());
    }

    /* Sheet Database */
    public Observable<Sheet> addSheet(final Sheet sheet) {
        return Observable.create(new ObservableOnSubscribe<Sheet>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Sheet> e) {
                if (e.isDisposed()) return;
                mDb.insert(Db.SheetTable.TABLE_NAME, Db.SheetTable.toContentValues(sheet));
                e.onNext(sheet);
                e.onComplete();
            }
        });
    }

    public Observable<List<Sheet>> getSheets() {
        return mDb.createQuery(Db.SheetTable.TABLE_NAME,
                "SELECT * FROM " + Db.SheetTable.TABLE_NAME)
                .mapToList(new Function<Cursor, Sheet>() {
                    @Override
                    public Sheet apply(@NonNull Cursor cursor) throws Exception {
                        return Sheet.create(Db.SheetTable.parseCursor(cursor));
                    }
                });
    }

    public Observable<Sheet> deleteSheet(final String uuid) {
        return Observable.create(new ObservableOnSubscribe<Sheet>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Sheet> e) throws Exception {
                if (e.isDisposed()) return;

                mDb.delete(Db.SheetTable.TABLE_NAME,
                        Db.SheetTable.COLUMN_UUID + "=?",
                        uuid);

                e.onComplete();
            }
        });
    }

    /* Group Database */
    public Observable<Group> addGroup(final Group group) {
        return Observable.create(new ObservableOnSubscribe<Group>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Group> e) {
                if (e.isDisposed()) return;
                mDb.insert(Db.GroupTable.TABLE_NAME, Db.GroupTable.toContentValues(group));
                e.onNext(group);
                e.onComplete();
            }
        });
    }

    public Observable<List<Group>> getGroups() {
        return mDb.createQuery(Db.GroupTable.TABLE_NAME,
                "SELECT * FROM " + Db.GroupTable.TABLE_NAME)
                .mapToList(new Function<Cursor, Group>() {
                    @Override
                    public Group apply(@NonNull Cursor cursor) throws Exception {
                        return Group.create(Db.GroupTable.parseCursor(cursor));
                    }
                });
    }

    public Observable<Group> deleteGroup(final String uuid) {
        return Observable.create(new ObservableOnSubscribe<Group>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Group> e) throws Exception {
                if (e.isDisposed()) return;

                mDb.delete(Db.GroupTable.TABLE_NAME,
                        Db.GroupTable.COLUMN_UUID + "=?",
                        uuid);

                e.onComplete();
            }
        });
    }
}
