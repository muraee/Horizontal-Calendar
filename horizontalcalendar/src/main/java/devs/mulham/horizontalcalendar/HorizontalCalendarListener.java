package devs.mulham.horizontalcalendar;

import java.util.Date;

public abstract class HorizontalCalendarListener {

    public abstract void onDateSelected(Date date, int position);

    public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy){}

    public boolean onDateLongClicked(Date date, int position){
        return false;
    }

}