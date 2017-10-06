package com.tcorner.msheet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Created by Tenten Ponce on 8/27/2017.
 */

public class DateUtil {

    public static final String GOOD_FORMAT_DATE = "MMM dd, yyyy";
    public static final String RAW_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";


    public static String formatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date parseDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.parse(date);
    }
}
