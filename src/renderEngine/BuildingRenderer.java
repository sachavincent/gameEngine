package renderEngine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.CLIP_PLANE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import entities.Camera;
import entities.ModelEntity;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.IndexBufferVao;
import renderEngine.structures.IndexVbo;
import renderEngine.structures.MaterialIndexVbo;
import renderEngine.structures.Vao;
import renderEngine.structures.Vbo;
import util.math.Matrix4f;

public class BuildingRenderer extends GameObjectRenderer<GameObjectShader> {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    private static BuildingRenderer instance;

    public static BuildingRenderer getInstance() {
        return instance == null ? (instance = new BuildingRenderer()) : instance;
    }

    private BuildingRenderer() {
        super(new GameObjectShader(), s -> {
            s.loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
            s.connectTextureUnits();
            s.loadClipPlane(CLIP_PLANE);
            s.loadSkyColor(RED, GREEN, BLUE);
        });
    }

    @Override
    protected void doPreRender() {
        glEnable(GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.shader.loadViewMatrix(Camera.getInstance().getViewMatrix());
        this.shader.loadClipPlane(CLIP_PLANE);
        this.shader.loadSkyColor(RED, GREEN, BLUE);
    }

    @Override
    protected void doRender(Set<Entry<AbstractModel, List<ModelEntity>>> entrySet) {
        Map<Material, List<Vao>> vaoMaterials = new HashMap<>();
        Map<Vao, Map<Material, Vbo>> vboMaterials = new HashMap<>();
        Map<Vao, List<ModelEntity>> vaoModels = new HashMap<>();
        for (var entry : entrySet) {
            AbstractModel abstractModel = entry.getKey();
            List<ModelEntity> modelEntities = entry.getValue();

            final IndexBufferVao vao = (IndexBufferVao) abstractModel.getVao();
            if (!vboMaterials.containsKey(vao))
                vboMaterials.put(vao, new HashMap<>());

            List<IndexVbo> indexVbos = vao.getIndexVbos();
            vao.bind();

            if (vao.isInstanced()) {
                for (IndexVbo indexVbo : indexVbos) {
                    if (indexVbo instanceof MaterialIndexVbo) {
                        Material material = ((MaterialIndexVbo) indexVbo).getMaterial();
                        if (!vaoMaterials.containsKey(material))
                            vaoMaterials.put(material, new ArrayList<>());
                        vaoMaterials.get(material).add(vao);

                        Map<Material, Vbo> materialVboMap = vboMaterials.get(vao);
                        materialVboMap.put(material, indexVbo);
                    }
                    if (!vaoModels.containsKey(vao))
                        vaoModels.put(vao, new ArrayList<>());
                    vaoModels.get(vao).addAll(modelEntities);
                }
            } else {
                modelEntities.forEach(modelEntity -> {
                    prepareInstance(modelEntity);
                    for (Vbo vbo : indexVbos) {
                        vbo.bind();
                        if (vbo instanceof MaterialIndexVbo)
                            prepareMaterial(((MaterialIndexVbo) vbo).getMaterial(), false);

                        glDrawElements(GL_TRIANGLES, vbo.getDataLength(), GL_UNSIGNED_INT, 0);
                        vbo.unbind();
                    }
                });
            }

            vao.unbind();
            unbindTexturedModel();
        }

        vaoMaterials.forEach((material, vaos) -> {
            prepareMaterial(material, true);

            vaos.forEach(vao -> {
                List<ModelEntity> modelEntities = vaoModels.get(vao);

                vao.bind();
                int k = 0;
                for (ModelEntity modelEntity : modelEntities) {
                    Matrix4f transformationMatrix = modelEntity.getTransformationMatrix();

                    this.shader.loadTransparency(modelEntity.getTransparency()); //TODO: Add transparency in floatbuffer
                    try {
                        floatBuffer = transformationMatrix.store(k++ * 16, floatBuffer);
                    } catch (IndexOutOfBoundsException ignored) {
                        break;
                    }
                }

                Vbo instanceVbo = vao.getInstanceVbo();
                instanceVbo.bind();
                glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
                Vbo vbo = vboMaterials.get(vao).get(material);
                vbo.bind();
                GL46.glDrawElementsInstanced(GL_TRIANGLES, vbo.getDataLength(), GL_UNSIGNED_INT, 0, modelEntities.size());
                instanceVbo.unbind();
                vbo.unbind();
                vao.unbind();
            });
        });
        floatBuffer.clear();

        glDisable(GL_BLEND);
    }

    void prepareMaterial(Material material, boolean instanced) {
//        if (texture.isTransparent())
//            MasterRenderer.disableCulling();

        this.shader.loadUseFakeLighting(false);
        this.shader.loadLights(material.hasNormalMap(), Camera.getInstance().getViewMatrix());
        this.shader.loadIsInstanced(instanced);
        this.shader.loadTransparency(1);

        this.shader.loadMaterial(material);

//        if (texture.isTransparent())
//            MasterRenderer.enableCulling(); // Reenable culling
    }

    @Override
    protected void prepareInstance(ModelEntity modelEntity) {
        super.prepareInstance(modelEntity);

        this.shader.loadTransparency(modelEntity.getTransparency());
    }

    private void unbindTexturedModel() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    protected void cleanUp() {
        super.cleanUp();

        MemoryUtil.memFree(floatBuffer);
    }
}