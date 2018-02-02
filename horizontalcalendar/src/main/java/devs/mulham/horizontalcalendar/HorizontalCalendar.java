package devs.mulham.horizontalcalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Calendar;

import devs.mulham.horizontalcalendar.adapter.DaysAdapter;
import devs.mulham.horizontalcalendar.adapter.HorizontalCalendarBaseAdapter;
import devs.mulham.horizontalcalendar.adapter.MonthsAdapter;
import devs.mulham.horizontalcalendar.model.CalendarItemStyle;
import devs.mulham.horizontalcalendar.model.HorizontalCalendarConfig;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalSnapHelper;
import devs.mulham.horizontalcalendar.utils.Utils;


/**
 * See {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @see HorizontalCalendarListener
 * @since v1.0.0
 */
public final class HorizontalCalendar {

    public enum Mode {DAYS, MONTHS}

    //region private Fields
    HorizontalCalendarView calendarView;
    private HorizontalCalendarBaseAdapter mCalendarAdapter;

    // Start & End Dates
    Calendar startDate;
    Calendar endDate;

    // Calendar Mode
    private Mode mode;
    // Number of Dates to Show on Screen
    private final int numberOfDatesOnScreen;

    // Interface events
    HorizontalCalendarListener calendarListener;

    private final int calendarId;
    /* Format, Colors & Font Sizes*/
    private final CalendarItemStyle defaultStyle;
    private final CalendarItemStyle selectedItemStyle;
    private final HorizontalCalendarConfig config;
    //endregion

    /**
     * Private Constructor to insure HorizontalCalendar can't be initiated the default way
     */
    HorizontalCalendar(Builder builder, HorizontalCalendarConfig config, CalendarItemStyle defaultStyle, CalendarItemStyle selectedItemStyle) {
        this.numberOfDatesOnScreen = builder.numberOfDatesOnScreen;
        this.calendarId = builder.viewId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.config = config;
        this.defaultStyle = defaultStyle;
        this.selectedItemStyle = selectedItemStyle;
        this.mode = builder.mode;
    }

    /* Init Calendar View */
    void init(View rootView, final Calendar defaultSelectedDate, HorizontalCalendarPredicate disablePredicate, CalendarEventsPredicate eventsPredicate) {
        calendarView = rootView.findViewById(calendarId);
        calendarView.setHasFixedSize(true);
        calendarView.setHorizontalScrollBarEnabled(false);
        calendarView.applyConfigFromLayout(this);

        HorizontalSnapHelper snapHelper = new HorizontalSnapHelper();
        snapHelper.attachToHorizontalCalendar(this);

        if (disablePredicate == null) {
            disablePredicate = defaultDisablePredicate;
        } else {
            disablePredicate = new HorizontalCalendarPredicate.Or(disablePredicate, defaultDisablePredicate);
        }

        if (mode == Mode.MONTHS){
            mCalendarAdapter = new MonthsAdapter(this, startDate, endDate, disablePredicate, eventsPredicate);
        } else {
            mCalendarAdapter = new DaysAdapter(this, startDate, endDate, disablePredicate, eventsPredicate);
        }

        calendarView.setAdapter(mCalendarAdapter);
        calendarView.setLayoutManager(new HorizontalLayoutManager(calendarView.getContext(), false));
        calendarView.addOnScrollListener(new HorizontalCalendarScrollListener());

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
        selectDate(Calendar.getInstance(), immediate);
    }

