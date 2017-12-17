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

    private final Context context;
    final Date dateStart;
    final Date dateEnd;
    private int cellWidth;
    private final int itemsCount;

    final HorizontalCalendar horizontalCalendar;

    HorizontalCalendarAdapter(HorizontalCalendarView horizontalCalendarView, Date dateStart, Date dateEnd) {
        this.context = horizontalCalendarView.getContext();
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.horizontalCalendar = horizontalCalendarView.getHorizontalCalendar();


        long diff = dateEnd.getTime() - dateStart.getTime(); //result in millis
        itemsCount = (int) TimeUnit.MILLISECONDS.toDays(diff) + 1;

        calculateCellWidth();
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar, viewGroup, false);

        convertView.setMinimumWidth(cellWidth);

        final DayViewHolder holder = new DayViewHolder(convertView);
        final Integer selectorColor = horizontalCalendar.getSelectorColor();
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
        int selectedItemPosition = horizontalCalendar.getSelectedDatePosition();

        // Selected Day
        if (position == selectedItemPosition) {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorSelected());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(horizontalCalendar.getSelectedDateBackground());
            } else {
                holder.layoutBackground.setBackgroundDrawable(horizontalCalendar.getSelectedDateBackground());
            }
            holder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorNormal());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(null);
            } else {
                holder.layoutBackground.setBackgroundDrawable(null);
            }
            holder.selectionView.setVisibility(View.INVISIBLE);
        }

        holder.txtDayNumber.setText(DateFormat.format(horizontalCalendar.getFormatDayNumber(), day).toString());
        holder.txtDayNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                horizontalCalendar.getTextSizeDayNumber());

        if (horizontalCalendar.isShowDayName()) {
            holder.txtDayName.setText(DateFormat.format(horizontalCalendar.getFormatDayName(), day).toString());
            holder.txtDayName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    horizontalCalendar.getTextSizeDayName());
        } else {
            holder.txtDayName.setVisibility(View.GONE);
        }

        if (horizontalCalendar.isShowMonthName()) {
            holder.txtMonthName.setText(DateFormat.format(horizontalCalendar.getFormatMonth(), day).toString());
            holder.txtMonthName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    horizontalCalendar.getTextSizeMonthName());
        } else {
            holder.txtMonthName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position, List<Object> payloads) {
        if ((payloads == null) || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }

        int selectedItemPosition = horizontalCalendar.getSelectedDatePosition();

        // Selected Day
        if (position == selectedItemPosition) {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorSelected());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(horizontalCalendar.getSelectedDateBackground());
            } else {
                holder.layoutBackground.setBackgroundDrawable(horizontalCalendar.getSelectedDateBackground());
            }
            holder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorNormal());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(null);
            } else {
                holder.layoutBackground.setBackgroundDrawable(null);
            }
            holder.selectionView.setVisibility(View.INVISIBLE);
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

    /**
     * calculate each item width depends on {@link HorizontalCalendar#numberOfDatesOnScreen}
     */
    private void calculateCellWidth() {
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

            Date date = getItem(holder.getAdapterPosition());

            if (!date.before(dateStart) && !date.after(dateEnd)) {
                horizontalCalendar.calendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_SLOW);
                horizontalCalendar.centerCalendarToPosition(holder.getAdapterPosition());
            }
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
            if ((calendarListener != null) && !date.before(dateStart) && !date.after(dateEnd)) {
                return calendarListener.onDateLongClicked(date, holder.getAdapterPosition());
            }
            return false;
        }
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView txtDayNumber;
        TextView txtDayName;
        TextView txtMonthName;
        View selectionView;
        View layoutBackground;
        View rootView;

        public DayViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            txtDayNumber = rootView.findViewById(R.id.dayNumber);
            txtDayName = rootView.findViewById(R.id.dayName);
            txtMonthName = rootView.findViewById(R.id.monthName);
            layoutBackground = rootView.findViewById(R.id.layoutBackground);
            selectionView = rootView.findViewById(R.id.selection_view);
        }
    }
}
