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
import entities.Entity;
import entities.Model;
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
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import scene.Scene;
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

    protected final Map<TexturedModel, List<Model>> renderModels       = new HashMap<>();
    private final   Map<Integer, Entity>            renderableEntities = new HashMap<>();

    private final Query query;

    private static BuildingRenderer instance;

    public static BuildingRenderer getInstance() {
        return instance == null ? (instance = new BuildingRenderer()) : instance;
    }

    private BuildingRenderer() {
        super(new GameObjectShader());
        this.query = new Query(GL33.GL_ANY_SAMPLES_PASSED);
        this.shader.start();
        ((GameObjectShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        ((GameObjectShader) this.shader).connectTextureUnits();
        this.shader.stop();
    }

    public void doZPass(Set<GameObject> gameObjects) {
        Set<GameObject> newGameObjects = this.query.initNewGameObjects(gameObjects);

        GameObject gameObject;
        if (this.query.isResultReady() || !this.query.isInUse()) {
            int visibleSamples = this.query.getResult();
            int id = this.query.getCandidateId();
            Entity candidateEntity = this.query.getCandidateEntity();
            if (visibleSamples > 0 && (!this.renderableEntities.containsKey(id) ||
                    !this.renderableEntities.get(id).equals(candidateEntity))) {
                this.renderableEntities.put(id, candidateEntity);
            } else if (visibleSamples == 0 && this.renderableEntities.containsKey(id)) {
                this.renderableEntities.remove(id);
            }
        }
        newGameObjects.forEach(newGameObject -> {
            Entity entityFromGameObject = createEntityFromGameObject(newGameObject);
            if (entityFromGameObject != null)
                this.renderableEntities.put(newGameObject.getId(), entityFromGameObject);
        });
        if (!this.query.isInUse()) {
            glColorMask(false, false, false, false);
            glDepthMask(false);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
            this.shader.start();
            Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
            ((GameObjectShader) this.shader).loadViewMatrix(viewMatrix);
            gameObject = this.query.getBestUpdateCandidate();
            if (gameObject != null && gameObject.hasComponent(BoundingBoxComponent.class)) {
                boolean displayBoundingBoxes = this.displayBoundingBoxes;
                this.displayBoundingBoxes = true;
                Entity entity = createEntityFromGameObject(gameObject);
                if (entity != null) {
                    Entity zPassCopy = new Entity(entity);
                    zPassCopy.getModels().forEach(model -> model.setScale(model.getScale() * 1.5f));

                    this.displayBoundingBoxes = displayBoundingBoxes;
                    if (!displayBoundingBoxes) {
                        entity = createEntityFromGameObject(gameObject);
                    }
                    this.query.start(gameObject.getId(), entity);
                    zPassCopy.getModels().forEach(zPassModel -> {
                        prepareTexturedModel(zPassModel.getTexturedModel(), false);
                        Entity zPassModelEntity = new Entity(zPassCopy);
                        prepareInstance(zPassModel);
                        glDrawElements(GL_TRIANGLES, zPassModel.getTexturedModel().getRawModel().getVertexCount(),
                                GL_UNSIGNED_INT, 0);
                    });
                    this.query.stop();
                }
            }
            this.shader.stop();
            glColorMask(true, true, true, true);
            glDepthMask(true);
        }
        this.renderableEntities.entrySet().removeIf(entry -> entry.getValue().isPreview() &&
                !Scene.getInstance().getPreviewedGameObjects().containsKey(entry.getKey()));
        this.renderableEntities.forEach((id, entity) -> {
            addModelToRender(entity);
        });
    }

    public void removeGameObject(GameObject gameObject) {
        this.renderableEntities.remove(gameObject.getId());
        this.query.getLastUpdateTimes().remove(gameObject.getId());
    }

    private void addModelToRender(Entity entity) {
        entity.getModels().forEach(model -> {
            TexturedModel texturedModel = model.getTexturedModel();
            if (!this.renderModels.containsKey(texturedModel))
                this.renderModels.put(texturedModel, new ArrayList<>());
            this.renderModels.get(texturedModel).add(model);
        });
    }

    private Entity createEntityFromGameObject(GameObject gameObject) {
        if (gameObject == null)
            return null;

        if (this.displayBoundingBoxes && !gameObject.hasComponent(BoundingBoxComponent.class))
            return null;

        TerrainPosition position = null;
        PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
        boolean preview = false;
        if (previewComponent != null && previewComponent.getPreviewPosition() != null) {
            Vector3f pos = previewComponent.getPreviewPosition(); // = null if no preview
            if (pos != null) {
                preview = true;
                position = pos.toTerrainPosition();
            }
        } else if (positionComponent != null)
            position = positionComponent.getPosition().toTerrainPosition();

        if (position == null)
            return null;
        Vector3f pos = position.toVector3f();
        if (gameObject.hasComponent(OffsetComponent.class))
            pos = pos.add(gameObject.getComponent(OffsetComponent.class).getOffset());

        float scale = gameObject.hasComponent(ScaleComponent.class) ? gameObject
                .getComponent(ScaleComponent.class).getScale() : 1;
        Direction direction =
                gameObject.hasComponent(DirectionComponent.class) ? gameObject.getComponent(DirectionComponent.class)
                        .getDirection() : Direction.defaultDirection();

        Entity entity;
        if (gameObject.hasComponent(SingleModelComponent.class) || this.displayBoundingBoxes) {
            TexturedModel model;
            if (preview)
                model = previewComponent.getTexture();
            else if (this.displayBoundingBoxes)
                model = gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox();
            else
                model = gameObject.getComponent(SingleModelComponent.class).getModel().getTexturedModel();

            entity = new Entity(new Model(pos, direction, scale, model));
        } else if (gameObject.hasComponent(MultipleModelsComponent.class)) {
            if (preview)
                entity = new Entity(new Model(pos, direction, scale, previewComponent.getTexture()));
            else {
                Vector3f finalPos = pos;
                MultipleModelsComponent multipleModelsComponent = gameObject
                        .getComponent(MultipleModelsComponent.class);
                Map<String, Model> concurrentModels = multipleModelsComponent.getConcurrentModels();
                List<Model> models = concurrentModels.values().stream().map(Model::new).peek(model -> {
                    Vector3f modelPosition = model.getPosition();
                    switch (direction) {
                        case NORTH:
                            break;
                        case WEST:
                            modelPosition = new Vector3f(-modelPosition.x, modelPosition.y, modelPosition.z);
                            break;
                        case SOUTH:
                            modelPosition = new Vector3f(-modelPosition.x, modelPosition.y, -modelPosition.z);
                            break;
                        case EAST:
                            modelPosition = new Vector3f(modelPosition.x, modelPosition.y, -modelPosition.z);
                            break;
                    }
                    model.setPosition(modelPosition.add(finalPos));
                    model.setScale(model.getScale() + scale);
                    if (!model.isFixedRotation()) {
                        model.setRotation(model.getRotation().add(new Vector3f(0, direction.getDegree(), 0)));
                    }
                }).collect(Collectors.toList());
                entity = new Entity(models);
            }
        } else {
            return null;
        }

        entity.setPreview(preview);
        return entity;
    }

    @Override
    public void render() {
        for (Entry<TexturedModel, List<Model>> entry : this.renderModels.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            TexturedModel texturedModel = entry.getKey();

            final RawModel rawModel = texturedModel.getRawModel();
            if (rawModel.isInstanced()) {
                int i = 0;
                prepareTexturedModel(texturedModel, true);
                for (Model model : entry.getValue()) {
                    Matrix4f transformationMatrix = Maths
                            .createTransformationMatrix(model.getPosition(), model.getRotation(), model.getScale());
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
                entry.getValue().forEach(model -> {
                    prepareInstance(model);
                    GL11.glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
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
            this.shader.start();

            Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
            glEnable(GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            ((GameObjectShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((GameObjectShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((GameObjectShader) this.shader).loadViewMatrix(viewMatrix);
        }

    }

    private void prepareTexturedModel(TexturedModel texturedModel, boolean isInstanced) {
        if (texturedModel == null)
            throw new IllegalArgumentException("TexturedModel null");

        RawModel model = texturedModel.getRawModel();
        ModelTexture texture = texturedModel.getModelTexture();
        boolean useNormalMap = texture.getNormalMap() != -1;

        GL30.glBindVertexArray(model.getVaoID());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (useNormalMap)
            GL20.glEnableVertexAttribArray(3);
        if (isInstanced)
            GL20.glEnableVertexAttribArray(4);

        ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isTransparent())
            MasterRenderer.disableCulling();

        ((GameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
//        ((GameObjectShader) this.shader).loadUseNormalMap(useNormalMap);
        ((GameObjectShader) this.shader)
                .loadLights(useNormalMap, LightRenderer.getInstance().getGameObjects(),
                        Camera.getInstance().getViewMatrix());
        ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        ((GameObjectShader) this.shader).loadIsInstanced(isInstanced);
        ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
        ((GameObjectShader) this.shader).loadColor(texture.getColor());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        if (useNormalMap) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL13.glBindTexture(GL_TEXTURE_2D, texture.getNormalMap());
        }
        if (texture.isTransparent())
            MasterRenderer.enableCulling(); // Reenable culling
    }

    @Override
    protected void cleanUp() {
        super.cleanUp();
        this.query.delete();
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

    private void prepareInstance(Model model) {
        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(model.getPosition(), model.getRotation(), model.getScale());

        if (transformationMatrix == null)
            return;

        ((GameObjectShader) this.shader).loadTransformationMatrix(transformationMatrix);
        ((GameObjectShader) this.shader).loadOffset(model.getTextureXOffset(), model.getTextureYOffset());
    }
}