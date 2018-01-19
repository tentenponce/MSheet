package com.tcorner.msheet.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * tag of group model
 * Created by Tenten Ponce on 10/9/2017.
 */

@AutoValue
public abstract class GroupTag implements Parcelable {

    public static GroupTag create(String uuid, String tag, String groupUuid, Date dateModified, int count) {
        return new AutoValue_GroupTag(uuid, tag, groupUuid, dateModified, count);
    }

    public static GroupTag create(String name, String groupUuid) {
        return create(UUID.randomUUID().toString(), name, groupUuid, Calendar.getInstance().getTime(), 0);
    }

    public static GroupTag create(GroupTag groupTag) {
        return create(groupTag.uuid(), groupTag.tag(), groupTag.groupUuid(), groupTag.dateModified(), groupTag.count());
    }

    public abstract String uuid();

    public abstract String tag();

    public abstract String groupUuid();

    public abstract Date dateModified();

    public abstract int count();
}
