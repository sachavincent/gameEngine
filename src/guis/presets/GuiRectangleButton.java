package guis.presets;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;
import java.util.List;
import javax.naming.SizeLimitExceededException;
import util.vector.Vector2f;

public class GuiRectangleButton<E> extends GuiPreset {

    private GuiRectangle buttonLayout;
    private GuiText      text;

    private boolean clicked;

    public GuiRectangleButton(GuiInterface parent, Text text, String textureBackground,
            GuiConstraintsManager constraintsManager) { //TODO: give name to access
        super(parent, constraintsManager);

        addComponents((E) textureBackground, text);
    }

    public GuiRectangleButton(GuiInterface parent, Text text, Color colorBackground,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addComponents((E) colorBackground, text);
    }

    public GuiRectangleButton(GuiInterface parent, String textureBackground, GuiConstraintsManager constraintsManager) {
        this(parent, null, textureBackground, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        this(parent, null, colorBackground, constraintsManager);
    }

    private void addComponents(E background, Text text) { //TODO: Color border?
        if (background instanceof Color)
            buttonLayout = new GuiRectangle(this, (Color) background, new RelativeConstraint(1f, this),
                    new RelativeConstraint(1, this));
        else
            buttonLayout = new GuiRectangle(this, (String) background, new RelativeConstraint(1f, this),
                    new RelativeConstraint(1, this));


        setListeners(buttonLayout);

        if (text != null) {
            try {
                setupText(text);
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();
            }

            this.getBasics().add(this.text);
        }

        this.getBasics().add(buttonLayout);
    }

    private void setupText(Text text) throws SizeLimitExceededException {
        text.setLineMaxSize(buttonLayout.getWidth());
        text.setCentered(false);

        List<Line> lines = text.getFont().getLoader().getLines(text);

        if (lines.size() > 1)
            throw new SizeLimitExceededException("Content must fit in one line.");

        Line line = lines.get(0);

        if (line == null)
            throw new IllegalArgumentException("Invalid text.");

        double textHeight = TextMeshCreator.LINE_HEIGHT * text.getFontSize() * 2;

        if (textHeight > buttonLayout.getHeight() * 2)
            throw new SizeLimitExceededException("Font size too large.");

        text.setPosition(new Vector2f(buttonLayout.getX() - line.getLineLength(),
                buttonLayout.getY() - textHeight / 2));

        this.text = new GuiText(this, text);
    }

    private void setListeners(GuiRectangle buttonLayout) {
        buttonLayout.setOnClick(() -> {

        });
    }

}
