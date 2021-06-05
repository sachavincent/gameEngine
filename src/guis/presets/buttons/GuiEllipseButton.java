package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiEllipse;
import guis.basics.GuiShape;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import java.awt.Color;
import renderEngine.GuiRenderer;

public class GuiEllipseButton extends GuiAbstractButton {

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, text, constraintsManager);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui, GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, text, tooltipGui, constraintsManager);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, borderColor, constraintsManager);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text) {
        super(parent, background, borderColor, text);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, borderColor, text, constraintsManager, cornerRadius);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui, GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, borderColor, text, tooltipGui, constraintsManager, cornerRadius);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui, int cornerRadius) {
        super(parent, background, borderColor, text, tooltipGui, cornerRadius);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            Text tooltipGui) {
        super(parent, background, borderColor, text, tooltipGui);
    }

    public GuiEllipseButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, borderColor, constraintsManager, cornerRadius);
    }

    @Override
    public GuiShape createShape(Background<?> baseColor, Color borderColor) {
        boolean filled = baseColor != null && !baseColor.equals(Background.NO_BACKGROUND);
        GuiEllipse guiEllipse = new GuiEllipse(this, baseColor, new RelativeConstraint(1),
                new RelativeConstraint(1), filled);
        guiEllipse.setBorderColor(borderColor);
        guiEllipse.setType(GuiRenderer.CIRCLE);
        return guiEllipse;
    }

    @Override
    protected void setFilter() {
        this.filterLayout = new GuiEllipse(this, new Background<>(Color.WHITE), new RelativeConstraint(1),
                new RelativeConstraint(1));
    }

    @Override
    public String toString() {
        return "GuiCircularButton{" +
                "filterLayout=" + this.filterLayout +
                '}';
    }
}