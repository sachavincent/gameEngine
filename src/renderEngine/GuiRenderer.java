package renderEngine;

import static guis.basics.GuiRectangle.POSITIONS_FILLED;
import static guis.basics.GuiRectangle.POSITIONS_UNFILLED;
import static org.lwjgl.opengl.GL11.*;

import engineTester.Game;
import guis.GuiInterface;
import guis.GuiTexture;
import guis.basics.GuiShape;
import guis.presets.Background;
import guis.presets.GuiTextInput;
import guis.presets.graphs.GuiDonutGraph;
import java.awt.Color;
import java.util.List;
import java.util.Set;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import renderEngine.fontRendering.TextMaster;
import renderEngine.shaders.GuiShader;
import util.math.Maths;
import util.math.Vector2f;

public class GuiRenderer {

    public final static int DEFAULT       = 0;
    public final static int DONUT         = 1;
    public final static int PROGRESS_ICON = 2;
    public final static int CIRCLE        = 3;

    public final static RawModel filledQuad     = Loader.getInstance().loadToVAO(POSITIONS_FILLED, 2);
    public final static RawModel unfilledQuad   = Loader.getInstance().loadToVAO(POSITIONS_UNFILLED, 2);
    public final static RawModel unfilledCircle = drawUnfilledCircle();
    public final static RawModel filledCircle   = drawFilledCircle();

    private static final GuiShader shader = new GuiShader();

    private static boolean displayDebugOutlines;

    public static void render() {
        shader.start();

        GL30.glBindVertexArray(filledQuad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
//        GL11.glDisable(GL42.GL_MULTISAMPLE);

        TextMaster.getInstance().removeText();

        Game.getInstance().getGuiTextInputs().forEach(GuiTextInput::updateCursor);
        Game.getInstance().getDisplayedGuis()
                .forEach(gui -> {
                    gui.render();
                    gui.getAllComponents()
                            .stream().filter(guiComponent -> guiComponent.isDisplayed() /*||
                            gui.getComponentsHideTransitions().get(guiComponent).stream()
                                    .anyMatch(transition -> !transition.isDone() && transition.isStarted())*/)
                            .forEach(guiComponent -> {
//                                handleBasicsRendering(guiComponent);
                                guiComponent.render();
                                if (displayDebugOutlines)
                                    renderOutlineForDebug(guiComponent);
                            });
//                    if (displayDebugOutlines)
//                        renderOutlineForDebug(gui);
                });

//        guis.stream()
//                .filter(gui -> !gui.isDisplayed() && gui.getHideTransitions().stream().allMatch(Transition::isDone))
//                .forEach(gui -> {
//                    gui.getComponents().keySet()
//                            .stream().filter(guiComponent -> !guiComponent.isDisplayed())
//                            .forEach(guiComponent -> {
//                                if (guiComponent instanceof GuiText) {
//                                    GuiText guiText = (GuiText) guiComponent;
//                                    guiText.getText().remove();
//                                }
//                            });
//                });

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL42.GL_MULTISAMPLE);
//        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);

        GL20.glDisableVertexAttribArray(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public static void renderOutlineForDebug(GuiInterface guiInterface) {
        if (!guiInterface.displayDebugOutline())
            return;

        GL30.glBindVertexArray(unfilledQuad.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        glDisable(GL_BLEND);

        GL33.glLineWidth(3);
        loadTexture(guiInterface.getDebugOutline());
        shader.loadType(0);
        shader.loadCornerRadius(0);
//        shader.loadOutlineWidth(0);
//        shader.loadBorderColor(Color.WHITE);
        shader.loadFilled(false);
        shader.loadBorderColor(guiInterface.getDebugOutline().getColor());
        shader.loadBorderEnabled(true);

        GL33.glDrawArrays(GL_LINE_LOOP, 0, unfilledQuad.getVertexCount());

        GL33.glLineWidth(2);

        glEnable(GL_BLEND);
    }
//
//    public static void renderUnfilledShape(GuiShape guiShape, int renderingMode, float cornerRadius) {
//        glDisable(GL_BLEND);
//
//        GL33.glLineWidth((float) guiShape.getOutlineWidth());
//
//        loadTexture(guiShape.getTexture(), cornerRadius, 0, Color.BLACK);
//
//        GL33.glDrawArrays(renderingMode, 0, guiShape.getTemplate().getVertexCount());
//
//        GL33.glLineWidth(2);
//
//        glEnable(GL_BLEND);
//    }

    public static void loadGui(GuiInterface guiInterface) {
        assert guiInterface != null;

        GuiTexture guiTexture = guiInterface.getTexture();
        loadTexture(guiTexture);
        shader.loadType(guiInterface.getType());
        shader.loadCornerRadius(guiInterface.getCornerRadius());
        int outlineWidth = 0;
        boolean filled = true;
        Color borderColor = Color.BLACK;
        boolean borderEnabled = false;
        if (guiInterface instanceof GuiShape) {
            outlineWidth = ((GuiShape) guiInterface).getOutlineWidth();
            borderColor = ((GuiShape) guiInterface).getBorderColor();
            filled = ((GuiShape) guiInterface).isFilled();
            borderEnabled = ((GuiShape) guiInterface).isBorderEnabled();
        }

        shader.loadOutlineWidth(outlineWidth);
        shader.loadBorderColor(borderColor);
        shader.loadFilled(filled);
        shader.loadBorderEnabled(borderEnabled);
    }

    private static void loadTexture(GuiTexture guiTexture) {
        bindTexture(guiTexture);

        shader.loadTransformation(Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale()));
        shader.loadWidth(guiTexture.getScale().x);
        shader.loadHeight(guiTexture.getScale().y);
        shader.loadAlpha(guiTexture.getAlpha());
        shader.loadColor(guiTexture.getColor());
    }

    public static void loadPercentage(float percentage) {
        shader.loadPercentage(percentage);
    }

    public static RawModel drawFilledCircle() {
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

        return Loader.getInstance().loadToVAO(allCircleVertices, 2);
    }

    public static RawModel drawUnfilledCircle() {
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

        return Loader.getInstance().loadToVAO(allCircleVertices, 2);
    }

    public static void renderDonutGraph(GuiDonutGraph<?> guiDonutGraph) {
        GuiTexture guiTexture = new GuiTexture(Background.BLACK_BACKGROUND, guiDonutGraph.getOuterCircle());
        bindTexture(guiTexture);

//        System.out.println("pos : " + guiTexture.getPosition());
        shader.loadTransformation(Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale()));

        shader.loadType(DONUT);
        shader.loadInnerCircleRadius(guiDonutGraph.getInnerCircle().getWidth());
        shader.loadOuterCircleRadius(guiDonutGraph.getOuterCircle().getWidth());

        List<Vector2f> points = guiDonutGraph.getRenderPoints();
        Set<Color> colors = guiDonutGraph.getRenderColors();

//        points.add(new Vector2f(.7f, 0f));
        shader.loadDonutColors(colors);
        shader.loadDonutLines(points);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, unfilledCircle.getVertexCount());
    }

    public static void bindTexture(GuiTexture guiTexture) {
        assert guiTexture != null;

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_EYE_PLANE);
        GL11.glBindTexture(GL_TEXTURE_2D, guiTexture.getTextureID());
    }

    public static void cleanUp() {
        shader.cleanUp();
    }

    public static void switchDisplayDebugOutlines() {
        displayDebugOutlines = !displayDebugOutlines;
    }
}