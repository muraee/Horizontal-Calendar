package devs.mulham.horizontalcalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * See {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @see HorizontalCalendarListener
 * @since v1.0.0
 */
public final class HorizontalCalendar {

    //region private Fields
    HorizontalCalendarView calendarView;
    private HorizontalCalendarAdapter mCalendarAdapter;

    //Start & End Dates
    private final Date dateStartCalendar;
    private final Date dateEndCalendar;

    //Interface events
    HorizontalCalendarListener calendarListener;

    final private RecyclerView.OnScrollListener onScrollListener = new HorizontalCalendarScrollListener();

    private final int calendarId;
    //Number of Dates to Show on Screen
    private final int numberOfDatesOnScreen;
    /* Format, Colors & Font Sizes*/
    private SimpleDateFormat dateFormat;
    private final String formatDayName;
    private final String formatDayNumber;
    private final String formatMonth;
    private int textColorNormal, textColorSelected;
    private Drawable selectedDateBackground;
    private Integer selectorColor;
    private float textSizeMonthName, textSizeDayNumber, textSizeDayName;

    private final boolean showMonthName;
    private final boolean showDayName;
    //endregion

    /**
     * Private Constructor to insure HorizontalCalendar can't be initiated the default way
     */
    HorizontalCalendar(Builder builder) {
        this.calendarId = builder.viewId;
        this.textColorNormal = builder.textColorNormal;
        this.textColorSelected = builder.textColorSelected;
        this.selectedDateBackground = builder.selectedDateBackground;
        this.selectorColor = builder.selectorColor;
        this.formatDayName = builder.formatDayName;
        this.formatDayNumber = builder.formatDayNumber;
        this.formatMonth = builder.formatMonth;
        this.textSizeMonthName = builder.textSizeMonthName;
        this.textSizeDayNumber = builder.textSizeDayNumber;
        this.textSizeDayName = builder.textSizeDayName;
        this.numberOfDatesOnScreen = builder.numberOfDatesOnScreen;
        this.dateStartCalendar = builder.dateStartCalendar;
        this.dateEndCalendar = builder.dateEndCalendar;
        this.showDayName = builder.showDayName;
        this.showMonthName = builder.showMonthName;
    }

    /* Init Calendar View */
    void loadHorizontalCalendar(View rootView, final Date defaultSelectedDate) {

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        calendarView = rootView.findViewById(calendarId);
        calendarView.setHasFixedSize(true);
        calendarView.setHorizontalScrollBarEnabled(false);
        calendarView.setHorizontalCalendar(this);

        HorizontalSnapHelper snapHelper = new HorizontalSnapHelper();
        snapHelper.attachToHorizontalCalendar(this);

        mCalendarAdapter = new HorizontalCalendarAdapter(calendarView, dateStartCalendar, dateEndCalendar);
        calendarView.setAdapter(mCalendarAdapter);
        calendarView.setLayoutManager(new HorizontalLayoutManager(calendarView.getContext(), false));
        calendarView.addOnScrollListener(onScrollListener);

        post(new Runnable() {
            @Override
            public void run() {
                centerToPositionWithNoAnimation(positionOfDate(defaultSelectedDate));
            }
        });

    }

    public HorizontalCalendarListener getCalendarListener() {
        return calendarListener;
    }

    public void setCalendarListener(HorizontalCalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    /**
     * Select today date and center the Horizontal Calendar to this date
     *
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the date of today
     *                  ,or false to play default scroll animation speed.
     */
    public void goToday(boolean immediate) {
        selectDate(new Date(), immediate);
    }

    /**
     * Select the date and center the Horizontal Calendar to this date
     *
     * @param date      The date to select
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the target date
     *                  ,or false to play default scroll animation speed.
     */
    public void selectDate(Date date, boolean immediate) {
        int datePosition = positionOfDate(date);
        if (immediate) {
            centerToPositionWithNoAnimation(datePosition);
            if (calendarListener != null) {
                calendarListener.onDateSelected(date, datePosition);
            }
        } else {
            calendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_NORMAL);
            centerCalendarToPosition(datePosition);
        }
    }

    /**
     * Smooth scroll Horizontal Calendar to center this position and select the new centered day.
     *
     * @param position The position to center the calendar to!
     */
    void centerCalendarToPosition(final int position) {
        if (position != -1) {
            int relativeCenterPosition = calculateRelativeCenterPosition(position);
            if (relativeCenterPosition == position){
                return;
            }

            calendarView.smoothScrollToPosition(relativeCenterPosition);
        }
    }

    /**
     * Scroll Horizontal Calendar to center this position and select the new centered day.
     *
     * @param position The position to center the calendar to!
     */
    void centerToPositionWithNoAnimation(final int position) {
        if (position != -1) {
            int relativeCenterPosition = calculateRelativeCenterPosition(position);
            if (relativeCenterPosition == position){
                return;
            }

            final int oldSelectedItem = calendarView.getPositionOfCenterItem();
            calendarView.scrollToPosition(relativeCenterPosition);
            calendarView.post(new Runnable() {
                @Override
                public void run() {
                    final int newSelectedItem = calendarView.getPositionOfCenterItem();
                    //refresh to update background colors
                    refreshItemsSelector(newSelectedItem, oldSelectedItem);
                }
            });
        }
    }

    private int calculateRelativeCenterPosition(final int position) {
        final int centerItem = calendarView.getPositionOfCenterItem();
        final int shiftCells = getShiftCells();
        int relativeCenterPosition = position;

        if (position > centerItem) {
            relativeCenterPosition = position + shiftCells;
        } else if (position < centerItem) {
            relativeCenterPosition = position - shiftCells;
        }

        return relativeCenterPosition;
    }

    int getShiftCells() {
        return numberOfDatesOnScreen / 2;
    }

    void refreshItemSelector(int position) {
        mCalendarAdapter.notifyItemChanged(position, "UPDATE_SELECTOR");
    }

    void refreshItemsSelector(int position1, int... positions) {
        refreshItemSelector(position1);
        if ((positions != null) && (positions.length > 0)) {
            for (int pos : positions) {
                refreshItemSelector(pos);
            }
        }
    }

    public void show() {
        calendarView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        calendarView.setVisibility(View.INVISIBLE);
    }

    public void post(Runnable runnable) {
        calendarView.post(runnable);
    }

    @TargetApi(21)
    public void setElevation(float elevation) {
        calendarView.setElevation(elevation);
    }

    /**
     * @return the current selected date
     */
    public Date getSelectedDate() {
        return mCalendarAdapter.getItem(calendarView.getPositionOfCenterItem());
    }

    /**
     * @return position of selected date in Horizontal Calendar
     */
    public int getSelectedDatePosition() {
        return calendarView.getPositionOfCenterItem();
    }

    /**
     * @param position The position of date
     * @return the date on this index
     * @throws IndexOutOfBoundsException if position is out of the calendar range
     */
    public Date getDateAt(int position) throws IndexOutOfBoundsException {
        return mCalendarAdapter.getItem(position);
    }

    /**
     * @param date The date to search for
     * @return true if the calendar contains this date or false otherwise
     */
    public boolean contains(Date date) {
        return positionOfDate(date) != -1;
    }

    //region Getters & Setters
    public Date getDateStartCalendar() {
        return dateStartCalendar;
    }

    public Date getDateEndCalendar() {
        return dateEndCalendar;
    }

    public String getFormatDayName() {
        return formatDayName;
    }

    public String getFormatDayNumber() {
        return formatDayNumber;
    }

    public String getFormatMonth() {
        return formatMonth;
    }

    public boolean isShowDayName() {
        return showDayName;
    }

    public boolean isShowMonthName() {
        return showMonthName;
    }

    public int getNumberOfDatesOnScreen() {
        return numberOfDatesOnScreen;
    }

    public Drawable getSelectedDateBackground() {
        return selectedDateBackground;
    }

    public void setSelectedDateBackground(Drawable selectedDateBackground) {
        this.selectedDateBackground = selectedDateBackground;
    }

    public int getTextColorNormal() {
        return textColorNormal;
    }

    public void setTextColorNormal(int textColorNormal) {
        this.textColorNormal = textColorNormal;
    }

    public int getTextColorSelected() {
        return textColorSelected;
    }

    public void setTextColorSelected(int textColorSelected) {
        this.textColorSelected = textColorSelected;
    }

    public Integer getSelectorColor() {
        return selectorColor;
    }

    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
    }

    public float getTextSizeMonthName() {
        return textSizeMonthName;
    }

    public void setTextSizeMonthName(float textSizeMonthName) {
        this.textSizeMonthName = textSizeMonthName;
    }

    public float getTextSizeDayNumber() {
        return textSizeDayNumber;
    }

    public void setTextSizeDayNumber(float textSizeDayNumber) {
        this.textSizeDayNumber = textSizeDayNumber;
    }

    public float getTextSizeDayName() {
        return textSizeDayName;
    }

    public void setTextSizeDayName(float textSizeDayName) {
        this.textSizeDayName = textSizeDayName;
    }
    //endregion

    /**
     * @return position of date in Calendar, or -1 if date does not exist
     */
    public int positionOfDate(Date date) {
        if (date.before(dateStartCalendar) || date.after(dateEndCalendar)) {
            return -1;
        }

        int position;
        if (isDatesDaysEquals(date, dateStartCalendar)) {
            position = 0;
        } else {
            long diff = date.getTime() - dateStartCalendar.getTime(); //result in millis
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            position = (int) days;
        }

        final int shiftCells = getShiftCells();
        return position + shiftCells;
    }

    /**
     * @return <code>true</code> if dates are equal; <code>false</code> otherwise
     */
    public boolean isDatesDaysEquals(Date date1, Date date2) {
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    public static class Builder {

        final int viewId;
        final View rootView;

        //Start & End Dates
        Date dateStartCalendar;
        Date dateEndCalendar;

        //Number of Days to Show on Screen
        int numberOfDatesOnScreen;

        /* Format, Colors & Font Sizes*/
        String formatDayName;
        String formatDayNumber;
        String formatMonth;
        int textColorNormal, textColorSelected;
        Drawable selectedDateBackground;
        Integer selectorColor;
        float textSizeMonthName, textSizeDayNumber, textSizeDayName;

        boolean showMonthName = true;
        boolean showDayName = true;
        Date defaultSelectedDate;

        /**
         * @param rootView pass the rootView for the Fragment where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        public Builder(View rootView, int viewId) {
            this.rootView = rootView;
            this.viewId = viewId;
        }

        /**
         * @param activity pass the activity where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        public Builder(Activity activity, int viewId) {
            this.rootView = activity.getWindow().getDecorView();
            this.viewId = viewId;
        }

        public Builder defaultSelectedDate(Date date) {
            defaultSelectedDate = date;
            return this;
        }

        public Builder startDate(Date dateStartCalendar) {
            this.dateStartCalendar = dateStartCalendar;
            return this;
        }

        public Builder endDate(Date dateEndCalendar) {
            this.dateEndCalendar = dateEndCalendar;
            return this;
        }

        public Builder datesNumberOnScreen(int numberOfItemsOnScreen) {
            this.numberOfDatesOnScreen = numberOfItemsOnScreen;
            return this;
        }

        public Builder dayNameFormat(String format) {
            this.formatDayName = format;
            return this;
        }

        public Builder dayNumberFormat(String format) {
            this.formatDayNumber = format;
            return this;
        }

        public Builder monthFormat(String format) {
            this.formatMonth = format;
            return this;
        }

        public Builder textColor(int textColorNormal, int textColorSelected) {
            this.textColorNormal = textColorNormal;
            this.textColorSelected = textColorSelected;
            return this;
        }

        public Builder selectedDateBackground(Drawable background) {
            this.selectedDateBackground = background;
            return this;
        }

        public Builder selectorColor(int selectorColor) {
            this.selectorColor = selectorColor;
            return this;
        }

        /**
         * Set the text size of the labels in scale-independent pixels
         *
         * @param textSizeMonthName the month name text size, in SP
         * @param textSizeDayNumber the day number text size, in SP
         * @param textSizeDayName   the day name text size, in SP
         */
        public Builder textSize(float textSizeMonthName, float textSizeDayNumber,
                                float textSizeDayName) {
            this.textSizeMonthName = textSizeMonthName;
            this.textSizeDayNumber = textSizeDayNumber;
            this.textSizeDayName = textSizeDayName;
            return this;
        }

        /**
         * Set the text size of the month name label in scale-independent pixels
         *
         * @param textSizeMonthName the month name text size, in SP
         */
        public Builder textSizeMonthName(float textSizeMonthName) {
            this.textSizeMonthName = textSizeMonthName;
            return this;
        }

        /**
         * Set the text size of the day number label in scale-independent pixels
         *
         * @param textSizeDayNumber the day number text size, in SP
         */
        public Builder textSizeDayNumber(float textSizeDayNumber) {
            this.textSizeDayNumber = textSizeDayNumber;
            return this;
        }

        /**
         * Set the text size of the day name label in scale-independent pixels
         *
         * @param textSizeDayName the day name text size, in SP
         */
        public Builder textSizeDayName(float textSizeDayName) {
            this.textSizeDayName = textSizeDayName;
            return this;
        }

        public Builder showDayName(boolean value) {
            showDayName = value;
            return this;
        }

        public Builder showMonthName(boolean value) {
            showMonthName = value;
            return this;
        }

        /**
         * @return Instance of {@link HorizontalCalendar} initiated with builder settings
         */
        public HorizontalCalendar build() {
            initDefaultValues();
            HorizontalCalendar horizontalCalendar = new HorizontalCalendar(this);
            horizontalCalendar.loadHorizontalCalendar(rootView, defaultSelectedDate);
            return horizontalCalendar;
        }

        private void initDefaultValues() {
            /* Defaults variables */
            if (numberOfDatesOnScreen <= 0) {
                numberOfDatesOnScreen = 5;
            }

            if ((formatDayName == null) && showDayName) {
                formatDayName = "EEE";
            }
            if (formatDayNumber == null) {
                formatDayNumber = "dd";
            }
            if ((formatMonth == null) && showMonthName) {
                formatMonth = "MMM";
            }
            if (dateStartCalendar == null) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MONTH, -1);
                dateStartCalendar = c.getTime();
            }
            if (dateEndCalendar == null) {
                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.MONTH, 1);
                dateEndCalendar = c2.getTime();
            }
            if (defaultSelectedDate == null) {
                defaultSelectedDate = new Date();
            }
        }
    }

    private class HorizontalCalendarScrollListener extends RecyclerView.OnScrollListener {

        int lastSelectedItem = -1;
        final Runnable selectedItemRefresher = new SelectedItemRefresher();

        HorizontalCalendarScrollListener() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //On Scroll, agenda is refresh to update background colors
            post(selectedItemRefresher);

            if (calendarListener != null) {
                calendarListener.onCalendarScroll(calendarView, dx, dy);
            }
        }

        private class SelectedItemRefresher implements Runnable {

            SelectedItemRefresher() {
            }

            @Override
            public void run() {
                final int positionOfCenterItem = calendarView.getPositionOfCenterItem();
                if ((lastSelectedItem == -1) || (lastSelectedItem != positionOfCenterItem)) {
                    //On Scroll, agenda is refresh to update background colors
                    //mCalendarAdapter.notifyItemRangeChanged(getSelectedDatePosition() - 2, 5, "UPDATE_SELECTOR");
                    refreshItemSelector(positionOfCenterItem);
                    if (lastSelectedItem != -1) {
                        refreshItemSelector(lastSelectedItem);
                    }
                    lastSelectedItem = positionOfCenterItem;
                }
            }
        }
    }
}