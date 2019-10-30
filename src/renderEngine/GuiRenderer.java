package renderEngine;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import fontRendering.TextMaster;
import guis.Gui;
import guis.GuiTexture;
import guis.basics.GuiCircle;
import guis.basics.GuiEllipse;
import guis.basics.GuiText;
import java.util.List;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.GuiShader;
import util.Maths;

public class GuiRenderer {

    private final static float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};

    private final Loader loader;

    private final RawModel defaultQuad;

    private RawModel  quad;
    private GuiShader shader;

    public GuiRenderer(GuiShader shader, Loader loader) {
        this.loader = loader;

        this.defaultQuad = loader.loadToVAO(POSITIONS, 2);
        this.quad = this.defaultQuad;

        this.shader = shader;
    }

    public void render(List<Gui> guis) {
        shader.start();

        if (quad == null)
            this.quad = defaultQuad;

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        guis.stream()
                .filter(Gui::isDisplayed)
                .forEach(gui -> {
                    renderQuad(gui.getBackground());
                    gui.getComponents()
//                            .stream().filter(GuiComponent::isDisplayed)
                            .forEach(guiComponent -> {
                                if (guiComponent instanceof GuiCircle) {
                                    GuiCircle guiCircle = (GuiCircle) guiComponent;
                                    this.quad = drawCircle();

                                    renderCircle(guiCircle.getTexture());
                                } else if (guiComponent instanceof GuiEllipse) {
                                    GuiEllipse guiEllipse = (GuiEllipse) guiComponent;
                                    this.quad = drawCircle();

                                    renderCircle(guiEllipse.getTexture());
                                } else if (guiComponent instanceof GuiText) {
                                    GuiText guiText = (GuiText) guiComponent;
                                    guiText.getText().remove();
                                    TextMaster.loadText(guiText.getText());
                                } else
                                    renderQuad(guiComponent.getTexture());
                            });

                    gui.animate();
                });
        shader.loadGuis(guis);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    private void renderCircle(GuiTexture guiTexture) {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        renderTexture(guiTexture);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, quad.getVertexCount());

        quad = null;
    }

    private void renderQuad(GuiTexture guiTexture) {
        if (quad == null) {
            this.quad = defaultQuad;

            GL30.glBindVertexArray(quad.getVaoID());
            GL20.glEnableVertexAttribArray(0);
        }

        renderTexture(guiTexture);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
    }

    private void renderTexture(GuiTexture guiTexture) {
        if (guiTexture == null)
            return;

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, guiTexture.getTextureID());
        shader.loadTransformation(
                Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale()));
        shader.loadAlpha(guiTexture.getAlpha());
        shader.loadColor(guiTexture.getColor());
    }

    @Deprecated
    private RawModel drawCircleV1() {
        float cx = 0;
        float cy = 0;
        float r = 1;

        int num_segments = (int) (8000 * Math.sqrt(r));

        float theta = (float) (2 * Math.PI / num_segments);
        float c = (float) Math.tan(theta);

        float s = (float) Math.cos(theta);

        float x = r;

        float t;
        float y = 0;

        float[] te = new float[(num_segments * 2)];
        for (int ii = 0; ii < num_segments; ii++) {
            te[ii] = x + cx;
            te[ii + 1] = y + cy;

            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        return loader.loadToVAO(te, 2);
    }

    private RawModel drawCircle() {
        float x = 0;
        float y = 0;
        float radius = 1;
        int numberOfSides = 800;
        int numberOfVertices = numberOfSides + 2;

        float twicePi = (float) (2.0f * Math.PI);

        float[] allCircleVertices = new float[(numberOfVertices) * 2];

        allCircleVertices[0] = x;
        allCircleVertices[1] = y;

        for (int i = 1; i < numberOfVertices; i++) {
            allCircleVertices[i * 2] = (float) (x + (radius * Math.cos(i * twicePi / numberOfSides)));
            allCircleVertices[(i * 2) + 1] = (float) (y + (radius * Math.sin(i * twicePi / numberOfSides)));
        }

        return loader.loadToVAO(allCircleVertices, 2);
    }
}