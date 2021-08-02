package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.ModelEntity;
import models.AnimatedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.AnimatedGameObjectShader;
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

public class AnimatedBuildingRenderer extends GameObjectRenderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    protected final Map<AnimatedModel, List<ModelEntity>> renderModels = new HashMap<>();
    private final Map<Integer, Entity> renderableEntities = new HashMap<>();

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
        entity.getModelEntities().forEach(modelEntity -> {
            AnimatedModel model = (AnimatedModel) modelEntity.getModel();
            if (!this.renderModels.containsKey(model))
                this.renderModels.put(model, new ArrayList<>());
            this.renderModels.get(model).add(modelEntity);
        });
    }

    @Override
    public void render() {
        Map<Material, List<Vao>> vaoMaterials = new HashMap<>();
        Map<Vao, List<ModelEntity>> vaoModels = new HashMap<>();
        for (Entry<AnimatedModel, List<ModelEntity>> entry : this.renderModels.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            AnimatedModel model = entry.getKey();
            List<Material> materials = model.getMaterials();
            final Vao vao = model.getVao();
            ModelType modelType = vao.getModelType();
            vao.bind(modelType.getAttributeNumbers());

            if (vao.isInstanced()) {
                for (Material material : materials) {
                    Vbo vbo = vao.getIndexVbos().get(material);
                    if (vbo != null) {
                        if (!vaoMaterials.containsKey(material))
                            vaoMaterials.put(material, new ArrayList<>());
                        if (!vaoModels.containsKey(material))
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
                    ((AnimatedGameObjectShader) this.shader).loadJointTransforms(((AnimatedModel) modelEntity.getModel()).getJointTransforms());
                    //TODO: Store joint transform as attrib divisor
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

    private void prepareMaterial(Material material, boolean instanced) {
        ModelTexture texture;
        if (material.hasDiffuseMap())
            texture = material.getDiffuseMap();
        else
            texture = ModelTexture.DEFAULT_MODEL;

        ((AnimatedGameObjectShader) this.shader).loadNumberOfRows(1);
        if (texture.isTransparent())
            MasterRenderer.disableCulling();

        ((AnimatedGameObjectShader) this.shader).loadUseFakeLighting(false);
        ((AnimatedGameObjectShader) this.shader)
                .loadLights(material.hasNormalMap(), LightRenderer.getInstance().getGameObjects(),
                        Camera.getInstance().getViewMatrix());
        ((AnimatedGameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        ((AnimatedGameObjectShader) this.shader).loadIsInstanced(instanced);
        ((AnimatedGameObjectShader) this.shader).loadAlpha(1);

        ((AnimatedGameObjectShader) this.shader).loadMaterial(material);

        if (texture.isTransparent())
            MasterRenderer.enableCulling(); // Reenable culling
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

        ((AnimatedGameObjectShader) this.shader).loadJointTransforms(((AnimatedModel) modelEntity.getModel()).getJointTransforms());
    }
}