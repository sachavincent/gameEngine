package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;

public class GuiRectangleButton extends GuiAbstractButton {

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipText,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, tooltipText, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, null, constraintsManager);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipText,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, text, tooltipText, constraintsManager, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text, Text tooltipText,
            int cornerRadius) {
        super(parent, background, text, tooltipText, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, text, constraintsManager, cornerRadius);
    }

    public GuiRectangleButton(GuiInterface parent, Background<?> background,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        this(parent, background, null, constraintsManager, cornerRadius);
    }

//    @Override
//    protected void addBackgroundComponent(Background<?> background) {
//        this.buttonLayout = new GuiRectangle(this, background, new RelativeConstraint(1), new RelativeConstraint(1));
//    }

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
