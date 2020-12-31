package guis.presets;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;

public abstract class GuiPreset extends GuiComponent {

    protected GuiPreset(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent);

        setConstraints(constraintsManager);
    }

    protected GuiPreset(GuiInterface parent) {
        super(parent);
    }
}
