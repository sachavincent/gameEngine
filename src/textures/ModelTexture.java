package textures;

import guis.presets.Background;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ModelTexture extends Texture {

    public static final Map<File, ModelTexture> TEXTURES = new HashMap<>();

    public static final ModelTexture NONE          = new ModelTexture();
    public static final ModelTexture DEFAULT_MODEL = new ModelTexture("#D500F9");

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

    public ModelTexture(Object background) {
        super(new Background<>(background));
    }
}