package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;

public class GuiRectangleButton extends GuiButton {

    public GuiRectangleButton(GuiInterface parent, String textureBackground, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, textureBackground, text, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Color colorBackground, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, colorBackground, text, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, String textureBackground, GuiConstraintsManager constraintsManager) {
        this(parent, textureBackground, null, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        this(parent, colorBackground, null, constraintsManager);
    }

    @Override
    protected void addBackgroundComponent(String background) {
        buttonLayout = new GuiRectangle(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        filterLayout = new GuiRectangle(this, Color.WHITE, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));
    }

    @Override
    protected void addBackgroundComponent(Color background) {
        buttonLayout = new GuiRectangle(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        filterLayout = new GuiRectangle(this, Color.WHITE, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));
    }

    @Override
    public void setBorder(Color color) {
        borderLayout = new GuiRectangle(this, color, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this), false);
    }

    @Override
    public String toString() {
        return "GuiRectangleButton{" +
                "buttonLayout=" + buttonLayout +
                '}';
    }
}
