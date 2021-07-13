package guis.presets;

import inputs.ClickType;
import inputs.callbacks.MousePressCallback;
import inputs.callbacks.MouseReleaseCallback;

public interface GuiClickablePreset {

    /**
     * @return true if handled
     */
    boolean onMousePress(int button);

    /**
     * @return true if handled
     */
    boolean onMouseRelease(int button);

    void setOnMouseRelease(MouseReleaseCallback onMouseReleaseCallback);

    void setOnMousePress(MousePressCallback onMousePressCallback);

    boolean isReleaseInsideNeeded();

    void setReleaseInsideNeeded(boolean releaseInsideNeeded);

    void reset();

    boolean isClicked();

    ClickType getClickType();

    void setClickType(ClickType clickType);
}
