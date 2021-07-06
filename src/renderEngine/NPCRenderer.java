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

import entities.Camera;
import entities.Camera.Direction;
import entities.Model;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import scene.components.*;
import scene.gameObjects.GameObject;
import textures.ModelTexture;
import util.Vao;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class NPCRenderer extends Renderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    protected final Map<TexturedModel, List<Model>> renderModels = new HashMap<>();

    private static NPCRenderer instance;

    public static NPCRenderer getInstance() {
        return instance == null ? (instance = new NPCRenderer()) : instance;
    }

    private NPCRenderer() {
        super(new GameObjectShader());

        this.shader.start();
        ((GameObjectShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        this.shader.stop();
    }

    @Override
    public void render() {
        for (Entry<TexturedModel, List<Model>> entry : this.renderModels.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            TexturedModel texturedModel = entry.getKey();

            final Vao vao = texturedModel.getVao();
            if (vao.isInstanced()) {
                int i = 0;
                prepareTexturedModel(texturedModel, true);
                for (Model model : entry.getValue()) {
                    Matrix4f transformationMatrix = Maths.createTransformationMatrix(model.getPosition(),
                            model.getRotation(), model.getScale());
                    try {
                        floatBuffer = transformationMatrix.store(i++ * 16, floatBuffer);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                glBindBuffer(GL_ARRAY_BUFFER, vao.getInstanceVbo().getId());
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
                glDrawElementsInstanced(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0,
                        entry.getValue().size());
                glBindBuffer(GL_ARRAY_BUFFER, 0);

                floatBuffer.clear();
            } else {
                prepareTexturedModel(texturedModel, false);
                entry.getValue().forEach(model -> {
                    prepareInstance(model);
                    GL11.glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);
                });
            }
            unbindTexturedModel();
        }

        this.renderModels.clear();
        this.shader.stop();
        glDisable(GL_BLEND);
    }

    @Override
    public void prepareRender(GameObject gameObject) {
        if (!this.shader.isStarted()) {
            glEnable(GL_BLEND);
            this.shader.start();
            Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
            ((GameObjectShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((GameObjectShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((GameObjectShader) this.shader)
                    .loadLights(false, LightRenderer.getInstance().getGameObjects(), viewMatrix);
            ((GameObjectShader) this.shader).loadViewMatrix(viewMatrix);
        }

        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        if (positionComponent == null)
            return;
        Vector3f position = positionComponent.getPosition();

        SingleModelComponent singleModelComponent = gameObject.getComponent(SingleModelComponent.class);
        if (singleModelComponent == null)
            return;
        Vao vao = singleModelComponent.getModel().getTexturedModel().getVao();

        ScaleComponent scaleComponent = gameObject.getComponent(ScaleComponent.class);
        float scale = 1;
        if (scaleComponent != null)
            scale = scaleComponent.getScale();

        PathComponent pathComponent = gameObject.getComponent(PathComponent.class);
        if (pathComponent != null) {

        }

        if (gameObject.hasComponent(OffsetComponent.class))
            position = position.add(gameObject.getComponent(OffsetComponent.class).getOffset());

        float boundingRadius = scale * vao.getMax().sub(vao.getMin()).scale(0.5f).length();

        if (FrustumCullingFilter.isPosInsideFrustum(position, boundingRadius)) {
            TexturedModel texture = singleModelComponent.getModel().getTexturedModel();
            TransparencyComponent transparencyComponent = gameObject.getComponent(TransparencyComponent.class);
            if (transparencyComponent != null)
                texture.getModelTexture().setAlpha(transparencyComponent.getAlpha());

            DirectionComponent directionComponent = gameObject.getComponent(DirectionComponent.class);
            Direction direction = directionComponent == null ? Direction.NORTH : directionComponent.getDirection();


            if (this.displayBoundingBoxes && gameObject.hasComponent(BoundingBoxComponent.class))
                handleTexture(this.renderModels, new Model(position, direction, scale,
                        gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox()));
            else
                handleTexture(this.renderModels, new Model(position, direction, scale, texture));
        }
    }

    private void prepareTexturedModel(TexturedModel texturedModel, boolean isInstanced) {
        if (texturedModel == null)
            throw new IllegalArgumentException("TexturedModel null");

        Vao vao = texturedModel.getVao();
        GL30.glBindVertexArray(vao.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (isInstanced)
            GL20.glEnableVertexAttribArray(3);

        ModelTexture texture = texturedModel.getModelTexture();

        if (texture != null) {
            ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
            if (texture.isTransparent())
                MasterRenderer.disableCulling();

            ((GameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
            ((GameObjectShader) this.shader).loadUseNormalMap(false);
            ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
            ((GameObjectShader) this.shader).loadIsInstanced(isInstanced);
            ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
            ((GameObjectShader) this.shader).loadColor(texture.getColor());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Model model) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(model.getPosition(),
                model.getRotation(), model.getScale());

        if (transformationMatrix == null)
            return;

        ((GameObjectShader) this.shader).loadTransformationMatrix(transformationMatrix);
        ((GameObjectShader) this.shader).loadOffset(model.getTextureXOffset(), model.getTextureYOffset());
    }
}