package main.renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.entities.Camera;
import main.entities.Entity;
import main.entities.Light;
import main.models.TexturedModel;
import main.shaders.StaticShader;
import main.shaders.TerrainShader;
import main.skybox.SkyboxRenderer;
import main.terrains.Terrain;
import main.util.vector.Matrix4f;
import main.util.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class MasterRenderer {

    private static final float FOV        = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE  = 1000;

    private static final float RED   = 0.5f;
    private static final float GREEN = 0.5f;
    private static final float BLUE  = 0.5f;

    private Matrix4f projectionMatrix;

    private StaticShader   shader = new StaticShader();
    private EntityRenderer renderer;
    private SkyboxRenderer skyboxRenderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader   terrainShader = new TerrainShader();


    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain>                    terrains = new ArrayList<>();

    public MasterRenderer(Loader loader) {
        enableCulling();

        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }

    public void renderScene(List<Entity> entities, List<Terrain> terrains, List<Light> lights, Camera camera,
            Vector4f clipPlane) {
        terrains.forEach(this::processTerrain);
        entities.forEach(this::processEntity);

        render(lights, camera, clipPlane);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        prepare();
        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera);
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }


    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }


    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
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