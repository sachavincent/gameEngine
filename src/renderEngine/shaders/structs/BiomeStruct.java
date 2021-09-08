package renderEngine.shaders.structs;

public class BiomeStruct extends StructLocation {

    public static final String NAME = "Biome";

    private static final String MATERIAL = "Material";
    private static final String MIN_HEIGHT = "MinHeight";
    private static final String MAX_HEIGHT = "MaxHeight";

    public BiomeStruct(Integer programID) {
        this(programID, NAME);
    }

    public BiomeStruct(Integer programID, String name) {
        super(programID, name,
                new Location(MATERIAL, Material.class),
                new Location(MIN_HEIGHT, Float.class),
                new Location(MAX_HEIGHT, Float.class));
    }
}
