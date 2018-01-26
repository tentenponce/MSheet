package com.tcorner.msheet.util;

import android.text.Layout;
import android.widget.TextView;

/**
 * view utilities
 * Created by Tenten Ponce on 1/26/2018.
 */

public class ViewUtil {

    /**
     * check if text on the textview has ellipsize/exceeded
     * <p>
     * Note: TextView mas have the attribute ellipsize
     *
     * @param textView textview to check
     * @return true if has ellipsize/exceeds, otherwise false
     */
    public static boolean isTextEllipsized(TextView textView) {
        Layout l = textView.getLayout();
        if (l != null) {
            int lines = l.getLineCount();
            if (lines > 0) {
                if (l.getEllipsisCount(lines - 1) > 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
