package devs.mulham.horizontalcalendar;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class HorizontalSnapHelper extends LinearSnapHelper {

    private HorizontalCalendar horizontalCalendar;

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        View snapView = super.findSnapView(layoutManager);

        if (horizontalCalendar.calendarView.getScrollState() != RecyclerView.SCROLL_STATE_DRAGGING){
            int selectedItemPosition;
            if (snapView == null){
                // no snapping required
                selectedItemPosition = horizontalCalendar.getSelectedDatePosition();
            } else {
                int[] snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView);
                if ((snapDistance[0] != 0) || (snapDistance[1] != 0)){
                    return snapView;
                }
                selectedItemPosition = layoutManager.getPosition(snapView);
            }

            horizontalCalendar.calendarListener.onDateSelected(horizontalCalendar.getDateAt(selectedItemPosition), selectedItemPosition);
        }

        return snapView;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        // Do nothing
    }

    public void attachToHorizontalCalendaar(@Nullable HorizontalCalendar horizontalCalendar) throws IllegalStateException {
        this.horizontalCalendar = horizontalCalendar;
        attachToRecyclerView();
    }

    private void attachToRecyclerView(){
        super.attachToRecyclerView(horizontalCalendar.calendarView);
    }
}
