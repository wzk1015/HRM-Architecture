package PlainTextEditor.GUI;

import java.awt.*;

public class FontSetting {

    private final Font fontFace;
    private final int fontSize;
    private final int fontStyle;
    private final Color fontColor;

    public FontSetting(Font face, int size, int style, Color color) {
        this.fontFace = face;
        this.fontSize = size;
        this.fontStyle = style;
        this.fontColor = color;
    }

    public Font getFontFace() {
        return fontFace;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public Color getFontColor() {
        return fontColor;
    }
}
