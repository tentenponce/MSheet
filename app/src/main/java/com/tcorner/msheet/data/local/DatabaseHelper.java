package com.tcorner.msheet.data.local;

import android.database.Cursor;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.tcorner.msheet.data.model.Collection;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
import com.tcorner.msheet.data.model.Sheet;

import java.util.ArrayList;
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

    public Observable<List<Sheet>> getGroupSheets(String groupUuid) {
        return mDb.createQuery(Db.SheetTable.TABLE_NAME,
                "SELECT * FROM " + Db.SheetTable.TABLE_NAME + " WHERE " + Db.SheetTable.COLUMN_GROUP_UUID + "=?",
                groupUuid)
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

    public Observable<Integer> getLastSheetCount() {
        return mDb.createQuery(Db.SheetTable.TABLE_NAME,
                "SELECT MAX(" + Db.SheetTable.COLUMN_SHEET_ORDER + ") AS " + Db.SheetTable.COLUMN_SHEET_ORDER
                        + " FROM " + Db.SheetTable.TABLE_NAME)
                .mapToOne(new Function<Cursor, Integer>() {
                    @Override
                    public Integer apply(@NonNull Cursor cursor) throws Exception {
                        return cursor.getInt(cursor.getColumnIndexOrThrow(Db.SheetTable.COLUMN_SHEET_ORDER));
                    }
                });
    }

    public Observable<Sheet> updateSheet(final Sheet sheet) {
        return Observable.create(new ObservableOnSubscribe<Sheet>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Sheet> e) throws Exception {
                mDb.update(Db.SheetTable.TABLE_NAME, Db.SheetTable.toContentValues(sheet),
                        Db.SheetTable.COLUMN_UUID + "=?", sheet.uuid());
                e.onNext(sheet);
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

    public Observable<Group> updateGroup(final Group group) {
        return Observable.create(new ObservableOnSubscribe<Group>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Group> e) throws Exception {
                mDb.update(Db.GroupTable.TABLE_NAME, Db.GroupTable.toContentValues(group),
                        Db.GroupTable.COLUMN_UUID + "=?", group.uuid());
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

                ArrayList<GroupTag> groupTags = new ArrayList<>();
                e.onNext(Group.create("", groupTags)); //TODO cause null does not accept... any solution for this? :(
                e.onComplete();
            }
        });
    }

    /* Group Tag Database */
    public Observable<List<GroupTag>> addGroupTags(final List<GroupTag> groupTags) {
        return Observable.create(new ObservableOnSubscribe<List<GroupTag>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<GroupTag>> e) {
                if (e.isDisposed()) return;

                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    for (GroupTag groupTag : groupTags) {
                        mDb.insert(Db.GroupTagTable.TABLE_NAME, Db.GroupTagTable.toContentValues(groupTag));
                    }

                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }

                e.onNext(groupTags);
                e.onComplete();
            }
        });
    }

    public Observable<GroupTag> deleteGroupTag(final String uuid) {
        return Observable.create(new ObservableOnSubscribe<GroupTag>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<GroupTag> e) {
                if (e.isDisposed()) return;

                mDb.delete(Db.GroupTagTable.TABLE_NAME,
                        Db.GroupTagTable.COLUMN_UUID + "=?",
                        uuid);

                e.onComplete();
            }
        });
    }

    public Observable<List<GroupTag>> deleteGroupTagByGroup(final String groupUuid) {
        return Observable.create(new ObservableOnSubscribe<List<GroupTag>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<GroupTag>> e) {
                if (e.isDisposed()) return;

                mDb.delete(Db.GroupTagTable.TABLE_NAME,
                        Db.GroupTagTable.COLUMN_GROUP_UUID + "=?",
                        groupUuid);

                e.onNext(new ArrayList<GroupTag>());
                e.onComplete();
            }
        });
    }

    public Observable<List<GroupTag>> getGroupTagsByGroup(String groupUuid) {
        return mDb.createQuery(Db.GroupTagTable.TABLE_NAME,
                "SELECT * FROM " + Db.GroupTagTable.TABLE_NAME + " WHERE " + Db.GroupTagTable.COLUMN_GROUP_UUID + "=?",
                groupUuid)
                .mapToList(new Function<Cursor, GroupTag>() {
                    @Override
                    public GroupTag apply(@NonNull Cursor cursor) throws Exception {
                        return GroupTag.create(Db.GroupTagTable.parseCursor(cursor));
                    }
                });
    }

    public Observable<List<GroupTag>> getGroupTags() {
        return mDb.createQuery(Db.GroupTagTable.TABLE_NAME,
                "SELECT * FROM " + Db.GroupTagTable.TABLE_NAME)
                .mapToList(new Function<Cursor, GroupTag>() {
                    @Override
                    public GroupTag apply(@NonNull Cursor cursor) throws Exception {
                        return GroupTag.create(Db.GroupTagTable.parseCursor(cursor));
                    }
                });
    }

    public Observable<List<GroupTag>> getDistinctGroupTags() {
        return mDb.createQuery(Db.GroupTagTable.TABLE_NAME,
                "SELECT " + Db.GroupTagTable.COLUMN_TAG + ", COUNT(" + Db.GroupTagTable.COLUMN_TAG + ") AS " + Db.GroupTagTable.COLUMN_COUNT
                        + " FROM " + Db.GroupTagTable.TABLE_NAME
                        + " GROUP BY " + Db.GroupTagTable.COLUMN_TAG + " ORDER BY COUNT(" + Db.GroupTagTable.COLUMN_TAG + ") DESC")
                .mapToList(new Function<Cursor, GroupTag>() {
                    @Override
                    public GroupTag apply(@NonNull Cursor cursor) throws Exception {
                        return GroupTag.create(Db.GroupTagTable.parseCursorDistinctGroupTag(cursor));
                    }
                });
    }

    /* Collection Database */
    public Observable<Collection> addCollection(final Collection collection) {
        return Observable.create(new ObservableOnSubscribe<Collection>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Collection> e) {
                if (e.isDisposed()) return;
                mDb.insert(Db.CollectionTable.TABLE_NAME, Db.CollectionTable.toContentValues(collection));
                e.onNext(collection);
                e.onComplete();
            }
        });
    }

    public Observable<Collection> updateCollection(final Collection collection) {
        return Observable.create(new ObservableOnSubscribe<Collection>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Collection> e) throws Exception {
                mDb.update(Db.CollectionTable.TABLE_NAME, Db.CollectionTable.toContentValues(collection),
                        Db.CollectionTable.COLUMN_UUID + "=?", collection.uuid());
                e.onNext(collection);
                e.onComplete();
            }
        });
    }

    public Observable<List<Collection>> getCollections() {
        return mDb.createQuery(Db.CollectionTable.TABLE_NAME,
                "SELECT * FROM " + Db.CollectionTable.TABLE_NAME)
                .mapToList(new Function<Cursor, Collection>() {
                    @Override
                    public Collection apply(@NonNull Cursor cursor) throws Exception {
                        return Collection.create(Db.CollectionTable.parseCursor(cursor));
                    }
                });
    }

    public Observable<Collection> deleteCollection(final String uuid) {
        return Observable.create(new ObservableOnSubscribe<Collection>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Collection> e) throws Exception {
                if (e.isDisposed()) return;

                mDb.delete(Db.CollectionTable.TABLE_NAME,
                        Db.CollectionTable.COLUMN_UUID + "=?",
                        uuid);

                ArrayList<Group> groups = new ArrayList<>();
                e.onNext(Collection.create("", groups)); //TODO cause null does not accept... any solution for this? :(
                e.onComplete();
            }
        });
    }
}
