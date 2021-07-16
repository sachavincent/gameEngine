package renderEngine;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import entities.Camera;
import entities.Camera.Direction;
import entities.Entity;
import entities.ModelEntity;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import models.Model;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import scene.Scene;
import scene.components.*;
import scene.gameObjects.GameObject;
import terrains.TerrainPosition;
import util.Vao;
import util.Vbo;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;
import util.parsing.ModelType;
import util.parsing.objParser.Material;

public class BuildingRenderer extends Renderer {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    //    protected final Map<TexturedModel, List<Model>> renderModels       = new HashMap<>();
    protected final Map<Model, List<ModelEntity>> renderModels       = new HashMap<>();
    private final   Map<Integer, Entity>          renderableEntities = new HashMap<>();

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
            Model model = modelEntity.getModel();
            if (!this.renderModels.containsKey(model))
                this.renderModels.put(model, new ArrayList<>());
            this.renderModels.get(model).add(modelEntity);
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
            ModelEntity modelEntity;
            if (preview)
                modelEntity = previewComponent.getTexture();
            else if (this.displayBoundingBoxes)
                modelEntity = gameObject.getComponent(BoundingBoxComponent.class).getBoundingBox().toModelEntity();
            else
                modelEntity = gameObject.getComponent(SingleModelComponent.class).getModel();

