package devs.mulham.horizontalcalendar;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * @author Mulham-Raee
 * @since  v1.0.0
 *
 * See {@link HorizontalCalendarView HorizontalCalendarView}
 */
public class HorizontalLayoutManager extends LinearLayoutManager {

    public static final float SPEED_NORMAL = 90f;
    public static final float SPEED_SLOW = 125f;

    float smoothScrollSpeed = SPEED_NORMAL;

    HorizontalLayoutManager(Context context, boolean reverseLayout) {
        super(context, HORIZONTAL, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return smoothScrollSpeed / displayMetrics.densityDpi;
            }

        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    float getSmoothScrollSpeed() {
        return smoothScrollSpeed;
    }

    void setSmoothScrollSpeed(float smoothScrollSpeed) {
        this.smoothScrollSpeed = smoothScrollSpeed;
    }

}
