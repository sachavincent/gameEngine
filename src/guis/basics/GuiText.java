package guis.basics;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;

public class GuiText extends GuiBasics {

    private Text text;

    public GuiText(GuiInterface gui, Text text) {
        super(gui, text == null ? null : text.getAWTColor(), null, null);

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
