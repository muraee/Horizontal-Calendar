package devs.mulham.horizontalcalendar;

import java.util.Date;

/**
 * @author Mulham-Raee
 * @since v1.2.5
 */
public interface HorizontalCalendarPredicate {

    boolean test(Date date);

    CalendarItemStyle style();

    class Or implements HorizontalCalendarPredicate {

        private final HorizontalCalendarPredicate firstPredicate;
        private final HorizontalCalendarPredicate secondPredicate;

        public Or(HorizontalCalendarPredicate firstPredicate, HorizontalCalendarPredicate secondPredicate) {
            this.firstPredicate = firstPredicate;
            this.secondPredicate = secondPredicate;
        }

        @Override
        public boolean test(Date date) {
            return firstPredicate.test(date) || secondPredicate.test(date);
        }

        @Override
        public CalendarItemStyle style() {
            return firstPredicate.style();
        }
    }
}
