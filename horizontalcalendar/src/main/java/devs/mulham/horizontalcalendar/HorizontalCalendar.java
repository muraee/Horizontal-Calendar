package devs.mulham.horizontalcalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import java.text.SimpleDateFormat;
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

    //Number of Dates to Show on Screen
    private final int numberOfDatesOnScreen;

    //Interface events
    HorizontalCalendarListener calendarListener;

    final private RecyclerView.OnScrollListener onScrollListener = new HorizontalCalendarScrollListener();

    private final int calendarId;
    /* Format, Colors & Font Sizes*/
    private SimpleDateFormat dateFormat;
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
        this.dateStartCalendar = builder.dateStartCalendar;
        this.dateEndCalendar = builder.dateEndCalendar;
        this.config = config;
        this.defaultStyle = defaultStyle;
        this.selectedItemStyle = selectedItemStyle;
    }

    /* Init Calendar View */
    void loadHorizontalCalendar(View rootView, final Date defaultSelectedDate, final HorizontalCalendarPredicate disablePredicate) {

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        calendarView = rootView.findViewById(calendarId);
        calendarView.setHasFixedSize(true);
        calendarView.setHorizontalScrollBarEnabled(false);
        calendarView.setHorizontalCalendar(this);

        HorizontalSnapHelper snapHelper = new HorizontalSnapHelper();
        snapHelper.attachToHorizontalCalendar(this);

        mCalendarAdapter = new HorizontalCalendarAdapter(this, dateStartCalendar, dateEndCalendar, disablePredicate);
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
            int relativeCenterPosition = calculateRelativeCenterPosition(position);
            if (relativeCenterPosition == position) {
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

    public boolean isItemDisabled(int position) {
        return mCalendarAdapter.isDisabled(position);
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

        // Start & End Dates
        Date dateStartCalendar;
        Date dateEndCalendar;
        Date defaultSelectedDate;

        // Number of Days to Show on Screen
        int numberOfDatesOnScreen;
        // Specified which dates should be disabled
        private HorizontalCalendarPredicate disablePredicate;

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

        public Builder defaultSelectedDate(Date date) {
            defaultSelectedDate = date;
            return this;
        }

        public Builder disableDates(HorizontalCalendarPredicate predicate) {
            disablePredicate = predicate;
            return this;
        }

        public ConfigBuilder configure(){
            if (configBuilder == null){
                configBuilder = new ConfigBuilder(this);
            }

            return configBuilder;
        }

        private void initDefaultValues() throws IllegalStateException{
            /* Defaults variables */
            if ((dateStartCalendar == null) || (dateEndCalendar == null)) {
                throw new IllegalStateException("HorizontalCalendar range was not specified, either startDate or endDate is null!");
            }
            HorizontalCalendarPredicate defaultDisablePredicate = new DefaultDisablePredicate(dateStartCalendar, dateEndCalendar);
            if (disablePredicate == null) {
                disablePredicate = defaultDisablePredicate;
            } else {
                disablePredicate = new HorizontalCalendarPredicate.Or(disablePredicate, defaultDisablePredicate);
            }
            if (numberOfDatesOnScreen <= 0) {
                numberOfDatesOnScreen = 5;
            }
            if (defaultSelectedDate == null) {
                defaultSelectedDate = new Date();
            }
        }

        /**
         * @return Instance of {@link HorizontalCalendar} initiated with builder settings
         */
        public HorizontalCalendar build() throws IllegalStateException{
            initDefaultValues();

            if (configBuilder == null){
                configBuilder = new ConfigBuilder(this);
                configBuilder.end();
            }
            CalendarItemStyle defaultStyle = configBuilder.createDefaultStyle();
            CalendarItemStyle selectedItemStyle = configBuilder.createSelectedItemStyle();
            HorizontalCalendarConfig config = configBuilder.createConfig();

            HorizontalCalendar horizontalCalendar = new HorizontalCalendar(this, config, defaultStyle, selectedItemStyle);
            horizontalCalendar.loadHorizontalCalendar(rootView, defaultSelectedDate, disablePredicate);
            return horizontalCalendar;
        }
    }

    private static class DefaultDisablePredicate implements HorizontalCalendarPredicate {

        private final Date startDate;
        private final Date endDate;

        DefaultDisablePredicate(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public boolean test(Date date) {
            return date.before(startDate) || date.after(endDate);
        }

        @Override
        public CalendarItemStyle style() {
            return new CalendarItemStyle(Color.GRAY, null);
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