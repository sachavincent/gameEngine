package renderEngine.shaders.structs;

public class Biome implements StructElement {

    private final Material material;
    private final float minHeight;
    private final float maxHeight;

    protected Biome() {
        this(Material.DEFAULT, 0.0f, 0.0f);
    }

    /**
     * Creates a biome with material between minHeight and maxHeight
     * material is interpolated if height < minHeight or height >= maxHeight
     * for all biomes
     *
     * @param material  material used to represent the biome
     * @param minHeight inclusive
     * @param maxHeight exclusive
     */
    public Biome(Material material, float minHeight, float maxHeight) {
        this.material = material;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public Material getMaterial() {
        return this.material;
    }

    public float getMinHeight() {
        return this.minHeight;
    }

    public float getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public Class<? extends StructLocation> getStructure() {
        return BiomeStruct.class;
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getMaterial(), getMinHeight(), getMaxHeight()};
    }
}
