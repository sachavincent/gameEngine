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
import models.AnimatedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import renderEngine.shaders.AnimatedGameObjectShader;
import renderEngine.shaders.structs.Material;
import textures.ModelTexture;
import util.math.Matrix4f;

public class AnimatedBuildingRenderer extends GameObjectRenderer<AnimatedGameObjectShader> {

    // TODO: 10000 = max instances
    private static FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(10000 * 16);

    private static AnimatedBuildingRenderer instance;

    public static AnimatedBuildingRenderer getInstance() {
        return instance == null ? (instance = new AnimatedBuildingRenderer()) : instance;
    }

    private AnimatedBuildingRenderer() {
        super(new AnimatedGameObjectShader(), s -> {
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
    }

    @Override
    protected void doRender(Set<Entry<AbstractModel, List<ModelEntity>>> entrySet) {
        Map<Material, List<Vao>> vaoMaterials = new HashMap<>();
        Map<Vao, List<ModelEntity>> vaoModels = new HashMap<>();
        for(var entry : entrySet) {
            AnimatedModel animatedModel = (AnimatedModel) entry.getKey();
            List<ModelEntity> modelEntities = entry.getValue();

            List<Material> materials = animatedModel.getMaterials();
            final Vao vao = animatedModel.getVao();
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
                    this.shader.loadJointTransforms(((AnimatedModel) modelEntity.getModel()).getJointTransforms());
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
                vao.unbind();
                floatBuffer.clear();
            });
        });

        glDisable(GL_BLEND);
    }

    private void prepareMaterial(Material material, boolean instanced) {
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

    @Override
    protected void prepareInstance(ModelEntity modelEntity) {
        super.prepareInstance(modelEntity);

        this.shader.loadJointTransforms(((AnimatedModel) modelEntity.getModel()).getJointTransforms());
    }
}