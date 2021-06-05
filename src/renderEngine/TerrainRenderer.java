package renderEngine;

import static renderEngine.MasterRenderer.BLUE;
import static renderEngine.MasterRenderer.GREEN;
import static renderEngine.MasterRenderer.RED;

import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.shaders.TerrainShader;
import scene.components.PositionComponent;
import scene.components.TextureComponent;
import scene.components.TexturePackComponent;
import scene.gameObjects.GameObject;
import textures.TerrainTexturePack;
import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;

public class TerrainRenderer extends Renderer {

    private static TerrainRenderer instance;

    public static TerrainRenderer getInstance() {
        return instance == null ? (instance = new TerrainRenderer()) : instance;
    }

    private TerrainRenderer() {
        super(new TerrainShader());

        this.shader.start();

        ((TerrainShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        ((TerrainShader) this.shader).connectTextureUnits();

        this.shader.stop();
    }

    @Override
    public void render() {
        this.gameObjects.forEach(terrain -> {
            RawModel rawModel = prepareTerrain(terrain);
            if (rawModel != null) {
                Vector3f position = terrain.getComponent(PositionComponent.class).getPosition();
                loadModelMatrix(position);
                GL11.glLineWidth(2);
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                unbindTexturedModel();
            }
        });

        this.gameObjects.clear();
        this.shader.stop();
    }

    public void prepareRender(GameObject gameObject) {
        if (!this.shader.isStarted()) {
            this.shader.start();
            ((TerrainShader) this.shader).loadClipPlane(MasterRenderer.getClipPlane());
            ((TerrainShader) this.shader).loadSkyColor(RED, GREEN, BLUE);
            ((TerrainShader) this.shader).loadLights(LightRenderer.getInstance().getGameObjects());
            ((TerrainShader) this.shader).loadViewMatrix();
        }

        this.gameObjects.add(gameObject);
    }

    private RawModel prepareTerrain(GameObject terrain) {
        TextureComponent textureComponent = terrain.getComponent(TextureComponent.class);
        if (textureComponent == null)
            return null;

        TexturedModel texture = textureComponent.getTexture();
        if (texture == null)
            return null;

        RawModel rawModel = texture.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        TexturePackComponent texturePackComponent = terrain.getComponent(TexturePackComponent.class);
        if (texturePackComponent == null)
            return null;

        bindTextures(terrain);

        ((TerrainShader) this.shader).loadShineVariables(1, 0);
        return rawModel;
    }

    private void bindTextures(GameObject terrain) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        TexturePackComponent texturePackComponent = terrain.getComponent(TexturePackComponent.class);
        if (texturePackComponent == null)
            return;
        TerrainTexturePack texturePack = texturePackComponent.getTerrainTexturePack();

        if (texturePack.getBackgroundTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
        }

        if (texturePack.getRedTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getRedTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
        }

        if (texturePack.getGreenTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getGreenTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
        }

        if (texturePack.getBlueTexture() != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBlueTexture().getTextureID());
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
        }

//        if (terrain.getBlendMap() != null && texturePack.equals(terrain.getTexturePack()))
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Vector3f position) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(position.getX(), 0, position.getZ()), 0, 0, 0, 1);

        if (transformationMatrix == null)
            return;

        ((TerrainShader) this.shader).loadTransformationMatrix(transformationMatrix);
    }
}