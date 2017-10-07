package com.tcorner.msheet.data.model;

import com.google.auto.value.AutoValue;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * piano sheet/piece model
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

@AutoValue
public abstract class Sheet {

    public static Sheet create(String uuid, String imagePath, String groupUuid, Date dateModified) {
        return new AutoValue_Sheet(uuid, imagePath, groupUuid, dateModified);
    }

    public static Sheet create(String imagePath, String groupUuid) {
        return create(UUID.randomUUID().toString(), imagePath, groupUuid, Calendar.getInstance().getTime());
    }

    public static Sheet create(Sheet sheet) {
        return create(sheet.uuid(), sheet.imagePath(), sheet.groupUuid(), sheet.dateModified());
    }

    public abstract String uuid();

    public abstract String imagePath();

    public abstract String groupUuid();

    public abstract Date dateModified();
}
