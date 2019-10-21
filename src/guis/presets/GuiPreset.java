package guis.presets;

import guis.Gui;
import guis.GuiComponent;
import guis.basics.GuiBasics;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiPreset extends GuiComponent {

    private List<GuiBasics> basics;

    GuiPreset(Gui parent, String texture) {
        super(parent, texture);//TODO: Handle colors

        this.basics = new ArrayList<>();
    }

    public List<GuiBasics> getBasics() {
        return this.basics;
    }

    @Override
    public void setConstraints(GuiConstraintsManager constraints) {
//        constraints.setWidthConstraint(); //Apply width TODO
//        constraints.setHeightConstraint(); // Apply height TODO
        this.basics.forEach(guiBasics -> guiBasics.setConstraints(constraints));
    }
}
