package renderEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWVidMode.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import util.Timer;
import util.math.Maths;

import java.io.PrintStream;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_ALIASED_LINE_WIDTH_RANGE;

public class DisplayManager {

    public static int WIDTH = 2560, HEIGHT = 1440, FRAMERATE_LIMIT = 300, CURRENT_FPS = FRAMERATE_LIMIT, TPS = 20;
    public static double MSPT;
    private static double FRAMERATE_LIMIT_NS = 1000000000 / (double) FRAMERATE_LIMIT;
    private static boolean LIMIT_FRAMERATE;
    public static float MIN_LINE_WIDTH;
    public static float MAX_LINE_WIDTH;
    private static long window;
    public static boolean VSYNC_ENABLED;

    public static List<Screen> screens;
    public static Screen currentScreen;
    public static int indexCurrentScreen;

    public static DisplayMode displayMode;

    private static GLFWErrorCallback callback;
    private static GLFWFramebufferSizeCallback callback2;
    private static GLFWWindowMaximizeCallback callback3;
    private static GLFWWindowPosCallback callback4;
    private static long firstWindow;

    public static PrintStream outStream;
    public static PrintStream errStream;

    public final static int MIN_FRAMERATE = 30;
    public final static int MAX_FRAMERATE = 300;
    public final static String FRAMERATE_INFINITE = "Inf.";
    ;

    public final static boolean IS_DEBUG = java.lang.management.ManagementFactory.
            getRuntimeMXBean().
            getInputArguments().toString().indexOf("jdwp") >= 0;

    public static void createDisplay() {
        screens = new ArrayList<>();

        callback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);
//        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_FALSE);
//        glfwWindowHint(GLFW_DOUBLEBUFFER, GL_FALSE);
//        glfwWindowHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); //TODO: temp ?

        glfwWindowHint(GLFW_SAMPLES, 8);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        displayMode = DisplayMode.defaultMode();
        indexCurrentScreen = 0;
        loadScreens();
        WIDTH = currentScreen.resolution.width;
        HEIGHT = currentScreen.resolution.height;
    }

    private static void setWindow() {
        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwWindowHint(GLFW_RED_BITS, mode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());

        window = glfwCreateWindow(mode.width(), mode.height(), "OpenGL Tests", 0, 0);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

//        DisplayUtils.enableDebugging();
//        DisplayUtils.disableDebugMessage(GL_DEBUG_SEVERITY_NOTIFICATION);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwSetInputMode(window, GLFW_LOCK_KEY_MODS, GLFW_TRUE);
        glfwSetFramebufferSizeCallback(window, (callback2 = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WIDTH = width;
                HEIGHT = height;
                glViewport(0, 0, WIDTH, HEIGHT);
            }
        }));

        glfwSetWindowMaximizeCallback(window, (callback3 = new GLFWWindowMaximizeCallback() {
            @Override
            public void invoke(long window, boolean maximized) {
                if (maximized && currentScreen != null)
                    glfwSetWindowPos(window, currentScreen.x, currentScreen.y);
            }
        }));
        glfwSetWindowPosCallback(window, (callback4 = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                if (currentScreen != null) {
                    currentScreen.x = xpos;
                    currentScreen.y = ypos;
                }
            }
        }));

//        GLFWImage image = GLFWImage.malloc();
//        ByteBuffer buf = null;
//        GLFWImage.Buffer images = null;
//        try {
//            PNGDecoder dec = new PNGDecoder(new FileInputStream(RES_PATH + "/insula_preview.png"));
//            int width = dec.getWidth();
//            int height = dec.getHeight();
//            buf = BufferUtils.createByteBuffer(width * height * 4);
//            dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
//            buf.flip();
//            image.set(width, height, buf);
//            images = GLFWImage.malloc(1);
//            images.put(0, image);
//
//            glfwSetWindowIcon(window, images);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (buf != null)
//                buf.clear();
//            if (images != null)
//                images.free();
//            image.free();
//        }

        glEnable(GL13.GL_MULTISAMPLE);

        float[] lineWidthRange = new float[2];
        GL30.glGetFloatv(GL_ALIASED_LINE_WIDTH_RANGE, lineWidthRange);
        MIN_LINE_WIDTH = lineWidthRange[0];
        MAX_LINE_WIDTH = lineWidthRange[1];

