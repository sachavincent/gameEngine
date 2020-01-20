package renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengles.GLES20.GL_NICEST;

import java.awt.Toolkit;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import util.Timer;

public class DisplayManager {

    public static int WIDTH              = 1280, HEIGHT = 720, FPS = 60;

    private static double  lastFrameTime;
    private static double  delta;
    private static long    window;
    private static boolean fullscreen;

    private static List<Long> screens;

    public static void createDisplay() {
        screens = new ArrayList<>();

        fullscreen = false;

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
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
        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Tests",
                fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        glfwMaximizeWindow(window);

        if (window == 0)
            throw new RuntimeException("Failed to create window");

        glfwGetWindowSize(window, w, h);
        WIDTH = w.get(0);
        HEIGHT = h.get(0);

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        lastFrameTime = getCurrentTime();
        glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WIDTH = width;
                HEIGHT = height;
                glViewport(0, 0, WIDTH, HEIGHT);
            }
        });
    }

    private static void getScreens() {
        PointerBuffer pointerBuffer = glfwGetMonitors();
        if (pointerBuffer == null)
            return; //TODO: raise exception

        int i = 0;

        while (true) {
            try {
                screens.add(pointerBuffer.get(i));
                i++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public static void updateDisplay() {
        if (window == 0)
            return;

        glfwPollEvents();
        glfwSwapBuffers(window);

        double currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;

        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        Timer.TASKS.values().forEach(timerTasks -> timerTasks.forEach(TimerTask::cancel));
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
}