            entity = new Entity(new ModelEntity(pos, direction, scale, modelEntity.getModel()));
        } else if (gameObject.hasComponent(MultipleModelsComponent.class)) {
            if (preview)
                entity = new Entity(new ModelEntity(pos, direction, scale, previewComponent.getTexture().getModel()));
            else {
                Vector3f finalPos = pos;
                MultipleModelsComponent multipleModelsComponent = gameObject
                        .getComponent(MultipleModelsComponent.class);
                Map<String, ModelEntity> concurrentModels = multipleModelsComponent.getConcurrentModels();
                List<ModelEntity> modelEntities = concurrentModels.values().stream().map(ModelEntity::new)
                        .peek(model -> {
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
                entity = new Entity(modelEntities);
            }
        } else {
            return null;
        }

        entity.setPreview(preview);
        return entity;
    }

    @Override
    public void render() {
        for (Entry<Model, List<ModelEntity>> entry : this.renderModels.entrySet()) {
            if (entry == null || entry.getKey() == null)
                continue;

            Model model = entry.getKey();
            List<Material> materials = model.getModelFile().getMTLFile().getMaterials();

            final Vao vao = model.getVao();
            ModelType modelType = vao.getModelType();
            vao.bind(modelType.getAttributeNumbers());

            int k = 0;

            for (ModelEntity modelEntity : entry.getValue()) {
                Matrix4f transformationMatrix = Maths
                        .createTransformationMatrix(modelEntity.getPosition(), modelEntity.getRotation(),
                                modelEntity.getScale());
                try {
                    floatBuffer = transformationMatrix.store(k++ * 16, floatBuffer);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
//            vao.getInstanceVbo().bind();
//            glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
            vao.getIndexVbo().bind();
//            prepareInstance(entry.getValue().get(0));
//            temp(materials, true);
//
//            int nbIndices = 0;
//            int nbVertices = 0;
//
//            for (Material material : materials) {
//                List<Integer> indices = vao.materialIndices.get(material);
//                long nbVerticesL = indices.stream().distinct().count();
//                int indicesLength = indices.size();
//
////                GL46.glDrawElementsBaseVertex(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, nbIndices, nbVertices);
////                GL46.glDrawElements(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, nbIndices);
////                GL46.glDrawElementsInstanced(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, nbIndices, 1);
//                nbIndices += indicesLength * 4;
//                nbVertices += nbVerticesL;
//            }


//            {
//                for (ModelEntity modelEntity : entry.getValue()) {
//                    prepareInstance(modelEntity);
//                    int indexCount = vao.getIndexCount();
//                    int nbMaterials = materials.size();
//                    int[] indicesNumbers = new int[nbMaterials];
//
//                    temp(materials, false);
//                    for (int i = 0; i < materials.size(); i++) {
//                        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(1);
//                        Material material = materials.get(i);
//                        List<Integer> indices = vao.materialIndices.get(material);
//                        indicesNumbers[i] = indices.size();
//                        PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(indices.size());
//                        pointerBuffer.put(0);
////                        for (int id : indicesNumbers) {
//                            indicesBuffer.put(indicesNumbers[i] * 4);
////                        }
//                        indicesBuffer.flip();
//                        GL33.glVertexAttribI1i(3, i);
//                        GL46.glMultiDrawElements(GL11.GL_TRIANGLES, indicesBuffer, GL11.GL_UNSIGNED_INT, pointerBuffer);
//                    }
//                }
//            }
            {
                for (ModelEntity modelEntity : entry.getValue()) {
                    prepareInstance(modelEntity);
                    int max1 = 0;
                    int max2 = 0;
                    Vbo vbo2 = Vbo.create(GL_DRAW_INDIRECT_BUFFER);
                    int[] indirect = new int[5 * materials.size()];
                    temp(materials, false);
                    for (int i = 0; i < materials.size(); i++) {
                        Material material = materials.get(i);
                        indirect[i * 5] = vao.materialIndices.get(material).size(); // Correct
                        indirect[i * 5 + 1] = 1; // Correct
                        indirect[i * 5 + 2] = max1;
                        indirect[i * 5 + 3] = 0;
                        indirect[i * 5 + 4] = i;
                        max1 += indirect[i * 5];
                        max2 += (int) vao.materialIndices.get(material).stream().distinct().count();
                    }
                    vbo2.bind();
                    vbo2.storeData(indirect);
                    GL46.glMultiDrawElementsIndirect(GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_INT, 0, materials.size(), 0);
                    vbo2.unbind();
                }
            }
            vao.getIndexVbo().unbind();
//            vao.getInstanceVbo().unbind();
            vao.unbind(modelType.getAttributeNumbers());
            unbindTexturedModel();
            floatBuffer.clear();
        }

        this.renderModels.clear();
        this.shader.stop();

        GL11.glDisable(GL_BLEND);
    }

    void temp(List<Material> materials, boolean instanced) {
//        ModelTexture texture;
//        if (material.hasDiffuseMap())
//            texture = material.getDiffuseMap();
//        else
//            texture = ModelTexture.DEFAULT_MODEL;

        ((GameObjectShader) this.shader).loadNumberOfRows(1);
//        if (texture.isTransparent())
//            MasterRenderer.disableCulling();

//        List<Boolean> useNormalMaps = materials.stream().map(Material::hasNormalMap).collect(Collectors.toList());
        ((GameObjectShader) this.shader).loadUseFakeLighting(false);
        ((GameObjectShader) this.shader)
                .loadLights(LightRenderer.getInstance().getGameObjects(),
                        Camera.getInstance().getViewMatrix());
//        ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        ((GameObjectShader) this.shader).loadIsInstanced(instanced);
        ((GameObjectShader) this.shader).loadAlpha(1);

        ((GameObjectShader) this.shader).loadMaterials(materials);

//        if (texture.isTransparent())
//            MasterRenderer.enableCulling(); // Reenable culling
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

    private void prepareTexturedModel(Model model, boolean isInstanced) {
//        if (model == null)
//            throw new IllegalArgumentException("TexturedModel null");
//
//        Vao vao = model.getVao();
//        ModelTexture texture = model.getModelFile();
//
//        GL30.glBindVertexArray(vao.getId());
//
//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        GL20.glEnableVertexAttribArray(2);
//        if (texture.doesUseNormalMap())
//            GL20.glEnableVertexAttribArray(3);
//        if (isInstanced)
//            GL20.glEnableVertexAttribArray(6);
//
//        ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
//        if (texture.isTransparent())
//            MasterRenderer.disableCulling();
//
//        ((GameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
//        ((GameObjectShader) this.shader).loadUseNormalMap(texture.doesUseNormalMap());
//        ((GameObjectShader) this.shader).loadUseSpecularMap(texture.doesUseSpecularMap());
//        ((GameObjectShader) this.shader)
//                .loadLights(texture.doesUseNormalMap(), LightRenderer.getInstance().getGameObjects(),
//                        Camera.getInstance().getViewMatrix());
//        ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//        ((GameObjectShader) this.shader).loadIsInstanced(isInstanced);
//        ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
//        ((GameObjectShader) this.shader).loadColor(texture.getColor());
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
//        if (texture.doesUseNormalMap()) {
//            GL13.glActiveTexture(GL13.GL_TEXTURE1);
//            GL13.glBindTexture(GL_TEXTURE_2D, texture.getNormalMap());
//        }
//        if (texture.doesUseSpecularMap()) {
//            GL13.glActiveTexture(GL13.GL_TEXTURE2);
//            GL13.glBindTexture(GL_TEXTURE_2D, texture.getSpecularMap());
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(ModelEntity modelEntity) {
        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(modelEntity.getPosition(), modelEntity.getRotation(),
                        modelEntity.getScale());

        if (transformationMatrix == null)
            return;

        ((GameObjectShader) this.shader).loadTransformationMatrix(transformationMatrix);
        ((GameObjectShader) this.shader).loadOffset(0, 0);
    }
}