//        System.out.println("min: " + MIN_LINE_WIDTH);
//        System.out.println("max: " + MAX_LINE_WIDTH);
    }

    public static void setCurrentFps(int fps) {
        LIMIT_FRAMERATE = fps < Integer.MAX_VALUE;

        FRAMERATE_LIMIT = fps;
        FRAMERATE_LIMIT_NS = 1000000000 / (double) fps;
    }

    public static void setFPS(String fps) {
        if (fps.equalsIgnoreCase(FRAMERATE_INFINITE))
            setCurrentFps(Integer.MAX_VALUE);
        else {
            try {
                int fpsValue = Integer.parseInt(fps);
                setCurrentFps(Maths.clamp(fpsValue, MIN_FRAMERATE, MAX_FRAMERATE));
            } catch (NumberFormatException e) {

            }
        }
    }

    private static void loadScreens() {
        PointerBuffer pointerBuffer = glfwGetMonitors();
        if (pointerBuffer == null)
            return; //TODO: raise exception

        for (int i = 0; i < pointerBuffer.limit(); i++) {
            long screenId = pointerBuffer.get(i);

            Buffer modes = glfwGetVideoModes(screenId);
            Set<Resolution> resolutions = new TreeSet<>();

            for (int j = 0; j < modes.limit(); j++) {
                GLFWVidMode glfwVidMode = modes.get(j);
                resolutions.add(new Resolution(glfwVidMode.width(), glfwVidMode.height()));
            }

            IntBuffer x = BufferUtils.createIntBuffer(1);
            IntBuffer y = BufferUtils.createIntBuffer(1);
            glfwGetMonitorPos(screenId, x, y);

            Screen screen = new Screen(screenId, x.get(0), y.get(0), screenId == glfwGetPrimaryMonitor(), resolutions);
            screens.add(screen);
            x.clear();
            y.clear();

            if (screenId == glfwGetPrimaryMonitor())
                currentScreen = screen;
        }
        if (currentScreen == null)
            throw new NullPointerException("Could not find primary screen");
    }

    public static void closeDisplay() {
        Timer.TASKS.values().forEach(timerTasks -> timerTasks.forEach(Timer::cancelTask));
        Timer.MISC_TASKS.forEach(Timer::cancelTask);
        Timer.TIMER.cancel();

        glfwSetWindowShouldClose(window, true);
    }

    public static long getWindow() {
        return window;
    }

    public static void freeCallbacks() {
        if (callback != null)
            callback.free();
        if (callback2 != null)
            callback2.free();
        if (callback3 != null)
            callback3.free();
        if (callback4 != null)
            callback4.free();
    }

    public static void createDisplayForTests() {
        screens = new ArrayList<>();

        callback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_AUTO_ICONIFY, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);

        glfwWindowHint(GLFW_SAMPLES, 8);

        loadScreens();
        currentScreen = screens.get(1);
        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Tests", 0, 0);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0); //Vsync off
        GL.createCapabilities();
    }

    public static void setDisplayMode(DisplayMode type) {
        displayMode = type;

        if (window == 0)
            setWindow();

        GLFWVidMode mode = glfwGetVideoMode(currentScreen.id);
        if (mode == null)
            throw new IllegalArgumentException(
                    "VidMode null for screen id=" + currentScreen.id + " at index " + indexCurrentScreen);

        switch (type) {
            case FULLSCREEN:
                glfwSetWindowAttrib(window, GLFW_AUTO_ICONIFY, GL_TRUE);

                glfwSetWindowMonitor(window, currentScreen.id, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                break;
            case BORDERLESS_WINDOWED:
                glfwSetWindowAttrib(window, GLFW_AUTO_ICONIFY, GL_FALSE);
                glfwSetWindowAttrib(window, GLFW_DECORATED, GL_FALSE);

                glfwSetWindowMonitor(window, currentScreen.id, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                break;
            case WINDOWED:
                glfwSetWindowAttrib(window, GLFW_DECORATED, GL_TRUE);
                glfwSetWindowAttrib(window, GLFW_AUTO_ICONIFY, GL_FALSE);

                glfwSetWindowMonitor(window, 0, currentScreen.x, currentScreen.y, mode.width(), mode.height(),
                        mode.refreshRate());
                glfwMaximizeWindow(window);
                break;
            default:
                break;
        }
    }

    public static void hideWindow() {
        glfwHideWindow(window);
    }

    public static void showWindow() {
        glfwShowWindow(window);
        glfwFocusWindow(window);
    }

    public static void setScreen(int index) {
        if (screens.size() <= index)
            return;

//        System.out.println("setScreen : " + displayMode.name());

        currentScreen = screens.get(index);
        indexCurrentScreen = index;
    }

    public static void setVsync(boolean vSync) {
        LIMIT_FRAMERATE = !vSync;
        VSYNC_ENABLED = vSync;
        glfwSwapInterval(vSync ? 1 : 0);
    }

    public static void setWindowSize(Resolution resolution) {
        //TODO
        if (indexCurrentScreen != 0)
            glfwSetWindowPos(window, currentScreen.x, currentScreen.y);
        if (displayMode == DisplayMode.BORDERLESS_WINDOWED || displayMode == DisplayMode.FULLSCREEN)
            glfwSetWindowSize(window, currentScreen.resolution.width, currentScreen.resolution.height);
        else
            glfwSetWindowSize(window, currentScreen.resolution.width, currentScreen.resolution.height - 50);
    }

    public static boolean isFramerateLimited() {
        return LIMIT_FRAMERATE;
    }

    public static double getFramerateLimitNS() {
        return FRAMERATE_LIMIT_NS;
    }

    public static class Resolution implements Comparable<Resolution> {

        private final int width;
        private final int height;

        public Resolution(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        @Override
        public String toString() {
            return this.width + " x " + this.height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Resolution that = (Resolution) o;
            return this.width == that.width &&
                    this.height == that.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.width, this.height);
        }

        @Override
        public int compareTo(Resolution o) {
            int compare = Integer.compare(o.width, this.width);
            if (compare != 0)
                return compare;

            return Integer.compare(o.height, this.height);
        }
    }

    public static class Screen {

        public long id;
        int x;
        int y;
        boolean isPrimary;
        public Resolution resolution;
        public Set<Resolution> resolutions;

        public Screen(long id, int x, int y, boolean isPrimary, Set<Resolution> resolutions) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.isPrimary = isPrimary;
            this.resolutions = resolutions;
            this.resolution = resolutions.stream().findFirst().orElse(null);
        }

        @Override
        public String toString() {
            return "Screen{" +
                    "id=" + this.id +
                    ", x=" + this.x +
                    ", y=" + this.y +
                    ", isPrimary=" + this.isPrimary +
                    ", resolution=" + this.resolution +
                    ", resolutions=" + this.resolutions +
                    '}';
        }
    }

    public enum DisplayMode {
        FULLSCREEN,
        BORDERLESS_WINDOWED,
        WINDOWED;

        public static DisplayMode defaultMode() {
            return FULLSCREEN;
        }

        @Override
        public String toString() {
            return name().replace("_", " ");
        }
    }
}
