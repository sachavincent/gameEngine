package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import renderEngine.shaders.HorizontalBlurShader;

public class HorizontalBlur {

    private final ImageRenderer        renderer;
    private final HorizontalBlurShader shader;

    public HorizontalBlur(int targetFboWidth, int targetFboHeight) {
        shader = new HorizontalBlurShader();
        shader.start();
        shader.loadTargetWidth(targetFboWidth);
        shader.stop();
        renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
    }

    public void render(int texture) {
        shader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad(false);
        shader.stop();
    }

    public int getOutputTexture() {
        return renderer.getOutputTexture();
    }

    public void cleanUp() {
        renderer.cleanUp();
        shader.cleanUp();
    }

}
