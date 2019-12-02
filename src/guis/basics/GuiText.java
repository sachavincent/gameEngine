package guis.basics;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.presets.GuiBackground;

public class GuiText extends GuiBasics {

    private Text text;

    public GuiText(GuiInterface gui, Text text) {
        super(gui, new GuiBackground(text == null ? null : text.getAWTColor()), null, null);

        this.text = text;
    }

    public Text getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return "GuiText{" +
                "text=" + text +
                ", " + super.toString() + "}";
    }
}
