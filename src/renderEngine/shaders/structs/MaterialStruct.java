package renderEngine.shaders.structs;

import textures.Texture;
import util.parsing.MaterialColor;

public class MaterialStruct extends StructLocation {

    public static final String NAME = "Material";

    private static final String EMISSION = "Emission";
    private static final String AMBIENT = "Ambient";
    private static final String DIFFUSE = "Diffuse";
    private static final String SPECULAR = "Specular";
    private static final String SHININESS = "Shininess";
    private static final String AMBIENT_MAP = "ambientMap";
    private static final String DIFFUSE_MAP = "diffuseMap";
    private static final String NORMAL_MAP = "normalMap";
    private static final String SPECULAR_MAP = "specularMap";
    private static final String USE_AMBIENT_MAP = "UseAmbientMap";
    private static final String USE_DIFFUSE_MAP = "UseDiffuseMap";
    private static final String USE_NORMAL_MAP = "UseNormalMap";
    private static final String USE_SPECULAR_MAP = "UseSpecularMap";

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
