package guis;

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

    GuiTexture<?> getTexture();

    void setDisplayed(boolean displayed);

    boolean isDisplayed();

    void updateTexturePosition();
}