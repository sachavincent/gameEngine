package main.guis;

import java.util.List;
import main.models.RawModel;
import main.renderEngine.Loader;
import main.util.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GuiRenderer {

    private final RawModel quad;

    private GuiShader shader;

    public GuiRenderer(Loader loader) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};

        quad = loader.loadToVAO(positions, 2);

        shader = new GuiShader();
    }

    public void render(List<Gui> guis) {
        shader.start();

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        guis.forEach(gui -> {
            renderTexture(gui.getBackground());
            gui.getComponents().forEach(guiComponent -> renderTexture(guiComponent.getTexture()));
        });

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    private void renderTexture(GuiTexture guiTexture) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, guiTexture.getTextureID());
        shader.loadTransformation(
                Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale()));
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
