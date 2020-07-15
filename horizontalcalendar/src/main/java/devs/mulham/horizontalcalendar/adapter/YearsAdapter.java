package devs.mulham.horizontalcalendar.adapter;

import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.R;
import devs.mulham.horizontalcalendar.model.HorizontalCalendarConfig;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarPredicate;
import devs.mulham.horizontalcalendar.utils.Utils;

/**
 * custom adapter for {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @since v1.3.3
 * <p>
 * See {devs.mulham.horizontalcalendar.R.layout#hc_item_calendar} Calendar CustomItem Layout
 */
public class YearsAdapter extends HorizontalCalendarBaseAdapter<DateViewHolder, Calendar> {

    public YearsAdapter(HorizontalCalendar horizontalCalendar, Calendar startDate, Calendar endDate, HorizontalCalendarPredicate disablePredicate, CalendarEventsPredicate eventsPredicate) {
        super(R.layout.hc_item_calendar, horizontalCalendar, startDate, endDate, disablePredicate, eventsPredicate);
    }

    @Override
    protected DateViewHolder createViewHolder(View itemView, int cellWidth) {
        final DateViewHolder holder = new DateViewHolder(itemView);

        holder.layoutContent.setMinimumWidth(cellWidth);
        //holder.textTop.setVisibility(View.GONE);

        return holder;
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
        Calendar year = getItem(position);
        HorizontalCalendarConfig config = horizontalCalendar.getConfig();

        final Integer selectorColor = horizontalCalendar.getConfig().getSelectorColor();
        if (selectorColor != null) {
            holder.selectionView.setBackgroundColor(selectorColor);
        }

        holder.textMiddle.setText(DateFormat.format(config.getFormatMiddleText(), year));
        holder.textMiddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.getSizeMiddleText());

        if (config.isShowTopText()) {
            holder.textTop.setText(DateFormat.format(config.getFormatTopText(), year));
            holder.textTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.getSizeTopText());
        } else {
            holder.textTop.setVisibility(View.GONE);
        }

        if (config.isShowBottomText()) {
            holder.textBottom.setText(DateFormat.format(config.getFormatBottomText(), year));
            holder.textBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.getSizeBottomText());
        } else {
            holder.textBottom.setVisibility(View.GONE);
        }

        showEvents(holder, year);
        applyStyle(holder, year, position);

    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position, List<Object> payloads) {
        if ((payloads == null) || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }

        Calendar date = getItem(position);
        applyStyle(holder, date, position);
    }

    @Override
    public Calendar getItem(int position) throws IndexOutOfBoundsException {
        if (position >= itemsCount) {
            throw new IndexOutOfBoundsException();
        }

        int yearsDiff = position - horizontalCalendar.getShiftCells();

        Calendar calendar = (Calendar) startDate.clone();
        calendar.add(Calendar.YEAR, yearsDiff);

        return calendar;
    }

    @Override
    protected int calculateItemsCount(Calendar startDate, Calendar endDate) {
        int year = Utils.yearsBetween(startDate, endDate) + 1;
        return year + (horizontalCalendar.getShiftCells() * 2);
    }

}
