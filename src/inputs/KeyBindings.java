package inputs;

public enum KeyBindings {

    DISPLAY_BOUNDING_BOXES("display_bounding_boxes"),
    FORWARD("forward"),
    BACKWARD("backward"),
    LEFT("left"),
    RIGHT("right"),

    ;


    protected final String name;
    protected       int    key;

    KeyBindings(String name) {
        this.name = name;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public int getKey() {
        return this.key;
    }
}
