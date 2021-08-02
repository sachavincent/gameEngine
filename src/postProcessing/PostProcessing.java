package postProcessing;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class PostProcessing {

    private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};

    private static RawModel quad;

    private static ContrastChanger     contrastChanger;
    private static HorizontalBlur      horizontalBlur;
    private static VerticalBlur        verticalBlur;
    private static MonochromaticFilter monochromaticFilter;
    private static PassthroughFilter   passthroughFilter;

    public static void init() {
        quad = Loader.getInstance().loadToVAO(POSITIONS, 2);
        contrastChanger = new ContrastChanger();
        monochromaticFilter = new MonochromaticFilter();
        passthroughFilter = new PassthroughFilter();

        horizontalBlur = new HorizontalBlur(DisplayManager.WIDTH, DisplayManager.HEIGHT);
        verticalBlur = new VerticalBlur(DisplayManager.WIDTH, DisplayManager.HEIGHT);
    }

    public static void doPostProcessing(int colourTexture) {
        start();

//        horizontalBlur.render(colourTexture);
//        verticalBlur.render(horizontalBlur.getOutputTexture());
//        monochromaticFilter.render(colourTexture);
//        contrastChanger.render(verticalBlur.getOutputTexture());

        passthroughFilter.render(colourTexture);

        end();
    }

    public static void cleanUp() {
        horizontalBlur.cleanUp();
        verticalBlur.cleanUp();
        contrastChanger.cleanUp();
        monochromaticFilter.cleanUp();
    }

    private static void start() {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
    }
}