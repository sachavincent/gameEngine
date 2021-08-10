package util.noise;

/**
 * Credits to KdotJPG
 * https://github.com/KdotJPG/Scattered-Biome-Blender
 */
public class GatheredPoint<TTag> {
    private final double x;
    private final double z;
    private final int hash;
    private TTag tag;

    public GatheredPoint(double x, double z, int hash) {
        this.x = x;
        this.z = z;
        this.hash = hash;
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
    }

    public double getHash() {
        return this.hash;
    }

    public TTag getTag() {
        return this.tag;
    }

    public void setTag(TTag tag) {
        this.tag = tag;
    }
}