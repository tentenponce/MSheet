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

    public abstract String uuid();

    public abstract byte[] image();

    public abstract String groupUuid();

    public abstract Date dateModified();

    public static Sheet create(String uuid, byte[] image, String groupUuid, Date dateModified) {
        return new AutoValue_Sheet(uuid, image, groupUuid, dateModified);
    }

    public static Sheet create(byte[] image, String groupUuid) {
        return create(UUID.randomUUID().toString(), image, groupUuid, Calendar.getInstance().getTime());
    }

    public static Sheet create(Sheet sheet) {
        return create(sheet.uuid(), sheet.image(), sheet.groupUuid(), sheet.dateModified());
    }
}
