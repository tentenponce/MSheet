package com.tcorner.msheet.data;

import com.tcorner.msheet.data.local.DatabaseHelper;
import com.tcorner.msheet.data.model.Collection;
import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
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

    /* Sheets */
    public Observable<Sheet> addSheet(Sheet sheet) {
        return databaseHelper.addSheet(sheet);
    }

    public Observable<Sheet> deleteSheet(String uuid) {
        return databaseHelper.deleteSheet(uuid);
    }

    public Observable<List<Sheet>> getSheets() {
        return databaseHelper.getSheets();
    }

    public Observable<List<Sheet>> getGroupSheets(String groupUuid) {
        return databaseHelper.getGroupSheets(groupUuid);
    }

    public Observable<Integer> getLastSheetCount() {
        return databaseHelper.getLastSheetCount();
    }

    public Observable<Sheet> updateSheet(Sheet sheet) {
        return databaseHelper.updateSheet(sheet);
    }

    /* Groups */
    public Observable<Group> addGroup(Group group) {
        return databaseHelper.addGroup(group);
    }

    public Observable<Group> updateGroup(Group group) {
        return databaseHelper.updateGroup(group);
    }

    public Observable<Group> deleteGroup(String uuid) {
        return databaseHelper.deleteGroup(uuid);
    }

    public Observable<List<Group>> getGroups() {
        return databaseHelper.getGroups();
    }

    /* Group Tag */
    public Observable<List<GroupTag>> addGroupTags(List<GroupTag> groupTags) {
        return databaseHelper.addGroupTags(groupTags);
    }

    public Observable<GroupTag> deleteGroupTag(String uuid) {
        return databaseHelper.deleteGroupTag(uuid);
    }

    public Observable<List<GroupTag>> deleteGroupTagByGroup(String groupUuid) {
        return databaseHelper.deleteGroupTagByGroup(groupUuid);
    }

    public Observable<List<GroupTag>> getGroupTagsByGroup(String groupUuid) {
        return databaseHelper.getGroupTagsByGroup(groupUuid);
    }

    public Observable<List<GroupTag>> getGroupTags() {
        return databaseHelper.getGroupTags();
    }

    public Observable<List<GroupTag>> getDistinctGroupTags() {
        return databaseHelper.getDistinctGroupTags();
    }

    /* Collections */
    public Observable<Collection> addCollection(Collection collection) {
        return databaseHelper.addCollection(collection);
    }

    public Observable<Collection> updateCollection(Collection collection) {
        return databaseHelper.updateCollection(collection);
    }

    public Observable<Collection> deleteCollection(String uuid) {
        return databaseHelper.deleteCollection(uuid);
    }

    public Observable<List<Collection>> getCollections() {
        return databaseHelper.getCollections();
    }
}
