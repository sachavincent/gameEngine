package renderEngine.fontRendering;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import java.util.Map;
import java.util.Set;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.shaders.FontShader;

public class FontRenderer {

    private final FontShader shader;

    public FontRenderer() {
        this.shader = new FontShader();
    }

    public void render(Map<FontType, Set<Text>> texts) {
        prepare();
        for (FontType font : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
            for (Text text : texts.get(font)) {
                renderText(text);
            }
        }
        endRendering();
    }

    public void cleanUp() {
        this.shader.cleanUp();
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.shader.start();
    }

    private void renderText(Text text) {
        GL30.glBindVertexArray(text.getMesh());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        this.shader.loadColor(text.getColor());
        this.shader.loadTranslation(text.getPosition());
        this.shader.loadCharWidths(text.getCharWidth(), text.getEdgeCharWidth());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void endRendering() {
        this.shader.stop();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}