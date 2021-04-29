package guis;

import guis.constraints.GuiGlobalConstraints;
import guis.transitions.Transition;

public interface GuiInterface {

    float getX();

    float getY();

    float getWidth();

    float getHeight();

    void setX(float x);

    void setY(float y);

    void setWidth(float width);

    void setHeight(float height);

    void setAlpha(float alpha);

    GuiTexture getTexture();

    void setDisplayed(boolean displayed);

    void addComponent(GuiComponent guiComponent, Transition... transitions);

    void addComponentToParent(GuiComponent guiComponent, Transition... transitions);

    void removeComponent(GuiComponent guiComponent);

    boolean isDisplayed();

    void updateTexturePosition();

    void setLayout(GuiGlobalConstraints guiConstraints);

    GuiTexture getDebugOutline();

    boolean displayDebugOutline();

    default boolean update() {
        return true;
    }
}