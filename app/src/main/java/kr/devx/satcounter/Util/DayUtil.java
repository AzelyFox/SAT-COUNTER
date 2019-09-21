package kr.devx.satcounter.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class DayUtil {

    public final static String CLOCK_FORMAT = "a h:mm";
    public final static String NORMAL_DATE_FORMAT = "MMMM d EEEE";

    public static String getClock() {
        try {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat settingDateFormat = new SimpleDateFormat(CLOCK_FORMAT, Locale.getDefault());
            return settingDateFormat.format(today.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static String getDate() {
        try {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat settingDateFormat = new SimpleDateFormat(NORMAL_DATE_FORMAT, Locale.getDefault());
            return settingDateFormat.format(today.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static int getDaysLeft(Date target) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar dday = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            dday.setTime(target);
            long day = dday.getTimeInMillis()/86400000;
            long tday = today.getTimeInMillis()/86400000;
            // long count = tday - day;
            long count = day - tday;
            return (int) count;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long getMillisecondLeft(Date target) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar dday = Calendar.getInstance();
            dday.setTime(target);
            long day = dday.getTimeInMillis();
            long tday = today.getTimeInMillis();
            long count = day - tday;
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isNewDateTime() {
        try {
            Calendar today = Calendar.getInstance();
            boolean IS_HOUR_ZERO = today.get(Calendar.HOUR_OF_DAY) == 0;
            boolean IS_MINUTE_ZERO = today.get(Calendar.MINUTE) == 0;
            return IS_HOUR_ZERO && IS_MINUTE_ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
