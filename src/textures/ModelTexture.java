package textures;

import guis.presets.Background;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ModelTexture extends Texture {

    public final static Map<File, ModelTexture> TEXTURES = new HashMap<>();

    public final static ModelTexture NONE = new ModelTexture();
    public final static ModelTexture DEFAULT_MODEL = new ModelTexture("#D500F9", true);

    private float shineDamper = 1, reflectivity = 0;

    private float alpha;
    private boolean useFakeLighting;

    private int numberOfRows = 1;

    public static ModelTexture createTexture(File file) {
        if (TEXTURES.containsKey(file))
            return TEXTURES.get(file);

        ModelTexture modelTexture = new ModelTexture(file);
        TEXTURES.put(file, modelTexture);
        return modelTexture;
    }

    /**
     * Internal Constructor for the NONE instance
     */
    private ModelTexture() {
        this.ID = -1;
    }

    public ModelTexture(String background) {
        this(background, false);
    }

    private ModelTexture(File file) {
        this(file, false);
    }

    public ModelTexture(Object background, boolean useFakeLighting) {
        super(new Background<>(background));

        this.useFakeLighting = useFakeLighting;
        this.alpha = -1;
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

