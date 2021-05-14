package inputs;

import language.Words;

public enum KeyBindings {

    DISPLAY_BOUNDING_BOXES("display_bounding_boxes"),
    FORWARD(Words.FORWARD.name()),
    LEFT(Words.LEFT.name()),
    BACKWARD(Words.BACKWARD.name()),
    RIGHT(Words.RIGHT.name()),

    ;


    protected final String   name;
    protected       KeyInput keyInput;

    KeyBindings(String name) {
        this.name = name;
    }


    public void setKeyInput(KeyInput keyInput) {
        this.keyInput = keyInput;
    }

    public String getName() {
        return this.name;
    }

    public int getKey() {
        return this.keyInput.getKey();
    }

    public KeyModifiers getModifier() {
        return this.keyInput.getValue();
    }

    public KeyInput getKeyInput() {
        return this.keyInput;
    }

    public static int getEmptyKey() {
        return 48;
    }


}
