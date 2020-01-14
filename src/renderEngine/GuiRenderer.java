package renderEngine;

import static guis.basics.GuiRectangle.POSITIONS_FILLED;
import static guis.basics.GuiRectangle.POSITIONS_UNFILLED;
import static org.lwjgl.opengl.GL11.*;

import fontRendering.TextMaster;
import guis.Gui;
import guis.GuiComponent;
import guis.GuiEscapeMenu;
import guis.GuiInterface;
import guis.GuiTexture;
import guis.basics.GuiBasics;
import guis.basics.GuiEllipse;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.presets.GuiPreset;
import guis.transitions.Transition;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import shaders.GuiShader;
import util.Maths;
import util.vector.Vector2f;

public class GuiRenderer {

    private static GuiRenderer instance;

    private final Loader loader;

    private final RawModel defaultFilledQuad;
    private final RawModel defaultUnfilledQuad;

    private RawModel  quad;
    private GuiShader shader;

    private List<Gui> guis;

    public static GuiRenderer getInstance() {
        return instance == null ? (instance = new GuiRenderer()) : instance;
    }

    private GuiRenderer() {
        this.loader = Loader.getInstance();

        this.defaultFilledQuad = loader.loadToVAO(POSITIONS_FILLED, 2);
        this.defaultUnfilledQuad = loader.loadToVAO(POSITIONS_UNFILLED, 2);
        this.quad = this.defaultFilledQuad;

        this.shader = new GuiShader();

        this.guis = new ArrayList<>();
    }

    public void renderGuis(List<Gui> guis) {
        guis.forEach(this::processGui);

        render();
    }

    private void render() {
        guis.add(GuiEscapeMenu.getEscapeMenu());

        shader.start();

        if (quad == null)
            this.quad = defaultFilledQuad;

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
//        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glDisable(GL42.GL_MULTISAMPLE);

        guis.stream()
//                .filter(gui -> gui.isDisplayed() ||
//                        gui.getHideTransitions().stream().anyMatch(transition -> !transition.isDone()) ||
//                        gui.getComponentsHideTransitions().values().stream().anyMatch(
//                                transitions -> transitions.stream().anyMatch(transition -> !transition.isDone())))
                .forEach(gui -> {
                    if (gui.isDisplayed() ||
                            gui.getHideTransitions().stream().anyMatch(transition -> !transition.isDone() && transition.isStarted()))
                        renderQuad(gui, true);

                    gui.getComponents().keySet()
                            .stream().filter(guiComponent -> guiComponent.isDisplayed() ||
                            gui.getComponentsHideTransitions().get(guiComponent).stream()
                                    .anyMatch(transition -> !transition.isDone() && transition.isStarted()))
                            .forEach(guiComponent -> {
                                if (guiComponent instanceof GuiPreset) {
                                    ((GuiPreset) guiComponent).getBasics().forEach(this::handleBasicsRendering);
                                } else
                                    handleBasicsRendering(guiComponent);
                            });
                    gui.animate();
                });

        guis.stream()
                .filter(gui -> !gui.isDisplayed() && gui.getHideTransitions().stream().allMatch(Transition::isDone))
                .forEach(gui -> {
                    gui.getComponents().keySet()
                            .stream().filter(guiComponent -> !guiComponent.isDisplayed())
                            .forEach(guiComponent -> {
                                if (guiComponent instanceof GuiText) {
                                    GuiText guiText = (GuiText) guiComponent;

                                    TextMaster.removeText(guiText.getText());
                                } else if (guiComponent instanceof GuiPreset) {
                                    List<GuiBasics> guiBasics = ((GuiPreset) guiComponent).getBasics();
                                    guiBasics.stream().filter(GuiText.class::isInstance)
                                            .map(guiText -> ((GuiText) guiText).getText())
                                            .forEach(TextMaster::removeText);
                                }
                            });
                });

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL42.GL_MULTISAMPLE);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        guis.clear();
        shader.stop();
    }

    private void handleBasicsRendering(GuiComponent<?> guiComponent) {
        if (guiComponent instanceof GuiEllipse) {
            GuiEllipse guiEllipse = (GuiEllipse) guiComponent;
            this.quad = guiEllipse.isFilled() ? drawFilledCircle() : drawUnfilledCircle();

            renderCircle(guiEllipse, guiEllipse.isFilled());
        } else if (guiComponent instanceof GuiShape) {
            GuiShape guiShape = (GuiShape) guiComponent;
            renderQuad(guiShape, guiShape.isFilled());
        } else if (guiComponent instanceof GuiText) {
            GuiText guiText = (GuiText) guiComponent;

            TextMaster.removeText(guiText.getText());
            TextMaster.loadText(guiText.getText());
        } else
            renderQuad(guiComponent, true);
    }

    private void renderCircle(GuiShape guiShape, boolean filled) {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (!filled) { // Probably temp
            renderUnfilledShape(guiShape, GL11.GL_LINE_STRIP);
        } else {
            renderTexture(guiShape.getTexture());

            GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, quad.getVertexCount());
        }
    }

    private void renderUnfilledShape(GuiShape guiShape, int renderingMode) {
        glDisable(GL_BLEND);

        Vector2f textureScale = guiShape.getTexture().getScale();
        float x = textureScale.x;
        float y = textureScale.y;
        IntStream.range(0, guiShape.getOutlineWidth()).forEach(width -> {
            textureScale.x -= (float) width / DisplayManager.WIDTH;

            textureScale.y -= (float) width / DisplayManager.HEIGHT;

            renderTexture(guiShape.getTexture());

            textureScale.x = x;
            textureScale.y = y;

            GL11.glDrawArrays(renderingMode, 0, quad.getVertexCount());
        });

        glEnable(GL_BLEND);
    }

    private void renderQuad(GuiInterface guiComponent, boolean filled) {
        this.quad = filled ? defaultFilledQuad : defaultUnfilledQuad;

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (!filled) {
            if (guiComponent instanceof GuiShape)
                renderUnfilledShape((GuiShape) guiComponent, GL11.GL_LINE_LOOP);
            else {
                renderTexture(guiComponent.getTexture());

                GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, quad.getVertexCount());
            }
        } else {
            renderTexture(guiComponent.getTexture());

            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
    }

    private void renderTexture(GuiTexture<?> guiTexture) {
        if (guiTexture == null)
            return;

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_EYE_PLANE);
        GL11.glBindTexture(GL_TEXTURE_2D, guiTexture.getTextureID());

        shader.loadTransformation(
                Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale()));

        shader.loadWidth(guiTexture.getScale().x);
        shader.loadHeight(guiTexture.getScale().y);

        shader.loadAlpha(guiTexture.getAlpha());
        shader.loadColor(guiTexture.getColor());

        shader.loadRadius(Gui.CORNER_RADIUS);
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

    public void cleanUp() {
        shader.cleanUp();
    }

    private void processGui(Gui gui) {
        guis.add(gui);
    }

}