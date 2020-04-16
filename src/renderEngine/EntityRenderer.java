package renderEngine;

import entities.Entity;
import items.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class EntityRenderer {

    private StaticShader shader;

    private boolean displayBoundingBoxes;

    public boolean areBoundingBoxesDisplayed() {
        return this.displayBoundingBoxes;
    }

    public void switchDisplayBoundingBoxes() {
        this.displayBoundingBoxes = !this.displayBoundingBoxes;
    }

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities, Terrain terrain) {
        terrain.getItems().forEach((pos, item) -> {
            TexturedModel texture = item.getTexture();
            handleTexture(entities, pos, item, texture);
            if (displayBoundingBoxes) {
                texture = item.getBoundingBox();

                handleTexture(entities, pos, item, texture);
            }
            if (item.isSelected()) {
                texture = item.getSelectionBox();

                handleTexture(entities, pos, item, texture);
            }
        });

        entities.forEach((texturedModel, batch) -> {
            prepareTexturedModel(texturedModel);

            batch.forEach(entity -> {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            });

            unbindTexturedModel();
        });
    }

    private void handleTexture(Map<TexturedModel, List<Entity>> entities, Vector3f pos, Item item,
            TexturedModel texture) {
        if (!entities.containsKey(texture))
            entities.put(texture, new ArrayList<>());

        entities.get(texture).add(new Entity(texture, pos, 0,
                item.getRotation(), 0, item.getScale(), 3));
    }

    private void prepareTexturedModel(TexturedModel texturedModel) {
        if (texturedModel == null)
            throw new IllegalArgumentException("TexturedModel null");

        RawModel model = texturedModel.getRawModel();

        GL30.glBindVertexArray(model.getVaoID());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        ModelTexture texture = texturedModel.getModelTexture();

        if (texture != null) {
            shader.loadNumberOfRows(texture.getNumberOfRows());
            if (texture.isTransparent())
                MasterRenderer.disableCulling();

            shader.loadFakeLightingVariable(texture.doesUseFakeLighting());
            shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModelTexture.DEFAULT_MODEL.getTextureID());
        }
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

        if (transformationMatrix == null)
            return;

        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

}
