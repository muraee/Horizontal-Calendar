package devs.mulham.horizontalcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * See {devs.mulham.horizontalcalendar.R.styleable#HorizontalCalendarView HorizontalCalendarView Attributes}
 *
 * @author Mulham-Raee
 * @version 1.0
 */
public class HorizontalCalendarView extends RecyclerView {

    int textColorNormal, textColorSelected;
    int selectedDateBackground;
    int selectorColor;
    private HorizontalCalendar horizontalCalendar;
    private final float FLING_SCALE_DOWN_FACTOR = 0.5f;

    public HorizontalCalendarView(Context context) {
        super(context);
    }

    public HorizontalCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HorizontalCalendarView,
                0, 0);

        try {
            textColorNormal = a.getColor(R.styleable.HorizontalCalendarView_textColorNormal, Color.LTGRAY);
            textColorSelected = a.getColor(R.styleable.HorizontalCalendarView_textColorSelected, Color.BLACK);
            selectedDateBackground = a.getColor(R.styleable.HorizontalCalendarView_selectedDateBackground, Color.TRANSPARENT);
            selectorColor = a.getColor(R.styleable.HorizontalCalendarView_selectorColor, fetchAccentColor());
        } finally {
            a.recycle();
        }

    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= FLING_SCALE_DOWN_FACTOR; // (between 0 for no fling, and 1 for normal fling, or more for faster fling).

        return super.fling(velocityX, velocityY);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (isInEditMode()) {
            setMeasuredDimension(widthSpec, 150);
        } else {
            super.onMeasure(widthSpec, heightSpec);
        }

    }

    public float getSmoothScrollSpeed() {
        return getLayoutManager().getSmoothScrollSpeed();
    }

    public void setSmoothScrollSpeed(float smoothScrollSpeed) {
        getLayoutManager().setSmoothScrollSpeed(smoothScrollSpeed);
    }

    @Override
    public HorizontalCalendarAdapter getAdapter() {
        return (HorizontalCalendarAdapter) super.getAdapter();
    }

    @Override
    public HorizontalLayoutManager getLayoutManager() {
        return (HorizontalLayoutManager) super.getLayoutManager();
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public HorizontalCalendar getHorizontalCalendar() {
        return horizontalCalendar;
    }

    public void setHorizontalCalendar(HorizontalCalendar horizontalCalendar) {

        if (horizontalCalendar.getTextColorNormal() == 0) {
            horizontalCalendar.setTextColorNormal(textColorNormal);
        }
        if (horizontalCalendar.getTextColorSelected() == 0) {
            horizontalCalendar.setTextColorSelected(textColorSelected);
        }
        if (horizontalCalendar.getSelectorColor() == 0) {
            horizontalCalendar.setSelectorColor(selectorColor);
        }
        if (horizontalCalendar.getSelectedDateBackground() == 0) {
            horizontalCalendar.setSelectedDateBackground(selectedDateBackground);
        }

        this.horizontalCalendar = horizontalCalendar;
    }

    /**
     * @return position of selected date on center of screen
     */
    public int getPositionOfCenterItem() {
        int numberOfDatesOnScreen = horizontalCalendar.getNumberOfDatesOnScreen();
        int firstVisibilePosition = getLayoutManager().findFirstCompletelyVisibleItemPosition();
        if (firstVisibilePosition == -1) {
            return -1;
        }
        return firstVisibilePosition + (numberOfDatesOnScreen / 2);
    }

}
