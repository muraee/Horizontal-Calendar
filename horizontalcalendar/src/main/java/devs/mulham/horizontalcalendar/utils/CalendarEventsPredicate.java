package devs.mulham.horizontalcalendar.utils;

import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.model.CalendarEvent;

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
public interface CalendarEventsPredicate {

    /**
     * @param date the date where the events will be attached to.
     * @return a list of {@link CalendarEvent} related to this date.
     */
    List<CalendarEvent> events(Calendar date);
}
