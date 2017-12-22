package devs.mulham.horizontalcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;

import devs.mulham.horizontalcalendar.adapter.HorizontalCalendarBaseAdapter;
import devs.mulham.horizontalcalendar.model.CalendarItemStyle;
import devs.mulham.horizontalcalendar.model.HorizontalCalendarConfig;

/**
 * See {devs.mulham.horizontalcalendar.R.styleable#HorizontalCalendarView HorizontalCalendarView Attributes}
 *
 * @author Mulham-Raee
 * @since v1.0.0
 */
public class HorizontalCalendarView extends RecyclerView {

    private CalendarItemStyle defaultStyle;
    private CalendarItemStyle selectedItemStyle;
    private HorizontalCalendarConfig config;
    private int shiftCells;

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
            int textColorNormal = a.getColor(R.styleable.HorizontalCalendarView_textColorNormal, Color.LTGRAY);
            int colorTopText = a.getColor(R.styleable.HorizontalCalendarView_colorTopText, textColorNormal);
            int colorMiddleText = a.getColor(R.styleable.HorizontalCalendarView_colorMiddleText, textColorNormal);
            int colorBottomText = a.getColor(R.styleable.HorizontalCalendarView_colorBottomText, textColorNormal);

            int textColorSelected = a.getColor(R.styleable.HorizontalCalendarView_textColorSelected, Color.BLACK);
            int colorTopTextSelected = a.getColor(R.styleable.HorizontalCalendarView_colorTopTextSelected, textColorSelected);
            int colorMiddleTextSelected = a.getColor(R.styleable.HorizontalCalendarView_colorMiddleTextSelected, textColorSelected);
            int colorBottomTextSelected = a.getColor(R.styleable.HorizontalCalendarView_colorBottomTextSelected, textColorSelected);
            Drawable selectedDateBackground = a.getDrawable(R.styleable.HorizontalCalendarView_selectedDateBackground);

            int selectorColor = a.getColor(R.styleable.HorizontalCalendarView_selectorColor, fetchAccentColor());
            float sizeTopText = getRawSizeValue(a, R.styleable.HorizontalCalendarView_sizeTopText,
                    HorizontalCalendarConfig.DEFAULT_SIZE_TEXT_TOP);
            float sizeMiddleText = getRawSizeValue(a, R.styleable.HorizontalCalendarView_sizeMiddleText,
                    HorizontalCalendarConfig.DEFAULT_SIZE_TEXT_MIDDLE);
            float sizeBottomText = getRawSizeValue(a, R.styleable.HorizontalCalendarView_sizeBottomText,
                    HorizontalCalendarConfig.DEFAULT_SIZE_TEXT_BOTTOM);


            defaultStyle = new CalendarItemStyle(colorTopText, colorMiddleText, colorBottomText, null);
            selectedItemStyle = new CalendarItemStyle(colorTopTextSelected, colorMiddleTextSelected, colorBottomTextSelected, selectedDateBackground);
            config = new HorizontalCalendarConfig(sizeTopText, sizeMiddleText, sizeBottomText, selectorColor);

        } finally {
            a.recycle();
        }

    }

    /**
     * get the raw value from a complex value ( Ex: complex = 14sp, returns 14)
     */
    private float getRawSizeValue(TypedArray a, int index, float defValue) {
        TypedValue outValue = new TypedValue();
        boolean result = a.getValue(index, outValue);
        if (!result) {
            return defValue;
        }

        return TypedValue.complexToFloat(outValue.data);
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
    public HorizontalCalendarBaseAdapter getAdapter() {
        return (HorizontalCalendarBaseAdapter) super.getAdapter();
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

    public void applyConfigFromLayout(HorizontalCalendar horizontalCalendar) {
        horizontalCalendar.getConfig().setupDefaultValues(config);
        horizontalCalendar.getDefaultStyle().setupDefaultValues(defaultStyle);
        horizontalCalendar.getSelectedItemStyle().setupDefaultValues(selectedItemStyle);

        // clean, not needed anymore
        config = null;
        defaultStyle = null;
        selectedItemStyle = null;

        this.shiftCells = horizontalCalendar.getNumberOfDatesOnScreen() / 2;
    }

    /**
     * @return position of selected date on center of screen
     */
    public int getPositionOfCenterItem() {
        final HorizontalLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return -1;
        } else {
            final int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            if (firstVisiblePosition == -1) {
                return -1;
            } else {
                return firstVisiblePosition + shiftCells;
            }
        }
    }
}
