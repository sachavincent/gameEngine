package engineTester;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

import display.Display;
import display.DisplayManager;
import guis.prefabs.GuiDebug;
import guis.prefabs.GuiEscapeMenu;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import guis.prefabs.GuiItemSelection;
import guis.prefabs.GuiMainMenu.GuiMainMenu;
import guis.prefabs.GuiSelectedItem;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.FrustumCullingFilter;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.PathRenderer;
import renderEngine.fontRendering.TextMaster;
import renderEngine.shaders.WaterShader;
import scene.gameObjects.GameObjectData;
import scene.gameObjects.GameObjectDatas;
import scene.gameObjects.GameObjects;
import scene.gameObjects.Light;
import scene.gameObjects.NPC;
import scene.gameObjects.Terrain;
import util.KeybindingsManager;
import util.SettingsManager;
import util.TimeSystem;
import util.Utils;
import util.math.Vector3f;
import water.WaterFrameBuffers;

public class MainGameLoop {

    public static void main(String[] args) {
//        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
//        if (!isDebug) {
        Display.createDisplay();
        SettingsManager.loadSettings();
        KeybindingsManager.loadKeyBindings();
        GuiDebug.getInstance();

//        double xoff = 0, yoff;
//        OpenSimplex2S simplex = new OpenSimplex2S((long) (Math.random() * 1000000L));
//        int w = 1024;
//        int h = w;
//        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
//        for (int x = 0; x < w; x++) {
//            yoff = 0;
//            for (int y = 0; y < h; y++) {
//                int noise = (int) (((simplex.noise2(xoff, yoff) + 1) / 2) * 256);
//                int value = noise << 16 | noise << 8 | noise;
//                image.setRGB(x, y, value);
//                yoff += 0.008;
////                yoff += y;
//            }
//            xoff += 0.008;
////            xoff += x;
//        }
//        File file = new File(RES_PATH + "/" + "test.png");
//        try {
//            System.out.println("Done with noise!");
//            ImageIO.write(image, "png", file);
//            System.out.println("Image noise saved!");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        } else {
//            DisplayManager.createDisplayForTests();
//        }

        /*InputStream stream = null;
        try {
            stream = new FileInputStream(RES_PATH + "/cursor.png");
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
        Rome.setOnGameStarted(game -> {
            GameObjects.init();

            Terrain terrain = new Terrain();
            terrain.placeAt(new Vector3f(0, 0, 0));
            NPC npc = new NPC();
            {
//            new Light(new Vector3f(0, 12, 0), new Vector3f(1.3f, 1.3f, 1.3f),new Vector3f(1, 0, 0));
//            new Light(new Vector3f(150, 12, 0), new Vector3f(1.3f, 1.3f, 1.3f),new Vector3f(1, 0, 0));
//            new Light(new Vector3f(150, 12, 150), new Vector3f(1.3f, 1.3f, 1.3f),new Vector3f(1, 0, 0));
//            new Light(new Vector3f(0, 12, 150), new Vector3f(1.3f, 1.3f, 1.3f),new Vector3f(1, 0, 0));
            }

            new Light.Sun(new Vector3f(1.3f, 1.3f, 1.3f), 300f);
        });

        GuiEscapeMenu.getInstance();
        GuiItemSelection.getInstance();
        PostProcessing.init();

        GameObjectData insulaOBJ = GameObjectDatas.INSULA; // Loading models (must be done before loop)

        GuiHouseDetails.getInstance(); // Loading GUI Instance (must be done before loop)
        GuiSelectedItem.getInstance();
        GuiDebug.getInstance();
        GuiMainMenu.getInstance().setDisplayed(true);
        TextMaster textMaster = TextMaster.getInstance();

        FrustumCullingFilter.updateFrustum();
        // Listeners at the end, after initializing all GUIs
        MouseUtils.setupListeners();
        KeyboardUtils.setupListeners();
        Rome.updateGuis();

        Fbo fbo = new Fbo(Display.getWindow().getWidth(), Display.getWindow().getHeight(), Fbo.DEPTH_RENDER_BUFFER);

        long lastUpdate = System.nanoTime();
        int nbFrames = 0;
        TimeSystem.resetTimer();
        int nbTicks = 0;
        long startTime = TimeSystem.getTimeMillis();

        List<Integer> tpsList = new ArrayList<>();
        while (!Display.getWindow().shouldClose()) {
            if (Display.isFramerateLimited()) {
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
                Rome.processLogic();
                DisplayManager.MSPT = Utils.formatDoubleToNDecimals((double) (System.nanoTime() - start) / 1000000d, 2);
                nbTicks++;
            }

            Rome.processRendering(fbo);
            nbFrames++;


            glfwPollEvents();
            Display.getWindow().swapBuffers();
            glfwPollEvents();

            while (TimeSystem.getTimeMillis() >= startTime + 1000L) {
                startTime += 1000;

                DisplayManager.CURRENT_FPS = nbFrames;

                tpsList.add(0, nbTicks);
                if (tpsList.size() > 5) {
                    tpsList.remove(tpsList.size() - 1);
                    List<Integer> copy = new ArrayList<>(tpsList);
                    Collections.sort(copy);
                    DisplayManager.TPS = copy.get(2);
                }
                nbFrames = 0;
                nbTicks = 0;
                GuiDebug.getInstance().updateDebugGui();
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

        Display.stop();
    }

}