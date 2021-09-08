package display;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;
import org.lwjgl.opengl.GL;
import util.DisplayUtils;

public class Display {

    public static final List<Monitor> MONITORS = new ArrayList<>();

    public static long PRIMARY_MONITOR;

    private static Monitor currentMonitor;

    private static DisplayMode displayMode;

    private static Window window;

    public static  boolean limitFramerate;
    private static boolean vsyncEnabled;

    private static MonitorChangedCallback monitorChangedCallback = monitor -> {
    };

    public static void createDisplay() {
        initGLFW();

        Display.displayMode = DisplayMode.defaultMode();
        loadScreens();

        Display.window = new Window();
    }

    private static void initGLFW() {
        boolean init = glfwInit();
        if (!init)
            throw new IllegalStateException("An error occured during glfwInit()");

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
//        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_FALSE);
//        glfwWindowHint(GLFW_DOUBLEBUFFER, GL_FALSE);
//        glfwWindowHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); //TODO: temp ?

        glfwWindowHint(GLFW_SAMPLES, 8);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        PRIMARY_MONITOR = glfwGetPrimaryMonitor();
    }


    public static void createDisplayForTests() {
        glfwInit();

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        glfwWindowHint(GLFW_SAMPLES, 8);

        Display.currentMonitor = MONITORS.get(1);
        Display.window = new Window();
        Display.window.makeContextCurrent();
        glfwSwapInterval(0); //Vsync off
        GL.createCapabilities();
    }

    public static void setDisplayMode(DisplayMode type) {
        Display.displayMode = type;

        switch (type) {
            case FULLSCREEN:
                Display.window.setWindowAttrib(GLFW_AUTO_ICONIFY, GLFW_TRUE);

                Display.window.setMonitor(Display.currentMonitor);
                break;
            case BORDERLESS_WINDOWED:
                Display.window.setWindowAttrib(GLFW_AUTO_ICONIFY, GLFW_FALSE);
                Display.window.setWindowAttrib(GLFW_DECORATED, GLFW_FALSE);

                Display.window.setMonitor(Display.currentMonitor);
                break;
            case WINDOWED:
                Display.window.setWindowAttrib(GLFW_DECORATED, GLFW_TRUE);
                Display.window.setWindowAttrib(GLFW_AUTO_ICONIFY, GLFW_FALSE);

                Display.window.setMonitor(0, Display.currentMonitor.getX(), Display.currentMonitor.getY());
                break;
            default:
                break;
        }
        Display.window.show();
    }

    private static void loadScreens() {
        PointerBuffer pointerBuffer = glfwGetMonitors();
        if (pointerBuffer == null)
            throw new IllegalStateException("No monitors were found!");

        for (int i = 0; i < pointerBuffer.limit(); i++) {
            long screenId = pointerBuffer.get(i);

            Buffer modes = glfwGetVideoModes(screenId);
            if (modes == null)
                continue;

            Set<Resolution> resolutions = new TreeSet<>();

            for (int j = 0; j < modes.limit(); j++) {
                GLFWVidMode glfwVidMode = modes.get(j);
                resolutions.add(new Resolution(glfwVidMode.width(), glfwVidMode.height()));
            }

            IntBuffer x = BufferUtils.createIntBuffer(1);
            IntBuffer y = BufferUtils.createIntBuffer(1);
            glfwGetMonitorPos(screenId, x, y);

            Monitor monitor = new Monitor(Display.MONITORS.size(), screenId, x.get(0), y.get(0),
                    screenId == Display.PRIMARY_MONITOR, resolutions);
            Display.MONITORS.add(monitor);
            x.clear();
            y.clear();

            if (screenId == Display.PRIMARY_MONITOR)
                Display.currentMonitor = monitor;
        }
        if (Display.currentMonitor == null)
            throw new NullPointerException("Could not find primary screen");
    }

    public static void setMonitor(Monitor monitor) {
        Display.currentMonitor = monitor;

        Display.setDisplayMode(Display.getDisplayMode());
        Display.getWindow().setResolution(Display.getCurrentMonitor().getResolution());

        Display.monitorChangedCallback.onMonitorChanged(monitor);
    }

    public static void setMonitor(int index) {
        if (Display.MONITORS.size() <= index)
            return;

        setMonitor(Display.MONITORS.get(index));
    }

    public static Monitor getCurrentMonitor() {
        return Display.currentMonitor;
    }

    public static DisplayMode getDisplayMode() {
        return Display.displayMode;
    }

    public static Window getWindow() {
        return Display.window;
    }

    public static void setVsync(boolean vSync) {
        Display.limitFramerate = !vSync;
        Display.vsyncEnabled = vSync;

        glfwSwapInterval(vSync ? 1 : 0);
    }

    public static void onMonitorChanged(MonitorChangedCallback callback) {
        MonitorChangedCallback previousCallback = Display.monitorChangedCallback;

        Display.monitorChangedCallback = monitor -> {
            previousCallback.onMonitorChanged(monitor);
            callback.onMonitorChanged(monitor);
        };
    }

    public static void stop() {
        GL.setCapabilities(null);

        DisplayUtils.freeCallback();
        GLFWErrorCallback glfwErrorCallback = glfwSetErrorCallback(null);
        if (glfwErrorCallback != null)
            glfwErrorCallback.free();

        Display.window.freeCallbacks();
        Display.window.destroy();
        glfwTerminate();
    }

    public static boolean isFramerateLimited() {
        return limitFramerate;
    }

    public static boolean isVsyncEnabled() {
        return vsyncEnabled;
    }
}