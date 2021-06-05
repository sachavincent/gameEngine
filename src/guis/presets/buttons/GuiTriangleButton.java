package guis.presets.buttons;

import guis.GuiInterface;
import guis.basics.GuiShape;
import guis.basics.GuiTriangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;

public class GuiTriangleButton extends GuiAbstractButton {

    public GuiTriangleButton(GuiInterface parent, Background<?> background, Color borderColor, int rotation,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, constraintsManager);

        setCornerRadius(0);
        setRotation(rotation);
    }

    public GuiTriangleButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, borderColor, 0, constraintsManager);
    }

    public GuiTriangleButton(GuiInterface parent, Background<?> background, Color borderColor, int rotation) {
        this(parent, background, borderColor, rotation, null);
    }

    public GuiTriangleButton(GuiInterface parent, Background<?> background, Color borderColor) {
        this(parent, background, borderColor, null);
    }

    public void setRotation(int rotation) {
        ((GuiTriangle) this.shape).setRotation(rotation);

        if (this.filterLayout != null)
            ((GuiTriangle) this.filterLayout).setRotation(rotation);
    }

    @Override
    public GuiShape createShape(Background<?> baseColor, Color borderColor) {
        boolean filled = baseColor != null && !baseColor.equals(Background.NO_BACKGROUND);
        GuiTriangle guiTriangle = new GuiTriangle(this, baseColor, new RelativeConstraint(1),
                new RelativeConstraint(1), filled);
        guiTriangle.setBorderColor(borderColor);
        return guiTriangle;
    }

    @Override
    protected void setFilter() {
        this.filterLayout = new GuiTriangle(this, new Background<>(Color.WHITE), new RelativeConstraint(1),
                new RelativeConstraint(1));
    }

    @Override
    public String toString() {
        return "GuiTriangleButton{" +
                "filterLayout=" + this.filterLayout +
                "} ";
    }
}
