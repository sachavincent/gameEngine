package util.parsing;

public enum ModelType {
    DEFAULT(".obj", 0, 1, 2),
    WITH_NORMAL_MAP(".obj", 0, 1, 2, 3),
    INSTANCED(".obj", 0, 1, 2, 6),
    INSTANCED_WITH_NORMAL_MAP(".obj", 0, 1, 2, 3, 6),
    ANIMATED(".dae", 0, 1, 2, 4, 5),
    ANIMATED_INSTANCED(".dae", 0, 1, 2, 4, 5, 6),
    ANIMATED_WITH_NORMAL_MAP(".dae", 0, 1, 2, 3, 4, 5),
    ANIMATED_INSTANCED_WITH_NORMAL_MAP(".dae", 0, 1, 2, 3, 4, 5, 6);

    private final String extension;
    private final int[]  attributeNumbers;

    ModelType(String extension, int... attributeNumbers) {
        this.extension = extension;
        this.attributeNumbers = attributeNumbers;
    }

    public int[] getAttributeNumbers() {
        return this.attributeNumbers;
    }

    public String getExtension() {
        return this.extension;
    }
}
