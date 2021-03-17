package guis.presets.buttons;

import guis.GuiInterface;
import guis.basics.GuiTriangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;

public class GuiTriangleButton extends GuiAbstractButton {

    public GuiTriangleButton(GuiInterface parent, Background<?> background, int rotation,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);

        setCornerRadius(0);
        setRotation(rotation);
    }

    public GuiTriangleButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        this(parent, background, 0, constraintsManager);
    }

    public GuiTriangleButton(GuiInterface parent, Background<?> background, int rotation) {
        this(parent, background, rotation, null);
    }

    public GuiTriangleButton(GuiInterface parent, Background<?> background) {
        this(parent, background, null);
    }


    public void setRotation(int rotation) {
        ((GuiTriangle) this.buttonShape).setRotation(rotation);

        if (this.filterLayout != null)
            ((GuiTriangle) this.filterLayout).setRotation(rotation);
        if (this.borderLayout != null)
            ((GuiTriangle) this.borderLayout).setRotation(rotation);
    }

    @Override
    protected void setButtonShape(Background<?> background) {
        this.buttonShape = new GuiTriangle(this, background, new RelativeConstraint(1), new RelativeConstraint(1));
    }

    @Override
    protected void setFilter() {
        this.filterLayout = new GuiTriangle(this, new Background<>(Color.WHITE), new RelativeConstraint(1),
                new RelativeConstraint(1));
    }

    @Override
    public void setBorder(Color color) {
        addBorderLayout(
                new GuiTriangle(this, new Background<>(color), new RelativeConstraint(1), new RelativeConstraint(1),
                        false));
    }

    @Override
    public String toString() {
        return "GuiTriangleButton{" +
                "filterLayout=" + filterLayout +
                ", borderLayout=" + borderLayout +
                "} ";
    }
}
