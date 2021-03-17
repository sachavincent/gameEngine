package guis.presets;

import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.constraints.GuiConstraintsManager;

public class GuiTextInput extends GuiPreset {

    private GuiRectangle cursor;

    private String text;

    protected GuiTextInput(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);
    }

    protected GuiTextInput(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);
    }

    protected GuiTextInput(GuiInterface parent, Background<?> background) {
        super(parent, background);
    }

    protected GuiTextInput(GuiInterface parent) {
        super(parent);
    }

//    @Override
//    public void onPress() {
//        super.onPress();
//
//        cursor.setDisplayed(true);
//    }
}
