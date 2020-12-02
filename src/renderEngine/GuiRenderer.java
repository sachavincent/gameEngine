package renderEngine;

import static guis.basics.GuiRectangle.POSITIONS_FILLED;
import static guis.basics.GuiRectangle.POSITIONS_UNFILLED;
import static org.lwjgl.opengl.GL11.*;

import fontRendering.TextMaster;
import guis.Gui;
import guis.GuiComponent;
import guis.GuiInterface;
import guis.GuiTexture;
import guis.basics.GuiBasics;
import guis.basics.GuiEllipse;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.presets.Background;
import guis.presets.GuiPreset;
import guis.presets.graphs.GuiDonutGraph;
import guis.transitions.Transition;
import java.awt.Color;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL42;
import shaders.GuiShader;
import util.math.Maths;
import util.math.Vector2f;

public class GuiRenderer {

    private static GuiRenderer instance;

    private final Loader loader;

    private final RawModel defaultFilledQuad;
    private final RawModel defaultUnfilledQuad;

    private       RawModel  quad;
    private final GuiShader shader;

    private final List<Gui> guis;

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

    public void addGui(Gui gui) {
        this.guis.add(gui);
    }

    public void render() {
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
                            gui.getHideTransitions().stream()
                                    .anyMatch(transition -> !transition.isDone() && transition.isStarted()))
                        renderQuad(gui, true, gui.getCornerRadius());

                    gui.getComponents().keySet()
                            .stream().filter(guiComponent -> guiComponent.isDisplayed() ||
                            gui.getComponentsHideTransitions().get(guiComponent).stream()
                                    .anyMatch(transition -> !transition.isDone() && transition.isStarted()))
                            .forEach(guiComponent -> {
                                if (guiComponent instanceof GuiPreset) {
                                    if (guiComponent instanceof GuiDonutGraph) {
                                        drawDonut((GuiDonutGraph<?>) guiComponent);
                                    } else {
                                        try {
                                            ((GuiPreset) guiComponent).getBasics().stream().filter(Objects::nonNull)
                                                    .forEach(guiBasics -> {
                                                        try {
                                                            handleBasicsRendering(guiBasics);
                                                        } catch (ConcurrentModificationException ignored) {
                                                        }
                                                    });
                                        } catch (ConcurrentModificationException ignored) {
                                        }
                                    }
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

                                    TextMaster.getInstance().removeText(guiText.getText());
                                } else if (guiComponent instanceof GuiPreset) {
                                    List<GuiBasics> guiBasics = ((GuiPreset) guiComponent).getBasics();
                                    guiBasics.stream().filter(GuiText.class::isInstance)
                                            .map(guiText -> ((GuiText) guiText).getText())
                                            .forEach(text -> TextMaster.getInstance().removeText(text));
                                }
                            });
                });

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL42.GL_MULTISAMPLE);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    public List<Gui> getGuis() {
        return this.guis;
    }

    private void handleBasicsRendering(GuiComponent guiComponent) {
        TextMaster textMaster = TextMaster.getInstance();
        if (guiComponent instanceof GuiEllipse) {
            GuiEllipse guiEllipse = (GuiEllipse) guiComponent;
            this.quad = guiEllipse.isFilled() ? drawFilledCircle() : drawUnfilledCircle();

            renderCircle(guiEllipse, guiEllipse.isFilled());
        } else if (guiComponent instanceof GuiShape) {
            GuiShape guiShape = (GuiShape) guiComponent;
            renderQuad(guiShape, guiShape.isFilled(), guiComponent.getCornerRadius());
        } else if (guiComponent instanceof GuiText) {
            GuiText guiText = (GuiText) guiComponent;

            textMaster.removeText(guiText.getText());
            textMaster.loadText(guiText.getText());
        } else
            renderQuad(guiComponent, true, guiComponent.getCornerRadius());
    }

    private void renderCircle(GuiShape guiShape, boolean filled) {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (!filled) { // Probably temp
            renderUnfilledShape(guiShape, GL11.GL_LINE_STRIP, 0);
        } else {
            renderTexture(guiShape.getTexture(), 0);

            GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, quad.getVertexCount());
        }
    }

    private void renderUnfilledShape(GuiShape guiShape, int renderingMode, float cornerRadius) {
        glDisable(GL_BLEND);

        GL33.glLineWidth((float) guiShape.getOutlineWidth());

        renderTexture(guiShape.getTexture(), cornerRadius);

        GL33.glDrawArrays(renderingMode, 0, quad.getVertexCount());

        GL33.glLineWidth(2);

        glEnable(GL_BLEND);
    }

    private void renderQuad(GuiInterface guiComponent, boolean filled, float cornerRadius) {
        this.quad = filled ? defaultFilledQuad : defaultUnfilledQuad;

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (!filled) {
            if (guiComponent instanceof GuiShape)
                renderUnfilledShape((GuiShape) guiComponent, GL11.GL_LINE_LOOP, cornerRadius);
            else {
                renderTexture(guiComponent.getTexture(), cornerRadius);

                GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, quad.getVertexCount());
            }
        } else {
            renderTexture(guiComponent.getTexture(), cornerRadius);

            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
    }

    private void renderTexture(GuiTexture guiTexture, float cornerRadius) {
        if (guiTexture == null)
            return;

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_EYE_PLANE);
        GL11.glBindTexture(GL_TEXTURE_2D, guiTexture.getTextureID());

        shader.loadTransformation(Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale()));
        shader.loadIsDonut(false);
        shader.loadWidth(guiTexture.getScale().x);
        shader.loadHeight(guiTexture.getScale().y);

        shader.loadAlpha(guiTexture.getAlpha());
        shader.loadColor(guiTexture.getColor());

        shader.loadRadius(cornerRadius);
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

    private void drawDonut(GuiDonutGraph<?> guiDonutGraph) {
        this.quad = drawUnfilledCircle();

        GL30.glBindVertexArray(this.quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GuiTexture guiTexture = new GuiTexture(Background.BLACK_BACKGROUND, guiDonutGraph.getOuterCircle());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_EYE_PLANE);
        GL11.glBindTexture(GL_TEXTURE_2D, guiTexture.getTextureID());

//        System.out.println("pos : " + guiTexture.getPosition());
        this.shader.loadTransformation(Maths.createTransformationMatrix(
                guiTexture.getPosition(), guiTexture.getScale()));

        this.shader.loadIsDonut(true);
        this.shader.loadInnerCircleRadius(guiDonutGraph.getInnerCircle().getFinalWidth());
        this.shader.loadOuterCircleRadius(guiDonutGraph.getOuterCircle().getFinalWidth());
        Vector2f center = new Vector2f(0, 0);
        this.shader.loadCenter(center);


        List<Vector2f> points = guiDonutGraph.getRenderPoints();
        Set<Color> colors = guiDonutGraph.getRenderColors();

//        points.add(new Vector2f(.7f, 0f));
        this.shader.loadDonutColors(colors);
        this.shader.loadDonutLines(points);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, this.quad.getVertexCount());

        renderCircle(guiDonutGraph.getInnerCircle(), false);
        renderCircle(guiDonutGraph.getOuterCircle(), false);
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}