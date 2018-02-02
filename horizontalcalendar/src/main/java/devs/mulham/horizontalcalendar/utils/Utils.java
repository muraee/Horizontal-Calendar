package devs.mulham.horizontalcalendar.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import devs.mulham.horizontalcalendar.HorizontalCalendar;

/**
 * @author Mulham-Raee
 * @since v1.3.0
 */
public final class Utils {

    /**
     * calculate each item width depends on {@link HorizontalCalendar#numberOfDatesOnScreen}
     */
    public static int calculateCellWidth(Context context, int itemsOnScreen) {
        WindowManager windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();

            display.getSize(size);
            int screenWidth = size.x;

            return screenWidth / itemsOnScreen;
        }

        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    public static int calculateRelativeCenterPosition(final int position, final int centerItem, final int shiftCells) {
        int relativeCenterPosition = position;

        if (position > centerItem) {
            relativeCenterPosition = position + shiftCells;
        } else if (position < centerItem) {
            relativeCenterPosition = position - shiftCells;
        }

        return relativeCenterPosition;
    }

    /**
     * @return <code>true</code> if dates are equal; <code>false</code> otherwise
     */
    public static boolean isSameDate(Calendar calendar1, Calendar calendar2){
        int day = calendar1.get(Calendar.DAY_OF_MONTH);

        return isSameMonth(calendar1, calendar2)
                && (day == calendar2.get(Calendar.DAY_OF_MONTH));
    }

    public static boolean isSameMonth(Calendar calendar1, Calendar calendar2){
        int month = calendar1.get(Calendar.MONTH);
        int year = calendar1.get(Calendar.YEAR);

        return (year == calendar2.get(Calendar.YEAR))
                && (month == calendar2.get(Calendar.MONTH));
    }

    public static boolean isDateBefore(Calendar date, Calendar origin){
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
        int year = date.get(Calendar.YEAR);

        if (year < origin.get(Calendar.YEAR)){
            return true;
        }

        return (year == origin.get(Calendar.YEAR)) && (dayOfYear < origin.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isDateAfter(Calendar date, Calendar origin){
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
        int year = date.get(Calendar.YEAR);

        if (year > origin.get(Calendar.YEAR)){
            return true;
        }

        return (year == origin.get(Calendar.YEAR)) && (dayOfYear > origin.get(Calendar.DAY_OF_YEAR));
    }

    public static int daysBetween(Calendar startInclusive, Calendar endExclusive){
        zeroTime(startInclusive);
        zeroTime(endExclusive);

        long diff = endExclusive.getTimeInMillis() - startInclusive.getTimeInMillis(); //result in millis
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    public static int monthsBetween(Calendar startInclusive, Calendar endExclusive){
        int startMonth = startInclusive.get(Calendar.MONTH);
        int endMonth = endExclusive.get(Calendar.MONTH);

        int startYear = startInclusive.get(Calendar.YEAR);
        int endYear = endExclusive.get(Calendar.YEAR);

        int yearsDiff = endYear - startYear;

        return (endMonth - startMonth) + (yearsDiff * 12);
    }

    public static void zeroTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
