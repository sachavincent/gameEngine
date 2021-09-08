package scene.components;

import terrain.HeightMapSupplier;
import textures.TerrainTexture;
import textures.Texture;

public class HeightMapComponent extends Component {

    private final float maxHeight;
    private final int width;
    private final int depth;

    private TerrainTexture texture;

    private Float[][] heights;

    public HeightMapComponent(float maxHeight, int width, int depth,
            HeightMapSupplier<TerrainTexture, Exception> heightMapSupplier) {
        this.maxHeight = maxHeight;
        this.width = width;
        this.depth = depth;

        heightMapSupplier.create(width, depth).onSuccess(texture -> {
            this.texture = texture;
            this.heights = texture.getHeights();
        }).onFailure(Throwable::printStackTrace);
    }

    public float getMaxHeight() {
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

    /**
     * Returns the height at given coordinate
     *
     * @param x coordinate
     * @param z coordinate
     * @return height between 0 and {@link HeightMapComponent#maxHeight}
     */
    public float getHeight(int x, int z) {
        if (x >= this.heights.length || z >= this.heights[0].length || x < 0 || z < 0)
            return 0;

        return this.heights[x][z] * this.maxHeight;
    }

    /**
     * Sets the new height at given coordinate
     *
     * @param x coordinate
     * @param z coordinate
     * @param height between 0 and {@link HeightMapComponent#maxHeight}
     */
    public void setHeight(int x, int z, float height) {
        if (x >= this.heights.length || z >= this.heights[0].length ||
                x < 0 || z < 0 || height < 0 || height > this.maxHeight)
            return;

        this.heights[x][z] = height / this.maxHeight;

        this.texture = new TerrainTexture(this.heights);
    }
}