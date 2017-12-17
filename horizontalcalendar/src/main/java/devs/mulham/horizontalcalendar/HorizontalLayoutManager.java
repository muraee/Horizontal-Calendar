package devs.mulham.horizontalcalendar;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @author Mulham-Raee
 * @since  v1.0.0
 *
 * See {@link HorizontalCalendarView HorizontalCalendarView}
 */
class HorizontalLayoutManager extends LinearLayoutManager {

    static final float SPEED_NORMAL = 40f;
    static final float SPEED_SLOW = 125f;

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
