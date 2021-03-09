package renderEngine;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;

import entities.Entity;
import items.Item;
import items.PlaceHolderItem;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import shaders.ItemShader;
import terrains.Terrain;
import terrains.TerrainPosition;
import textures.ModelTexture;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class ItemRenderer {

    private final ItemShader shader;

    private boolean displayBoundingBoxes;

    private boolean updateNeeded;

    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    public boolean areBoundingBoxesDisplayed() {
        return this.displayBoundingBoxes;
    }

    public void switchDisplayBoundingBoxes() {
        this.displayBoundingBoxes = !this.displayBoundingBoxes;
        this.updateNeeded = true;
    }

    public ItemRenderer(ItemShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        this.shader.start();
        this.shader.loadProjectionMatrix(projectionMatrix);
        this.shader.stop();
    }

    public void render() {
        final Terrain terrain = Terrain.getInstance();
        if (updateNeeded) {
            entities.clear();

            List<Item> items = terrain.getItems().stream()
                    .filter(item -> !(item instanceof PlaceHolderItem))
                    .filter(Item::isInsideFrustum).collect(Collectors.toList());
            items.forEach(item -> {
                TerrainPosition p = item.getPosition();
                Vector3f pos = new Vector3f(p.getX(), 0.05, p.getZ()); // TODO Remplacer y par height


                TexturedModel texture = item.getTexture();
//                if (terrain.getPreviewedItem() != null && terrain.getPreviewItemPositions().contains(p)) {
//                    texture = item.getPreviewTexture();
//                }

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


            if (terrain.getPreviewedItem() != null) {
                Item previewItem = terrain.getPreviewedItem().getPreviewItem();
                Set<TerrainPosition> previewItemPositions = terrain.getPreviewItemPositions();
                previewItemPositions.forEach(position -> {
                    TexturedModel previewTexture = previewItem.getPreviewTexture();
                    Vector3f pos = new Vector3f(position.getX(), 0.05, position.getZ()); // TODO Remplacer y par height
                    handleTexture(entities, pos, previewItem, previewTexture);
                });
            }
            updateNeeded = false;
        }

        for (Entry<TexturedModel, List<Entity>> entry : entities.entrySet()) {
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
    }

    private void handleTexture(Map<TexturedModel, List<Entity>> entities, Vector3f pos, Item item,
            TexturedModel texture) {
        if (!entities.containsKey(texture))
            entities.put(texture, new ArrayList<>());

        entities.get(texture).add(new Entity(texture, pos, 0,
                item.getFacingDirection().getDegree(), 0, item.getScale(), 3));
    }

    private void prepareTexturedModel(TexturedModel texturedModel, boolean isInstanced) {
        if (texturedModel == null)
            throw new IllegalArgumentException("TexturedModel null");

        RawModel model = texturedModel.getRawModel();

        GL30.glBindVertexArray(model.getVaoID());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (isInstanced)
            GL20.glEnableVertexAttribArray(3);

        ModelTexture texture = texturedModel.getModelTexture();

        if (texture != null) {
            shader.loadNumberOfRows(texture.getNumberOfRows());
            if (texture.isTransparent())
                MasterRenderer.disableCulling();

            shader.loadFakeLightingVariable(texture.doesUseFakeLighting());
            shader.loadDirectionalColor(texture.doesUseDirectionalColor());
            shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
            shader.loadIsInstanced(isInstanced);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            if (texture.isTransparent())
                MasterRenderer.enableCulling();
        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            glBindTexture(GL_TEXTURE_2D, ModelTexture.DEFAULT_MODEL.getTextureID());
        }
//        GL30.glBindVertexArray(model.getVaoID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);

        GL30.glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        if (transformationMatrix == null)
            return;

        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }
}
