package guis;

import guis.constraints.GuiGlobalConstraints;
import guis.transitions.Transition;

public interface GuiInterface {

    float getX();

    float getY();

    float getStartX();

    float getStartY();

    float getFinalWidth();

    float getFinalHeight();

    float getFinalX();

    float getFinalY();

    float getWidth();

    float getHeight();

    void setStartX(float startX);

    void setX(float x);

    void setY(float y);

    void setStartY(float startY);

    void setFinalX(float finalX);

    void setFinalY(float finalY);

    void setFinalWidth(float finalWidth);

    void setFinalHeight(float finalHeight);

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