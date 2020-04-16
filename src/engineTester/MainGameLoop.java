package engineTester;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static renderEngine.DisplayManager.FPS;
import static renderEngine.DisplayManager.getWindow;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import guis.Gui;
import guis.GuiEscapeMenu;
import guis.GuiEscapeMenu.MenuButton;
import guis.presets.GuiBackground;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.transitions.SlidingDirection;
import guis.transitions.SlidingTransition;
import guis.transitions.Transition.Trigger;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import items.Item;
import items.buildings.Insula;
import items.roads.DirtRoadItem;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import language.TextConverter;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.glfw.GLFW;
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
import renderEngine.OBJLoader;
import shaders.WaterShader;
import terrains.Terrain;
import textures.FontTexture;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.MousePicker;
import util.Timer;
import util.math.Maths;
import util.math.Vector3f;
import util.math.Vector4f;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterTile;

public class MainGameLoop {

    public static void main(String[] args) {
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

        TextMaster.init(loader);

        FontTexture roboto = new FontTexture("roboto.png");

        FontType font = new FontType(roboto.getTextureID(), new File("res/roboto.fnt"));

        TerrainTexture backgroundTexture = new TerrainTexture("blue.png");
        TerrainTexture rTexture = new TerrainTexture("blue.png");
        TerrainTexture gTexture = new TerrainTexture("green.png");
        TerrainTexture bTexture = new TerrainTexture("blue.png");

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture("white.png");

//
//        ModelData data = OBJFileLoader.loadOBJ("tree");
//        RawModel treeModel = loader
//                .loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());

        TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("tree", loader),
                new ModelTexture("tree.png"));

        TexturedModel cube = new TexturedModel(OBJLoader.loadObjModel("cube", loader),
                new ModelTexture("red.png"));

        cube.getModelTexture().setTransparent(true);
        cube.getModelTexture().setUseFakeLighting(true);
        cube.getModelTexture().setReflectivity(0);

        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
                new ModelTexture("grassTexture.png"));
        grass.getModelTexture().setTransparent(true);
        grass.getModelTexture().setUseFakeLighting(true);

        ModelTexture fernTextureAtlas = new ModelTexture("fernAtlas.png");
        fernTextureAtlas.setNumberOfRows(2);

        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
        fern.getModelTexture().setTransparent(true);

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);

        Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "black.png");

        List<Entity> entities = new ArrayList<>();
//
//        entities.add(new Entity(lamp, new Vector3f(185, -4.7f, -293), 0, 0, 0, 1));
//        entities.add(new Entity(lamp, new Vector3f(370, 4.2f, -300), 0, 0, 0, 1));
//        entities.add(new Entity(lamp, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));

//        Random random = new Random();
//        for (int i = 0; i < 999; i++) {
//            float x = random.nextFloat() * 800 - 400;
//            float z = random.nextFloat() * -600;
//            float y = terrain.getHeightOfTerrain(x, z);
//            if (i % 3 == 0)
//                entities.add(new Entity(tree, new Vector3f(x, y, z), 0, 0, 0, 3));
//            else if (i % 3 == 1)
//                entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1));
//            else if (i % 3 == 2)
//                entities.add(new Entity(fern, new Vector3f(x, y, z), 0, 0, 0, 0.6f, random.nextInt(4)));
//        }

        MasterRenderer renderer = MasterRenderer.getInstance();

        RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture("playerTexture.png"));
        Player player = new Player(stanfordBunny, new Vector3f(0, 5, 0), 0, 100, 0, 0.6f);
//        entities.add(player);
//        Camera camera = new Camera(terrain, new Vector3f(0, 30, 0), 20);
        Camera camera = new Camera(player);

        List<Gui> guis = new ArrayList<>();


        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(),
                buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(75, -75, 0);
//        waters.add(water);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

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
                .setTransitionsToAllButtons(new SlidingTransition(Trigger.SHOW, 400, SlidingDirection.RIGHT), 200,
                        true)
                .setTransitionsToAllButtons(new SlidingTransition(Trigger.HIDE, 400, SlidingDirection.LEFT), 200,
                        true)
                //.setFont TODO
                .setTransitions(new SlidingTransition(Trigger.SHOW, 400, SlidingDirection.RIGHT))
                .setTransitions(new SlidingTransition(Trigger.HIDE, 400, SlidingDirection.LEFT))
                .create();

        MouseUtils.setupListeners(guis, camera);
        KeyboardUtils.setupListeners(guis);

        Fbo fbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        double frameCap = 1.0 / FPS;
        double time = Timer.getTime();
        double unprocessed = 0;

        double frameTime = 0;

        GuiRenderer guiRenderer = GuiRenderer.getInstance();

        PostProcessing.init(loader);

        DirtRoadItem dirtRoad = new DirtRoadItem();

        final int[] rotY = {0};
        Random random = new Random();
        Insula item1 = new Insula();
