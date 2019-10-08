package main.entities;

import static org.lwjgl.glfw.GLFW.*;

import main.renderEngine.DisplayManager;
import main.terrains.Terrain;
import main.util.vector.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);
    private float    pitch    = 90, yaw = 40, roll = 0;
    private float   movementSpeed = 1.4f;
    private boolean middleButtonPressed;

    private double xPos, yPos;

    private float     angleAroundFocus;
    private float     distanceFromFocus;
    private Focusable focus;

    public Camera(Focusable focus) {
        GLFW.glfwSetScrollCallback(DisplayManager.getWindow(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                position.z -= 0.2f;
            }
        });


    }

    public Camera(Terrain terrain, Vector3f position, float temp) {
        this.position = position;
//        this.focus = new TerrainPoint(terrain, position.add(new Vector3f(0, 0, distanceFromFocus)));
        this.focus = new TerrainPoint(terrain, new Vector3f(position.x + 60, position.y, position.z + 60), temp);
        distanceFromFocus = (float) this.focus.get2DDistanceFromCamera(this);
        final int maxZoom = 50;
//        pitch = (float) (Math.exp((distanceFromFocus - maxZoom) / 10));
//        if (pitch > 30)
//            pitch = 30;

        glfwSetKeyCallback(DisplayManager.getWindow(), new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    focus.getPosition().x += movementSpeed * Math.sin(Math.toRadians(180 - yaw));
                    focus.getPosition().z += movementSpeed * Math.cos(Math.toRadians(180 - yaw));
                } else if (key == GLFW_KEY_S && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    focus.getPosition().x -= movementSpeed * Math.sin(Math.toRadians(180 - yaw));
                    focus.getPosition().z -= movementSpeed * Math.cos(Math.toRadians(180 - yaw));
                } else if (key == GLFW_KEY_A && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    focus.getPosition().x += movementSpeed * Math.cos(Math.toRadians(180 - yaw));
                    focus.getPosition().z -= movementSpeed * Math.sin(Math.toRadians(180 - yaw));
                } else if (key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    focus.getPosition().x -= movementSpeed * Math.cos(Math.toRadians(180 - yaw));
                    focus.getPosition().z += movementSpeed * Math.sin(Math.toRadians(180 - yaw));
                }
            }
        });

        GLFW.glfwSetScrollCallback(DisplayManager.getWindow(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (yoffset == -1.0) {
                    distanceFromFocus += 1.6f;
                    position.y += 3;
                } else if (yoffset == 1.0 && distanceFromFocus >= maxZoom) {
                    distanceFromFocus -= 1.6f;
                    if (distanceFromFocus < maxZoom)
                        distanceFromFocus = maxZoom;
                    if (position.y > (focus.getPosition().y + focus.getHeight()))
                        position.y -= 3;
                }
//                pitch = (float) (Math.exp((distanceFromFocus - maxZoom) / 10));zz
//                if (pitch > 30)
//                    pitch = 30;
            }
        });

//        GLFW.glfwSetMouseButtonCallback(DisplayManager.getWindow(), new GLFWMouseButtonCallback() {
//            @Override
//            public void invoke(long window, int button, int action, int mods) {
//                if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
//                    if (action == GLFW.GLFW_PRESS) {
//                        middleButtonPressed = true;
//                    } else if (action == GLFW.GLFW_RELEASE) {
//                        middleButtonPressed = false;
//                    }
//                }
//            }
//        });

//        GLFW.glfwSetCursorPosCallback(DisplayManager.getWindow(), new GLFWCursorPosCallback() {
//            @Override
//            public void invoke(long window, double xpos, double ypos) {
//                if (middleButtonPressed) {
//                    if (xPos < xpos) {
//                        angleAroundFocus += 1;
//                        if (angleAroundFocus >= 360)
//                            angleAroundFocus %= 360;
//                    } else if (xPos > xpos) {
//                        angleAroundFocus -= 1;
//                        if (angleAroundFocus <= 0)
//                            angleAroundFocus += 360;
//                    }
//
////                    if (yPos < ypos) {
////                        pitch += 1;
////                        if (pitch >= 360)
////                            pitch %= 360;
////                    } else if (yPos > ypos) {
////                        pitch -= 1;
////                        if (pitch <= 0)
////                            pitch += 360;
////                    }
//                    xPos = xpos;
//                    yPos = ypos;
//                }
//            }
//        });
    }

    public void move() {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);

        this.yaw = 180 - angleAroundFocus;
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        position.x = focus.getPosition().x - (float) (horizDistance * Math.sin(Math.toRadians(angleAroundFocus)));
        position.z = focus.getPosition().z - (float) (horizDistance * Math.cos(Math.toRadians(angleAroundFocus)));
//        if (middleButtonPressed)
//            position.y = focus.getPosition().y + verticDistance;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getRoll() {
        return this.roll;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromFocus * Math.cos(Math.toRadians(20)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromFocus * Math.sin(Math.toRadians(20)));
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public void setMiddleButtonPressed(boolean middleButtonPressed) {
        this.middleButtonPressed = middleButtonPressed;
    }
}
