package textures;

import guis.presets.GuiBackground;

public class ModelTexture extends Texture {

    public final static ModelTexture DEFAULT_MODEL = new ModelTexture("purple.png", true);

    private float shineDamper = 1, reflectivity = 0;

    private boolean transparent;
    private boolean useFakeLighting;

    private int numberOfRows = 1;

    public ModelTexture(String pathFile) {
        super(new GuiBackground<>(pathFile));
    }

    public ModelTexture(String pathFile, boolean useFakeLighting) {
        super(new GuiBackground<>(pathFile));

        this.useFakeLighting = useFakeLighting;
    }

    public float getShineDamper() {
        return this.shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return this.reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public boolean doesUseFakeLighting() {
        return this.useFakeLighting;
    }

    public void setUseFakeLighting(boolean useFakeLighting) {
        this.useFakeLighting = useFakeLighting;
    }

    public int getNumberOfRows() {
        return this.numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }
}

