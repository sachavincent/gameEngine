package renderEngine;

import org.lwjgl.opengl.GL11;
import util.math.Matrix4f;
import util.math.Vector4f;

public class MasterRenderer {

    private static final float FOV = 70;//TODO:Config
    public static final float NEAR_PLANE = 0.5f;
    public static final float FAR_PLANE = 1000;

    public static final float RED = 0.5f;
    public static final float GREEN = 0.5f;
    public static final float BLUE = 0.5f;

    private Matrix4f projectionMatrix;

    private static MasterRenderer instance;
    private static Vector4f clipPlane;

    public static void setClipPlane(Vector4f clipPlane) {
        MasterRenderer.clipPlane = clipPlane;
    }

    public static MasterRenderer getInstance() {
        return instance == null ? (instance = new MasterRenderer()) : instance;
    }

    private MasterRenderer() {
        enableCulling();

        createProjectionMatrix();
    }

    public static Vector4f getClipPlane() {
        return clipPlane;
    }

    /**
     * Doesn't render normals facing away from the camera
     */
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void cleanUp() {
        TerrainRenderer.getInstance().cleanUp();
        BuildingRenderer.getInstance().cleanUp();
        SkyboxRenderer.getInstance().cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}