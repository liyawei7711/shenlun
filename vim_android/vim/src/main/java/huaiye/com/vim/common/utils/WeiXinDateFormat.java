package huaiye.com.vim.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

public class WeiXinDateFormat {

    public final static String DATA_FORMATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间戳格式转换
     */
    static String dayNames[] = {AppUtils.getString(R.string.date_sunday), AppUtils.getString(R.string.date_monday), AppUtils.getString(R.string.date_tuesday), AppUtils.getString(R.string.date_wednesday), AppUtils.getString(R.string.date_thursday), AppUtils.getString(R.string.date_friday), AppUtils.getString(R.string.date_saturday)};

    public static String getChatTime(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);

        String timeFormat;
        String yearTimeFormat;
        String am_pm = "";
        int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            am_pm = AppUtils.getString(R.string.date_before_dawn);
        } else if (hour >= 6 && hour < 12) {
            am_pm = AppUtils.getString(R.string.date_morning);
        } else if (hour == 12) {
            am_pm = AppUtils.getString(R.string.date_noon);
        } else if (hour > 12 && hour < 18) {
            am_pm = AppUtils.getString(R.string.date_afternoon);
        } else if (hour >= 18) {
            am_pm = AppUtils.getString(R.string.date_night);
        }
        timeFormat = AppUtils.getString(R.string.date_format_1) + am_pm + "HH:mm";
        yearTimeFormat = AppUtils.getString(R.string.date_format_2) + am_pm + "HH:mm";

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timesamp);
                        break;
                    case 1:
                        result = AppUtils.getString(R.string.date_yesterday) + getHourAndMin(timesamp);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if (dayOfMonth == todayOfMonth) {//表示是同一周
                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek != 1) {//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(timesamp);
                            } else {
                                result = getTime(timesamp, timeFormat);
                            }
                        } else {
                            result = getTime(timesamp, timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timesamp, timeFormat);
                        break;
                }
            } else {
                result = getTime(timesamp, timeFormat);
            }
        } else {
            result = getYearTime(timesamp, yearTimeFormat);
        }
        return result;
    }

    static String dayNamesYingWen[] = {AppUtils.getString(R.string.date_sunday_yingwen),
            AppUtils.getString(R.string.date_monday_yingwen),
            AppUtils.getString(R.string.date_tuesday_yingwen),
            AppUtils.getString(R.string.date_wednesday_yingwen),
            AppUtils.getString(R.string.date_thursday_yingwen),
            AppUtils.getString(R.string.date_friday_yingwen),
            AppUtils.getString(R.string.date_saturday_yingwen)};

    public static String getChatTimeYingWen(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);

        String yearTimeFormat;
        String am_pm = "";
        int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            am_pm = AppUtils.getString(R.string.date_before_dawn_yingwen);
        } else if (hour >= 6 && hour < 12) {
            am_pm = AppUtils.getString(R.string.date_morning_yingwen);
        } else if (hour == 12) {
            am_pm = AppUtils.getString(R.string.date_noon_yingwen);
        } else if (hour > 12 && hour < 18) {
            am_pm = AppUtils.getString(R.string.date_afternoon_yingwen);
        } else if (hour >= 18) {
            am_pm = AppUtils.getString(R.string.date_night_yingwen);
        }

        yearTimeFormat = AppUtils.getString(R.string.date_format_2_yingwen);

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timesamp) + " " + am_pm;
                        break;
                    default:
                        result = getYearTime(timesamp, yearTimeFormat);
                        break;
                }
            } else {
                result = getYearTime(timesamp, yearTimeFormat);
            }
        } else {
            result = getYearTime(timesamp, yearTimeFormat);
        }
        return result;
    }

    /**
     * 当天的显示时间格式
     *
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }


    /**
     * 不同一周的显示时间格式
     *
     * @param time
     * @param timeFormat
     * @return
     */
    public static long getLongTime(String time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        try {
            Date data = format.parse(time);
            return data.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 不同一周的显示时间格式
     *
     * @param time
     * @param timeFormat
     * @return
     */
    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     *
     * @param time
     * @param yearTimeFormat
     * @return
     */
    public static String getYearTime(long time, String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }

}
