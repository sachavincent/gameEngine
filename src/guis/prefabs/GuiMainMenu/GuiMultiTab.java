package guis.prefabs.GuiMainMenu;

import fontMeshCreator.Text;
import guis.Gui;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PatternGlobalConstraint;
import guis.constraints.RatioedPatternGlobalConstraint;
import guis.presets.Background;
import guis.presets.buttons.GuiRectangleButton;
import inputs.callbacks.BackCallback;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiMultiTab extends Gui {

    protected final GuiRectangle backArea;
    protected final GuiRectangle tabArea;
    protected final GuiRectangle content;

    private int selectedTabNum;

    private final List<GuiTab>       tabs;
    private final GuiRectangleButton guiBack;

    public GuiMultiTab(Background<?> background, GuiConstraintsManager guiConstraintsManager) {
        super(background);

        setConstraints(guiConstraintsManager);

        this.tabs = new ArrayList<>();

        setLayout(new RatioedPatternGlobalConstraint(1, 2, 0, .01f, 100f, 15f, 100f, 85f));

        GuiRectangle upperArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        upperArea.setLayout(new RatioedPatternGlobalConstraint(2, 1, 0, 0, 5f, 100f, 95f, 100f));

        this.backArea = new GuiRectangle(upperArea, Background.NO_BACKGROUND);
        Background<String> back = new Background<>("back_arrow.png");
        this.guiBack = new GuiRectangleButton(this.backArea, back, (GuiConstraintsManager) null);

        this.tabArea = new GuiRectangle(upperArea, Background.NO_BACKGROUND);
        this.tabArea.setLayout(new PatternGlobalConstraint(1, 1, .02f, .02f));

        this.content = new GuiRectangle(this, Background.NO_BACKGROUND);
        this.selectedTabNum = -1;
    }

    public void addGuiTab(GuiTab guiTab) {
        if (this.tabs.contains(guiTab))
            return;

        this.tabArea.setLayout(new PatternGlobalConstraint(this.tabs.size() + 1, 1, .02f, .02f));
        Text text = new Text(guiTab.getName(), .8f, DEFAULT_FONT, Color.BLACK);
        text.setUpperCase(true);
        GuiRectangleButton rectangleButton = new GuiRectangleButton(this.tabArea, Background.NO_BACKGROUND, text);
        final int num = this.tabs.size();
        rectangleButton.setOnPress(() -> displayTab(num));
        guiTab.setTabMenu(rectangleButton);

        this.tabs.add(guiTab);
    }

    public void removeGuiTab(GuiTab guiTab) {
        this.tabs.remove(guiTab);

        this.tabArea.removeComponent(guiTab.getTabMenu());
        this.tabArea.setLayout(new PatternGlobalConstraint(this.tabs.size(), 1, .02f, .02f));
    }

    public List<GuiTab> getTabs() {
        return Collections.unmodifiableList(this.tabs);
    }

    public void displayTab(int tabNum) {
        if (this.tabs.size() <= tabNum || tabNum == this.selectedTabNum)
            return;

        hideTab(this.selectedTabNum); // Hides former

        GuiTab newGuiTab = this.tabs.get(tabNum);
        newGuiTab.setDisplayed(true); // Shows new

        this.selectedTabNum = tabNum;
    }

    public void hideTab(int tabNum) {
        if (this.tabs.size() <= tabNum || tabNum < 0)
            return;

        GuiTab formerGuiTab = this.tabs.get(tabNum);
        formerGuiTab.setDisplayed(false); // Hides

        this.selectedTabNum = -1;
    }

    public int getSelectedTabNum() {
        return this.selectedTabNum;
    }

    @Override
    public void setDisplayed(boolean displayed) {
        super.setDisplayed(displayed);

        if (displayed) {
            this.selectedTabNum = -1;
            displayTab(0);
        } else {
            hideTab(this.selectedTabNum);
        }
    }

    public void onBackButtonPress(BackCallback backCallback) {
        if (this.guiBack != null)
            this.guiBack.setOnPress(backCallback::onBack);
    }
}
