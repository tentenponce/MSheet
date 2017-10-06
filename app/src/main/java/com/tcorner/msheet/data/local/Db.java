package com.tcorner.msheet.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.tcorner.msheet.data.model.Sheet;
import com.tcorner.msheet.util.DateUtil;

import java.text.ParseException;

public class Db {

    public Db() {
    }

    public abstract static class SheetTable {
        public static final String TABLE_NAME = "sheetTable";

        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_GROUP_UUID = "groupUuid";
        public static final String COLUMN_DATE_MODIFIED = "dateModified";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_UUID + " TEXT PRIMARY KEY, " +
                        COLUMN_IMAGE + " TEXT NOT NULL, " +
                        COLUMN_GROUP_UUID + " TEXT NOT NULL, " +
                        COLUMN_DATE_MODIFIED + " DATETIME" +
                        ");";

        public static ContentValues toContentValues(Sheet sheet) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UUID, sheet.uuid());
            values.put(COLUMN_IMAGE, sheet.image());
            values.put(COLUMN_GROUP_UUID, sheet.groupUuid());
            values.put(COLUMN_DATE_MODIFIED, DateUtil.formatDate(sheet.dateModified(),
                    DateUtil.RAW_FORMAT_DATE));

            return values;
        }

        public static Sheet parseCursor(Cursor cursor) {
            try {
                return Sheet.create(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_UUID)),
                        DateUtil.parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_MODIFIED))));
            } catch (ParseException e) {
                return Sheet.create(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UUID)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_UUID)),
                        null);
            }
        }
    }
}
