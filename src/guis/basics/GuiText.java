package guis.basics;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiBackground;
import java.util.List;
import javax.naming.SizeLimitExceededException;
import util.math.Vector2f;

public class GuiText extends GuiBasics {

    private Text text;

    public GuiText(GuiInterface gui, Text text) {
        super(gui, new GuiBackground<>(text == null ? null : text.getAWTColor()), null, null);

        setText(text);
    }

    public GuiText(GuiInterface gui, Text text, GuiConstraintsManager guiConstraintsManager) {
        super(gui, new GuiBackground<>(text == null ? null : text.getAWTColor()), guiConstraintsManager);

        setText(text);
    }

    public Text getText() {
        return this.text;
    }

    private void setText(Text text) {
        if (text == null)
            return;

        text.setLineMaxSize(getWidth());
        text.setCentered(false);

        List<Line> lines = text.getFont().getLoader().getLines(text);

        if (lines.size() > 1) {
            try {
                throw new SizeLimitExceededException("Content must fit in one line: " + text.getTextString());
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();
                return;
            }
        }

        Line line = lines.get(0);

        if (line == null) {
            try {
                throw new IllegalArgumentException("Invalid text: " + text.getTextString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                return;
            }
        }

        double textHeight = TextMeshCreator.LINE_HEIGHT * text.getFontSize() * 2;

        if (textHeight > getHeight() * 2) {
            try {
                throw new SizeLimitExceededException("Font size too large.");
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();

                return;
            }
        }

        text.setPosition(new Vector2f(getX() - line.getLineLength(), -getY() - textHeight / 2));

        this.text = text;
    }

    @Override
    public String toString() {
        return "GuiText{" +
                "text=" + text +
                ", " + super.toString() + "}";
    }
}
