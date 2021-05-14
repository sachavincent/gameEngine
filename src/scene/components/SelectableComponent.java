package scene.components;

import inputs.callbacks.MousePressCallback;

public class SelectableComponent implements Component {

    private boolean selected;

    private final MousePressCallback mousePressCallback;

    public SelectableComponent(MousePressCallback mousePressCallback) {
        this.mousePressCallback = button -> {
            this.selected = true;
            mousePressCallback.onPress(button);
        };
    }

    public MousePressCallback getPressCallback() {
        return this.mousePressCallback;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
