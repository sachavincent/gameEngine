package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import util.math.Matrix4f;
import util.math.Vector4f;

public class MasterRenderer {

    private static final float FOV        = 70;
    public static final  float NEAR_PLANE = 0.1f;
    public static final  float FAR_PLANE  = 1000;

    private static final float RED   = 0.5f;
    private static final float GREEN = 0.5f;
    private static final float BLUE  = 0.5f;

    private Matrix4f projectionMatrix;

    private StaticShader   shader = new StaticShader();
    private EntityRenderer entityRenderer;
    private SkyboxRenderer skyboxRenderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader   terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    private Terrain                          terrain;

    private static MasterRenderer instance;

    public EntityRenderer getEntityRenderer() {
        return this.entityRenderer;
    }

    public static MasterRenderer getInstance() {
        return instance == null ? (instance = new MasterRenderer()) : instance;
    }

    private MasterRenderer() {
        enableCulling();

        createProjectionMatrix();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(Loader.getInstance(), projectionMatrix);
    }

    public void renderScene(List<Entity> entities, Terrain terrain, List<Light> lights,
            Camera camera, Vector4f clipPlane) {
        processTerrain(terrain);
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
        entityRenderer.render(entities, terrain);
        shader.stop();

        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrain);
        terrainShader.stop();


        skyboxRenderer.render(camera);
        entities.clear();
    }

    private void processTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    private void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null)
            batch.add(entity);
        else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    static void enableCulling() {
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
        //TODO
    }


    static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }


    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    private void prepare() {
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