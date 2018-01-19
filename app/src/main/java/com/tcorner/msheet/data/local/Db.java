package com.tcorner.msheet.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.tcorner.msheet.data.model.Group;
import com.tcorner.msheet.data.model.GroupTag;
import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

class Db {

    private Db() {
    }

    abstract static class GroupTagTable {
        static final String TABLE_NAME = "groupTagTable";

        static final String COLUMN_UUID = "uuid";
        static final String COLUMN_TAG = "tag";
        static final String COLUMN_GROUP_UUID = "group_uuid";
        static final String COLUMN_DATE_MODIFIED = "dateModified";
        static final String COLUMN_COUNT = "tagCount";

        static final String CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_UUID + " TEXT PRIMARY KEY, " +
                        COLUMN_GROUP_UUID + " TEXT NOT NULL, " +
                        COLUMN_TAG + " TEXT NOT NULL, " +
                        COLUMN_DATE_MODIFIED + " DATETIME" +
                        ");";

        private GroupTagTable() {
        }

        static ContentValues toContentValues(GroupTag groupTag) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UUID, groupTag.uuid());
            values.put(COLUMN_TAG, groupTag.tag());
            values.put(COLUMN_GROUP_UUID, groupTag.groupUuid());
            values.put(COLUMN_DATE_MODIFIED, DateUtil.formatDate(groupTag.dateModified(),
                    DateUtil.RAW_FORMAT_DATE));

            return values;
        }

        static GroupTag parseCursor(Cursor cursor) {
            try {
                return GroupTag.create(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_UUID)),
                        DateUtil.parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_MODIFIED))), 0);
            } catch (ParseException e) {
                return GroupTag.create(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_UUID)),
                        null, 0);
            }
        }

        static GroupTag parseCursorDistinctGroupTag(Cursor cursor) {
            return GroupTag.create(
                    "",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG)),
                    "",
                    Calendar.getInstance().getTime(),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT)));
        }
    }

    abstract static class GroupTable {
        static final String TABLE_NAME = "groupTable";

        static final String COLUMN_UUID = "uuid";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DATE_MODIFIED = "dateModified";

        static final String CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_UUID + " TEXT PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_DATE_MODIFIED + " DATETIME" +
                        ");";

        private GroupTable() {
        }

        static ContentValues toContentValues(Group group) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UUID, group.uuid());
            values.put(COLUMN_NAME, group.name());
            values.put(COLUMN_DATE_MODIFIED, DateUtil.formatDate(group.dateModified(),
                    DateUtil.RAW_FORMAT_DATE));

            return values;
        }

        static Group parseCursor(Cursor cursor) {
            try {
                return Group.create(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        DateUtil.parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_MODIFIED))),
                        new ArrayList<GroupTag>());
            } catch (ParseException e) {
                return Group.create(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        null, new ArrayList<GroupTag>());
            }
        }
    }

    abstract static class SheetTable {
        static final String TABLE_NAME = "sheetTable";

        static final String COLUMN_UUID = "uuid";
        static final String COLUMN_SHEET_ORDER = "sheetOrder";
        static final String COLUMN_IMAGE = "image";
        static final String COLUMN_GROUP_UUID = "groupUuid";
        static final String COLUMN_DATE_MODIFIED = "dateModified";

        static final String CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_UUID + " TEXT PRIMARY KEY, " +
                        COLUMN_SHEET_ORDER + " INTEGER NOT NULL, " +
                        COLUMN_IMAGE + " TEXT NOT NULL, " +
                        COLUMN_GROUP_UUID + " TEXT NOT NULL, " +
                        COLUMN_DATE_MODIFIED + " DATETIME" +
                        ");";

        private SheetTable() {
        }

        static ContentValues toContentValues(Sheet sheet) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UUID, sheet.uuid());
            values.put(COLUMN_SHEET_ORDER, sheet.sheetOrder());
            values.put(COLUMN_IMAGE, sheet.imagePath());
            values.put(COLUMN_GROUP_UUID, sheet.groupUuid());
            values.put(COLUMN_DATE_MODIFIED, DateUtil.formatDate(sheet.dateModified(),
                    DateUtil.RAW_FORMAT_DATE));

            return values;
        }

        static Sheet parseCursor(Cursor cursor) {
            try {
                return Sheet.create(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SHEET_ORDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_UUID)),
                        DateUtil.parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_MODIFIED))));
            } catch (ParseException e) {
                return Sheet.create(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SHEET_ORDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_UUID)),
                        null);
            }
        }
    }
}
