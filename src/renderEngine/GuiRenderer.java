package renderEngine;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import fontRendering.TextMaster;
import guis.Gui;
import guis.GuiTexture;
import guis.basics.GuiCircle;
import guis.basics.GuiEllipse;
import guis.basics.GuiShape;
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

    private final static float[] POSITIONS_FILLED   = {-1, 1, -1, -1, 1, 1, 1, -1};
    private final static float[] POSITIONS_UNFILLED = {-1, -1, -1, 1, 1, 1, 1, -1};

    private final Loader loader;

    private final RawModel defaultFilledQuad;
    private final RawModel defaultUnfilledQuad;

    private RawModel  quad;
    private GuiShader shader;

    public GuiRenderer(GuiShader shader, Loader loader) {
        this.loader = loader;

        this.defaultFilledQuad = loader.loadToVAO(POSITIONS_FILLED, 2);
        this.defaultUnfilledQuad = loader.loadToVAO(POSITIONS_UNFILLED, 2);
        this.quad = this.defaultFilledQuad;

        this.shader = shader;
    }

    public void render(List<Gui> guis) {
        shader.start();

        if (quad == null)
            this.quad = defaultFilledQuad;

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        guis.stream()
                .filter(Gui::isDisplayed)
                .forEach(gui -> {
                    renderQuad(gui.getBackground(), true);
                    gui.getComponents()
//                            .stream().filter(GuiComponent::isDisplayed)
                            .forEach(guiComponent -> {
                                if (guiComponent instanceof GuiCircle) {
                                    GuiCircle guiCircle = (GuiCircle) guiComponent;
                                    this.quad = guiCircle.isFilled() ? drawFilledCircle() : drawUnfilledCircle();

                                    renderCircle(guiCircle.getTexture(), guiCircle.isFilled());
                                } else if (guiComponent instanceof GuiEllipse) {
                                    GuiEllipse guiEllipse = (GuiEllipse) guiComponent;
                                    this.quad = guiEllipse.isFilled() ? drawFilledCircle() : drawUnfilledCircle();

                                    renderCircle(guiEllipse.getTexture(), guiEllipse.isFilled());
                                } else if (guiComponent instanceof GuiText) {
                                    GuiText guiText = (GuiText) guiComponent;
                                    guiText.getText().remove();

                                    TextMaster.loadText(guiText.getText());
                                } else if (guiComponent instanceof GuiShape) {
                                    GuiShape guiShape = (GuiShape) guiComponent;
                                    renderQuad(guiComponent.getTexture(), guiShape.isFilled());
                                } else
                                    renderQuad(guiComponent.getTexture(), true);
                            });

                    gui.animate();
                });
        shader.loadGuis(guis);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    private void renderCircle(GuiTexture guiTexture, boolean filled) {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        renderTexture(guiTexture);

        GL11.glDrawArrays(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP, 0, quad.getVertexCount());
    }

    private void renderQuad(GuiTexture guiTexture, boolean filled) {
        this.quad = filled ? defaultFilledQuad : defaultUnfilledQuad;

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GL11.glDisable(GL11.GL_BLEND);

        renderTexture(guiTexture);

        GL11.glDrawArrays(filled ? GL11.GL_TRIANGLE_STRIP : GL11.GL_LINE_LOOP, 0, quad.getVertexCount());
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

    private RawModel drawFilledCircle() {
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

    private RawModel drawUnfilledCircle() {
        float x = 0;
        float y = 0;
        float radius = 1;
        int numberOfSides = 800;
        int numberOfVertices = numberOfSides + 1;

        float twicePi = (float) (2.0f * Math.PI);

        float[] allCircleVertices = new float[(numberOfVertices) * 2];

        for (int i = 0; i < numberOfVertices; i++) {
            allCircleVertices[i * 2] = (float) (x + (radius * Math.cos(i * twicePi / numberOfSides)));
            allCircleVertices[(i * 2) + 1] = (float) (y + (radius * Math.sin(i * twicePi / numberOfSides)));
        }

        return loader.loadToVAO(allCircleVertices, 2);
    }
}