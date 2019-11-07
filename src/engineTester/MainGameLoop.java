package engineTester;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static renderEngine.DisplayManager.FPS;

import entities.Camera;
import entities.Entity;
import entities.Light;
import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import guis.Gui;
import guis.basics.GuiBasics;
import guis.basics.GuiRectangle;
import guis.basics.GuiSquare;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import guis.constraints.SideConstraint.Side;
import guis.transitions.FadeTransition;
import guis.transitions.SlidingDirection;
import guis.transitions.SlidingTransition;
import inputs.KeyboardUtils;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.TexturedModel;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import renderEngine.DisplayManager;
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
import util.MouseUtils;
import util.Timer;
import util.vector.Vector3f;
import util.vector.Vector4f;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterTile;

public class MainGameLoop {

    public static void main(String[] args) {
        glfwInit();
        DisplayManager.createDisplay();
        glfwSwapInterval(0);
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
        Loader loader = new Loader();

        TextMaster.init(loader);

        FontTexture roboto = new FontTexture("roboto.png");

        FontType font = new FontType(roboto.getTextureID(), new File("res/roboto.fnt"));

        TerrainTexture backgroundTexture = new TerrainTexture("blue.png");
        TerrainTexture rTexture = new TerrainTexture("blue.png");
        TerrainTexture gTexture = new TerrainTexture("green.png");
        TerrainTexture bTexture = new TerrainTexture("blue.png");

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture("blendMap.png");

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

        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
                new ModelTexture("lampTexture.png"));

        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector3f(0, 1000, 300), new Vector3f(0.4f, 0.4f, 0.4f));
        lights.add(sun);

        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap.png");

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

        Entity e = new Entity(tree, new Vector3f(8.35, 0.5, 17.35), 0, 0, 0);
        entities.add(e);
        MasterRenderer renderer = new MasterRenderer(loader);

        Camera camera = new Camera(terrain, new Vector3f(-50, 25, -30), 20);


        List<Gui> guis = new ArrayList<>();

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(75, -75, 0);
        waters.add(water);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        Gui right_gui = new Gui(Color.RED);

        GuiConstraintsManager constraints = new GuiConstraintsManager();
        constraints.setDefault();

        right_gui.setTransitions(new SlidingTransition(400, SlidingDirection.LEFT), new FadeTransition(400));
//        right_gui.setTransitions(new SlidingTransition(400, SlidingDirection.LEFT));
        constraints.setWidthConstraint(new RelativeConstraint(0.1f));
        constraints.setHeightConstraint(new RelativeConstraint(0.75f));
        constraints.setxConstraint(new SideConstraint(Side.RIGHT));
        right_gui.setConstraints(constraints);

        GuiBasics square = new GuiSquare(right_gui, Color.BLUE);
        square.setHeightConstraint(new RelativeConstraint(0.24f));
        square.setYConstraint(new RelativeConstraint(0f));
        square.setOnClick(() -> {
            System.out.println("test");
        });

        GuiConstraintsManager constraints2 = new GuiConstraintsManager();
        constraints2.setWidthConstraint(new PixelConstraint(100));
        constraints2.setHeightConstraint(new PixelConstraint(100));
//        GuiSlider slider = new GuiRectangleSlider(right_gui, Color.BLUE, Color.BLACK, constraints2);

//        GuiRectangleButton guiRectangleButton = new GuiRectangleButton(right_gui,
//                new Text("jsp", .3f, font, Color.WHITE), Color.RED, constraints2);
//        GuiRectangleButton guiCircularButton = new GuiRectangleButton(right_gui, "path.png", constraints2);
//        GuiCircularButton b = new GuiCircularButton(right_gui, Color.BLACK, constraints2);
//        b.setBorder(Color.WHITE);
        GuiRectangle guiCircle = new GuiRectangle(right_gui, Color.BLACK, new PixelConstraint(100),
                new PixelConstraint(100), false);
        guiCircle.setOutlineWidth(5);
//        guiCircle.setWidthConstraint(new RelativeConstraint(1f));

