package guis.presets.buttons;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiPreset;
import java.util.List;
import javax.naming.SizeLimitExceededException;
import util.vector.Vector2f;

abstract class GuiButton extends GuiPreset {

    GuiBasics buttonLayout;
    GuiText   text;

    private boolean clicked;

    GuiButton(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);
    }

    void setListeners() {
        buttonLayout.setOnClick(() -> {
            setClicked(true);
            System.out.println("Click");
        });
    }

    void addBasics(Text text) {
        if (text != null) {
            try {
                setupText(text);
            } catch (SizeLimitExceededException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            addBasic(this.text);
        }

        addBasic(buttonLayout);
    }

    void setupText(Text text) throws SizeLimitExceededException, IllegalArgumentException {
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

    public boolean isClicked() {
        return this.clicked;
    }

    void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

}
