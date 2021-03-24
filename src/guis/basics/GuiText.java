package guis.basics;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import java.util.List;
import javax.naming.SizeLimitExceededException;
import renderEngine.fontRendering.TextMaster;
import util.math.Vector2f;

public class GuiText extends GuiBasics {

    private Text text;

    private Line line;

    public GuiText(GuiInterface parent, Text text) {
        this(parent, text, null);
    }

    public GuiText(GuiInterface parent, Text text, GuiConstraintsManager guiConstraintsManager) {
        super(parent, new Background<>(text == null ? null : text.getColor()), guiConstraintsManager);

        setText(text);
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
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

                text.setTextString("");
                this.text = text;
                return;
            }
        }

        line = lines.get(0);

        if (line == null) {
            try {
                throw new IllegalArgumentException("Invalid text: " + text.getTextString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                return;
            }
        }

        double textHeight = text.getTextHeight();

        if (textHeight > getHeight() * 2) {
            try {
                throw new SizeLimitExceededException("Font size too large.");
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();

                return;
            }
        }

        this.text = text;

        update();
    }

    @Override
    public void setX(float x) {
        super.setX(x);

        update();
    }

    @Override
    public void setY(float y) {
        super.setY(y);

        update();
    }

    public Line getLine() {
        return this.line;
    }

    @Override
    public boolean update() {
        if (this.text == null || this.line == null)
            return false;

        this.text.setPosition(new Vector2f(getX() - line.getLineLength(), -getY() - text.getTextHeight() / 2));

        return true;
    }

    @Override
    public String toString() {
        return "GuiText{" +
                "text=" + text +
                ", " + super.toString() + "}";
    }

    @Override
    public void render() {
        if (this.text == null)
            return;

        if (isDisplayed())
            TextMaster.getInstance().loadText(getText());
    }
}
