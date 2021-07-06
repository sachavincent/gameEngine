package renderEngine;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import entities.Camera;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import pathfinding.Path;
import renderEngine.shaders.GameObjectShader;
import scene.components.SingleModelComponent;
import scene.gameObjects.GameObject;
import textures.ModelTexture;
import util.Vao;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector2f;

public class PathRenderer extends Renderer {

    private static PathRenderer instance;

    public static PathRenderer getInstance() {
        return instance == null ? (instance = new PathRenderer()) : instance;
    }

    private Map<Path, Color> tempPathsList = new HashMap<>();
    private boolean          updateNeeded;

    private PathRenderer() {
        super(new GameObjectShader());

        this.shader.start();
        ((GameObjectShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        this.shader.stop();
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
    public void render() {
        GL11.glLineWidth(5);

        for (GameObject gameObject : this.gameObjects) {
            TexturedModel texture = gameObject.getComponent(SingleModelComponent.class).getModel().getTexturedModel();
            prepareTexturedModel(texture);
            GL11.glDrawElements(GL11.GL_LINES, texture.getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }

        GL11.glLineWidth(2);

        this.gameObjects.clear();
        this.shader.stop();
    }

    public void prepareRender(GameObject gameObject) {
        if (!this.shader.isStarted()) {
            this.shader.start();
            Matrix4f viewMatrix = Camera.getInstance().getViewMatrix();
            ((GameObjectShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((GameObjectShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((GameObjectShader) this.shader)
                    .loadLights(false, LightRenderer.getInstance().getGameObjects(), viewMatrix);
            ((GameObjectShader) this.shader).loadViewMatrix(viewMatrix);
            ((GameObjectShader) this.shader)
                    .loadTransformationMatrix(Maths.createTransformationMatrix(new Vector2f(0, 0), new Vector2f(1, 1)));
            ((GameObjectShader) this.shader).loadOffset(0, 0);
        }

        this.gameObjects.add(gameObject);
    }

    private void prepareTexturedModel(TexturedModel texturedModel) {
        if (texturedModel == null)
            return;

        ModelTexture texture = texturedModel.getModelTexture();
        Vao vao = texturedModel.getVao();
        GL30.glBindVertexArray(vao.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ((GameObjectShader) this.shader).loadNumberOfRows(texture.getNumberOfRows());

        ((GameObjectShader) this.shader).loadUseFakeLighting(texture.doesUseFakeLighting());
        ((GameObjectShader) this.shader).loadUseNormalMap(false);
        ((GameObjectShader) this.shader).loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        ((GameObjectShader) this.shader).loadIsInstanced(false);
        ((GameObjectShader) this.shader).loadAlpha(texture.getAlpha());
        ((GameObjectShader) this.shader).loadColor(texture.getColor());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    public void removePath(Path path) {
        this.tempPathsList.remove(path);
    }
}