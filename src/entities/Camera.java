package entities;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import renderEngine.DisplayManager;
import util.math.Vector3f;

public class Camera {

    private float distanceFromPlayer = 35;
    private float angleAroundPlayer  = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float    pitch    = 20;
    private float    yaw      = 0;
    private float    roll;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        GLFW.glfwSetScrollCallback(DisplayManager.getWindow(), GLFWScrollCallback.create((window, xoffset, yoffset) -> {
            float zoomLevel = (float) (yoffset * 0.9f);
            distanceFromPlayer -= zoomLevel;
            if (distanceFromPlayer < 5) {
                distanceFromPlayer = 5;
            }
        }));

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
        yaw %= 360;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticDistance + 4;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch + 4)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch + 4)));
    }

}
