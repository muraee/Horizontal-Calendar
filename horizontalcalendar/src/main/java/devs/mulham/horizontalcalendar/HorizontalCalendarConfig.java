package devs.mulham.horizontalcalendar;

/**
 * @author Mulham-Raee
 * @since v1.2.5
 */
public class HorizontalCalendarConfig {

    static final float DEFAULT_SIZE_TEXT_TOP = 14f;
    static final float DEFAULT_SIZE_TEXT_MIDDLE = 24f;
    static final float DEFAULT_SIZE_TEXT_BOTTOM = 14f;

    static final String DEFAULT_FORMAT_TEXT_TOP = "MMM";
    static final String DEFAULT_FORMAT_TEXT_MIDDLE = "dd";
    static final String DEFAULT_FORMAT_TEXT_BOTTOM = "EEE";

    /* Format & Font Sizes*/
    private String formatTopText;
    private String formatMiddleText;
    private String formatBottomText;
    private float sizeTopText;
    private float sizeMiddleText;
    private float sizeBottomText;

    private Integer selectorColor;
    private boolean showTopText;
    private boolean showBottomText;

    public HorizontalCalendarConfig() {
    }

    public HorizontalCalendarConfig(float sizeTopText, float sizeMiddleText, float sizeBottomText, Integer selectorColor) {
        this.sizeTopText = sizeTopText;
        this.sizeMiddleText = sizeMiddleText;
        this.sizeBottomText = sizeBottomText;
        this.selectorColor = selectorColor;
    }

    public String getFormatTopText() {
        return formatTopText;
    }

    public void setFormatTopText(String formatTopText) {
        this.formatTopText = formatTopText;
    }

    public String getFormatMiddleText() {
        return formatMiddleText;
    }

    public void setFormatMiddleText(String formatMiddleText) {
        this.formatMiddleText = formatMiddleText;
    }

    public String getFormatBottomText() {
        return formatBottomText;
    }

    public void setFormatBottomText(String formatBottomText) {
        this.formatBottomText = formatBottomText;
    }

    public float getSizeTopText() {
        return sizeTopText;
    }

    public void setSizeTopText(float sizeTopText) {
        this.sizeTopText = sizeTopText;
    }

    public float getSizeMiddleText() {
        return sizeMiddleText;
    }

    public void setSizeMiddleText(float sizeMiddleText) {
        this.sizeMiddleText = sizeMiddleText;
    }

    public float getSizeBottomText() {
        return sizeBottomText;
    }

    public void setSizeBottomText(float sizeBottomText) {
        this.sizeBottomText = sizeBottomText;
    }

    public Integer getSelectorColor() {
        return selectorColor;
    }

    public void setSelectorColor(Integer selectorColor) {
        this.selectorColor = selectorColor;
    }

    public boolean isShowTopText() {
        return showTopText;
    }

    public void setShowTopText(boolean showTopText) {
        this.showTopText = showTopText;
    }

    public boolean isShowBottomText() {
        return showBottomText;
    }

    public void setShowBottomText(boolean showBottomText) {
        this.showBottomText = showBottomText;
    }

    public void setupDefaultValues(HorizontalCalendarConfig defaultConfig) {
        if (defaultConfig == null) {
            return;
        }
        if (selectorColor == null) {
            selectorColor = defaultConfig.selectorColor;
        }
        if (sizeTopText == 0) {
            sizeTopText = defaultConfig.sizeTopText;
        }
        if (sizeMiddleText == 0) {
            sizeMiddleText = defaultConfig.sizeMiddleText;
        }
        if (sizeBottomText == 0) {
            sizeBottomText = defaultConfig.sizeBottomText;
        }
    }
}
