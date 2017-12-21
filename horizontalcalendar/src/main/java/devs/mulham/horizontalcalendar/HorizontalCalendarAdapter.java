package devs.mulham.horizontalcalendar;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * custom adapter for {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @since v1.0.0
 * <p>
 * See {devs.mulham.horizontalcalendar.R.layout#item_calendar} Calendar CustomItem Layout
 */
class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.DayViewHolder> {

    private final Date dateStart;
    private final HorizontalCalendarPredicate disablePredicate;
    private int cellWidth;
    private final int itemsCount;

    private final CalendarItemStyle disabledItemStyle;

    HorizontalCalendar horizontalCalendar;

    HorizontalCalendarAdapter(HorizontalCalendar horizontalCalendar, Date dateStart, Date dateEnd, HorizontalCalendarPredicate disablePredicate) {
        this.horizontalCalendar = horizontalCalendar;
        this.dateStart = dateStart;
        this.disablePredicate = disablePredicate;
        this.disabledItemStyle = disablePredicate.style();

        long diff = dateEnd.getTime() - dateStart.getTime(); //result in millis
        itemsCount = (int) TimeUnit.MILLISECONDS.toDays(diff) + 1;

        calculateCellWidth(horizontalCalendar.calendarView.getContext());
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calendar, viewGroup, false);

        convertView.setMinimumWidth(cellWidth);

        final DayViewHolder holder = new DayViewHolder(convertView);
        final Integer selectorColor = horizontalCalendar.getConfig().getSelectorColor();
        if (selectorColor != null) {
            holder.selectionView.setBackgroundColor(selectorColor);
        }

        holder.rootView.setOnClickListener(new MyOnClickListener(holder));
        holder.rootView.setOnLongClickListener(new MyOnLongClickListener(holder));

        return holder;
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        Date day = getItem(position);
        HorizontalCalendarConfig config = horizontalCalendar.getConfig();

        holder.textMiddle.setText(DateFormat.format(config.getFormatMiddleText(), day).toString());
        holder.textMiddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.getSizeMiddleText());

        if (config.isShowTopText()) {
            holder.textTop.setText(DateFormat.format(config.getFormatTopText(), day).toString());
            holder.textTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.getSizeTopText());
        } else {
            holder.textTop.setVisibility(View.GONE);
        }

        if (config.isShowBottomText()) {
            holder.textBottom.setText(DateFormat.format(config.getFormatBottomText(), day).toString());
            holder.textBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.getSizeBottomText());
        } else {
            holder.textBottom.setVisibility(View.GONE);
        }

        applyStyle(holder, day, position);

    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position, List<Object> payloads) {
        if ((payloads == null) || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }

        Date day = getItem(position);
        applyStyle(holder, day, position);
    }

    private void applyStyle(DayViewHolder holder, Date day, int position) {
        int selectedItemPosition = horizontalCalendar.getSelectedDatePosition();

        boolean isDisabled = disablePredicate.test(day);
        holder.rootView.setEnabled(!isDisabled);
        if (isDisabled && (disabledItemStyle != null)) {
            applyStyle(holder, disabledItemStyle);
            holder.selectionView.setVisibility(View.INVISIBLE);
            return;
        }

        // Selected Day
        if (position == selectedItemPosition) {
            applyStyle(holder, horizontalCalendar.getSelectedItemStyle());
            holder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            applyStyle(holder, horizontalCalendar.getDefaultStyle());
            holder.selectionView.setVisibility(View.INVISIBLE);
        }
    }

    private void applyStyle(DayViewHolder holder, CalendarItemStyle itemStyle) {
        holder.textTop.setTextColor(itemStyle.getColorTopText());
        holder.textMiddle.setTextColor(itemStyle.getColorMiddleText());
        holder.textBottom.setTextColor(itemStyle.getColorBottomText());

        if (Build.VERSION.SDK_INT >= 16) {
            holder.layoutBackground.setBackground(itemStyle.getBackground());
        } else {
            holder.layoutBackground.setBackgroundDrawable(itemStyle.getBackground());
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount + (horizontalCalendar.getShiftCells() * 2);
    }

    public Date getItem(int position) throws IndexOutOfBoundsException {
        if (position >= getItemCount()) {
            throw new IndexOutOfBoundsException();
        }

        int daysDiff = position - horizontalCalendar.getShiftCells();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateStart);
        calendar.add(Calendar.DATE, daysDiff);

        return calendar.getTime();
    }

    public boolean isDisabled(int position) {
        Date date = getItem(position);
        return disablePredicate.test(date);
    }

    /**
     * calculate each item width depends on {@link HorizontalCalendar#numberOfDatesOnScreen}
     */
    private void calculateCellWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        int screenWidth = size.x;

        cellWidth = screenWidth / horizontalCalendar.getNumberOfDatesOnScreen();
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final DayViewHolder holder;

        MyOnClickListener(DayViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            if (holder.getAdapterPosition() == -1)
                return;

            horizontalCalendar.calendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_SLOW);
            horizontalCalendar.centerCalendarToPosition(holder.getAdapterPosition());
        }
    }

    private class MyOnLongClickListener implements View.OnLongClickListener {
        private final DayViewHolder holder;

        MyOnLongClickListener(DayViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onLongClick(View v) {
            Date date = getItem(holder.getAdapterPosition());
            HorizontalCalendarListener calendarListener = horizontalCalendar.getCalendarListener();
            if (calendarListener != null) {
                return calendarListener.onDateLongClicked(date, holder.getAdapterPosition());
            }
            return false;
        }
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView textTop;
        TextView textMiddle;
        TextView textBottom;
        View selectionView;
        View layoutBackground;
        View rootView;

        DayViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            textTop = rootView.findViewById(R.id.hc_text_top);
            textMiddle = rootView.findViewById(R.id.hc_text_middle);
            textBottom = rootView.findViewById(R.id.hc_text_bottom);
            layoutBackground = rootView.findViewById(R.id.hc_layoutBackground);
            selectionView = rootView.findViewById(R.id.hc_selector);
        }
    }
}
