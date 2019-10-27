package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiOval;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import java.awt.Color;

public class GuiCircularButton extends GuiButton {

    public GuiCircularButton(GuiInterface parent, Text text, String textureBackground,
            GuiConstraintsManager constraintsManager) { //TODO: give name to access
        super(parent, constraintsManager);

        addComponents(textureBackground, text);

        setListeners();
    }

    public GuiCircularButton(GuiInterface parent, Text text, Color colorBackground,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addComponents(colorBackground, text);

        setListeners();
    }


    void addComponents(String background, Text text) { //TODO: Color border?
        buttonLayout = new GuiOval(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        super.addBasics(text);
    }

    void addComponents(Color background, Text text) { //TODO: Color border?

        buttonLayout = new GuiOval(this, background, new RelativeConstraint(1, this),
                new RelativeConstraint(1, this));

        super.addBasics(text);
    }

    public GuiCircularButton(GuiInterface parent, String textureBackground, GuiConstraintsManager constraintsManager) {
        this(parent, null, textureBackground, constraintsManager);
    }

    public GuiCircularButton(GuiInterface parent, Color colorBackground, GuiConstraintsManager constraintsManager) {
        this(parent, null, colorBackground, constraintsManager);
    }
}