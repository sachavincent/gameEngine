package scene.components;

import inputs.callbacks.PressCallback;

public class SelectableComponent implements Component {

    private boolean selected;

    private final PressCallback pressCallback;

    public SelectableComponent(PressCallback pressCallback) {
        this.pressCallback = () -> {
            this.selected = true;
            pressCallback.onPress();
        };
    }

    public PressCallback getPressCallback() {
        return this.pressCallback;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
