package renderEngine;

import entities.Camera;
import entities.ModelEntity;
import models.AbstractModel;
import org.lwjgl.opengl.GL11;
import pathfinding.Path;
import renderEngine.shaders.AnimatedGameObjectShader;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector2f;

import java.awt.*;
import java.util.List;
import java.util.*;

import static renderEngine.MasterRenderer.*;

public class PathRenderer extends GameObjectRenderer<AnimatedGameObjectShader> {

    private static PathRenderer instance;

    public static PathRenderer getInstance() {
        return instance == null ? (instance = new PathRenderer()) : instance;
    }

    private Map<Path, Color> tempPathsList = new HashMap<>();
    private boolean updateNeeded;

    private PathRenderer() {
        super(new AnimatedGameObjectShader(), s -> {
            s.loadTransformationMatrix(
                    Maths.createTransformationMatrix(new Vector2f(0, 0), new Vector2f(1, 1)));
            s.loadOffset(0, 0);
            s.loadClipPlane(CLIP_PLANE);
            s.loadSkyColor(RED, GREEN, BLUE);
            s.loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        });
    }

    public Map<Path, Color> getTempPathsList() {
        return Collections.unmodifiableMap(this.tempPathsList);
    }

    public void setTempPathsList(Map<Path, Color> tempPathsList) {
        this.tempPathsList = tempPathsList;
        setUpdateNeeded(true);
    }

    public void addToTempPathsList(Map<Path, Color> tempPathsList) {
        this.tempPathsList.putAll(tempPathsList);
        setUpdateNeeded(true);
    }

    public boolean isUpdateNeeded() {
        return this.updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    @Override
    protected void doPreRender() {
        Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
        this.shader.loadLights(false, viewMatrix);
        this.shader.loadViewMatrix(viewMatrix);
        GL11.glLineWidth(5);
    }

    @Override
    protected void doRender(Set<Map.Entry<AbstractModel, List<ModelEntity>>> entrySet) {
        for (var entry : entrySet) {
            AbstractModel abstractModel = entry.getKey();
            List<ModelEntity> modelEntities = entry.getValue();
            Vao vao = abstractModel.getVao();
            vao.bind();
//            AbstractModel texture = .getComponent(SingleModelComponent.class).getModel().getModel();
//            prepareTexturedModel(texture);
//            Vao vao = texture.getVao();
//            vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::bind);//TEMP TODO
//            GL11.glDrawElements(GL11.GL_LINES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
//            vao.getIndexVbos().values().stream().findFirst().ifPresent(Vbo::unbind);//TEMP TODO
            vao.unbind();
            unbindTexturedModel();
        }

        GL11.glLineWidth(2);

        this.shader.stop();
    }

    private void prepareTexturedModel(AbstractModel model) {
        if (model == null)
            return;

//        ModelTexture texture = model.getModelFile();
//        Vao vao = model.getVao();
//        GL30.glBindVertexArray(vao.getId());
//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        GL20.glEnableVertexAttribArray(2);
//        ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());
//
//        ((GameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
//        ((GameObjectShader) this.shader).loadUseNormalMap(false);
//        ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//        ((GameObjectShader) this.shader).loadIsInstanced(false);
//        ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
//        ((GameObjectShader) this.shader).loadColor(texture.getColor());
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
    }

    private void unbindTexturedModel() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void removePath(Path path) {
        this.tempPathsList.remove(path);
    }
}