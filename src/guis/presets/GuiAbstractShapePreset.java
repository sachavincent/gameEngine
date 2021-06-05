package guis.presets;

import guis.basics.GuiShape;
import java.awt.Color;

public interface GuiAbstractShapePreset {

    GuiShape createShape(Background<?> baseColor, Color borderColor);

    GuiShape getShape();
}
