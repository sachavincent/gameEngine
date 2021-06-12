package renderEngine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import entities.Camera.Direction;
import entities.Entity;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import scene.components.*;
import scene.gameObjects.GameObject;
import terrains.TerrainPosition;
import textures.ModelTexture;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class BuildingRenderer extends Renderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    protected final Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    private static BuildingRenderer instance;

    public static BuildingRenderer getInstance() {
        return instance == null ? (instance = new BuildingRenderer()) : instance;
    }

    private BuildingRenderer() {
        super(new GameObjectShader());

        this.shader.start();
        ((GameObjectShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        ((GameObjectShader) this.shader).connectTextureUnits();
        this.shader.stop();
    }

    @Override
    public void render() {
        for (Entry<TexturedModel, List<Entity>> entry : this.entities.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            TexturedModel texturedModel = entry.getKey();

            final RawModel rawModel = texturedModel.getRawModel();
            if (rawModel.isInstanced()) {
                int i = 0;
                prepareTexturedModel(texturedModel, true);
                for (Entity entity : entry.getValue()) {
                    Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                            entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
                    try {
                        floatBuffer = transformationMatrix.store(i++ * 16, floatBuffer);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                glBindBuffer(GL_ARRAY_BUFFER, rawModel.getVboID());
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
                glDrawElementsInstanced(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0,
                        entry.getValue().size());
                glBindBuffer(GL_ARRAY_BUFFER, 0);

                floatBuffer.clear();
            } else {
                prepareTexturedModel(texturedModel, false);
                entry.getValue().forEach(entity -> {
                    prepareInstance(entity);
                    GL11.glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
                });
            }
            unbindTexturedModel();
        }

        this.entities.clear();
        this.shader.stop();
        glDisable(GL_BLEND);
    }

    @Override
    public void prepareRender(GameObject gameObject) {
        if (!this.shader.isStarted()) {
            glEnable(GL_BLEND);
            this.shader.start();

            Matrix4f viewMatrix = Maths.createViewMatrix();
            ((GameObjectShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((GameObjectShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((GameObjectShader) this.shader).loadLights(LightRenderer.getInstance().getGameObjects(), viewMatrix);
            ((GameObjectShader) this.shader).loadViewMatrix(viewMatrix);
        }

        TerrainPosition position = null;
        TexturedModel texture = null;
        PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
        if (previewComponent != null) {
            position = previewComponent.getPreviewPosition(); // = null if no preview
            texture = previewComponent.getTexture();
        }
        if (position == null) { // If there's no preview
            PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
            if (positionComponent != null)
                position = positionComponent.getPosition().toTerrainPosition();
        }
        if (position == null)
            return;
        TextureComponent textureComponent = gameObject.getComponent(TextureComponent.class);

        RawModel rawModel = textureComponent.getTexture().getRawModel();
        if (rawModel == null)
            return;

        ScaleComponent scaleComponent = gameObject.getComponent(ScaleComponent.class);
        float scale = 1;
        if (scaleComponent != null)
            scale = scaleComponent.getScale();

        float boundingRadius = scale * rawModel.getMax().sub(rawModel.getMin()).scale(0.5f).length();

        Vector3f pos = position.toVector3f();
        if (gameObject.hasComponent(OffsetComponent.class))
            pos = pos.add(gameObject.getComponent(OffsetComponent.class).getOffset());

        if (FrustumCullingFilter.isPosInsideFrustum(pos, boundingRadius)) {
            if (texture == null)
                texture = textureComponent.getTexture();
            DirectionComponent directionComponent = gameObject.getComponent(DirectionComponent.class);
            Direction direction = directionComponent == null ? Direction.NORTH : directionComponent.getDirection();

            if (this.displayBoundingBoxes && gameObject.hasComponent(BoundingBoxComponent.class))
                handleTexture(this.entities, pos, direction, scale,
                        gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox());
            else if (!this.displayBoundingBoxes)
                handleTexture(this.entities, pos, direction, scale, texture);
        }
    }

    private void prepareTexturedModel(TexturedModel texturedModel, boolean isInstanced) {
        if (texturedModel == null)
            throw new IllegalArgumentException("TexturedModel null");

        RawModel model = texturedModel.getRawModel();
        boolean areTangentsOn = model.areTangentsOn();

        GL30.glBindVertexArray(model.getVaoID());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (areTangentsOn)
            GL20.glEnableVertexAttribArray(3);
        if (isInstanced)
            GL20.glEnableVertexAttribArray(4);

        ModelTexture texture = texturedModel.getModelTexture();

        if (texture != null) {
            ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
            if (texture.isTransparent())
                MasterRenderer.disableCulling();

            ((GameObjectShader) this.shader).loadFakeLightingVariable(texture.doesUseFakeLighting());
            ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
            ((GameObjectShader) this.shader).loadIsInstanced(isInstanced);
            ((GameObjectShader) this.shader).loadAreTangentsOn(areTangentsOn);
            ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
            ((GameObjectShader) this.shader).loadColor(texture.getColor());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL13.glBindTexture(GL_TEXTURE_2D, texture.getNormalMap());

            if (texture.isTransparent())
                MasterRenderer.enableCulling(); // Reenable culling
        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            glBindTexture(GL_TEXTURE_2D, ModelTexture.DEFAULT_MODEL.getTextureID());
        }
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        if (transformationMatrix == null)
            return;

        ((GameObjectShader) this.shader).loadTransformationMatrix(transformationMatrix);
        ((GameObjectShader) this.shader).loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }
}