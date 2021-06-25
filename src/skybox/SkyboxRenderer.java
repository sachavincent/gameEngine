package skybox;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.Renderer;
import renderEngine.shaders.SkyboxShader;
import scene.gameObjects.GameObject;

public class SkyboxRenderer extends Renderer {

    private static final float SIZE = 500f;

    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };

    private static final String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};

    private static SkyboxRenderer instance;

    private final RawModel cube;
    private final int      texture;

    public static SkyboxRenderer getInstance() {
        return instance == null ? (instance = new SkyboxRenderer()) : instance;
    }

    public SkyboxRenderer() {
        super(new SkyboxShader());

        this.cube = Loader.getInstance().loadToVAO(VERTICES, 3);
        this.texture = Loader.getInstance().loadCubeMap(TEXTURE_FILES);

        this.shader.start();
        ((SkyboxShader) this.shader).loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix());
        this.shader.stop();
    }

    public void render() {
        GL30.glBindVertexArray(this.cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, this.texture);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        GL30.glBindVertexArray(0);

        GL11.glDepthMask(true);
        this.shader.stop();
    }

    @Override
    public void prepareRender(GameObject gameObject) {
        this.shader.start();
        ((SkyboxShader) this.shader).loadViewMatrix();
        GL11.glDepthMask(false);
    }
}
