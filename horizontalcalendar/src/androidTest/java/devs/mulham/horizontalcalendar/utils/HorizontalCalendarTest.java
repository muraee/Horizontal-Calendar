package devs.mulham.horizontalcalendar.utils;

import android.test.AndroidTestRunner;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;

/**
 * Created by Shasin on 11/01/2018.
 */


//@RunWith(MockitoJUnitRunner.class)
public class HorizontalCalendarTest {
//
    @Mock HorizontalCalendarListener horizontalCalendarListener;
    @Mock HorizontalCalendarView horizontalCalendarView;
    @InjectMocks HorizontalCalendar horizontalCalendar;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIfGoTodayInvokeScrollToTodayMethod(){
        int position = 40;
        horizontalCalendar.goToday(true);
        verify(horizontalCalendar,times(1)).scrollToTodayPositionWithNoAnimation(position);
    }

    @Test
    public void testIfGoNextDayInvokeScrollToPosition(){
        int position = 40;
        Calendar date = Calendar.getInstance();
        horizontalCalendar.goNextDay(date);
        verify(horizontalCalendar,times(1)).scrollToPositionWithNoAnimation(position);
    }

    @Test
    public void testIfGoPreviousDayInvokeScrollToPosition(){
        int position = 40;
        Calendar date = Calendar.getInstance();
        horizontalCalendar.goPreviousDay(date);
        verify(horizontalCalendar,times(1)).scrollToPositionWithNoAnimation(position);
    }

}
