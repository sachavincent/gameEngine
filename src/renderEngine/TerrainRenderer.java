package renderEngine;

import java.util.ArrayList;
import java.util.List;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class TerrainRenderer {

    private final TerrainShader shader;

    private final Terrain terrain;

    private List<RawModel> paths = new ArrayList<>();

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        terrain = Terrain.getInstance();

        this.shader = shader;
        shader.start();

        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();

        shader.stop();
    }

    public void setPaths(List<RawModel> paths) {
        this.paths = paths;
    }

    public void render() {
        GL11.glLineWidth(2);

        prepareTerrain();
        loadModelMatrix();
        GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        unbindTexturedModel();

        shader.loadUniformColor(true);
        prepareColorlessTerrain();
        loadModelMatrix();
        terrain.setY(terrain.getY() + 0.01f);
        GL11.glDrawElements(GL11.GL_LINES, terrain.getModelGrid().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        unbindTexturedModel();
        terrain.setY(terrain.getY() - 0.01f);

        GL11.glLineWidth(5);
        shader.loadUniformColor(true);
        for (RawModel path : paths) {
            GL30.glBindVertexArray(path.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            bindTextures(terrain.getBlueTexturePack());

            GL11.glDrawElements(GL11.GL_LINES, path.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }

        shader.loadUniformColor(false);
    }

    private void prepareTerrain() {
        RawModel rawModel = terrain.getModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        bindTextures(terrain.getTexturePack());

        shader.loadShineVariables(1, 0);
    }

    private void prepareColorlessTerrain() {
        RawModel rawModel = terrain.getModelGrid();

        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        bindTextures(terrain.getRedTexturePack());

//        shader.loadShineVariables(1, 0);
    }

    private void bindTextures(TerrainTexturePack texturePack) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        if (texturePack.getBackgroundTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
        }

        if (texturePack.getrTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
        }

        if (texturePack.getgTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
        }

        if (texturePack.getbTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
        }

        if (terrain.getBlendMap() != null && texturePack.equals(terrain.getTexturePack()))
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix() {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), terrain.getY(), terrain.getZ()), 0, 0, 0, 1);

        if (transformationMatrix == null)
            return;

        shader.loadTransformationMatrix(transformationMatrix);
    }

}
