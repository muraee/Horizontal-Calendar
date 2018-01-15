package devs.mulham.horizontalcalendar.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import devs.mulham.horizontalcalendar.R;
import devs.mulham.horizontalcalendar.model.CalendarEvent;

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<CalendarEvent> eventList;

    public EventsAdapter(List<CalendarEvent> eventList) {
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ImageView imageView = new ImageView(context);

        Drawable circle = ContextCompat.getDrawable(context, R.drawable.ic_circle_white_8dp);
        Drawable drawableWrapper = DrawableCompat.wrap(circle);

        imageView.setImageDrawable(drawableWrapper);

        return new EventViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        CalendarEvent event = getItem(position);

        ImageView imageView = (ImageView) holder.itemView;

        imageView.setContentDescription(event.getDescription());
        DrawableCompat.setTint(imageView.getDrawable(), event.getColor());
    }

    public CalendarEvent getItem(int position) throws IndexOutOfBoundsException {
        return eventList.get(position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void update(List<CalendarEvent> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        EventViewHolder(View itemView) {
            super(itemView);
        }
    }
}

