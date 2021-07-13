package renderEngine;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import entities.Camera;
import entities.Entity;
import entities.ModelEntity;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import models.AnimatedModel;
import models.Model;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.AnimatedGameObjectShader;
import scene.Scene;
import scene.gameObjects.GameObject;
import util.Vao;
import util.Vbo;
import util.math.Maths;
import util.math.Matrix4f;

public class AnimatedBuildingRenderer extends Renderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    protected final Map<Model, List<ModelEntity>> renderModels       = new HashMap<>();
    private final   Map<Integer, Entity>          renderableEntities = new HashMap<>();

//    private final Query query;

    private static AnimatedBuildingRenderer instance;

    public static AnimatedBuildingRenderer getInstance() {
        return instance == null ? (instance = new AnimatedBuildingRenderer()) : instance;
    }

    private AnimatedBuildingRenderer() {
        super(new AnimatedGameObjectShader());
//        this.query = new Query(GL33.GL_ANY_SAMPLES_PASSED);
        this.shader.start();
        ((AnimatedGameObjectShader) this.shader)
                .loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        ((AnimatedGameObjectShader) this.shader).connectTextureUnits();
        this.shader.stop();
    }

    public void doZPass(Set<GameObject> gameObjects) {
//        Set<GameObject> newGameObjects = this.query.initNewGameObjects(gameObjects);
//
//        GameObject gameObject;
//        if (this.query.isResultReady() || !this.query.isInUse()) {
//            int visibleSamples = this.query.getResult();
//            int id = this.query.getCandidateId();
//            Entity candidateEntity = this.query.getCandidateEntity();
//            if (visibleSamples > 0 && (!this.renderableEntities.containsKey(id) ||
//                    !this.renderableEntities.get(id).equals(candidateEntity))) {
//                this.renderableEntities.put(id, candidateEntity);
//            } else if (visibleSamples == 0 && this.renderableEntities.containsKey(id)) {
//                this.renderableEntities.remove(id);
//            }
//        }
//        newGameObjects.forEach(newGameObject -> {
//            Entity entityFromGameObject = createEntityFromGameObject(newGameObject);
//            if (entityFromGameObject != null)
//                this.renderableEntities.put(newGameObject.getId(), entityFromGameObject);
//        });
        gameObjects.forEach(newGameObject -> {
            Entity entityFromGameObject = createEntityFromGameObject(newGameObject);
            if (entityFromGameObject != null)
                this.renderableEntities.put(newGameObject.getId(), entityFromGameObject);
        });
//        if (!this.query.isInUse()) {
//            glColorMask(false, false, false, false);
//            glDepthMask(false);
//            glEnable(GL_BLEND);
//            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
//            this.shader.start();
//            Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
//            ((AnimatedGameObjectShader) this.shader).loadViewMatrix(viewMatrix);
//            gameObject = this.query.getBestUpdateCandidate();
//            if (gameObject != null && gameObject.hasComponent(BoundingBoxComponent.class)) {
//                boolean displayBoundingBoxes = this.displayBoundingBoxes;
//                this.displayBoundingBoxes = true;
//                Entity entity = createEntityFromGameObject(gameObject);
//                if (entity != null) {
//                    Entity zPassCopy = new Entity(entity);
//                    zPassCopy.getModels().forEach(model -> model.setScale(model.getScale() * 1.5f));
//
//                    this.displayBoundingBoxes = displayBoundingBoxes;
//                    if (!displayBoundingBoxes) {
//                        entity = createEntityFromGameObject(gameObject);
//                    }
//                    this.query.start(gameObject.getId(), entity);
//                    zPassCopy.getModels().forEach(zPassModel -> {
//                        prepareTexturedModel(zPassModel.getTexturedModel(), false);
//                        Entity zPassModelEntity = new Entity(zPassCopy);
//                        prepareInstance(zPassModel);
//                        glDrawElements(GL_TRIANGLES, zPassModel.getTexturedModel().getRawModel().getVertexCount(),
//                                GL_UNSIGNED_INT, 0);
//                    });
//                    this.query.stop();
//                }
//            }
//            this.shader.stop();
//            glColorMask(true, true, true, true);
//            glDepthMask(true);
//        }
        this.renderableEntities.entrySet().removeIf(entry -> entry.getValue().isPreview() &&
                !Scene.getInstance().getPreviewedGameObjects().containsKey(entry.getKey()));
        this.renderableEntities.forEach((id, entity) -> {
            addModelToRender(entity);
        });
    }

    public void removeGameObject(GameObject gameObject) {
        this.renderableEntities.remove(gameObject.getId());
//        this.query.getLastUpdateTimes().remove(gameObject.getId());
    }

    private void addModelToRender(Entity entity) {
        entity.getModelEntities().forEach(model -> {
            Model texturedModel = model.getModel();
            if (!this.renderModels.containsKey(texturedModel))
                this.renderModels.put(texturedModel, new ArrayList<>());
            this.renderModels.get(texturedModel).add(model);
        });
    }

    private Entity createEntityFromGameObject(GameObject gameObject) {
        if (gameObject == null)
            return null;

//        if (this.displayBoundingBoxes && !gameObject.hasComponent(BoundingBoxComponent.class))
            return null;

//        TerrainPosition position = null;
//        PreviewComponent previewComponent = gameObject.getComponent(PreviewComponent.class);
//        PositionComponent positionComponent = gameObject.getComponent(PositionComponent.class);
//        boolean preview = false;
//        if (previewComponent != null && previewComponent.getPreviewPosition() != null) {
//            Vector3f pos = previewComponent.getPreviewPosition(); // = null if no preview
//            if (pos != null) {
//                preview = true;
//                position = pos.toTerrainPosition();
//            }
//        } else if (positionComponent != null)
//            position = positionComponent.getPosition().toTerrainPosition();
//
//        if (position == null)
//            return null;
//        Vector3f pos = position.toVector3f();
//        if (gameObject.hasComponent(OffsetComponent.class))
//            pos = pos.add(gameObject.getComponent(OffsetComponent.class).getOffset());
//
//        float scale = gameObject.hasComponent(ScaleComponent.class) ? gameObject
//                .getComponent(ScaleComponent.class).getScale() : 1;
//        Direction direction =
//                gameObject.hasComponent(DirectionComponent.class) ? gameObject.getComponent(DirectionComponent.class)
//                        .getDirection() : Direction.defaultDirection();
//
//        Entity entity;
//        if (gameObject.hasComponent(AnimatedModelComponent.class) || this.displayBoundingBoxes) {
//            Model model;
//            if (preview)
//                model = previewComponent.getTexture();
//            else if (this.displayBoundingBoxes)
//                model = gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox();
//            else
//                model = gameObject.getComponent(AnimatedModelComponent.class).getModel().getModel();
//
//            entity = new Entity(new ModelEntity(pos, direction, scale, model));
//        } else if (gameObject.hasComponent(MultipleModelsComponent.class)) {
//            if (preview)
//                entity = new Entity(new ModelEntity(pos, direction, scale, previewComponent.getTexture()));
//            else {
//                Vector3f finalPos = pos;
//                MultipleModelsComponent multipleModelsComponent = gameObject
//                        .getComponent(MultipleModelsComponent.class);
//                Map<String, ModelEntity> concurrentModels = multipleModelsComponent.getConcurrentModels();
//                List<ModelEntity> modelEntities = concurrentModels.values().stream().map(ModelEntity::new).peek(model -> {
//                    Vector3f modelPosition = model.getPosition();
//                    switch (direction) {
//                        case NORTH:
//                            break;
//                        case WEST:
//                            modelPosition = new Vector3f(-modelPosition.x, modelPosition.y, modelPosition.z);
//                            break;
//                        case SOUTH:
//                            modelPosition = new Vector3f(-modelPosition.x, modelPosition.y, -modelPosition.z);
//                            break;
//                        case EAST:
//                            modelPosition = new Vector3f(modelPosition.x, modelPosition.y, -modelPosition.z);
//                            break;
//                    }
//                    model.setPosition(modelPosition.add(finalPos));
//                    model.setScale(model.getScale() + scale);
//                    if (!model.isFixedRotation()) {
//                        model.setRotation(model.getRotation().add(new Vector3f(0, direction.getDegree(), 0)));
//                    }
//                }).collect(Collectors.toList());
//                entity = new Entity(modelEntities);
//            }
//        } else {
//            return null;
//        }
//
//        entity.setPreview(preview);
//        return entity;
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
                    Matrix4f transformationMatrix = Maths
                            .createTransformationMatrix(modelEntity.getPosition(), modelEntity.getRotation(), modelEntity
                                    .getScale());
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
                    vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
                    int nbIndices;
                    if (model.getModel() instanceof AnimatedModel)
                        nbIndices = ((AnimatedModel) model.getModel()).getIndicesLength();
                    else
                        nbIndices = model.getModel().getVao().getIndexCount();
                    
//                    GL11.glDrawElements(GL_TRIANGLES, nbIndices, GL_UNSIGNED_INT, 0);
                    vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
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
            ((AnimatedGameObjectShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((AnimatedGameObjectShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((AnimatedGameObjectShader) this.shader).loadViewMatrix(viewMatrix);
        }

    }

    private void prepareTexturedModel(Model model, boolean isInstanced) {
//        if (model == null)
            throw new IllegalArgumentException("TexturedModel null");
//
//        Vao vao = model.getVao();
//        ModelTexture texture = model.getModelFile();
//        boolean useNormalMap = texture.getNormalMap() != -1;
//
//        GL30.glBindVertexArray(vao.getId());
//
//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        GL20.glEnableVertexAttribArray(2);
//        if (model instanceof AnimatedModel) {
//            GL20.glEnableVertexAttribArray(4);
//            GL20.glEnableVertexAttribArray(5);
//        }
//        if (useNormalMap)
//            GL20.glEnableVertexAttribArray(3);
//        if (isInstanced)
//            GL20.glEnableVertexAttribArray(6);
//
//        ((AnimatedGameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
//        if (texture.isTransparent())
//            MasterRenderer.disableCulling();
//
//        ((AnimatedGameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
//        ((AnimatedGameObjectShader) this.shader).loadUseNormalMap(useNormalMap);
//        ((AnimatedGameObjectShader) this.shader)
//                .loadLights(useNormalMap, LightRenderer.getInstance().getGameObjects(),
//                        Camera.getInstance().getViewMatrix());
//        ((AnimatedGameObjectShader) this.shader)
//                .loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//        ((AnimatedGameObjectShader) this.shader).loadIsInstanced(isInstanced);
//        ((AnimatedGameObjectShader) this.shader).loadAlpha(texture.getAlpha());
//        ((AnimatedGameObjectShader) this.shader).loadColor(texture.getColor());
//        if (model instanceof AnimatedModel)
//            ((AnimatedGameObjectShader) this.shader)
//                    .loadJointTransforms(Arrays.asList(((AnimatedModel) model).getJointTransforms()));
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
//        if (useNormalMap) {
//            GL13.glActiveTexture(GL13.GL_TEXTURE1);
//            GL13.glBindTexture(GL_TEXTURE_2D, texture.getNormalMap());
//        }
//        if (texture.isTransparent())
//            MasterRenderer.enableCulling(); // Reenable culling
    }

    @Override
    protected void cleanUp() {
        super.cleanUp();
//        this.query.delete();
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);
        GL20.glDisableVertexAttribArray(6);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(ModelEntity modelEntity) {
        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(modelEntity.getPosition(), modelEntity.getRotation(), modelEntity.getScale());

        if (transformationMatrix == null)
            return;

        ((AnimatedGameObjectShader) this.shader).loadTransformationMatrix(transformationMatrix);
    }
}