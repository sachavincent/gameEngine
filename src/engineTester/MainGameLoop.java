package engineTester;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static renderEngine.DisplayManager.getWindow;

import guis.prefabs.GuiDebug;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.prefabs.GuiSelectedItem;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import items.OBJGameObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.opengl.GL;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.FrustumCullingFilter;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.PathRenderer;
import renderEngine.fontRendering.TextMaster;
import renderEngine.shaders.WaterShader;
import scene.Scene;
import scene.components.PositionComponent;
import scene.gameObjects.*;
import terrains.TerrainPosition;
import util.KeybindingsManager;
import util.SettingsManager;
import util.TimeSystem;
import util.Utils;
import util.math.Vector3f;
import util.math.Vector4f;
import water.WaterFrameBuffers;

public class MainGameLoop {

    public static void main(String[] args) {
//        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        glfwInit();
//        if (!isDebug) {
        DisplayManager.createDisplay();
        SettingsManager.loadSettings();
        KeybindingsManager.loadKeyBindings();
        GuiDebug.getInstance();
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


//        TextConverter.loadLanguage(Language.FRENCH);

        Loader loader = Loader.getInstance();

        MasterRenderer renderer = MasterRenderer.getInstance();
        PathRenderer.getInstance();

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();

        Terrain terrain = new Terrain();
        terrain.addComponent(new PositionComponent(new Vector3f(0, 0, 0)));

        Game.getInstance();
        GuiEscapeMenu.getInstance();
        GuiItemSelection.getInstance();
        PostProcessing.init();

        OBJGameObject insulaOBJ = OBJGameObjects.INSULA; // Loading models (must be done before loop)
        NPC npc = new NPC();

        GuiHouseDetails.getInstance(); // Loading GUI Instance (must be done before loop)
        GuiSelectedItem.getInstance();
        GuiDebug.getInstance();
        GuiMainMenu.getInstance().setDisplayed(true);
        TextMaster textMaster = TextMaster.getInstance();

        {
            Market market = GameObject.newInstance(Market.class, new TerrainPosition(51, 81));
            Insula insula = GameObject.newInstance(Insula.class, new TerrainPosition(49, 89));
            for (int z = 75; z < 87; z++)
                GameObject.newInstance(DirtRoad.class, new TerrainPosition(45, z));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(44, 75));
            GameObject.newInstance(DirtRoad.class, new TerrainPosition(46, 75));
//            Utils.getPositionsFromConsoleLogs("test.log").forEach((clazz, positions) -> {
//                GameObject.newInstances(clazz, positions);
//            });
//            TerrainPosition v1 = new TerrainPosition(27, 13);
//            TerrainPosition v2 = new TerrainPosition(22, 20);
//
//            int[] roadPositions = new int[]{
//                    v1.getX(), v1.getZ(),
//                    v2.getX(), v2.getZ(),
//                    22,19,
//                    22,18,
//                    22,17,
//                    22,16,
//                    23,16,
//                    24,16,
//                    25,16,
//                    22,15,
//                    23,15,
//                    24,15,
//                    27,14,
//                    27,15,
//                    27,16,
//                    26,16,
//            };
//
//            TerrainPosition[] roads = TerrainPosition.toPositionArray(roadPositions);
//
//            GameObject.newInstances(DirtRoad.class, roads);
        }

        Scene.getInstance();
        new Light(new Vector3f(-250, 150, -250), new Vector3f(2.2f, 2.2f, 2.2f));
        new Light(new Vector3f(250, 150, 250), new Vector3f(2.2f, 2.2f, 2.2f));
        new Light(new Vector3f(-250, 150, 250), new Vector3f(2.2f, 2.2f, 2.2f));
        new Light(new Vector3f(250, 150, -250), new Vector3f(2.2f, 2.2f, 2.2f));

        FrustumCullingFilter.updateFrustum();
        // Listeners at the end, after initializing all GUIs
        MouseUtils.setupListeners();
        KeyboardUtils.setupListeners();
        Game.getInstance().updateGuis();

        Fbo fbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        Vector4f clipPlane = new Vector4f(0, -1, 0, 1000000);
        MasterRenderer.setClipPlane(clipPlane);

        long lastUpdate = System.nanoTime();
        int nbFrames = 0;
        TimeSystem.resetTimer();
        int nbTicks = 0;
        long startTime = TimeSystem.getTimeMillis();

        List<Integer> tpsList = new ArrayList<>();
        while (!glfwWindowShouldClose(getWindow())) {
            if (DisplayManager.isFramerateLimited()) {
                while (System.nanoTime() < lastUpdate + DisplayManager.getFramerateLimitNS()) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            lastUpdate = System.nanoTime();

            TimeSystem.updateTimer();

            int elapsedTicks = TimeSystem.getElapsedTicks();
            for (int i = 0; i < Math.min(10, elapsedTicks); i++) {
                long start = System.nanoTime();
                Game.getInstance().processLogic();
                DisplayManager.MSPT = Utils.formatDoubleToNDecimals((double) (System.nanoTime() - start) / 1000000d, 2);
                nbTicks++;
            }

            Game.getInstance().processRendering(fbo);
            nbFrames++;


            glfwPollEvents();
            glfwSwapBuffers(DisplayManager.getWindow());
            glfwPollEvents();

            while (TimeSystem.getTimeMillis() >= startTime + 1000L) {
                startTime += 1000;

                DisplayManager.FPS = nbFrames;

                tpsList.add(0, nbTicks);
                if (tpsList.size() > 5) {
                    tpsList.remove(tpsList.size() - 1);
                    List<Integer> copy = new ArrayList<>(tpsList);
                    Collections.sort(copy);
                    DisplayManager.TPS = copy.get(2);
                }
                nbFrames = 0;
                nbTicks = 0;
                GuiDebug.getInstance().updateInfoGui();
            }
        }

        SettingsManager.saveSettings();
        KeybindingsManager.saveKeyBindings();

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