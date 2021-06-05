package textures;

import guis.presets.Background;

public class ModelTexture extends Texture {

    public final static ModelTexture DEFAULT_MODEL = new ModelTexture("purple.png", true);

    private float shineDamper = 1, reflectivity = 0;

    private float   alpha;
    private boolean useFakeLighting;

    private boolean directionalColor;

    private int numberOfRows = 1;

    public ModelTexture(String background) {
        this(background, false);
    }

    public ModelTexture(String background, boolean useFakeLighting) {
        super(new Background<>(background));

        this.useFakeLighting = useFakeLighting;
        this.alpha = -1;
    }

    public boolean doesUseDirectionalColor() {
        return this.directionalColor;
    }

    public void setDirectionalColor(boolean directionalColor) {
        this.directionalColor = directionalColor;
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
        return this.alpha < 1f && this.alpha >= 0;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return this.alpha;
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

