package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import renderEngine.shaders.SkyboxShader;
import renderEngine.structures.AttributeData;
import renderEngine.structures.AttributeData.DataType;
import renderEngine.structures.Data;
import renderEngine.structures.Vao;

public class SkyboxRenderer extends Renderer<SkyboxShader> {

    private static final float SIZE = 500f;

    private static final Float[] VERTICES = {
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

        AttributeData<Float> verticesAttribute = new AttributeData<>(0, 3, VERTICES, DataType.FLOAT);
        Data data = Data.createData(verticesAttribute);
        this.vao = Vao.createVao(data, data.getVaoType());
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