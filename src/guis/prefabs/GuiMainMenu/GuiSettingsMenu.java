package guis.prefabs.GuiMainMenu;

import guis.Gui;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;

public class GuiSettingsMenu extends GuiMultiTab {

    public GuiSettingsMenu(Gui parent) {
        super(Background.NO_BACKGROUND, new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent))
                .setHeightConstraint(new RelativeConstraint(1, parent))
                .setxConstraint(new CenterConstraint(parent))
                .setyConstraint(new RelativeConstraint(0, parent))
                .create());

        new GuiDisplaySettings(this);
        new GuiControlsSettings(this);
        new GuiLanguageSettings(this);
    }


    enum State {
        GRAPHICS,
        CONTROLS,
        LANGUAGES,
        SOUND
    }
}
