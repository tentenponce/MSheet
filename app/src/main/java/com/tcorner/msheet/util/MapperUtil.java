package com.tcorner.msheet.util;

import com.tcorner.msheet.data.model.GroupTag;

import java.util.ArrayList;
import java.util.List;

/**
 * mapper util
 * Created by Tenten Ponce on 1/18/2018.
 */

public class MapperUtil {

    public static List<String> mapTagsToString(List<GroupTag> groupTags) {
        List<String> stringGroupTags = new ArrayList<>();

        for (GroupTag groupTag : groupTags) {
            stringGroupTags.add(groupTag.tag());
        }

        return stringGroupTags;
    }
}
