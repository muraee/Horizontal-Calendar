package devs.mulham.horizontalcalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * custom adapter for {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @version 1.0
 * See {devs.mulham.horizontalcalendar.R.layout#item_calendar} Calendar CostumItem Layout
 */
class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.DayViewHolder> {

    private final Context context;
    private ArrayList<Date> datesList;
    private int widthCell;
    private HorizontalCalendar horizontalCalendar;
    private int numberOfDates;
    private HorizontalCalendarView horizontalCalendarView;

    HorizontalCalendarAdapter(HorizontalCalendarView horizontalCalendarView, ArrayList<Date> datesList) {
        this.horizontalCalendarView = horizontalCalendarView;
        this.context = horizontalCalendarView.getContext();
        this.datesList = datesList;
        this.horizontalCalendar = horizontalCalendarView.getHorizontalCalendar();
        this.numberOfDates = horizontalCalendar.getNumberOfDatesOnScreen();
        calculateCellWidth();
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar, viewGroup, false);

        //convertView.setLayoutParams(new FrameLayout.LayoutParams(widthCell, convertView.getLayoutParams().height + 15));
        convertView.setMinimumWidth(widthCell);

        final DayViewHolder holder = new DayViewHolder(convertView);
        holder.selectionView.setBackgroundColor(horizontalCalendar.getSelectorColor());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int stepX = getStepX(v);
                Date date = datesList.get(holder.getAdapterPosition());

                if (!date.before(horizontalCalendar.getDateStartCalendar())
                        && !date.after(horizontalCalendar.getDateEndCalendar())) {
                    //horizontalCalendarView.smoothScrollBy(stepX, 0);
                    horizontalCalendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_SLOW);
                    horizontalCalendar.centerCalendarToPosition(holder.getAdapterPosition());
                }
            }
        });

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Date date = datesList.get(holder.getAdapterPosition());
                HorizontalCalendarListener calendarListener = horizontalCalendar.getCalendarListener();
                if (calendarListener != null && !date.before(horizontalCalendar.getDateStartCalendar())
                        && !date.after(horizontalCalendar.getDateEndCalendar())) {
                    return calendarListener.onDateLongClicked(date, holder.getAdapterPosition());
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        Date day = datesList.get(position);
        int selectedItemPosition = horizontalCalendarView.getPositionOfCenterItem();

        // Selected Day
        if (position == selectedItemPosition) {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.layoutBackground.setBackgroundColor(horizontalCalendar.getSelectedDateBackground());
            holder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.layoutBackground.setBackgroundColor(Color.TRANSPARENT);
            holder.selectionView.setVisibility(View.INVISIBLE);

        }

        holder.txtDayNumber.setText(DateFormat.format(horizontalCalendar.getFormatDayNumber(), day).toString());
        holder.txtDayName.setText(DateFormat.format(horizontalCalendar.getFormatDay(), day).toString());
        holder.txtMonthName.setText(DateFormat.format("MMM", day).toString());

    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public Date getItem(int position) {
        return datesList.get(position);
    }

    /**
     * calculate each item width depends on {@link HorizontalCalendar#numberOfDatesOnScreen}
     */
    private void calculateCellWidth() {

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        int widthScreen;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);

            widthScreen = size.x;
        } else {
            widthScreen = display.getWidth();
        }

        widthCell = widthScreen / numberOfDates;
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
            txtDayNumber = (TextView) rootView.findViewById(R.id.dayNumber);
            txtDayName = (TextView) rootView.findViewById(R.id.dayName);
            txtMonthName = (TextView) rootView.findViewById(R.id.monthName);
            layoutBackground = rootView.findViewById(R.id.layoutBackground);
            selectionView = rootView.findViewById(R.id.selection_view);

        }
    }
}
