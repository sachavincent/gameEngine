package engineTester;

import entities.Camera;
import guis.Gui;
import guis.presets.GuiTextInput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.GuiRenderer;
import renderEngine.fontRendering.TextMaster;
import scene.components.Component;
import util.TimeSystem;

public class Rome {

    private static Game game;

    private static final List<Gui>          guis          = new ArrayList<>();
    private static final List<GuiTextInput> guiTextInputs = new ArrayList<>();
    private static final List<Gui>          displayedGuis = new ArrayList<>();

    private static GameStartedCallback onGameStartedCallback = g -> {
    };

    public static Game getGame() {
        return Rome.game;
    }

    public static List<Gui> getAllGuis() {
        return Rome.guis;
    }

    public static List<Gui> getDisplayedGuis() {
        return Rome.displayedGuis;
    }

    public static List<GuiTextInput> getGuiTextInputs() {
        return Rome.guiTextInputs;
    }

    public static void addGui(Gui gui) {
        Rome.guis.add(gui);
        Rome.displayedGuis.add(gui);
    }

    public static void updateGuis() {
        Rome.guis.forEach(
                gui -> Rome.guiTextInputs.addAll(gui.getAllComponents().stream().filter(GuiTextInput.class::isInstance)
                        .map(GuiTextInput.class::cast).collect(Collectors.toList())));
    }

    public static void startGame() {
        if (isGameStarted())
            throw new IllegalStateException("Game has already started!");
        Rome.game = new Game();

        Rome.onGameStartedCallback.onGameStarted(Rome.game);
    }

    public static boolean isGameStarted() {
        return Rome.game != null;
    }

    public static void setOnGameStarted(GameStartedCallback onGameStartedCallback) {
        Rome.onGameStartedCallback = onGameStartedCallback;
    }


    public static float prev = TimeSystem.getTimeMillis();

    //    static ModelTexture texture = ModelTexture.createTexture(new ResourceFile("sun.png"));
//    static SunRenderer sunRenderer = new SunRenderer();
//    static Sun sun = new Sun(texture, 55);
//    static {
//        Vector3f lightDir = new Vector3f(0.55f, -0.34f, 1);
//        sun.setDirection(lightDir.getX(), lightDir.y, lightDir.getZ());
//    }
    public static void processRendering(Fbo fbo) {
        if (Rome.isGameStarted()) {
            Camera.getInstance().move();
            Rome.getGame().getScene().render();
//            sunRenderer.render(sun, Camera.getInstance());

            Rome.getGame().getScene().getGameObjects()
                    .forEach(gameObject -> gameObject.getComponents().values().forEach(Component::render));
        } else {
            fbo.bindFrameBuffer();
//            Rome.getGame().getScene().render();
//            sunRenderer.render(sun, Camera.getInstance());

            fbo.unbindFrameBuffer();
            PostProcessing.doPostProcessing(fbo.getColourTexture());
        }

        GuiRenderer.render();
        TextMaster.getInstance().render();
    }

    /**
     * Called every in-game Tick
     */
    public static void processLogic() {
        if (!isGameStarted())
            return;

        Rome.getGame().processLogic();
    }

    @FunctionalInterface
    public interface GameStartedCallback {

        void onGameStarted(Game game);
    }
}