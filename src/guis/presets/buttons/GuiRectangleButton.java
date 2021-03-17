package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;

public class GuiRectangleButton extends GuiAbstractButton {

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, tooltipGui, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text) {
        super(parent, background, text);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, text, constraintsManager, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, text, tooltipGui, constraintsManager, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            int cornerRadius) {
        super(parent, background, text, tooltipGui, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui) {
        super(parent, background, text, tooltipGui);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, constraintsManager, cornerRadius);
    }


    @Override
    protected void setButtonShape(Background<?> background) {
        this.buttonShape = new GuiRectangle(this, background, new RelativeConstraint(1), new RelativeConstraint(1));
    }

    @Override
    protected void setFilter() {
        this.filterLayout = new GuiRectangle(this, new Background<>(Color.WHITE), new RelativeConstraint(1),
                new RelativeConstraint(1));
    }

    @Override
    public void setBorder(Color color) {
        addBorderLayout(
                new GuiRectangle(this, new Background<>(color), new RelativeConstraint(1), new RelativeConstraint(1),
                        false));
    }


    @Override
    public String toString() {
        return "GuiRectangleButton{" +
                "filterLayout=" + filterLayout +
                ", borderLayout=" + borderLayout +
                "} ";
    }
}
