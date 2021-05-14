package language;

public enum Words {

    // Menus
    RESUME("Resume"),
    SAVE_AND_QUIT("Save & Quit"),
    QUICK_SAVE("Quick Save"),
    QUIT("Quit"),
    SETTINGS("Settings"),
    NEW_GAME("New Game"),
    LOAD_GAME("Load Game"),

    // Settings
    DISPLAY("Display"),
    CONTROLS("Controls"),
    LANGUAGE("Language"),
    TARGET_DISPLAY("Target Display"),
    DISPLAY_MODE("Display Mode"),
    RESOLUTION("Resolution"),
    FPS_LIMIT("FPS Limit"),
    VERTICAL_SYNC("V-Sync"),
    WINDOWED("Windowed"),
    BORDERLESS_WINDOWED("Borderless Windowed"),
    FULLSCREEN("Fullscreen"),
    MONITOR("Monitor"),

    // Misc
    ON("On"),
    OFF("Off"),

    // HouseDetails Menu
    MONEY("Money"),
    PEOPLE("People"),
    CATEGORY_SOCIAL("Social"),
    CATEGORY_FOOD("Food"),
    CATEGORY_DRINKS("Drinks"),
    AVAILABLE_SPACE("Available"),

    // GameObjects
    DIRT_ROAD("Dirt Road"),
    INSULA("Insula"),
    MARKET("Market"),

    // Classes
    FARMER("Farmer"),

    // Resources
    FISH("Fish"),
    BREAD("Bread"),
    WHEAT("Wheat"),

    // Languages
    ENGLISH("English"),
    FRENCH("French"),

    // KeyBindings
    PRESS_KEY("Press Key"),
    PRESS_TO_ASSIGN("Press To Assign"),

    FORWARD("Forward"),
    LEFT("Left"),
    BACKWARD("Backward"),
    RIGHT("Right"),

    ;

    private final String string;

    Words(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    public static Words getWordFromString(String value) {
        for (Words word : values()) {
            if (word.getString().equals(value))
                return word;
        }

        return null;
    }

    @Override
    public String toString() {
        return TextConverter.getWordInCurrentLanguage(this.string);
    }
}
