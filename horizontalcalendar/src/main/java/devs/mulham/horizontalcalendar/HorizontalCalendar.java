package devs.mulham.horizontalcalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * See {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @version 1.1
 * @see HorizontalCalendarListener
 */
public class HorizontalCalendar {

    //region private Fields
    private HorizontalCalendarView calendarView;
    private HorizontalCalendarAdapter mCalendarAdapter;
    private ArrayList<Date> mListDays;
    private boolean loading;
    private DateHandler handler;

    //Start & End Dates
    private Date dateStartCalendar;
    private Date dateEndCalendar;

    //Interface events
    private HorizontalCalendarListener calendarListener;
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //On scroll end, the dateSelect event is call
                //and agenda is center to the good item
                int position = calendarView.getPositionOfCenterItem();

                if (calendarListener != null) {
                    calendarListener.onDateSelected(mListDays.get(position), position);
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //On Scroll, agenda is refresh to update background colors
            post(new Runnable() {
                @Override
                public void run() {
                    mCalendarAdapter.notifyDataSetChanged();
                }
            });

            if (calendarListener != null) {
                calendarListener.onCalendarScroll(calendarView, dx, dy);
            }

        }
    };

    //RootView
    private final View rootView;
    private final int calendarId;
    //Number of Dates to Show on Screen
    private final int numberOfDatesOnScreen;
    /* Format, Colors & Font Sizes*/
    private SimpleDateFormat dateFormat;
    private final String formatDayName;
    private final String formatDayNumber;
    private final String formatMonth;
    private int textColorNormal, textColorSelected;
    private int selectedDateBackground;
    private int selectorColor;
    private float textSizeMonthName, textSizeDayNumber, textSizeDayName;

    private final boolean showMonthName;
    private final boolean showDayName;
    //endregion

    /**
     * Private Constructor to insure HorizontalCalendar can't be initiated the default way
     */
    private HorizontalCalendar(Builder builder) {
        this.rootView = builder.rootView;
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

        handler = new DateHandler(this, builder.defaultSelectedDate);
    }

    /* Init Calendar View */
    private void loadHorizontalCalendar() {

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        mListDays = new ArrayList<>();
        calendarView = (HorizontalCalendarView) rootView.findViewById(calendarId);
        calendarView.setHasFixedSize(true);
        calendarView.setHorizontalScrollBarEnabled(false);
        calendarView.setHorizontalCalendar(this);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(calendarView);

        hide();
        new InitializeDatesList().execute();
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
     * ,or false to play default scroll animation speed.
     */
    public void goToday(boolean immediate) {
        selectDate(new Date(), immediate);
    }

    /**
     * Select the date and center the Horizontal Calendar to this date
     *
     * @param date The date to select
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the target date
     * ,or false to play default scroll animation speed.
     */
    public void selectDate(Date date, boolean immediate) {
        if (loading) {
            handler.date = date;
            handler.immediate = immediate;
        } else {
            if (immediate) {
                int datePosition = positionOfDate(date);
                centerToPositionWithNoAnimation(datePosition);
                calendarListener.onDateSelected(date, datePosition);
            } else {
                calendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_NORMAL);
                centerCalendarToPosition(positionOfDate(date));
            }
        }
    }

    /**
     * Center the Horizontal Calendar to this position and select the day on this position
     *
     * @param position The position to center the calendar to!
     */
    protected void centerCalendarToPosition(int position) {
        if (position != -1) {
            int shiftCells = numberOfDatesOnScreen / 2;
            int centerItem = calendarView.getPositionOfCenterItem();

            if (position > centerItem) {
                calendarView.smoothScrollToPosition(position + shiftCells);
            } else if (position < centerItem) {
                calendarView.smoothScrollToPosition(position - shiftCells);
            }
        }
    }

    protected void centerToPositionWithNoAnimation(final int position) {
        if (position != -1) {
            int shiftCells = numberOfDatesOnScreen / 2;
            int centerItem = calendarView.getPositionOfCenterItem();

            if (position > centerItem) {
                calendarView.scrollToPosition(position + shiftCells);
            } else if (position < centerItem) {
                calendarView.scrollToPosition(position - shiftCells);
            }

            calendarView.post(new Runnable() {
                @Override
                public void run() {
                    mCalendarAdapter.notifyDataSetChanged();
                }
            });
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
        return mListDays.get(calendarView.getPositionOfCenterItem());
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
     * @throws IndexOutOfBoundsException
     */
    public Date getDateAt(int position) throws IndexOutOfBoundsException {
        return mCalendarAdapter.getItem(position);
    }

    /**
     * @param date The date to search for
     * @return true if the calendar contains this date or false otherwise
     */
    public boolean contains(Date date) {
        return mListDays.contains(date);
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

    public int getSelectedDateBackground() {
        return selectedDateBackground;
    }

    public void setSelectedDateBackground(int selectedDateBackground) {
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

    public int getSelectorColor() {
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
        int position = -1;
        for (int i = 0; i < mListDays.size(); i++) {
            if (isDatesDaysEquals(date, mListDays.get(i))) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * @return true if dates are equal
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
        int selectedDateBackground;
        int selectorColor;
        float textSizeMonthName, textSizeDayNumber, textSizeDayName;

        boolean showMonthName = true;
        boolean showDayName = true;
        Date defaultSelectedDate;

        /**
         * @param rootView pass the rootView for the Fragment where HorizontalCalendar is attached
         * @param viewId the id specified for HorizontalCalendarView in your layout
         */
        public Builder(View rootView, int viewId) {
            this.rootView = rootView;
            this.viewId = viewId;
        }

        /**
         * @param activity pass the activity where HorizontalCalendar is attached
         * @param viewId the id specified for HorizontalCalendarView in your layout
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

        public Builder selectedDateBackground(int backgroundColor) {
            this.selectedDateBackground = backgroundColor;
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
            horizontalCalendar.loadHorizontalCalendar();
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
            if (defaultSelectedDate == null){
                defaultSelectedDate = new Date();
            }
        }
    }

    private class InitializeDatesList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            loading = true;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ArrayList of dates is set with all the dates between
            //start and end date
            GregorianCalendar calendar = new GregorianCalendar();

            calendar.setTime(dateStartCalendar);
            calendar.add(Calendar.DATE, -(numberOfDatesOnScreen / 2));
            Date dateStartBefore = calendar.getTime();
            calendar.setTime(dateEndCalendar);
            calendar.add(Calendar.DATE, numberOfDatesOnScreen / 2);
            Date dateEndAfter = calendar.getTime();

            Date date = dateStartBefore;
            while (!date.after(dateEndAfter)) {
                mListDays.add(date);
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mCalendarAdapter = new HorizontalCalendarAdapter(calendarView, mListDays);
            calendarView.setAdapter(mCalendarAdapter);
            calendarView.setLayoutManager(new HorizontalLayoutManager(calendarView.getContext(), false));

            show();
            handler.sendMessage(new Message());
            calendarView.addOnScrollListener(onScrollListener);
        }
    }

    private static class DateHandler extends Handler {

        private final WeakReference<HorizontalCalendar> horizontalCalendar;
        public Date date = null;
        public boolean immediate = true;

        public DateHandler(HorizontalCalendar horizontalCalendar, Date defaultDate) {
            this.horizontalCalendar = new WeakReference<>(horizontalCalendar);
            this.date = defaultDate;
        }

        @Override
        public void handleMessage(Message msg) {
            HorizontalCalendar calendar = horizontalCalendar.get();
            if (calendar != null) {
                calendar.loading = false;
                if (date != null) {
                    calendar.selectDate(date, immediate);
                }

            }
        }
    }
}