package guis.basics;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import java.awt.Color;
import javax.naming.SizeLimitExceededException;
import renderEngine.fontRendering.TextMaster;
import util.math.Vector2f;

public class GuiText extends GuiBasics {

    protected Text text;

    public GuiText(GuiInterface parent, Text text) {
        this(parent, text, null);
    }

    public GuiText(GuiInterface parent, Text text, GuiConstraintsManager guiConstraintsManager) {
        super(parent, new Background<>(text == null ? null : Color.BLACK), guiConstraintsManager);

        setText(text);
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        if (text == null)
            return;

        text.setMaxLineLength(getWidth());

        double textHeight = text.getTotalTextHeight();

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
        if (this.text == null || this.text.getLines().isEmpty())
            return null;

        return this.text.getLines().get(0);
    }

    @Override
    public boolean update() {
        if (this.text == null)
            return false;

        float y = (float) (-getY() - this.text.getTotalTextHeight() / 2);
        if (!this.text.isCenteredVertically())
            y = -getY() - getHeight();

        if (this.text.isCenteredHorizontally())
            this.text.setPosition(new Vector2f(getX() - getWidth(), y));
        else
            this.text.setPosition(
                    new Vector2f(getX() - getWidth()/*+ getLine().getLineLength()*/, y));

        this.text.setTopLeftCorner(new Vector2f(this.getX() - this.getWidth(), this.getY() - this.getHeight()));
        this.text.setBottomRightCorner(new Vector2f(this.getX() + this.getWidth(), this.getY() + this.getHeight()));

        super.update();

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

        if (this.text.isStringChanged()) {
            setText(this.text);
        }

        if (isDisplayed())
            TextMaster.getInstance().loadText(this.text);
    }
}
