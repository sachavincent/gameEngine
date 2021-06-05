package inputs;

import static org.lwjgl.glfw.GLFW.*;

import inputs.requests.Request;
import inputs.requests.Request.RequestType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.lwjgl.system.Callback;
import renderEngine.DisplayManager;

public class KeyboardUtils {

    private static Callback callback;

    private static final Map<RequestType, Queue<Request>> requests = new HashMap<>();

    static {
        requests.put(RequestType.KEY, new LinkedList<>());
        requests.put(RequestType.CHAR, new LinkedList<>());
    }

    public static boolean isCapsLock;
    public static boolean isNumLock;
    public static boolean isSuper;
    public static boolean isAlt;
    public static boolean isControl;
    public static boolean isShift;

    private static KeyModifiers keyModifier = KeyModifiers.NONE; // Used out of requests

    public static void cancelRequest(Request request) {
        requests.get(request.getRequestType()).remove(request);
    }

    public static void cancelRequest(RequestType requestType) {
        requests.get(requestType).poll();
    }

    public static void request(Request request) {
        requests.get(request.getRequestType()).add(request);
    }

    private static boolean handleRequests(RequestType requestType, int action, char pressedKey, int scancode) {
        if (requests.isEmpty())
            return false;

        Queue<Request> requests = KeyboardUtils.requests.get(requestType);
        if (requests.isEmpty())
            return false;

        Request request = requests.element();

        if (request != null)
            return request.getOnHandleRequest().onHandle(action, pressedKey, scancode, request, requests);

        return false;
    }

    public static void setupListeners() {
        glfwSetCharCallback(DisplayManager.getWindow(), (window, codepoint) -> {
            handleRequests(RequestType.CHAR, GLFW_PRESS, (char) codepoint, 0);
        });
        callback = glfwSetKeyCallback(DisplayManager.getWindow(), (w, key, scancode, action, mods) -> {
            // Caps lock & Num lock handling
            {
                int numLock = (mods & GLFW_MOD_NUM_LOCK);
                int capsLock = ((mods - numLock) & GLFW_MOD_CAPS_LOCK);
                int superPressed = ((mods - capsLock) & GLFW_MOD_SUPER);
                int altPressed = ((mods - superPressed) & GLFW_MOD_ALT);
                int ctrlPressed = ((mods - altPressed) & GLFW_MOD_CONTROL);
                int shiftPressed = ((mods - ctrlPressed) & GLFW_MOD_SHIFT);

                isCapsLock = capsLock == GLFW_MOD_CAPS_LOCK;
                isNumLock = numLock == GLFW_MOD_NUM_LOCK;
                isSuper = superPressed == GLFW_MOD_SUPER;
                isAlt = altPressed == GLFW_MOD_ALT;
                isControl = ctrlPressed == GLFW_MOD_CONTROL;
                isShift = shiftPressed == GLFW_MOD_SHIFT;
            }
            boolean handled = handleRequests(RequestType.KEY, action, (char) key, scancode);
            if (handled)
                return;

            KeyModifiers keyModifierFromInputKey = KeyModifiers.getKeyModifierFromInputKey(key);
            if (keyModifierFromInputKey != null)
                if (action == GLFW_PRESS)
                    keyModifier = keyModifier.combineKeyModifiers(keyModifierFromInputKey);
                else if (action == GLFW_RELEASE)
                    keyModifier = keyModifier.removeFromKeyModifiers(keyModifierFromInputKey);

            Key keyFromInput = Key.getKeyFromInput(new KeyInput((char) key, keyModifier));
            if (keyFromInput != null)
                keyFromInput.on(action);
        });
    }

    public static void freeCallbacks() {
        if (callback != null)
            callback.free();
    }
}