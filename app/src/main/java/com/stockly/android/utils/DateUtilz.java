package com.stockly.android.utils;

import android.nfc.FormatException;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 07/08/2017.
 * <p>
 * DateUtilz
 * Date util provide functions for date parsing and formatting
 * into different variants like format time only or date only etc.
 */

public class DateUtilz {
    public static final String[] DATE_FORMATS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss"
    };
    public static final String[] TIME_FORMATS = new String[]{
            "HH:mm:ss.SSSSSS",
            "HH:mm:ss'Z'"
    };
    private static final SimpleDateFormat sDATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()); //2017-07-31T02:38:56.086057
    private static final SimpleDateFormat sDATE_TIME_FORMATS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'", Locale.getDefault());
    private static final SimpleDateFormat sDATE_TIME_FORMATS1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat sDATE_TIME_FORMAT_TO = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());//2017-07-31T02:38:56.086057
    private static final SimpleDateFormat sDATE_TIME_FORMAT_DISPLAY = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());//2017-07-31T02:38:56.086057
    private static final SimpleDateFormat sDATE_FORMAT_DISPLAY = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()); //2017-07-31
    public static final SimpleDateFormat sDAY_FORMAT = new SimpleDateFormat("d", Locale.getDefault());
    public static final SimpleDateFormat sMONTH_FORMAT = new SimpleDateFormat("MMMM", Locale.getDefault());
    private static final SimpleDateFormat sTIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat sTIME_FORMAT_AM_PM = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat sTIME_FORMAT_AM_PM_WITHOUT_AMPM = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat sTIME_FORMAT_AM_PM_WITHOUT_AMPM_Seconds = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat sAM_PM = new SimpleDateFormat("a", Locale.getDefault());
    private static final SimpleDateFormat sTIME_FORMAT_HOURS_MINS = new SimpleDateFormat("HH 'Hours' ,mm 'Mins'", Locale.getDefault());
    private static final SimpleDateFormat sTIME_MINUTES = new SimpleDateFormat("mm", Locale.getDefault());
    private static final SimpleDateFormat sTIME_HOURS = new SimpleDateFormat("HH", Locale.getDefault());
    // date of birth date formatters
    private static final SimpleDateFormat PARSE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    private static final SimpleDateFormat SERVER_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DOB_DATE_FORMAT = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());


    /**
     * Set time zone UTC for standard time for date parsing.
     */
    static {
        PARSE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        sDATE_TIME_FORMATS.setTimeZone(TimeZone.getTimeZone("UTC"));
        sTIME_FORMAT_AM_PM.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    public static Date parseDobDate(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return null;
        }
        try {
            return PARSE_FORMAT.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDobDate(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        try {
            return PARSE_FORMAT.format(dateTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Date parseServerDate(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return null;
        }
        try {
            return SERVER_FORMAT.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String formatDateServer(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        try {
            return SERVER_FORMAT.format(dateTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String formatDateDob(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        try {
            return DOB_DATE_FORMAT.format(dateTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String formatTime24hrs(String time /*01:23 am*/) {
        try {
            return sTIME_FORMAT.format(sTIME_FORMAT_AM_PM.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @param dateTime LIke 2017-07-31T02:38:56.086057
     * @return Like 01:23 (actually in AM/Pm
     */
    public static String formatTimeAmPm(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        try {
            return sTIME_FORMAT_AM_PM.format(sDATE_TIME_FORMATS.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * @param dateTime LIke 2017-07-31T02:38:56.086057
     * @return Like 01:23 (actually in AM/Pm
     */
    public static String formatTimeWithOutAmPm(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        try {
            return sTIME_FORMAT_AM_PM_WITHOUT_AMPM.format(sDATE_TIME_FORMATS1.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatTimeWithOutAmPmAndSeconds(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        try {
            return sTIME_FORMAT_AM_PM_WITHOUT_AMPM_Seconds.format(sDATE_TIME_FORMAT.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date parseDate(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return new Date();
        }
        try {
            return sDATE_TIME_FORMAT.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String formatDateOnly(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        try {
            return PARSE_FORMAT.format(sDATE_TIME_FORMATS.parse(dateTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String day(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return sDAY_FORMAT.format(dateTime);
    }

    public static String dateTime(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return sDATE_TIME_FORMAT_TO.format(dateTime);
    }

    public static String month(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return sMONTH_FORMAT.format(dateTime);
    }

    /**
     * @param dateTime LIke 2017-07-31T02:38:56.086057
     * @return Like AM/PM
     */
    public static String formatAmPm(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        try {
            return sAM_PM.format(sDATE_TIME_FORMAT.parse(dateTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param dateTime LIke 2017-07-31T02:38:56.086057
     * @return Like 01:23 (actually in AM/Pm
     */
    public static String formatTimeWithOutAmPm(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return sTIME_FORMAT_AM_PM_WITHOUT_AMPM.format(dateTime);
    }

    /**
     * @param dateTime LIke 2017-07-31T02:38:56.086057
     * @return Like AM/PM
     */
    public static String formatAmPm(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return sAM_PM.format(dateTime);
    }

    public static String getRelativeDateTime(String dateTime) {
        return getRelativeDateTime(parseDate(dateTime));
    }

    public static String getRelativeDateTime(Date dateTime) {
        long now = System.currentTimeMillis();
        return DateUtils.getRelativeTimeSpanString(dateTime.getTime(), now, DateUtils.DAY_IN_MILLIS).toString();
    }

    public static Date parseTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return new Date();
        }
        for (String s : TIME_FORMATS) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(s, Locale.getDefault());
                if (time.contains("Z")) {
                    return simpleDateFormat.parse(time);
                } else {
                    return simpleDateFormat.parse(time);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Date();
    }

    public static String getHoursMinutesTime(String time) {
        return sTIME_FORMAT_HOURS_MINS.format(parseTime(time));
    }

    public static String getMinutes(String time) {
        return sTIME_MINUTES.format(parseTime(time));
    }

    public static String getHours(String time) {
        return sTIME_HOURS.format(parseTime(time));
    }


    public static String getNowDateTime() {
        return sDATE_TIME_FORMAT_DISPLAY.format(new Date());
    }

    public static String getDate(Date date) {
        return sDATE_TIME_FORMATS.format(date);
    }

    public static String getNowTime() {
        return sTIME_FORMAT_AM_PM_WITHOUT_AMPM.format(new Date());
    }

    public static HashMap<String, Object> getTimeFrame(String legend) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("end", getDate(atEndOfDay(new Date())));
        if (legend.equalsIgnoreCase("1D")) {
            body.put("start", getDate(atStartOfDay(new Date())));
            body.put("timeframe", "5Min");
        } else if (legend.equalsIgnoreCase("1W")) {
            body.put("start", getDate(atOneWeek(new Date())));
            body.put("timeframe", "1Hour");
        } else if (legend.equalsIgnoreCase("3M")) {
            body.put("start", getDate(atThreeMonth(new Date())));
            body.put("timeframe", "1Day");
        } else if (legend.equalsIgnoreCase("6M")) {
            body.put("start", getDate(atSixMonth(new Date())));
            body.put("timeframe", "1Day");
        } else if (legend.equalsIgnoreCase("1Y")) {
            body.put("start", getDate(atOneYear(new Date())));
            body.put("timeframe", "3Day");
        } else if (legend.equalsIgnoreCase("5Y")) {
            body.put("start", getDate(atFiveYear(new Date())));
            body.put("timeframe", "7Day");
        } else {
            body.put("start", getDate(atPreviousDay(new Date())));
            body.put("timeframe", "5Min");
        }
        return body;
    }

    public static Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -1);
        return calendar.getTime();
    }

    public static Date atOneWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        return calendar.getTime();
    }

    public static Date atPreviousDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static Date atThreeMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -3);
        return calendar.getTime();
    }

    public static Date atSixMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -6);
        return calendar.getTime();
    }

    public static Date atOneYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    public static Date atFiveYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -5);
        return calendar.getTime();
    }
}
