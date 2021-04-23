package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import renderEngine.shaders.ContrastShader;

public class ContrastChanger {

    private final ImageRenderer  renderer;
    private final ContrastShader shader;

    public ContrastChanger() {
        shader = new ContrastShader();
        renderer = new ImageRenderer();
    }

    public void render(int texture) {
        shader.start();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        renderer.renderQuad(false);

        shader.stop();
    }

    public void cleanUp() {
        renderer.cleanUp();
        shader.cleanUp();
    }
}
