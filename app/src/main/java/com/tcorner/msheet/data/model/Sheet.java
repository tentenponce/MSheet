package com.tcorner.msheet.data.model;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

/**
 * piano sheet/piece model
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

public class Sheet {

    private String uuid;
    private Bitmap bitmap;
    private String groupUuid;
    private Date dateModified;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}
