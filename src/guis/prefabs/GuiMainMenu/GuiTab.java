package guis.prefabs.GuiMainMenu;

import guis.Gui;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import language.Words;

public class GuiTab extends Gui {

    private GuiRectangleButton tabMenu;

    private final Words name;

    public GuiTab(Background<?> background, GuiMultiTab guiMultiTab, Words name) {
        super(background);

        this.name = name;

        guiMultiTab.addGuiTab(this);
    }

    public String getName() {
        return this.name.getString();
    }

    public GuiRectangleButton getTabMenu() {
        return this.tabMenu;
    }

    public void setTabMenu(GuiRectangleButton tabMenu) {
        this.tabMenu = tabMenu;
    }
}
