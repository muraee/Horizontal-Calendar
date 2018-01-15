package devs.mulham.horizontalcalendar.model;

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
public class CalendarEvent {

    private int color;
    private String description;

    public CalendarEvent(int color){
        this.color = color;
    }

    public CalendarEvent(int color, String description) {
        this.color = color;
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
