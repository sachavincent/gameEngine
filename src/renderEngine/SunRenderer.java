package renderEngine;

import entities.Camera;
import entities.ModelEntity;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL31;
import renderEngine.shaders.SunShader;
import renderEngine.shaders.structs.Material;
import util.math.Matrix4f;
import util.math.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SunRenderer extends GameObjectRenderer<SunShader> {

    private static SunRenderer instance;

    public static SunRenderer getInstance() {
        return instance == null ? (instance = new SunRenderer()) : instance;
    }
    private SunRenderer() {
        super(new SunShader(), SunShader::connectTextureUnits);
    }

    @Override
    protected void doPreRender() {
        GL11.glDisable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void doRender(Set<Map.Entry<AbstractModel, List<ModelEntity>>> entrySet) {
        for (var entry : entrySet) {
            AbstractModel model = entry.getKey();
            List<ModelEntity> modelEntities = entry.getValue();
            for (ModelEntity modelEntity : modelEntities) {
                if (model.getMaterials().isEmpty())
                    continue;

                Material material = model.getMaterials().get(0);
                if (!material.hasDiffuseMap())
                    continue;

                Vao vao = model.getVao();
                vao.bind();
                shader.loadMVPMatrix(calculateMvpMatrix(modelEntity));
                GL13.glActiveTexture(GL13.GL_TEXTURE0);

                GL11.glBindTexture(GL31.GL_TEXTURE_2D, material.getDiffuseMap().getID());
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
                vao.unbind();
            }
            GL13.glActiveTexture(0);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private Matrix4f calculateMvpMatrix(ModelEntity modelEntity) {
        float scale = modelEntity.getScale();
        Camera camera = Camera.getInstance();
        Matrix4f modelMatrix = new Matrix4f();
        Vector3f sunPos = modelEntity.getPosition();
        Matrix4f.translate(sunPos, modelMatrix, modelMatrix);
        Matrix4f modelViewMat = applyViewMatrix(modelMatrix, camera.getViewMatrix());
        Matrix4f.scale(new Vector3f(scale, scale, scale), modelViewMat, modelViewMat);
        return Matrix4f.mul(MasterRenderer.getInstance().getProjectionMatrix(), modelViewMat, null);
    }

    /**
     * Check the particle tutorial for explanations of this. Basically we remove
     * the rotation effect of the view matrix, so that the sun quad is always
     * facing the camera.
     *
     * @param modelMatrix
     * @param viewMatrix
     * @return The model-view matrix.
     */
    private Matrix4f applyViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        modelMatrix.m00 = viewMatrix.m00;
        modelMatrix.m01 = viewMatrix.m10;
        modelMatrix.m02 = viewMatrix.m20;
        modelMatrix.m10 = viewMatrix.m01;
        modelMatrix.m11 = viewMatrix.m11;
        modelMatrix.m12 = viewMatrix.m21;
        modelMatrix.m20 = viewMatrix.m02;
        modelMatrix.m21 = viewMatrix.m12;
        modelMatrix.m22 = viewMatrix.m22;
        return Matrix4f.mul(viewMatrix, modelMatrix, null);
    }
}