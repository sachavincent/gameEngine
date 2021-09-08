package display;

import language.Words;

public enum DisplayMode {
    FULLSCREEN(Words.FULLSCREEN),
    BORDERLESS_WINDOWED(Words.BORDERLESS_WINDOWED),
    WINDOWED(Words.WINDOWED);

    private final Words name;

    DisplayMode(Words name) {
        this.name = name;
    }

    public static DisplayMode defaultMode() {
        return FULLSCREEN;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }
}