    /**
     * Select the date and center the Horizontal Calendar to this date
     *
     * @param date      The date to select
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the target date
     *                  ,or false to play default scroll animation speed.
     */
    public void selectDate(Calendar date, boolean immediate) {
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
    public void centerCalendarToPosition(final int position) {
        if (position != -1) {
            int relativeCenterPosition = Utils.calculateRelativeCenterPosition(position, calendarView.getPositionOfCenterItem(), numberOfDatesOnScreen / 2);
            if (relativeCenterPosition == position) {
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
            final int oldSelectedItem = calendarView.getPositionOfCenterItem();
            int relativeCenterPosition = Utils.calculateRelativeCenterPosition(position, oldSelectedItem, numberOfDatesOnScreen / 2);
            if (relativeCenterPosition == position) {
                return;
            }

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

    void refreshItemsSelector(int position1, int... positions) {
        mCalendarAdapter.notifyItemChanged(position1, "UPDATE_SELECTOR");
        if ((positions != null) && (positions.length > 0)) {
            for (int pos : positions) {
                mCalendarAdapter.notifyItemChanged(pos, "UPDATE_SELECTOR");
            }
        }
    }

    public boolean isItemDisabled(int position) {
        return mCalendarAdapter.isDisabled(position);
    }

    public void refresh() {
        mCalendarAdapter.notifyDataSetChanged();
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
    public Calendar getSelectedDate() {
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
    public Calendar getDateAt(int position) throws IndexOutOfBoundsException {
        return mCalendarAdapter.getItem(position);
    }

    /**
     * @param date The date to search for
     * @return true if the calendar contains this date or false otherwise
     */
    public boolean contains(Calendar date) {
        return positionOfDate(date) != -1;
    }

    public HorizontalCalendarView getCalendarView() {
        return calendarView;
    }

    public Context getContext() {
        return calendarView.getContext();
    }

    public void setRange(Calendar startDate, Calendar endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        mCalendarAdapter.update(startDate, endDate, false);
    }

    public CalendarItemStyle getDefaultStyle() {
        return defaultStyle;
    }

    public CalendarItemStyle getSelectedItemStyle() {
        return selectedItemStyle;
    }

    public HorizontalCalendarConfig getConfig() {
        return config;
    }

    public int getNumberOfDatesOnScreen() {
        return numberOfDatesOnScreen;
    }

    public int getShiftCells() {
        return numberOfDatesOnScreen / 2;
    }

    /**
     * @return position of date in Calendar, or -1 if date does not exist
     */
    public int positionOfDate(Calendar date) {
        if (Utils.isDateBefore(date, startDate) || Utils.isDateAfter(date, endDate)) {
            return -1;
        }

        int position;
        if (mode == Mode.DAYS){
            if (Utils.isSameDate(date, startDate)) {
                position = 0;
            } else {
                position = Utils.daysBetween(startDate, date);
            }
        } else {
            if (Utils.isSameMonth(date, startDate)) {
                position = 0;
            } else {
                position = Utils.monthsBetween(startDate, date);
            }
        }

        final int shiftCells = numberOfDatesOnScreen / 2;
        return position + shiftCells;
    }

    public static class Builder {

        final int viewId;
        final View rootView;

        // Start & End Dates
        Calendar startDate;
        Calendar endDate;
        Calendar defaultSelectedDate;

        Mode mode;
        // Number of Days to Show on Screen
        int numberOfDatesOnScreen;
        // Specified which dates should be disabled
        private HorizontalCalendarPredicate disablePredicate;
        // Add events to each Date
        private CalendarEventsPredicate eventsPredicate;

        private ConfigBuilder configBuilder;

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

        public Builder range(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder datesNumberOnScreen(int numberOfItemsOnScreen) {
            this.numberOfDatesOnScreen = numberOfItemsOnScreen;
            return this;
        }

        public Builder defaultSelectedDate(Calendar date) {
            defaultSelectedDate = date;
            return this;
        }

        public Builder disableDates(HorizontalCalendarPredicate predicate) {
            disablePredicate = predicate;
            return this;
        }

        public Builder addEvents(CalendarEventsPredicate predicate) {
            eventsPredicate = predicate;
            return this;
        }

        public ConfigBuilder configure() {
            if (configBuilder == null) {
                configBuilder = new ConfigBuilder(this);
            }

            return configBuilder;
        }

        private void initDefaultValues() throws IllegalStateException {
            /* Defaults variables */
            if ((startDate == null) || (endDate == null)) {
                throw new IllegalStateException("HorizontalCalendar range was not specified, either startDate or endDate is null!");
            }

            if (mode == null) {
                mode = Mode.DAYS;
            }
            if (numberOfDatesOnScreen <= 0) {
                numberOfDatesOnScreen = 5;
            }
            if (defaultSelectedDate == null) {
                defaultSelectedDate = Calendar.getInstance();
            }
        }

        /**
         * @return Instance of {@link HorizontalCalendar} initiated with builder settings
         */
        public HorizontalCalendar build() throws IllegalStateException {
            initDefaultValues();

            if (configBuilder == null) {
                configBuilder = new ConfigBuilder(this);
                configBuilder.end();
            }
            CalendarItemStyle defaultStyle = configBuilder.createDefaultStyle();
            CalendarItemStyle selectedItemStyle = configBuilder.createSelectedItemStyle();
            HorizontalCalendarConfig config = configBuilder.createConfig();

            HorizontalCalendar horizontalCalendar = new HorizontalCalendar(this, config, defaultStyle, selectedItemStyle);
            horizontalCalendar.init(rootView, defaultSelectedDate, disablePredicate, eventsPredicate);
            return horizontalCalendar;
        }
    }

    private final HorizontalCalendarPredicate defaultDisablePredicate = new HorizontalCalendarPredicate() {

        @Override
        public boolean test(Calendar date) {
            return Utils.isDateBefore(date, startDate) || Utils.isDateAfter(date, endDate);
        }

        @Override
        public CalendarItemStyle style() {
            return new CalendarItemStyle(Color.GRAY, null);
        }
    };

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
                    refreshItemsSelector(positionOfCenterItem);
                    if (lastSelectedItem != -1) {
                        refreshItemsSelector(lastSelectedItem);
                    }
                    lastSelectedItem = positionOfCenterItem;
                }
            }
        }
    }
}