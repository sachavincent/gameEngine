package renderEngine.shaders.structs;

import textures.Texture;
import util.parsing.MaterialColor;

public class MaterialStruct extends StructLocation {

    public final static String NAME = "Material";

    private final static String EMISSION = "Emission";
    private final static String AMBIENT = "Ambient";
    private final static String DIFFUSE = "Diffuse";
    private final static String SPECULAR = "Specular";
    private final static String SHININESS = "Shininess";
    private final static String AMBIENT_MAP = "ambientMap";
    private final static String DIFFUSE_MAP = "diffuseMap";
    private final static String NORMAL_MAP = "normalMap";
    private final static String SPECULAR_MAP = "specularMap";
    private final static String USE_AMBIENT_MAP = "UseAmbientMap";
    private final static String USE_DIFFUSE_MAP = "UseDiffuseMap";
    private final static String USE_NORMAL_MAP = "UseNormalMap";
    private final static String USE_SPECULAR_MAP = "UseSpecularMap";

    public MaterialStruct(Integer programID) {
        this(programID, NAME);
    }

    public MaterialStruct(Integer programID, String name) {
        super(programID, name,
                new Location(EMISSION, MaterialColor.class),
                new Location(AMBIENT, MaterialColor.class),
                new Location(DIFFUSE, MaterialColor.class),
                new Location(SPECULAR, MaterialColor.class),
                new Location(SHININESS, Float.class),
                new Location(AMBIENT_MAP, Texture.class),
                new Location(DIFFUSE_MAP, Texture.class),
                new Location(NORMAL_MAP, Texture.class),
                new Location(SPECULAR_MAP, Texture.class),
                new Location(USE_AMBIENT_MAP, Boolean.class),
                new Location(USE_DIFFUSE_MAP, Boolean.class),
                new Location(USE_NORMAL_MAP, Boolean.class),
                new Location(USE_SPECULAR_MAP, Boolean.class));
    }
}
