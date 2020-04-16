package renderEngine;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();

        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();

        shader.stop();
    }

    public void render(Terrain terrain) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();

//            prepareColorlessTerrain(terrain);
//            terrain.setY(terrain.getY() + 0.01f);
//            loadModelMatrix(terrain);
//            GL11.glDrawElements(GL11.GL_LINES, terrain.getModelGrid().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
//            unbindTexturedModel();
//            terrain.setY(terrain.getY() - 0.01f);
    }

    private void prepareTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        bindTextures(terrain);

        shader.loadShineVariables(1, 0);
    }

    private void prepareColorlessTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModelGrid();

        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        bindColorlessTextures(terrain);

//        shader.loadShineVariables(1, 0);
    }

    private void bindTextures(Terrain terrain) {
        TerrainTexturePack texturePack = terrain.getTexturePack();
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

        if (terrain.getBlendMap() != null)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void bindColorlessTextures(Terrain terrain) {
        String file = "red.png";

        TerrainTexture backgroundTexture = new TerrainTexture(file);
        TerrainTexture rTexture = new TerrainTexture(file);
        TerrainTexture gTexture = new TerrainTexture(file);
        TerrainTexture bTexture = new TerrainTexture(file);

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
//        TerrainTexturePack texturePack = terrain.getTexturePack();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        if (texturePack.getBackgroundTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
        }
        if (texturePack.getrTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
        }

        if (texturePack.getgTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
        }

        if (texturePack.getbTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
        }

//        if (terrain.getBlendMap() != null)
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), terrain.getY(), terrain.getZ()), 0, 0, 0, 1);

        if (transformationMatrix == null)
            return;

        shader.loadTransformationMatrix(transformationMatrix);
    }

}
