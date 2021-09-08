package util.parsing;

public enum ModelType {
    DEFAULT(".obj"),
    WITH_NORMAL_MAP(".obj"),
    INSTANCED(".obj"),
    INSTANCED_WITH_NORMAL_MAP(".obj"),
    ANIMATED(".dae"),
    ANIMATED_WITH_NORMAL_MAP(".dae"),
    ANIMATED_INSTANCED(".dae"),
    ANIMATED_INSTANCED_WITH_NORMAL_MAP(".dae");

    private final String extension;

    ModelType(String extension) {
        this.extension = extension;
    }

    public boolean isNormalMap() {
        return (ordinal() & 1) == 1;
    }

    public boolean isInstanced() {
        return ((ordinal() >> 1) & 1) == 1;
    }

    public boolean isAnimated() {
        return ((ordinal()  >> 2) & 1) == 1;
    }

    public String getExtension() {
        return this.extension;
    }
}
