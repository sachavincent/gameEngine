package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import java.awt.Color;

public class GuiRectangleButton extends GuiAbstractButton {

    public GuiRectangleButton(GuiInterface parent, GuiBackground<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, GuiBackground<?> background,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, null, constraintsManager);
    }

    @Override
    protected void addBackgroundComponent(GuiBackground<?> background) {
        buttonLayout = new GuiRectangle(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        filterLayout = new GuiRectangle(this, new GuiBackground(Color.WHITE), new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));
    }


    @Override
    public void setBorder(Color color) {
        borderLayout = new GuiRectangle(this, new GuiBackground(color), new RelativeConstraint(1, this),
                new RelativeConstraint(1, this), false);
    }

    @Override
    public String toString() {
        return "GuiRectangleButton{" +
                "buttonLayout=" + buttonLayout +
                '}';
    }
}
