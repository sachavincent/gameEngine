package renderEngine;

import entities.Camera;
import entities.ModelEntity;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.AnimatedGameObjectShader;
import util.math.Matrix4f;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15C.*;
import static renderEngine.MasterRenderer.*;

public class NPCRenderer extends GameObjectRenderer<AnimatedGameObjectShader> {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    private static NPCRenderer instance;

    public static NPCRenderer getInstance() {
        return instance == null ? (instance = new NPCRenderer()) : instance;
    }

    private NPCRenderer() {
        super(new AnimatedGameObjectShader(), s -> {
            s.loadClipPlane(CLIP_PLANE);
            s.loadSkyColor(RED, GREEN, BLUE);
            s.loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        });
    }

    @Override
    protected void doPreRender() {
        glEnable(GL_BLEND);
        Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
        this.shader.loadLights(false, viewMatrix);
        this.shader.loadViewMatrix(viewMatrix);
    }

    @Override
    protected void doRender(Set<Entry<AbstractModel, List<ModelEntity>>> entrySet) {
        for (Entry<AbstractModel, List<ModelEntity>> entry : entrySet) {
            if (entry == null || entry.getKey() == null)
                continue;

            AbstractModel model = entry.getKey();

            final Vao vao = model.getVao();
            vao.bind();

            if (vao.isInstanced()) {
                int i = 0;
                prepareTexturedModel(model, true);
                for (ModelEntity modelEntity : entry.getValue()) {
                    Matrix4f transformationMatrix = modelEntity.getTransformationMatrix();
                    try {
                        floatBuffer = transformationMatrix.store(i++ * 16, floatBuffer);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                Vbo instanceVbo = vao.getInstanceVbo();
                instanceVbo.bind();
                glBindBuffer(GL_ARRAY_BUFFER, instanceVbo.getId());
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
//                glDrawElementsInstanced(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0,
//                        entry.getValue().size());
                glBindBuffer(GL_ARRAY_BUFFER, 0);

                instanceVbo.unbind();
                vao.unbind();
                floatBuffer.clear();
            } else {
                prepareTexturedModel(model, false);
                entry.getValue().forEach(modelEntity -> {
                    prepareInstance(modelEntity);
                    vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
                    GL11.glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);
                    vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
                });
                vao.unbind();
            }
            unbindTexturedModel();
        }

        glDisable(GL_BLEND);
    }

    private void prepareTexturedModel(AbstractModel model, boolean isInstanced) {
        if (model == null)
            throw new IllegalArgumentException("TexturedModel null");

//        Vao vao = model.getVao();
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}