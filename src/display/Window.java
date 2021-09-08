package display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL40;
import util.DisplayUtils;
import util.math.Maths;
import util.math.Vector2f;

public class Window {

    private static final String WINDOW_TITLE = "GameEngine";

    private final long id;

    private boolean displayed;
    private boolean focused;

    private int x, y;
    private int width, height;

    public Window() {
        GLFWVidMode mode = glfwGetVideoMode(Display.PRIMARY_MONITOR);
        glfwWindowHint(GLFW_RED_BITS, mode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.id = glfwCreateWindow((this.width = mode.width()),
                (this.height = mode.height()), WINDOW_TITLE, 0, 0);
        glfwMakeContextCurrent(this.id);
        GL.createCapabilities();

        DisplayUtils.enableDebugging();
        DisplayUtils.disableDebugMessage(GL_DEBUG_SEVERITY_NOTIFICATION);

        glfwSetInputMode(this.id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwSetInputMode(this.id, GLFW_LOCK_KEY_MODS, GLFW_TRUE);

        glEnable(GL13.GL_MULTISAMPLE);
        glEnable(GL40.GL_FRAMEBUFFER_SRGB); // Gamma correction

        setFocusCallback((window, maximized) -> {
            if (maximized)
                glfwSetWindowPos(window, this.x, this.y);
        });

        setFramebufferSizeCallback((window, width, height) -> {
            this.width = width;
            this.height = height;
        });

        setWindowPosCallback((window, xpos, ypos) -> {
            this.x = xpos;
            this.y = ypos;
            Monitor currentMonitor = Display.getCurrentMonitor();
            if (this.x >= 0 && this.y >= 0) { // Maximized
                if (currentMonitor.getX() != this.x) { // Monitor manually changed
                    Display.MONITORS.stream()
                            .filter(monitor -> monitor.getX() == this.x)
                            .findFirst().ifPresent(Display::setMonitor);
                }
            }
        });
    }

    public void show() {
        if (!this.displayed) {
            glfwShowWindow(this.id);
            this.displayed = true;
        }

        focus();
    }

    public void focus() {
        if (!this.focused) {
            glfwFocusWindow(this.id);
            this.focused = true;
        }
    }

    public void unfocus() {
        if (this.focused) {
            this.focused = false;
        }
    }

    public void setFramebufferSizeCallback(GLFWFramebufferSizeCallbackI callback) {
        glfwSetFramebufferSizeCallback(this.id, callback);
    }

    public void setWindowPosCallback(GLFWWindowPosCallbackI callback) {
        glfwSetWindowPosCallback(this.id, callback);
    }

    public void setFocusCallback(GLFWWindowFocusCallbackI callback) {
        glfwSetWindowFocusCallback(this.id, callback);
    }

    public void hide() {
        glfwHideWindow(this.id);

        this.displayed = false;
        unfocus();
    }

    public void maximize() {
        glfwMaximizeWindow(this.id);

        focus();
    }

    public void setMaximizeCallback(GLFWWindowMaximizeCallbackI callback) {
        glfwSetWindowMaximizeCallback(this.id, callback);
    }

    public void iconify() {
        glfwIconifyWindow(this.id);

        unfocus();
    }

    public void setIconifyCallback(GLFWWindowIconifyCallbackI callback) {
        glfwSetWindowIconifyCallback(this.id, callback);
    }

    public void restore() {
        glfwRestoreWindow(this.id);

        focus();
    }

    public void close() {
        glfwSetWindowShouldClose(this.id, true);
    }

    public void destroy() {
        glfwDestroyWindow(this.id);
    }

    public void setRefreshCallback(GLFWWindowRefreshCallbackI callback) {
        glfwSetWindowRefreshCallback(this.id, callback);
    }

    public void setCharCallback(GLFWCharCallbackI callback) {
        glfwSetCharCallback(this.id, callback);
    }

    public void setCharModsCallback(GLFWCharModsCallbackI callback) {
        glfwSetCharModsCallback(this.id, callback);
    }

    public void setKeyCallback(GLFWKeyCallbackI callback) {
        glfwSetKeyCallback(this.id, callback);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
        glfwSetMouseButtonCallback(this.id, callback);
    }

    public void setScrollCallback(GLFWScrollCallbackI callback) {
        glfwSetScrollCallback(this.id, callback);
    }

    public void setCursorPosCallback(GLFWCursorPosCallbackI callback) {
        glfwSetCursorPosCallback(this.id, callback);
    }

    public void setWindowAttrib(int attribName, int attribValue) {
        glfwSetWindowAttrib(this.id, attribName, attribValue);
    }

    public void setMonitor(Monitor monitor) {
        setMonitor(monitor.getId(), 0, 0);
    }

    public Vector2f getCursorPos() {
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(this.id, posX, posY);

        Vector2f cursorPos = new Vector2f(posX.get(), posY.get());

        posX.clear();
        posY.clear();

        double newX = cursorPos.getX();
        newX /= Display.getWindow().getWidth();
        newX *= 2;
        newX -= 1;
        cursorPos.setX(Maths.clamp((float) newX, -1, 1));

        double newY = cursorPos.getY();
        newY /= Display.getWindow().getHeight();
        newY *= -2;
        newY += 1;
        cursorPos.setY(Maths.clamp((float) newY, -1, 1));

        return cursorPos;
    }

    public void setMonitor(long monitorId, int xpos, int ypos) {
        GLFWVidMode mode;
        if (monitorId == 0)
            mode = glfwGetVideoMode(Display.getCurrentMonitor().getId());
        else
            mode = glfwGetVideoMode(monitorId);

        glfwSetWindowMonitor(this.id, monitorId, xpos, ypos,
                mode.width(), mode.height(), mode.refreshRate());

        maximize();
    }

    public void setResolution(Resolution resolution) {
        //TODO
        Monitor currentMonitor = Display.getCurrentMonitor();
        if (Display.getCurrentMonitor().getIndex() != 0) {
            glfwSetWindowPos(this.id, currentMonitor.getX(), currentMonitor.getY());
        }
        Resolution res = currentMonitor.getResolution();
        setSize(res.getWidth(), res.getHeight() - (Display.getDisplayMode() == DisplayMode.WINDOWED ? 50 : 0));
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(this.id, width, height);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(this.id);
    }

    public void swapBuffers() {
        glfwSwapBuffers(this.id);
    }

    public void freeCallbacks() {
        Callbacks.glfwFreeCallbacks(this.id);
    }

    public void makeContextCurrent() {
        glfwMakeContextCurrent(this.id);
    }

    public void setClipboardString(CharSequence content) {
        glfwSetClipboardString(this.id, content);
    }

    public CharSequence getClipboardString() {
        return glfwGetClipboardString(this.id);
    }
}