package renderEngine.shaders.structs;

public class BiomeStruct extends StructLocation {

    public final static String NAME = "Biome";

    private final static String MATERIAL = "Material";
    private final static String MIN_HEIGHT = "MinHeight";
    private final static String MAX_HEIGHT = "MaxHeight";

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
