package util.noise;

/**
 * Credits to KdotJPG
 * https://github.com/KdotJPG/Scattered-Biome-Blender
 */
public class LinkedBiomeWeightMap {
    private final int biome;
    private double[] weights;
    private final LinkedBiomeWeightMap next;

    public LinkedBiomeWeightMap(int biome, LinkedBiomeWeightMap next) {
        this.biome = biome;
        this.next = next;
    }

    public LinkedBiomeWeightMap(int biome, int chunkColumnCount, LinkedBiomeWeightMap next) {
        this.biome = biome;
        this.weights = new double[chunkColumnCount];
        this.next = next;
    }

    public int getBiome() {
        return this.biome;
    }

    public double[] getWeights() {
        return this.weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public LinkedBiomeWeightMap getNext() {
        return this.next;
    }
}