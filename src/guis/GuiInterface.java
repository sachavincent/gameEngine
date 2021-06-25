package guis;

import guis.constraints.layout.GuiLayout;
import guis.transitions.Transition;
import inputs.callbacks.CloseCallback;
import inputs.callbacks.OpenCallback;
import inputs.callbacks.UpdateCallback;

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

    void setLayout(GuiLayout guiConstraints);

    GuiTexture getDebugOutline();

    boolean displayDebugOutline();

    default boolean update() {
        return true;
    }

    void setOnUpdate(UpdateCallback callback);

    void setOnClose(CloseCallback callback);

    void setOnOpen(OpenCallback callback);

    void focus();

    void unfocus();

    boolean isFocused();

    float getCornerRadius();

    int getType();
}