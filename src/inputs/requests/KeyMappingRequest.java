package inputs.requests;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import inputs.KeyInput;
import inputs.KeyModifiers;
import inputs.callbacks.HandleRequestCallback;
import inputs.callbacks.KeyCallback;

public class KeyMappingRequest extends Request {

    private KeyModifiers keyModifier;

    private final KeyCallback callback;

    private static final HandleRequestCallback ON_HANDLE_REQUEST_CALLBACK = (action, pressedKey, r, requests) -> {
        if (action == GLFW_REPEAT) // Ignores repeat
            return false;

        KeyMappingRequest request = (KeyMappingRequest) r;
        KeyModifiers keyModifier = KeyModifiers.getKeyModifierFromInputKey(pressedKey);
        KeyModifiers requestKeyModifier = request.getKeyModifier();
        if (keyModifier != KeyModifiers.NONE) {
            if (action == GLFW_PRESS)
                keyModifier = requestKeyModifier.combineKeyModifiers(keyModifier);
            else if (action == GLFW_RELEASE)
                keyModifier = requestKeyModifier.removeFromKeyModifiers(keyModifier);

            request.setKeyModifier(keyModifier);

            return false;
        } else { // Key pressed
            KeyInput keyInput = new KeyInput((char) pressedKey, request.getKeyModifier());
            if (request.getCallback().onKeyRequest(action, keyInput)) {
                requests.remove();
            }
        }

        return true;
    };

    public KeyMappingRequest(KeyCallback callback) {
        super(ON_HANDLE_REQUEST_CALLBACK);

        this.callback = callback;
        this.keyModifier = KeyModifiers.NONE;
    }

    public KeyMappingRequest(KeyMappingRequest request) {
        this(request.callback);
    }

    public KeyCallback getCallback() {
        return this.callback;
    }

    public KeyModifiers getKeyModifier() {
        return this.keyModifier;
    }

    public void setKeyModifier(KeyModifiers keyModifier) {
        this.keyModifier = keyModifier;
    }
}