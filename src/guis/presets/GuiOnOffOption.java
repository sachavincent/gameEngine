package guis.presets;

import guis.GuiInterface;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiOnOffOption.OnOff;
import java.awt.Color;
import java.util.Arrays;

public class GuiOnOffOption extends GuiMultiOption<OnOff> {

    private final static OnOff[] OPTIONS = new OnOff[]{OnOff.ON, OnOff.OFF};

    public GuiOnOffOption(GuiInterface parent, Background<?> background, OnOff defaultValue,
            GuiConstraintsManager constraintsManager, Color arrowsColor) {
        super(parent, background, defaultValue, constraintsManager, arrowsColor, Arrays.asList(OPTIONS));
    }

    public enum OnOff {
        ON(true),
        OFF(false);

        boolean value;

        OnOff(boolean value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return name();
        }

        public boolean getValue() {
            return this.value;
        }

        public static OnOff getType(boolean b) {
            return b ? ON : OFF;
        }
    }
}
