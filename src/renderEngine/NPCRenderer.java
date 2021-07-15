package renderEngine;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import entities.Camera;
import entities.Camera.Direction;
import entities.ModelEntity;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import models.Model;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.AnimatedGameObjectShader;
import scene.components.*;
import scene.gameObjects.GameObject;
import util.Vao;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class NPCRenderer extends Renderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    protected final Map<Model, List<ModelEntity>> renderModels = new HashMap<>();

    private static NPCRenderer instance;

    public static NPCRenderer getInstance() {
        return instance == null ? (instance = new NPCRenderer()) : instance;
    }

    private NPCRenderer() {
        super(new AnimatedGameObjectShader());

        this.shader.start();
        ((AnimatedGameObjectShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        this.shader.stop();
    }

    @Override
    public void render() {
        for (Entry<Model, List<ModelEntity>> entry : this.renderModels.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            Model texturedModel = entry.getKey();

            final Vao vao = texturedModel.getVao();
            if (vao.isInstanced()) {
                int i = 0;
                prepareTexturedModel(texturedModel, true);
                for (ModelEntity modelEntity : entry.getValue()) {
                    Matrix4f transformationMatrix = Maths.createTransformationMatrix(modelEntity.getPosition(),
                            modelEntity.getRotation(), modelEntity.getScale());
                    try {
                        floatBuffer = transformationMatrix.store(i++ * 16, floatBuffer);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                glBindBuffer(GL_ARRAY_BUFFER, vao.getInstanceVbo().getId());
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
//                glDrawElementsInstanced(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0,
//                        entry.getValue().size());
                glBindBuffer(GL_ARRAY_BUFFER, 0);

                floatBuffer.clear();
            } else {
                prepareTexturedModel(texturedModel, false);
                entry.getValue().forEach(model -> {
                    prepareInstance(model);
//                    vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
//                    GL11.glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);
//                    vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
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
            ((AnimatedGameObjectShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((AnimatedGameObjectShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((AnimatedGameObjectShader) this.shader)
                    .loadLights(false, LightRenderer.getInstance().getGameObjects(), viewMatrix);
            ((AnimatedGameObjectShader) this.shader).loadViewMatrix(viewMatrix);
        }

        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        if (positionComponent == null)
            return;
        Vector3f position = positionComponent.getPosition();

        SingleModelComponent singleModelComponent = gameObject.getComponent(SingleModelComponent.class);
        if (singleModelComponent == null)
            return;
        Vao vao = singleModelComponent.getModel().getModel().getVao();

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
            Model texture = singleModelComponent.getModel().getModel();
            TransparencyComponent transparencyComponent = gameObject.getComponent(TransparencyComponent.class);
            if (transparencyComponent != null)
                texture.getModelTexture().setAlpha(transparencyComponent.getAlpha());

            DirectionComponent directionComponent = gameObject.getComponent(DirectionComponent.class);
            Direction direction = directionComponent == null ? Direction.NORTH : directionComponent.getDirection();


            if (this.displayBoundingBoxes && gameObject.hasComponent(BoundingBoxComponent.class))
                handleTexture(this.renderModels, new ModelEntity(position, direction, scale,
                        gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox()));
            else
                handleTexture(this.renderModels, new ModelEntity(position, direction, scale, texture));
        }
    }

    private void prepareTexturedModel(Model model, boolean isInstanced) {
        if (model == null)
            throw new IllegalArgumentException("TexturedModel null");
//
//        Vao vao = model.getVao();
//        GL30.glBindVertexArray(vao.getId());
//
//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        GL20.glEnableVertexAttribArray(2);
//        if (isInstanced)
//            GL20.glEnableVertexAttribArray(3);
//
//        ModelTexture texture = model.getModelFile();
//
//        if (texture != null) {
//            ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
//            if (texture.isTransparent())
//                MasterRenderer.disableCulling();
//
//            ((GameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
//            ((GameObjectShader) this.shader).loadUseNormalMap(false);
//            ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//            ((GameObjectShader) this.shader).loadIsInstanced(isInstanced);
//            ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
//            ((GameObjectShader) this.shader).loadColor(texture.getColor());
//
//            GL13.glActiveTexture(GL13.GL_TEXTURE0);
//            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
//
//            if (texture.isTransparent())
//                MasterRenderer.enableCulling(); // Reenable culling
//        } else {
//            GL13.glActiveTexture(GL13.GL_TEXTURE0);
//
//            glBindTexture(GL_TEXTURE_2D, ModelTexture.DEFAULT_MODEL.getTextureID());
//        }
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(ModelEntity modelEntity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(modelEntity.getPosition(),
                modelEntity.getRotation(), modelEntity.getScale());

        if (transformationMatrix == null)
            return;

        ((AnimatedGameObjectShader) this.shader).loadTransformationMatrix(transformationMatrix);
    }
}