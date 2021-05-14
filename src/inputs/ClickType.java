package inputs;

import org.lwjgl.glfw.GLFW;

public enum ClickType {
    NONE(-1),
    M1(GLFW.GLFW_MOUSE_BUTTON_LEFT),
    M2(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
    MIDDLE(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);

    protected int button;

    ClickType(int button) {
        this.button = button;
    }

    public int getButton() {
        return this.button;
    }

    public static ClickType getClickTypeFromButton(int button) {
        for (ClickType clickType : values()) {
            if (clickType.getButton() == button)
                return clickType;
        }

        return NONE;
    }
}
