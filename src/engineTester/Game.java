package engineTester;

import guis.Gui;
import guis.presets.GuiTextInput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import scene.Scene;

public class Game {

    private static Game instance;

    private int       numberOfPeople;
    private GameState gameState;

    private final List<Gui>          guis          = new ArrayList<>();
    private final List<GuiTextInput> guiTextInputs = new ArrayList<>();
    private final List<Gui>          displayedGuis = new ArrayList<>();

    private Game() {
        this.gameState = GameState.NOT_STARTED;
    }

    public static Game getInstance() {
        return instance == null ? (instance = new Game()) : instance;
    }

    public int getNumberOfPeople() {
        return this.numberOfPeople;
    }

    public boolean addPerson() {
//        if (this.numberOfPeople >= Terrain.getInstance().getMaxPeopleCapacity())
        if (this.numberOfPeople >= Scene.getInstance().getMaxPeopleCapacity())
            return false;

        this.numberOfPeople++;

        return true;
    }

    public void removePerson() {
        if (this.numberOfPeople <= 0)
            return;

        this.numberOfPeople--;
    }

    public void start() {
        if (this.gameState == GameState.NOT_STARTED)
            this.gameState = GameState.STARTED;
        else
            throw new IllegalStateException("Incorrect GameState=" + this.gameState.name());
    }

    public void pause() {
        if (this.gameState == GameState.STARTED)
            this.gameState = GameState.PAUSED;
        else
            throw new IllegalStateException("Incorrect GameState=" + this.gameState.name());
    }

    public void resume() {
        if (this.gameState == GameState.PAUSED)
            this.gameState = GameState.STARTED;
        else
            throw new IllegalStateException("Incorrect GameState=" + this.gameState.name());
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public List<Gui> getAllGuis() {
        return this.guis;
    }

    public List<Gui> getDisplayedGuis() {
        return this.displayedGuis;
    }

    public List<GuiTextInput> getGuiTextInputs() {
        return this.guiTextInputs;
    }

    public void addGui(Gui gui) {
        this.guis.add(gui);
    }

    public void updateGuis() {
        this.guis.forEach(
                gui -> this.guiTextInputs.addAll(gui.getAllComponents().stream().filter(GuiTextInput.class::isInstance)
                        .map(GuiTextInput.class::cast).collect(Collectors.toList())));
    }

    public enum GameState {
        STARTED,
        PAUSED,
        NOT_STARTED;
    }
}
