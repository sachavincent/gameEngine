package scene.components;

import terrain.HeightMapSupplier;
import textures.TerrainTexture;
import textures.Texture;

public class HeightMapComponent extends Component {

    private final int maxHeight;
    private final int width;
    private final int depth;

    private TerrainTexture texture;

    private Float[][] heights;

    public HeightMapComponent(int maxHeight, int width, int depth,
                              HeightMapSupplier<TerrainTexture, Exception> heightMapSupplier) {
        this.maxHeight = maxHeight;
        this.width = width;
        this.depth = depth;

        heightMapSupplier.create(width, depth).onSuccess(texture -> {
            this.texture = texture;
            this.heights = texture.getHeights();
        }).onFailure(e -> {
            e.printStackTrace();
//            try {
//                throw e.getClass().getDeclaredConstructor(String.class).newInstance("Error while generating HeightMap: \n\t" +
//                        Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).
//                                collect(Collectors.joining("\n\t")));
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
        });
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getDepth() {
        return this.depth;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Float[][] getHeights() {
        return this.heights;
    }

    public float getHeight(int x, int z) {
        return this.heights[x][z] * this.maxHeight;
    }

}