package com.example.hourlymaids.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateTimeUtils {

    public static String YYYYMM = "yyyy-MM";

    public static String YYYYMMDD = "yyyy-MM-dd";

    public static String YYYYMMDDhhmmss = "yyyy-MM-dd HH:mm:ss";

    public static String YYYYMMDDhhmmssSSS = "yyyy-MM-dd HH:mm:ss:SSS";

    public static String TIME_YYYYMMDDhhmmssSSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String hhmmssSSS = "HH:mm:ss:SSS";

    public static String hhmmss = "HH:mm:ss";

    public static String Z = "Z";

    public static String Z_OFFSET = "+00:00";

    public static final String MONTH_NAME = "dd MMMM yyyy";

    public static String MMDDYYYY = "MM-dd-yyyy";

    public static String DDMMYYYY = "dd-MM-yyyy";
    public static String DDMMYYYYHHMMSS = "dd-MM-yyyy HH:mm:ss";


    /**
     * Convert String To Date Or Null
     *
     * @param date
     * @param pattern
     * @return Date
     * @author at-hungnguyen2
     */
    public static Date convertStringToDateOrNull(String date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert Date to String
     *
     * @param date
     * @param pattern
     * @return Date
     * @author at-hungnguyen2
     */
    public static String convertDateToStringOrEmpty(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Format time to string string.
     *
     * @param date the date
     * @param time the time
     * @return the string
     */
    public static String convertStringByFormatDateTimeOrNull(Date date, String time) {
        try {
            String dateFm = convertDateToStringOrEmpty(date, YYYYMMDD);
            SimpleDateFormat sdf = new SimpleDateFormat(hhmmssSSS);
            sdf.parse(time);
            String resultFm = "%s %s";
            Date result = convertStringToDateOrNull(String.format(resultFm, dateFm, time), YYYYMMDDhhmmssSSS);
            return convertDateToStringOrEmpty(result, TIME_YYYYMMDDhhmmssSSS);

        } catch (Exception e) {
            return null;
        }
    }

    public static Date getCurrentDateWithUTC() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDDhhmmss);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localDateFormat = new SimpleDateFormat(YYYYMMDDhhmmss);
        return localDateFormat.parse(simpleDateFormat.format(new Date()));
    }

    /**
     * Convert time utc to time zone long.
     *
     * @param date     the date
     * @param timeZone the time zone
     * @return the long
     */
    public static long convertTimeUTCToTimeZoneToGetTimeDiffrence(Date date, String timeZone) {
        try {
            //Get time difference between 2 TZ contains time DST
            Instant dateUtc = date.toInstant();
            ZoneId zone = ZoneId.of(timeZone);
            // get offset
            ZonedDateTime time = ZonedDateTime.ofInstant(dateUtc, zone);
            ZoneOffset zoneOffset = time.getOffset();
            return zoneOffset.getTotalSeconds() * 1000;
        } catch (Exception e) {
            return 0L;
        }
    }
    public static List<Date> getDatesBetweenDateRange(Date startDate, Date endDate) {
        List<Date> dateList = new ArrayList<>();
        Long startMillisecond = startDate.getTime();
        Long endMillisecond = endDate.getTime();

        for (; startMillisecond <= endMillisecond; startMillisecond += 86400000) {
            dateList.add(new Date(startMillisecond));
        }
        return dateList;
    }
}