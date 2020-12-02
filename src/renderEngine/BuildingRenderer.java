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
import items.buildings.BuildingItem;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.InstancedRawModel;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import shaders.BuildingShader;
import terrains.Terrain;
import textures.ModelTexture;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;

public class BuildingRenderer {

    private final BuildingShader shader;

    private boolean displayBoundingBoxes;

    private boolean updateNeeded;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    private final static int MATRIX_SIZE_FLOATS = 4 * 4;

    // TODO: 5000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(5000 * MATRIX_SIZE_FLOATS);

    public boolean areBoundingBoxesDisplayed() {
        return this.displayBoundingBoxes;
    }

    public void switchDisplayBoundingBoxes() {
        this.displayBoundingBoxes = !this.displayBoundingBoxes;
        this.updateNeeded = true;
    }

    public BuildingRenderer(BuildingShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        this.shader.start();
        this.shader.loadProjectionMatrix(projectionMatrix);
        this.shader.stop();
    }

    public void render() {
        if (updateNeeded) {
            entities = new HashMap<>();

//            RawModel ra = OBJLoader.loadObjModel("cube");
//            TexturedModel model = new TexturedModel(ra);
//            List<Entity> list = new ArrayList<>();
//            list.add(new Entity(model, new Vector3f(50, 0, 10), 0, 0, 0, 1));
//            entities.put(model, list);

            final Terrain terrain = Terrain.getInstance();

            final Map<Vector2f, BuildingItem> buildings = terrain.getBuildings();
            buildings.forEach((p, item) -> {
                Vector3f pos = new Vector3f(p.x, 0.05, p.y); // TODO Remplacer y par height


                TexturedModel texture = item.getTexture();
                if (terrain.getPreviewItemPositions() != null && terrain.getPreviewItemPositions().contains(p)) {
                    texture = item.getPreviewTexture();
                }

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

//        Vector3f pos = terrain.getPreviewItemPosition();
//        if (pos != null) { // Preview
//            Item previewItem = terrain.getPreviewItem();
//            handleTexture(entities, pos, previewItem, previewItem.getPreviewTexture());
//        }

            updateNeeded = false;
        }
        entities.entrySet().stream().filter(entry -> entry.getKey() != null).forEach(entry -> {
            floatBuffer.clear();

            TexturedModel texturedModel = entry.getKey();

            final RawModel rawModel = texturedModel.getRawModel();
            if (rawModel instanceof InstancedRawModel) {
                prepareTexturedModel(texturedModel, true);
                int i = 0;
                for (Entity entity : entry.getValue()) {
                    Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                            entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

                    floatBuffer = transformationMatrix
                            .store(i++ * MATRIX_SIZE_FLOATS, floatBuffer);
                }

                glBindBuffer(GL_ARRAY_BUFFER, ((InstancedRawModel) rawModel).getVboID());
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
                glDrawElementsInstanced(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0,
                        entry.getValue().size());
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            } else {
                prepareTexturedModel(texturedModel, false);
                entry.getValue().forEach(entity -> {
                    prepareInstance(entity);
                    GL11.glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
                });
            }
            unbindTexturedModel();
        });
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
