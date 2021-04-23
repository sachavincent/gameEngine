package engineTester;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static renderEngine.DisplayManager.getWindow;

import engineTester.Game.GameState;
import entities.Camera;
import fontMeshCreator.FontType;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.prefabs.GuiSelectedItem;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import language.TextConverter;
import org.lwjgl.opengl.GL;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.FrustumCullingFilter;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.fontRendering.TextMaster;
import scene.*;
import scene.components.PositionComponent;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.gameObjects.Insula;
import scene.gameObjects.Light;
import scene.gameObjects.Market;
import scene.gameObjects.Terrain;
import renderEngine.shaders.WaterShader;
import terrains.TerrainPosition;
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
        boolean isDebug = java.lang.management.ManagementFactory.
                getRuntimeMXBean().
                getInputArguments().toString().indexOf("jdwp") >= 0;
        glfwInit();
//        if (!isDebug) {
        DisplayManager.createDisplay();
        SettingsManager.loadSettings();
//        } else {
//            DisplayManager.createDisplayForTests();
//        }

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


        MasterRenderer renderer = MasterRenderer.getInstance();
        Camera camera = Camera.getInstance();

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(),
                buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(75, -75, 0);
//        waters.add(water);

        GuiEscapeMenu.getInstance();
        GuiItemSelection.getInstance();

        Fbo fbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        PostProcessing.init();

        GuiHouseDetails.getInstance(); // Loading GUI Instance (must be done before loop)
        GuiSelectedItem.getInstance();
        GuiMainMenu.getInstance().setDisplayed(true);
        TextMaster textMaster = TextMaster.getInstance();

//        AbstractMarket.getInstance().place(new TerrainPosition(50, 50));
//
//        TerrainPosition[] positions = new TerrainPosition[15];
//        TerrainPosition[] positions2 = new TerrainPosition[14];
//        for (int i = 55; i < 70; i++)
//            positions[i - 55] = new TerrainPosition(50, i);
//        for (int i = 50; i < 64; i++)
//            positions2[i - 50] = new TerrainPosition(i, 70);

//        AbstractInsula.getInstance().place(new TerrainPosition(62, 74));
//        AbstractDirtRoadItem.getInstance().place(positions);
//        AbstractDirtRoadItem.getInstance().place(new TerrainPosition(51, 55));
//        AbstractDirtRoadItem.getInstance().place(new TerrainPosition(49, 55));
//        AbstractDirtRoadItem.getInstance().place(new TerrainPosition(51, 63));
//        AbstractDirtRoadItem.getInstance().place(new TerrainPosition(62, 71));

//        List<TerrainPosition> pos = Arrays.asList(positions2);
//        Collections.reverse(pos);
//        positions2 = pos.toArray(new TerrainPosition[0]);
//        AbstractDirtRoadItem.getInstance().place(positions2);
        Scene.getInstance();
//        DirtRoad dirtRoad = new DirtRoad();
        Light sun = new Light(new Vector3f(75, 50, 75), new Vector3f(1, 1, 1));
        new Light(new Vector3f(-75, 50, -75), new Vector3f(1, 1, 1));
        new Light(new Vector3f(75, 50, -75), new Vector3f(1, 1, 1));
        new Light(new Vector3f(-75, 50, 75), new Vector3f(1, 1, 1));

//        insula.addComponent(new PositionComponent(new TerrainPosition(62, 74)));
        Terrain terrain = new Terrain();
        terrain.addComponent(new PositionComponent(new Vector3f(0, 0, 0)));

        Insula insula = new Insula();
        insula.addComponent(new PositionComponent(new TerrainPosition(50, 50)));
        Market market = new Market();
        market.addComponent(new PositionComponent(new TerrainPosition(50, 80)));

        TerrainPosition[] positions = new TerrainPosition[23];
        for (int i = 53; i < 76; i++)
            positions[i - 53] = new TerrainPosition(50, i);

        GameObject.newInstances(DirtRoad.class, positions);

        FrustumCullingFilter.updateFrustum();
        // Listeners at the end, after initializing all GUIs
        MouseUtils.setupListeners();
        KeyboardUtils.setupListeners();
        Game.getInstance().updateGuis();

        double time = Timer.getTime();
        double unprocessed = 0;
        int frames = 0;
        double frameTime = 0;
        double time2;
        double diff;
        boolean canRender;
        Vector4f clipPlane = new Vector4f(0, -1, 0, 1000000);
        MasterRenderer.setClipPlane(clipPlane);
        while (!glfwWindowShouldClose(getWindow())) {
            canRender = false;
            time2 = Timer.getTime();
            diff = time2 - time;
            unprocessed += diff;
            frameTime += diff;
            time = time2;

            while (unprocessed >= DisplayManager.FRAME_CAP && !glfwWindowShouldClose(getWindow())) {
                unprocessed -= DisplayManager.FRAME_CAP;
                canRender = true;

//                if (glfwGetKey(DisplayManager.getWindow(), GLFW_KEY_ESCAPE) == GL_TRUE)
//                    DisplayManager.closeDisplay();


                if (frameTime >= 1.0) {
                    frameTime = 0;
//                    System.out.println("frames: " + frames);
                    GuiHouseDetails.getInstance().setCurrentCategoryPercentage(new Random().nextInt(100));
                    frames = 0;
                }
//                glfwPollEvents();
            }

            if (canRender) {
                glfwPollEvents();

                if (Game.getInstance().getGameState() == GameState.STARTED) {
                    camera.move();

                    MasterRenderer.renderScene();
                    Scene.getInstance().render();

                   Scene.getInstance().updateHighlightedPaths();
                } else {
                    fbo.bindFrameBuffer();
                    MasterRenderer.renderScene();
                    Scene.getInstance().render();

                    fbo.unbindFrameBuffer();
                    PostProcessing.doPostProcessing(fbo.getColourTexture());
                }
                GuiRenderer.render();
                textMaster.render();

                glfwSwapBuffers(DisplayManager.getWindow());
                frames++;
            }
        }

        SettingsManager.saveSettings();
        PostProcessing.cleanUp();
        fbo.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        renderer.cleanUp();
        GuiRenderer.cleanUp();
        loader.cleanUp();
        textMaster.cleanUp();

        MouseUtils.freeCallbacks();
        KeyboardUtils.freeCallbacks();
        DisplayManager.freeCallbacks();

        GL.setCapabilities(null);
        DisplayManager.closeDisplay();

        glfwTerminate();
    }

}