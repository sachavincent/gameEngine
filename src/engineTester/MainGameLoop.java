package engineTester;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static renderEngine.DisplayManager.FPS;
import static renderEngine.DisplayManager.getWindow;

import abstractItem.ItemPreviews;
import entities.Camera;
import entities.Entity;
import entities.Light;
import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import guis.constraints.PatternConstraints;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiEscapeMenu.MenuButton;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiSelectedItem;
import guis.presets.GuiBackground;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import language.TextConverter;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import shaders.WaterShader;
import textures.FontTexture;
import util.Timer;
import util.math.Vector3f;
import util.math.Vector4f;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterTile;

public class MainGameLoop {

    public static void main(String[] args) {
//        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);

        glfwInit();
        DisplayManager.createDisplay();
        glfwSwapInterval(1);
        GL.createCapabilities();
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        int hotspotX = 3;
        int hotspotY = 6;
        /*InputStream stream = null;
        try {
            stream = new FileInputStream("res/cursor.png");
            BufferedImage image = ImageIO.read(stream);

            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            // convert image to RGBA format
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];

                    buffer.put((byte) ((pixel >> 16) & 0xFF));  // red
                    buffer.put((byte) ((pixel >> 8) & 0xFF));   // green
                    buffer.put((byte) (pixel & 0xFF));          // blue
                    buffer.put((byte) ((pixel >> 24) & 0xFF));  // alpha
                }
            }
            buffer.flip(); // this will flip the cursor image vertically

            // create a GLFWImage
            GLFWImage cursorImg = GLFWImage.create();
            cursorImg.width(width);     // set up image width
            cursorImg.height(height);   // set up image height
            cursorImg.pixels(buffer);   // pass image data
            // create custom cursor and store its ID
            long cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX, hotspotY);

            // set current cursor
            GLFW.glfwSetCursor(DisplayManager.getWindow(), cursorID);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        TextConverter.loadDefaultLanguage();
//        TextConverter.loadLanguage(Language.FRENCH);

        Loader loader = Loader.getInstance();

        FontTexture roboto = new FontTexture("roboto.png");

        FontType font = new FontType(roboto.getTextureID(), new File("res/roboto.fnt"));


        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);


        List<Entity> entities = new ArrayList<>();

        MasterRenderer renderer = MasterRenderer.getInstance();

        Camera camera = Camera.getInstance();

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(),
                buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(75, -75, 0);
//        waters.add(water);

        new GuiEscapeMenu.Builder()
                .setBackground(new GuiBackground<>(new Color(109, 109, 109, 80)))
                .addButton(MenuButton.RESUME, ButtonType.RECTANGLE,
                        new GuiBackground<>(new Color(109, 109, 109, 100)))
                .addButton(MenuButton.SAVE_AND_QUIT, ButtonType.RECTANGLE,
                        new GuiBackground<>(new Color(109, 109, 109, 100)))
                .addButton(MenuButton.QUICK_SAVE, ButtonType.RECTANGLE,
                        new GuiBackground<>(new Color(109, 109, 109, 100)))
                .addButton(MenuButton.SETTINGS, ButtonType.RECTANGLE,
                        new GuiBackground<>(new Color(109, 109, 109, 100)))
                .addButton(MenuButton.QUIT, ButtonType.RECTANGLE,
                        new GuiBackground<>(new Color(109, 109, 109, 100)))
//                .setTransitionsToAllButtons(new SlidingTransition(Trigger.SHOW, 400, SlidingDirection.RIGHT), 200,
//                        true)
//                .setTransitionsToAllButtons(new SlidingTransition(Trigger.HIDE, 400, SlidingDirection.LEFT), 200,
//                        true)
                //.setFont TODO
//                .setTransitions(new SlidingTransition(Trigger.SHOW, 400, SlidingDirection.RIGHT))
//                .setTransitions(new SlidingTransition(Trigger.HIDE, 400, SlidingDirection.LEFT))
                .create();

        new GuiItemSelection.Builder()
                .setChildrenConstraints(new PatternConstraints(5, 3, .02f))
                .setBackground(new GuiBackground<>(new Color(109, 109, 109, 80)))
                .addButton(GuiItemSelection.MenuButton.DIRT_ROAD, ButtonType.RECTANGLE, ItemPreviews.DIRT_ROAD)
                .addButton(GuiItemSelection.MenuButton.INSULA, ButtonType.RECTANGLE, ItemPreviews.INSULA)
                .addButton(GuiItemSelection.MenuButton.MARKET, ButtonType.RECTANGLE, ItemPreviews.MARKET)
                .create();

        MouseUtils.setupListeners();
        KeyboardUtils.setupListeners();

        Fbo fbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        double frameCap = 1.0 / FPS;
        double time = Timer.getTime();
        double unprocessed = 0;

        double frameTime = 0;

        GuiRenderer guiRenderer = GuiRenderer.getInstance();

        PostProcessing.init(loader);

        new GuiSelectedItem.Builder().create();

        TextMaster textMaster = TextMaster.getInstance();
        while (!glfwWindowShouldClose(getWindow())) {
            boolean canRender = false;
            double time2 = Timer.getTime();
            double diff = time2 - time;
            unprocessed += diff;
            frameTime += diff;
            time = time2;

            while (unprocessed >= frameCap) {
                unprocessed -= frameCap;
                canRender = true;

//                if (glfwGetKey(DisplayManager.getWindow(), GLFW_KEY_ESCAPE) == GL_TRUE)
//                    DisplayManager.closeDisplay();


                if (frameTime >= 1.0) {
                    frameTime = 0;
//                    System.out.println("FPS: " + frames);
                }
            }

            if (canRender) {
                DisplayManager.updateDisplay();

                camera.move();

                GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

                buffers.bindReflectionFrameBuffer();

                renderer.renderScene(entities, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f));

                buffers.bindRefractionFrameBuffer();
                renderer.renderScene(entities, lights, camera, new Vector4f(0, -1, 0, water.getHeight() + 1f));

                GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

                buffers.unbindCurrentFrameBuffer();

                if (GuiEscapeMenu.getEscapeMenu().isDisplayed()) {
                    fbo.bindFrameBuffer();

                    renderer.renderScene(entities, lights, camera, new Vector4f(0, -1, 0, 1000000));
                    waterRenderer.render(waters, camera, sun);
                    fbo.unbindFrameBuffer();

                    PostProcessing.doPostProcessing(fbo.getColourTexture());
                } else {
                    renderer.renderScene(entities, lights, camera, new Vector4f(0, -1, 0, 1000000));
                    waterRenderer.render(waters, camera, sun);
                }
                renderer.renderScene(entities, lights, camera, new Vector4f(0, -1, 0, 1000000));
                waterRenderer.render(waters, camera, sun);

                guiRenderer.render();

                textMaster.render();//TODO: Handle double rendering w/ GuiRenderer
                System.gc();
            }
        }

        PostProcessing.cleanUp();
        fbo.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        renderer.cleanUp();
        guiRenderer.cleanUp();
        loader.cleanUp();
        textMaster.cleanUp();

        MouseUtils.freeCallbacks();
        KeyboardUtils.freeCallbacks();
        DisplayManager.freeCallbacks();

        GL.setCapabilities(null);
        DisplayManager.closeDisplay();
    }

}