//        item1.setScale(.5f);
        terrain.placeItem(item1, new Vector3f(0, 0.01, 0));
        while (!glfwWindowShouldClose(DisplayManager.getWindow())) {
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


                DisplayManager.updateDisplay();

                if (frameTime >= 1.0) {
                    frameTime = 0;

//                    System.out.println("FPS: " + frames);
                }
            }

            if (canRender) {
                player.move(terrain);
                camera.move();

//                picker.update();

                GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

                GLFW.glfwSetMouseButtonCallback(getWindow(), (w, button, action, mods) -> {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                        if (action == GLFW.GLFW_PRESS) {
                            picker.update();
                            Vector3f currentTerrainPoint = picker.getCurrentTerrainPoint();

                            if (currentTerrainPoint == null)
                                return;

                            currentTerrainPoint.x = 2 * (float) Math.floor(currentTerrainPoint.x / 2) + 1;
                            currentTerrainPoint.y = (float) Math.round(currentTerrainPoint.y) + 0.01f;
                            currentTerrainPoint.z = 2 * (float) Math.floor(currentTerrainPoint.z / 2) + 1;
//                            entities.add(new Entity(dirtRoad.getTexture(), currentTerrainPoint, 0,
//                                    90, 0, 1f, 3));
//                            System.out.println(currentTerrainPoint);
//                            System.out.println("Player: " + player.getPosition());
                            Insula item2 = new Insula();
////                            DirtRoadItem item = new DirtRoadItem();
//                            item.onClick(() -> {
//
//                            });
//                            item2.setScale(.5f);
                            terrain.placeItem(item2, currentTerrainPoint);
                        }
                    } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                        if (action == GLFW.GLFW_PRESS) {
//                            camera.setMiddleButtonPressed(true);
                            System.out.println("middle button");
                        } else if (action == GLFW.GLFW_RELEASE) {
//                            camera.setMiddleButtonPressed(false);
                        }
                    } else if (button == GLFW_MOUSE_BUTTON_2) {
                        if (action == GLFW.GLFW_PRESS) {
                            picker.update();
                            for (Entry<Vector3f, Item> entry : terrain.getItems().entrySet()) {
                                Vector3f pos = entry.getKey();
                                Item item = entry.getValue();

                                Vector3f min = item.getBoundingBox().getRawModel().getMin();
                                Vector3f max = item.getBoundingBox().getRawModel().getMax();

                                float minX = min.x;
                                float minY = min.y;
                                float minZ = min.z;

                                float maxX = max.x;
                                float maxY = max.y;
                                float maxZ = max.z;

                                Vector3f[] rec1 = new Vector3f[4];
                                Vector3f[] rec2 = new Vector3f[4];

                                rec1[0] = new Vector3f(minX + pos.x, minY + pos.y, minZ + pos.z);
                                rec1[1] = new Vector3f(minX + pos.x, maxY + pos.y, minZ + pos.z);
                                rec1[2] = new Vector3f(minX + pos.x, minY + pos.y, maxZ + pos.z);
                                rec1[3] = new Vector3f(minX + pos.x, maxY + pos.y, maxZ + pos.z);

                                rec2[0] = new Vector3f(maxX + pos.x, minY + pos.y, minZ + pos.z);
                                rec2[1] = new Vector3f(maxX + pos.x, maxY + pos.y, minZ + pos.z);
                                rec2[2] = new Vector3f(maxX + pos.x, minY + pos.y, maxZ + pos.z);
                                rec2[3] = new Vector3f(maxX + pos.x, maxY + pos.y, maxZ + pos.z);

                                Vector3f p = Maths.temp(rec1, rec2, camera.getPosition(), picker);

                                if (p == null) {
                                    rec1[0] = new Vector3f(minX + pos.x, minY + pos.y, maxZ + pos.z);
                                    rec1[1] = new Vector3f(minX + pos.x, maxY + pos.y, maxZ + pos.z);
                                    rec1[2] = new Vector3f(maxX + pos.x, minY + pos.y, maxZ + pos.z);
                                    rec1[3] = new Vector3f(maxX + pos.x, maxY + pos.y, maxZ + pos.z);

                                    rec2[0] = new Vector3f(minX + pos.x, minY + pos.y, minZ + pos.z);
                                    rec2[1] = new Vector3f(minX + pos.x, maxY + pos.y, minZ + pos.z);
                                    rec2[2] = new Vector3f(maxX + pos.x, minY + pos.y, minZ + pos.z);
                                    rec2[3] = new Vector3f(maxX + pos.x, maxY + pos.y, minZ + pos.z);

                                    p = Maths.temp(rec1, rec2, camera.getPosition(), picker);
                                }

                                if (p != null) {
                                    if (!item.isSelected())
                                        item.select();
                                    else
                                        item.unselect();

                                    System.out.println("Intersection avec " + item.getId());

                                    break;
                                }
                            }
                        }
                    }
                });

//                buffers.bindReflectionFrameBuffer();
//
//                float distance = 2 * (camera.getPosition().y - water.getHeight());
//                camera.getPosition().y -= distance;
//                camera.invertPitch();
//
//                renderer.renderScene(entities, Collections.singletonList(terrain), lights, camera,
//                        new Vector4f(0, 1, 0, -water.getHeight() + 1f));
//
//                camera.getPosition().y += distance;
//                camera.invertPitch();
//
//                buffers.bindRefractionFrameBuffer();
//                renderer.renderScene(entities, Collections.singletonList(terrain), lights, camera,
//                        new Vector4f(0, -1, 0, water.getHeight() + 1f));
//
//                GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
//
//                buffers.unbindCurrentFrameBuffer();

                if (GuiEscapeMenu.getEscapeMenu().isDisplayed()) {
                    fbo.bindFrameBuffer();

                    renderer.renderScene(entities, terrain, lights, camera,
                            new Vector4f(0, -1, 0, 1000000));
                    waterRenderer.render(waters, camera, sun);
                    fbo.unbindFrameBuffer();

                    PostProcessing.doPostProcessing(fbo.getColourTexture());
                } else {
                    renderer.renderScene(entities, terrain, lights, camera,
                            new Vector4f(0, -1, 0, 1000000));
                    waterRenderer.render(waters, camera, sun);
                }

                guiRenderer.renderGuis(guis);

                TextMaster.render();//TODO: Handle double rendering w/ GuiRenderer
            }
        }

        PostProcessing.cleanUp();
        fbo.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        renderer.cleanUp();
        guiRenderer.cleanUp();
        loader.cleanUp();
        TextMaster.cleanUp();
        DisplayManager.closeDisplay();
    }

}