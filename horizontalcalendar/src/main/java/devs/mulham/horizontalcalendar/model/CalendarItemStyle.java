package devs.mulham.horizontalcalendar.model;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

/**
 * @author Mulham-Raee
 * @since v1.2.5
 */
public class CalendarItemStyle {

    private int colorTopText;
    private int colorMiddleText;
    private int colorBottomText;
    private Drawable background;
    private Typeface textFontFamily;

    public CalendarItemStyle() {
    }

    public CalendarItemStyle(int textColor, Drawable background) {
        this(textColor, textColor, textColor, background, null);
    }

    public CalendarItemStyle(int colorTopText, int colorMiddleText, int colorBottomText, Drawable background, Typeface textFontFamily) {
        this.colorTopText = colorTopText;
        this.colorMiddleText = colorMiddleText;
        this.colorBottomText = colorBottomText;
        this.background = background;
        this.textFontFamily = textFontFamily;
    }

    public int getColorTopText() {
        return colorTopText;
    }

    public CalendarItemStyle setColorTopText(int colorTopText) {
        this.colorTopText = colorTopText;
        return this;
    }

    public int getColorMiddleText() {
        return colorMiddleText;
    }

    public CalendarItemStyle setColorMiddleText(int colorMiddleText) {
        this.colorMiddleText = colorMiddleText;
        return this;
    }

    public int getColorBottomText() {
        return colorBottomText;
    }

    public CalendarItemStyle setColorBottomText(int colorBottomText) {
        this.colorBottomText = colorBottomText;
        return this;
    }

    public Drawable getBackground() {
        return background;
    }

    public CalendarItemStyle setBackground(Drawable background) {
        this.background = background;
        return this;
    }

    public Typeface getTextFontFamily() {
        return textFontFamily;
    }

    public CalendarItemStyle setTextFontFamily(Typeface typeface) {
        this.textFontFamily = typeface;
        return this;
    }

    public void setupDefaultValues(CalendarItemStyle defaultValues) {
        if (defaultValues == null) {
            return;
        }
        if (colorTopText == 0) {
            colorTopText = defaultValues.colorTopText;
        }
        if (colorMiddleText == 0) {
            colorMiddleText = defaultValues.colorMiddleText;
        }
        if (colorBottomText == 0) {
            colorBottomText = defaultValues.colorBottomText;
        }
        if (background == null) {
            background = defaultValues.background;
        }
        if (textFontFamily == null) {
            textFontFamily = defaultValues.textFontFamily;
        }
    }
}
