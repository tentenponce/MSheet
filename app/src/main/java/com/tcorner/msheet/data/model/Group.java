package com.tcorner.msheet.data.model;

import com.google.auto.value.AutoValue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * group of piano sheets/piece
 * 1 song
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

@AutoValue
public abstract class Group {

    public static Group create(String uuid, String name, Date dateModified, List<String> tags) {
        return new AutoValue_Group(uuid, name, dateModified, tags);
    }

    public static Group create(String name, List<String> tags) {
        return create(UUID.randomUUID().toString(), name, Calendar.getInstance().getTime(), tags);
    }

    public static Group create(Group group) {
        return create(group.uuid(), group.name(), group.dateModified(), group.tags());
    }

    public abstract String uuid();

    public abstract String name();

    public abstract Date dateModified();

    public abstract List<String> tags();
}
