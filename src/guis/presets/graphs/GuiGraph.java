package guis.presets.graphs;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiPreset;

public abstract class GuiGraph extends GuiPreset {

    GuiGraph(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);
    }
}
