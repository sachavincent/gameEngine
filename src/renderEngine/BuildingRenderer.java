package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.ModelEntity;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import scene.Scene;
import scene.gameObjects.GameObject;
import textures.ModelTexture;
import util.math.Matrix4f;
import util.parsing.Material;
import util.parsing.ModelType;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.Map.Entry;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static renderEngine.MasterRenderer.*;

public class BuildingRenderer extends GameObjectRenderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    protected final Map<AbstractModel, List<ModelEntity>> renderModels = new HashMap<>();
    private final Map<Integer, Entity> renderableEntities = new HashMap<>();

//    private final Query query;

    private static BuildingRenderer instance;

    public static BuildingRenderer getInstance() {
        return instance == null ? (instance = new BuildingRenderer()) : instance;
    }

    private BuildingRenderer() {
        super(new GameObjectShader());
//        this.query = new Query(GL33.GL_ANY_SAMPLES_PASSED);
        this.shader.start();
        ((GameObjectShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        ((GameObjectShader) this.shader).connectTextureUnits();
        this.shader.stop();
    }

    public void doZPass(Set<GameObject> gameObjects) {
//        Set<GameObject> newGameObjects = this.query.initNewGameObjects(gameObjects);
        Set<GameObject> newGameObjects = gameObjects;

        GameObject gameObject;
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
        newGameObjects.forEach(newGameObject -> {
            Entity entityFromGameObject = GameObject.createEntityFromGameObject(newGameObject, this.displayBoundingBoxes);
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
//            ((GameObjectShader) this.shader).loadViewMatrix(viewMatrix);
//            gameObject = this.query.getBestUpdateCandidate();
//            if (gameObject != null && gameObject.hasComponent(BoundingBoxComponent.class)) {
//                boolean displayBoundingBoxes = this.displayBoundingBoxes;
//                this.displayBoundingBoxes = true;
//                Entity entity = createEntityFromGameObject(gameObject);
//                if (entity != null) {
//                    Entity zPassCopy = new Entity(entity);
////                    zPassCopy.getModels().forEach(model -> model.setScale(model.getScale() * 1.5f));
//
//                    this.displayBoundingBoxes = displayBoundingBoxes;
//                    if (!displayBoundingBoxes) {
//                        entity = createEntityFromGameObject(gameObject);
//                    }
//                    this.query.start(gameObject.getId(), entity);
//                    zPassCopy.getModelEntities().forEach(zPassModel -> {
//                        prepareTexturedModel(zPassModel.getModel(), false);
//                        Entity zPassModelEntity = new Entity(zPassCopy);
//                        prepareInstance(zPassModel);
//                        glDrawElements(GL_TRIANGLES, zPassModel.getModel().getVao().getIndexCount(),
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
        entity.getModelEntities().forEach(modelEntity -> {
            AbstractModel model = modelEntity.getModel();
            if (!this.renderModels.containsKey(model))
                this.renderModels.put(model, new ArrayList<>());
            this.renderModels.get(model).add(modelEntity);
        });
    }

    @Override
    public void render() {
        Map<Material, List<Vao>> vaoMaterials = new HashMap<>();
        Map<Vao, List<ModelEntity>> vaoModels = new HashMap<>();
        for (Entry<AbstractModel, List<ModelEntity>> entry : this.renderModels.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            AbstractModel model = entry.getKey();
            List<Material> materials = new ArrayList<>(model.getVao().getIndexVbos().keySet());
            final Vao vao = model.getVao();
            ModelType modelType = vao.getModelType();
            vao.bind(modelType.getAttributeNumbers());

            if (vao.isInstanced()) {
                for (Material material : materials) {
                    Vbo vbo = vao.getIndexVbos().get(material);
                    if (vbo != null) {
                        if (!vaoMaterials.containsKey(material))
                            vaoMaterials.put(material, new ArrayList<>());
                        if (!vaoModels.containsKey(vao))
                            vaoModels.put(vao, new ArrayList<>());
                        vaoMaterials.get(material).add(vao);
                        vaoModels.get(vao).addAll(entry.getValue());
                    }
                }
            } else {
                entry.getValue().forEach(modelEntity -> {
                    prepareInstance(modelEntity);

                    for (Material material : materials) {
                        Vbo vbo = vao.getIndexVbos().get(material);
                        vbo.bind();
                        int indicesCount = vbo.getDataLength();
                        prepareMaterial(material, false);
                        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
                        vbo.unbind();
                    }
                });
            }

            vao.unbind(modelType.getAttributeNumbers());
            unbindTexturedModel();
        }

        vaoMaterials.forEach((material, vaos) -> {
            prepareMaterial(material, true);

            vaos.forEach(vao -> {
                List<ModelEntity> modelEntities = vaoModels.get(vao);

                vao.bind(vao.getModelType().getAttributeNumbers());
                int k = 0;
                for (ModelEntity modelEntity : modelEntities) {
                    Matrix4f transformationMatrix = modelEntity.getTransformationMatrix();
                    try {
                        floatBuffer = transformationMatrix.store(k++ * 16, floatBuffer);
                    } catch (IndexOutOfBoundsException ignored) {
                        break;
                    }
                }

                vao.getInstanceVbo().bind();
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
                Vbo vbo = vao.getIndexVbos().get(material);
                vbo.bind();
                int indicesCount = vbo.getDataLength();
                GL46.glDrawElementsInstanced(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0, modelEntities.size());
                vao.getInstanceVbo().unbind();
                vbo.unbind();
                vao.unbind(vao.getModelType().getAttributeNumbers());
                floatBuffer.clear();
            });
        });
        floatBuffer.clear();

        this.renderModels.clear();
        this.shader.stop();
        glDisable(GL_BLEND);
    }

    void prepareMaterial(Material material, boolean instanced) {
        ModelTexture texture;
        if (material.hasDiffuseMap())
            texture = material.getDiffuseMap();
        else
            texture = ModelTexture.DEFAULT_MODEL;

        ((GameObjectShader) this.shader).loadNumberOfRows(1);
        if (texture.isTransparent())
            MasterRenderer.disableCulling();

        ((GameObjectShader) this.shader).loadUseFakeLighting(false);
        ((GameObjectShader) this.shader)
                .loadLights(material.hasNormalMap(), LightRenderer.getInstance().getGameObjects(),
                        Camera.getInstance().getViewMatrix());
        ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        ((GameObjectShader) this.shader).loadIsInstanced(instanced);
        ((GameObjectShader) this.shader).loadAlpha(1);

        ((GameObjectShader) this.shader).loadMaterial(material);

        if (texture.isTransparent())
            MasterRenderer.enableCulling(); // Reenable culling
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

    @Override
    protected void cleanUp() {
        super.cleanUp();
//        this.query.delete();
    }

    private void unbindTexturedModel() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    @Override
    protected void prepareInstance(ModelEntity modelEntity) {
        super.prepareInstance(modelEntity);

        ((GameObjectShader) this.shader).loadOffset(0, 0);
    }
}