package scene.components;

import inputs.callbacks.MousePressCallback;

/**
 * Requires BoundingBoxComponent to work
 */
public class SelectableComponent extends Component {

    private boolean selected;

    private final MousePressCallback mousePressCallback;

    public SelectableComponent(MousePressCallback mousePressCallback) {
        this.mousePressCallback = button -> {
            this.selected = true;
            mousePressCallback.onPress(button);
            return true;
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
