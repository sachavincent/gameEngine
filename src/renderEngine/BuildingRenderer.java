package renderEngine;

import entities.Camera;
import entities.ModelEntity;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.GameObjectShader;
import renderEngine.shaders.structs.Material;
import textures.ModelTexture;
import util.math.Matrix4f;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.Map.Entry;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static renderEngine.MasterRenderer.*;

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
            s.loadClipPlane(MasterRenderer.getClipPlane());
            s.loadSkyColor(RED, GREEN, BLUE);
            s.loadOffset(0, 0);
        });
    }

    @Override
    protected void doPreRender() {
        glEnable(GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.shader.loadViewMatrix(Camera.getInstance().getViewMatrix());
    }

    @Override
    protected void doRender(Set<Entry<AbstractModel, List<ModelEntity>>> entrySet) {
        Map<Material, List<Vao>> vaoMaterials = new HashMap<>();
        Map<Vao, List<ModelEntity>> vaoModels = new HashMap<>();
        for (var entry : entrySet) {
            AbstractModel abstractModel = entry.getKey();
            List<ModelEntity> modelEntities = entry.getValue();

            List<Material> materials = new ArrayList<>(abstractModel.getVao().getIndexVbos().keySet());
            final Vao vao = abstractModel.getVao();
            vao.bind();

            if (vao.isInstanced()) {
                for (Material material : materials) {
                    Vbo vbo = vao.getIndexVbos().get(material);
                    if (vbo != null) {
                        if (!vaoMaterials.containsKey(material))
                            vaoMaterials.put(material, new ArrayList<>());
                        if (!vaoModels.containsKey(vao))
                            vaoModels.put(vao, new ArrayList<>());
                        vaoMaterials.get(material).add(vao);
                        vaoModels.get(vao).addAll(modelEntities);
                    }
                }
            } else {
                modelEntities.forEach(modelEntity -> {
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
                vao.unbind();
            });
        });
        floatBuffer.clear();

        glDisable(GL_BLEND);
    }

    void prepareMaterial(Material material, boolean instanced) {
        ModelTexture texture;
        if (material.hasDiffuseMap())
            texture = material.getDiffuseMap();
        else
            texture = ModelTexture.DEFAULT_MODEL;

        this.shader.loadNumberOfRows(1);
        if (texture.isTransparent())
            MasterRenderer.disableCulling();

        this.shader.loadUseFakeLighting(false);
        this.shader.loadLights(material.hasNormalMap(), Camera.getInstance().getViewMatrix());
        this.shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        this.shader.loadIsInstanced(instanced);
        this.shader.loadAlpha(1);

        this.shader.loadMaterial(material);

        if (texture.isTransparent())
            MasterRenderer.enableCulling(); // Reenable culling
    }

    private void unbindTexturedModel() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}