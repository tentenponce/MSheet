package com.tcorner.msheet.data.model;

import java.util.Date;
import java.util.List;

/**
 * group of piano sheets/piece
 * 1 song
 * Created by Exequiel Egbert V. Ponce on 9/12/2017.
 */

public class Group {

    private String uuid;
    private String name;
    private Date dateModified;
    private List<String> tags;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
