package renderEngine;

import java.util.List;
import java.util.Map;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import util.Maths;
import util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class EntityRenderer {

    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities) {
        entities.keySet().forEach(texturedModel -> {
            prepareTexturedModel(texturedModel);

            List<Entity> batch = entities.get(texturedModel);
            batch.forEach(entity -> {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            });

            unbindTexturedModel();
        });
    }

    private void prepareTexturedModel(TexturedModel texturedModel) {
        RawModel model = texturedModel.getRawModel();

        GL30.glBindVertexArray(model.getVaoID());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        ModelTexture texture = texturedModel.getModelTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isTransparent())
            MasterRenderer.disableCulling();

        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        if (texturedModel.getModelTexture() != null)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getModelTexture().getTextureID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        if(transformationMatrix == null)
            return;

        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

}
