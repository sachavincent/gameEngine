package main.renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Toolkit;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

public class DisplayManager {

    public static int WIDTH              = Toolkit.getDefaultToolkit().getScreenSize().width, HEIGHT = Toolkit
            .getDefaultToolkit()
            .getScreenSize().height, FPS = 60;

    private static double  lastFrameTime;
    private static double  delta;
    private static long    window;
    private static boolean fullscreen;

    private static List<Long> screens;

    public static void createDisplay() {
        screens = new ArrayList<>();

        fullscreen = false;

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
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
                fullscreen ? glfwGetPrimaryMonitor() : screens.get(screen), 0);
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
        boucle:
        while (true) {
            try {
                screens.add(pointerBuffer.get(i));
                i++;
            } catch (IndexOutOfBoundsException e) {
                break boucle;
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
        glfwDestroyWindow(window);
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
