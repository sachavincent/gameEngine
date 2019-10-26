package guis.presets;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.constraints.GuiConstraintsManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiPreset extends GuiComponent {

    private List<GuiBasics> basics;

    @Deprecated
    private float scale;

    GuiPreset(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent);

        setConstraints(constraintsManager);

        this.basics = new ArrayList<>();
    }

    @Deprecated
    public float getScale() {
        return this.scale;
    }

    @Deprecated
    public void setScale(float scale) {
        if (scale < 0)
            return; //todo raise esc

        this.scale = scale;

        for (GuiBasics guiBasics : this.basics) {
            guiBasics.setWidth(guiBasics.getBaseWidth() * scale);
            guiBasics.setHeight(guiBasics.getBaseHeight() * scale);

            if (guiBasics.getWidth() > getParent().getWidth() || guiBasics.getHeight() > getParent().getHeight()) {
                guiBasics.setWidth(guiBasics.getBaseWidth());
                guiBasics.setHeight(guiBasics.getBaseHeight());

                return; //TODO: Exc ou qqch
            }
        }
    }

    public List<GuiBasics> getBasics() {
        return this.basics;
    }
}