//        System.out.println(guiCircle);
//       new GuiOval(right_gui, Color.DARK_GRAY, new RelativeConstraint(1f), new RelativeConstraint(1f));

        guis.add(right_gui);

        MouseUtils.setupListeners(guis);
        KeyboardUtils.setupListeners(guis);

        double frameCap = 1.0 / FPS;
        double time = Timer.getTime();
        double unprocessed = 0;

        double frameTime = 0;

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

                if (glfwGetKey(DisplayManager.getWindow(), GLFW_KEY_ESCAPE) == GL_TRUE)
                    DisplayManager.closeDisplay();


                DisplayManager.updateDisplay();

                if (frameTime >= 1.0) {
                    frameTime = 0;

//                    System.out.println("FPS: " + frames);
                }
            }

            if (canRender) {
                camera.move();

                picker.update();

                GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

//            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//            if(!terrain.isPointOnTerrain(terrainPoint.x, terrainPoint.z))
//                terrainPoint = null;

//            if (terrainPoint != null) {
//                GLFW.glfwSetMouseButtonCallback(DisplayManager.getWindow(),
//                        (window, button, action, mods) -> {
//                            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
//                                Vector3f v = picker.getCurrentTerrainPoint();
//                                if (v == null || !terrain.isPointOnTerrain(v.x, v.z))
//                                    return;
//
////                                System.out.println("Clicked at: " + v);
//                                float roundedX = (int) v.getX();
//                                float roundedZ = (int) v.getZ();
//                                v.setX(roundedX + cube.getRawModel().getWidth() / 2);
//                                v.setZ(roundedZ - cube.getRawModel().getDepth() / 2);
////                                System.out.println("Put at: " + v);
//                                v.setY(terrain.getHeightOfTerrain(v.getX(), v.getZ()));
//
//                                TextMaster.removeText();
//                                TextMaster.loadText(
//                                        new Text("tttt", 1, font, new Vector2f(0, 0.5f), 1f, true));
//
//                                Entity e1 = new Entity(cube, v, 0, 0, 0);
//                                entities.add(e1);
//                            }
//                            //temp
//
//                            if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
//                                if (action == GLFW.GLFW_PRESS) {
//                                    camera.setMiddleButtonPressed(true);
//                                } else if (action == GLFW.GLFW_RELEASE) {
//                                    camera.setMiddleButtonPressed(false);
//                                }
//                            }
//                        });
//                GLFW.glfwSetCursorPosCallback(DisplayManager.getWindow(), (window, button, action) -> {
//                    Vector3f v = picker.getCurrentTerrainPoint();
//                    if (v == null || !terrain.isPointOnTerrain(v.x, v.z))
//                        return;
//                    v.setY(terrain.getHeightOfTerrain(v.getX(), v.getZ()));
//                    Entity e1 = new Entity(tree, v, 0, 0, 0);
//                    entities.add(e1);
//                });
//            }

//                System.out.println(slider.getValue());

                buffers.bindReflectionFrameBuffer();

                float distance = 2 * (camera.getPosition().y - water.getHeight());
                camera.getPosition().y -= distance;
                camera.invertPitch();

                renderer.renderScene(entities, Collections.singletonList(terrain), lights, camera,
                        new Vector4f(0, 1, 0, -water.getHeight() + 1f));

                camera.getPosition().y += distance;
                camera.invertPitch();

                buffers.bindRefractionFrameBuffer();
                renderer.renderScene(entities, Collections.singletonList(terrain), lights, camera,
                        new Vector4f(0, -1, 0, water.getHeight() + 1f));

                buffers.unbindCurrentFrameBuffer();
                renderer.renderScene(entities, Collections.singletonList(terrain), lights, guis, camera,
                        new Vector4f(0, -1, 0, 1000000));

                waterRenderer.render(waters, camera, sun);

                TextMaster.render();

            }
        }

        buffers.cleanUp();
        waterShader.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        TextMaster.cleanUp();

        DisplayManager.closeDisplay();
    }

}