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

    void setChildrenConstraints(GuiGlobalConstraints guiConstraints);

    GuiTexture getDebugOutline();

    default boolean update() {
        return true;
    }

}