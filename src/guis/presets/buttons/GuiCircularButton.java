package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiEllipse;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import java.awt.Color;

public class GuiCircularButton extends GuiAbstractButton {

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background, Text text, Text tooltipText,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, tooltipText, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background, Text text, Text tooltipText) {
        super(parent, background, text, tooltipText);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background, Text text, Text tooltipText,
            int cornerRadius) {
        super(parent, background, text, tooltipText, cornerRadius);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, text, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, null, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background, Text text, Text tooltipText,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, text, tooltipText, constraintsManager, cornerRadius);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, text, constraintsManager, cornerRadius);
    }

    public GuiCircularButton(GuiInterface parent, GuiBackground<?> background,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        this(parent, background, null, constraintsManager, cornerRadius);
    }

    @Override
    protected void addBackgroundComponent(GuiBackground<?> background) {
        buttonLayout = new GuiEllipse(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        filterLayout = new GuiEllipse(this, new GuiBackground<>(Color.WHITE), new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));
    }

    @Override
    public void setBorder(Color color) {
        addBorderLayout(new GuiEllipse(this, new GuiBackground<>(color), new RelativeConstraint(1, this),
                new RelativeConstraint(1, this), false));
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