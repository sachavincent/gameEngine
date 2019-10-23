package guis.presets;

import guis.GuiComponent;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiPreset extends GuiComponent {

    private List<GuiBasics> basics;

    private float scale;

    GuiPreset(GuiInterface parent, String texture) {
        super(parent, texture);//TODO: Handle colors

        this.basics = new ArrayList<>();
    }

    public float getScale() {
        return this.scale;
    }

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
