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
import java.util.Deque;
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
import scene.gameObjects.Light;
import scene.gameObjects.NPC;
import scene.gameObjects.OBJGameObject;
import scene.gameObjects.Terrain;
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

        GuiEscapeMenu.getInstance();
        GuiItemSelection.getInstance();

        PostProcessing.init();

        OBJGameObject insulaOBJ = OBJGameObjects.INSULA; // Loading models (must be done before loop)
        NPC npc = new NPC();

        GuiHouseDetails.getInstance(); // Loading GUI Instance (must be done before loop)
        GuiSelectedItem.getInstance();
        GuiMainMenu.getInstance().setDisplayed(true);
        TextMaster textMaster = TextMaster.getInstance();

        {
        }

        Scene.getInstance();
        Light sun = new Light(new Vector3f(75, 50, 75), new Vector3f(1, 1, 1));
        new Light(new Vector3f(-75, 50, -75), new Vector3f(1, 1, 1));
        new Light(new Vector3f(75, 50, -75), new Vector3f(1, 1, 1));
        new Light(new Vector3f(-75, 50, 75), new Vector3f(1, 1, 1));

        FrustumCullingFilter.updateFrustum();
        // Listeners at the end, after initializing all GUIs
        MouseUtils.setupListeners();
        KeyboardUtils.setupListeners();
        Game.getInstance().updateGuis();

        Fbo fbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        Vector4f clipPlane = new Vector4f(0, -1, 0, 1000000);
        MasterRenderer.setClipPlane(clipPlane);

        long lastTime = System.nanoTime();
        long unprocessed = 0;
        int nbFrames = 0;
        long now;
        long diff;
        int nbTicks = 0;
        int nbFramesIgnored = 0;
        double ns = 1000000000d / TimeSystem.TICK_RATE;
        long startTime = System.currentTimeMillis();


        while (!glfwWindowShouldClose(getWindow())) {
            TimeSystem.updateTimer();
            now = System.nanoTime();
            diff = now - lastTime;
            unprocessed += diff;
            lastTime = now;
            while (unprocessed >= DisplayManager.FRAME_CAP)
                unprocessed -= DisplayManager.FRAME_CAP;

            for (int i = 0; i < Math.min(10, TimeSystem.getElapsedTicks()); i++) {
                long start = System.nanoTime();
                Deque<Double> msptList = GuiDebug.getInstance().msptList;
                Game.getInstance().processLogic();
                if (msptList.size() > 9)
                    msptList.removeFirst();
                msptList.add(Utils.formatDoubleToNDecimals(((double) System.nanoTime() - start) / 1000000d, 2));
                nbTicks++;
            }

            double timeForNextUpdate = now + ns;
            long nanoTime = System.nanoTime();
            boolean draw = nanoTime < timeForNextUpdate || nbFramesIgnored > DisplayManager.MAX_FRAMES_IGNORED;
//            boolean draw = true;
            if (draw) {
                Game.getInstance().processRendering(fbo);
                nbFrames++;
                nbFramesIgnored = 0;
            } else
                nbFramesIgnored++;

            if (System.currentTimeMillis() - startTime > 1000) {
//                Random r = new Random();
//                char c1 = (char) (r.nextInt(26) + 'a');
//                char c2 = (char) (r.nextInt(26) + 'a');
//                char c3 = (char) (r.nextInt(26) + 'a');
//                GuiDebug.getInstance().consoleLogs.getText()
//                        .addTextString(c1 + "\n\n" + c2 + "\n\n" + c3 + "\n\n", Color.WHITE);
//                System.out.println("t");
                startTime += 1000;

                DisplayManager.FPS = nbFrames;
                nbFrames = 0;
                Deque<Integer> tpsList = GuiDebug.getInstance().tpsList;
                if (tpsList.size() > 9)
                    tpsList.removeFirst();
                tpsList.add(nbTicks);
                DisplayManager.TPS = nbTicks;
                nbTicks = 0;
                GuiDebug.getInstance().updateInfoGui();
            }
            if (draw)
                glfwSwapBuffers(DisplayManager.getWindow());
            glfwPollEvents();
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