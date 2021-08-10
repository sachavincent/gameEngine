package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import renderEngine.shaders.SkyboxShader;
import util.parsing.ModelType;

public class SkyboxRenderer extends Renderer<SkyboxShader> {

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

    private final Vao vao;
    private final int texture;

    public static SkyboxRenderer getInstance() {
        return instance == null ? (instance = new SkyboxRenderer()) : instance;
    }

    public SkyboxRenderer() {
        super(new SkyboxShader(), s ->
                s.loadProjectionMatrix(MasterRenderer.getInstance().getProjectionMatrix()));

        this.vao = Vao.createVao(new MeshData(VERTICES), ModelType.DEFAULT);
        this.texture = Loader.getInstance().loadCubeMap(TEXTURE_FILES);
    }

    @Override
    public void render() {
        this.shader.start();
        GL11.glDepthMask(false);

        this.vao.bind();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, this.texture);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, VERTICES.length / 3);

        this.vao.unbind();
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);

        GL11.glDepthMask(true);
        this.shader.stop();
    }
}