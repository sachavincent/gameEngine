package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiEllipse;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;

public class GuiCircularButton extends GuiButton {

    public GuiCircularButton(GuiInterface parent, String textureBackground, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, textureBackground, text, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, Color colorBackground, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, colorBackground, text, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, String textureBackground, GuiConstraintsManager constraintsManager) {
        this(parent, textureBackground, null, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        this(parent, colorBackground, null, constraintsManager);
    }

    @Override
    protected void addBackgroundComponent(String background) { //TODO: Color border?
        buttonLayout = new GuiEllipse(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        filterLayout = new GuiEllipse(this, Color.WHITE, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));
    }

    @Override
    protected void addBackgroundComponent(Color background) { //TODO: Color border?
        buttonLayout = new GuiEllipse(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        filterLayout = new GuiEllipse(this, Color.WHITE, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));
    }

    @Override
    public void setBorder(Color color) {
        borderLayout = new GuiEllipse(this, color, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this), false);
    }

    @Override
    public String toString() {
        return "GuiCircularButton{" +
                "filterLayout=" + filterLayout +
                ", buttonLayout=" + buttonLayout +
                ", borderLayout=" + borderLayout +
                '}';
    }
}