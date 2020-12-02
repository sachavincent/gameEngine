package renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_ALIASED_LINE_WIDTH_RANGE;

import de.matthiasmann.twl.utils.PNGDecoder;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import util.Timer;

public class DisplayManager {

    public static int WIDTH = 2560, HEIGHT = 1440, FPS = 1440;

    public static  float   MIN_LINE_WIDTH;
    public static  float   MAX_LINE_WIDTH;
    private static double  lastFrameTime;
    private static double  delta;
    private static long    window;
    private static boolean fullscreen;

    private static List<Long> screens;

    private static GLFWErrorCallback           callback;
    private static GLFWFramebufferSizeCallback callback2;

    public static void createDisplay() {
        screens = new ArrayList<>();

        fullscreen = false;

        callback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_AUTO_ICONIFY, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);
//        glfwWindowHint(GLFW_DOUBLEBUFFER, GL_FALSE);
        //glfwWindowHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); //TODO: temp ?

        glfwWindowHint(GLFW_SAMPLES, 8);
//        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);

        getScreens();

//        Scanner scanner = new Scanner(System.in);
//        int screen = scanner.nextInt();
//        if(screen >= screens.size())
//            return;

        int screen = 1; //TODO temp
//        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Tests",
//                fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Tests",
                screens.get(1), 0);
//        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Tests",
//                0, 0);
        if (window == 0)
            throw new RuntimeException("Failed to create window");
//        glfwMaximizeWindow(window);
        glfwGetWindowSize(window, w, h);
        WIDTH = w.get(0);
        HEIGHT = h.get(0);

        w.clear();
        h.clear();
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
//        glfwSwapInterval(1);
        GL.createCapabilities();
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        glfwShowWindow(window);
        lastFrameTime = getCurrentTime();
        glfwSetFramebufferSizeCallback(window, (callback2 = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WIDTH = width;
                HEIGHT = height;
                glViewport(0, 0, WIDTH, HEIGHT);
            }
        }));
        GLFWImage image = GLFWImage.malloc();
        try {
            PNGDecoder dec = new PNGDecoder(new FileInputStream("res/insula_preview.png"));
            int width = dec.getWidth();
            int height = dec.getHeight();
            ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
            dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            image.set(width, height, buf);
            GLFWImage.Buffer images = GLFWImage.malloc(1);
            images.put(0, image);

            glfwSetWindowIcon(window, images);
            buf.clear();
            images.free();
            image.free();
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] lineWidthRange = new float[2];
        GL30.glGetFloatv(GL_ALIASED_LINE_WIDTH_RANGE, lineWidthRange);
        MIN_LINE_WIDTH = lineWidthRange[0];
        MAX_LINE_WIDTH = lineWidthRange[1];

//        System.out.println("min: " + MIN_LINE_WIDTH);
//        System.out.println("max: " + MAX_LINE_WIDTH);
    }

    private static void getScreens() {
        PointerBuffer pointerBuffer = glfwGetMonitors();
        if (pointerBuffer == null)
            return; //TODO: raise exception

        int i = 0;

        while (true) {
            try {
                screens.add(pointerBuffer.get(i++));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public static void updateDisplay() {
        if (window == 0)
            return;

//        glfwPollEvents();
//        glfwSwapBuffers(window);

        double currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;

        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        Timer.TASKS.values().forEach(timerTasks -> timerTasks.forEach(Timer::cancelTask));
        Timer.MISC_TASKS.forEach(Timer::cancelTask);
        Timer.TIMER.cancel();

        glfwSetWindowShouldClose(window, true);
    }

    private static double getCurrentTime() {
        return glfwGetTime() * 1000;
    }

    public static double getFrameTimeSeconds() {
        return delta;
    }

    public static long getWindow() {
        return window;
    }

    public static void freeCallbacks() {
        if (callback != null)
            callback.free();
        if (callback2 != null)
            callback2.free();
    }
}
