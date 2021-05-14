package inputs;

import static org.lwjgl.glfw.GLFW.*;

public enum KeyModifiers {
    NONE,
    LCTRL,
    RCTRL,
    LALT,
    RALT,
    LSHIFT,
    RSHIFT,
    LCTRL_LALT,
    RCTRL_RALT,
    LCTRL_LSHIFT,
    RCTRL_RSHIFT,
    RALT_RSHIFT,
    LALT_LSHIFT;

    public static KeyModifiers getKeyModifierFromName(String name) {
        for (KeyModifiers keyModifier : values()) {
            if (keyModifier.name().equals(name)) {
                return keyModifier;
            }
        }
        return NONE;
    }

    public String formatName() {
        return name().equals(NONE.name()) ? "" : name().replace('_', '+');
    }

    public static KeyModifiers getKeyModifierFromInputKey(int key) {
        switch (key) {
            case GLFW_KEY_LEFT_SHIFT:
                return LSHIFT;
            case GLFW_KEY_LEFT_CONTROL:
                return LCTRL;
            case GLFW_KEY_LEFT_ALT:
                return LALT;
            case GLFW_KEY_RIGHT_SHIFT:
                return RSHIFT;
            case GLFW_KEY_RIGHT_CONTROL:
                return RCTRL;
            case GLFW_KEY_RIGHT_ALT:
                return RALT;
        }
        return NONE;
    }

    public KeyModifiers combineKeyModifiers(KeyModifiers keyModifier) {
        switch (this) {
            case NONE:
                return keyModifier;
            case LALT:
                if (keyModifier == LSHIFT)
                    return LALT_LSHIFT;
                if (keyModifier == LCTRL)
                    return LCTRL_LALT;
            case LSHIFT:
                if (keyModifier == LCTRL)
                    return LCTRL_LSHIFT;
                if (keyModifier == LALT)
                    return LALT_LSHIFT;
            case LCTRL:
                if (keyModifier == LALT)
                    return LCTRL_LALT;
                if (keyModifier == LSHIFT)
                    return LCTRL_LSHIFT;

            case RALT:
                if (keyModifier == RSHIFT)
                    return RALT_RSHIFT;
                if (keyModifier == RCTRL)
                    return RCTRL_RALT;
            case RSHIFT:
                if (keyModifier == RCTRL)
                    return RCTRL_RSHIFT;
                if (keyModifier == RALT)
                    return RALT_RSHIFT;
            case RCTRL:
                if (keyModifier == RALT)
                    return RCTRL_RALT;
                if (keyModifier == RSHIFT)
                    return RCTRL_RSHIFT;
        }
        return this;
    }

    public KeyModifiers removeFromKeyModifiers(KeyModifiers keyModifier) {
        if (this == keyModifier)
            return NONE;

        switch (this) {
            case LCTRL_LALT:
                if (keyModifier == LALT)
                    return LCTRL;
                else if (keyModifier == LCTRL)
                    return LALT;
            case RCTRL_RALT:
                if (keyModifier == RALT)
                    return RCTRL;
                else if (keyModifier == RCTRL)
                    return RALT;
            case LCTRL_LSHIFT:
                if (keyModifier == LSHIFT)
                    return LCTRL;
                else if (keyModifier == LCTRL)
                    return LSHIFT;
            case RCTRL_RSHIFT:
                if (keyModifier == RSHIFT)
                    return RCTRL;
                else if (keyModifier == RCTRL)
                    return RSHIFT;
            case RALT_RSHIFT:
                if (keyModifier == RSHIFT)
                    return RALT;
                else if (keyModifier == RALT)
                    return RSHIFT;
            case LALT_LSHIFT:
                if (keyModifier == LSHIFT)
                    return LALT;
                else if (keyModifier == LALT)
                    return LSHIFT;
        }
        return this;
    }
}
