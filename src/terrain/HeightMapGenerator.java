package terrain;

import textures.TerrainTexture;
import util.feedback.Failure;
import util.feedback.Feedback;
import util.feedback.Success;
import util.noise.LinkedBiomeWeightMap;
import util.noise.NoiseGenerator;
import util.noise.OpenSimplex2S;
import util.noise.ScatteredBiomeBlender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

public class HeightMapGenerator implements HeightMapSupplier<TerrainTexture, Exception> {

    private static final double CHUNK_WIDTH = 0.0625;
    private static final double BLEND_RADIUS_PADDING = 0.03125;
    private static final double POINT_FREQUENCY = 0.001875;
    private static final double BIOME_NOISE_FREQUENCY = 0.00009375;

    private static final int JITTER_SEED = 454515;
    private static final int NOISE_SEED = 545451; //-271070861

    private static final int N_OCTAVES = 2;

    private final OpenSimplex2S[] noises;
    private final NoiseGenerator[] biomeNoiseGenerators;
    private final OpenSimplex2S[] terrainNoises;
    private final long seed;
    private double biomeNoiseFrequency;

    private final File file;

    /**
     * If this constructor is used, the HeightMap is not saved
     */
    public HeightMapGenerator(long seed) {
        this(seed, null);
    }

    /**
     * If this constructor is used, the HeightMap is saved in given file
     *
     * @param file where the HeightMap will be saved
     */
    public HeightMapGenerator(long seed, File file) {
        this.file = file;
        this.biomeNoiseGenerators = new NoiseGenerator[3];
        this.noises = new OpenSimplex2S[this.biomeNoiseGenerators.length * N_OCTAVES];
        this.seed = seed;
        this.terrainNoises = new OpenSimplex2S[6];
        for (int i = 0; i < this.terrainNoises.length; i++)
            this.terrainNoises[i] = new OpenSimplex2S(this.seed + i);
        for (int i = 0; i < this.noises.length; i++)
            this.noises[i] = new OpenSimplex2S((this.seed & 42) + i);
    }

    @Override
    public Feedback<TerrainTexture, Exception> create(int width, int depth) {
        createBiomes(width);
        Float[][] heights = new Float[width][depth];
        try {
            boolean saveAsFile = this.file != null;
            int[] intHeights = new int[width * depth * 3];
            int chunkWidth = (int) (CHUNK_WIDTH * width);
            this.biomeNoiseFrequency = BIOME_NOISE_FREQUENCY * width;

            ScatteredBiomeBlender blender = new ScatteredBiomeBlender(
                    POINT_FREQUENCY * width, BLEND_RADIUS_PADDING * width,
                    chunkWidth);

            long jitterSeed = this.seed | 1337;

            for (int zc = 0; zc < depth; zc += chunkWidth) {
                for (int xc = 0; xc < width; xc += chunkWidth) {
                    LinkedBiomeWeightMap firstBiomeWeightMap = blender.getBlendForChunk(
                            jitterSeed, xc, zc, (x, z) -> getBiomeAt(x, z, width));

                    for (int zi = 0; zi < chunkWidth; zi++) {
                        for (int xi = 0; xi < chunkWidth; xi++) {
                            int z = zc + zi;
                            int x = xc + xi;
                            double height = 0;
                            double riverHeight = this.biomeNoiseGenerators[2].get(x, z);
                            if (riverHeight < 0 || getBiomeAt(x, z, width) == 1) {
                                for (LinkedBiomeWeightMap entry = firstBiomeWeightMap;
                                     entry != null; entry = entry.getNext()) {
                                    double weight = entry.getWeights() == null ?
                                            1 : entry.getWeights()[zi * chunkWidth + xi];
                                    int biome = entry.getBiome();

                                    double thisHeight = this.biomeNoiseGenerators[biome].getNoise(x, z);
                                    height += thisHeight * weight;
                                }
                            }
                            heights[x][z] = (float) height / 255.0f;
                            int idx = x + z * depth;
                            intHeights[idx * 3] = (int) height;
                            intHeights[idx * 3 + 1] = 0;
                            intHeights[idx * 3 + 2] = 0;
                        }
                    }
                }
            }
            if (saveAsFile) {
                BufferedImage image = new BufferedImage(width, depth, BufferedImage.TYPE_INT_RGB);
                WritableRaster raster = image.getRaster();
                raster.setPixels(0, 0, width, depth, intHeights);
                if (!ImageIO.write(image, "png", this.file))
                    throw new IllegalStateException("Error while writing image: " + this.file.getName());
            }
        } catch (Exception e) {
            return new Failure<>(e);
        }

        return new Success<>(new TerrainTexture(heights));
    }

    private void createBiomes(int width) {
        double div = 768.0 / (double) width;
        // Plains
        this.biomeNoiseGenerators[0] = (x, z) -> {
//            double value = this.terrainNoises[0].noise2(x * 0.015 * div, z * 0.015 * div);
//            value *= (2. / 3.) * 3;
//            value += 12;
            return 24;
        };
        // Mountains
        this.biomeNoiseGenerators[1] = (x, z) -> {
            double value = 1 - Math.abs(this.terrainNoises[1].noise2(x * 0.005 * div, z * 0.005 * div));
            value += (1 - Math.abs(this.terrainNoises[2].noise2(x * 0.01 * div, z * 0.01 * div))) * 0.5;
            value += (1 - Math.abs(this.terrainNoises[3].noise2(x * 0.02 * div, z * 0.02 * div))) * 0.25;
            value += (1 - Math.abs(this.terrainNoises[4].noise2(x * 0.04 * div, z * 0.04 * div))) * 0.1625;
//            value *= 122.71428571428571;
            value *= 146.71428571428571;
            return value;
        };
        // Rivers
        this.biomeNoiseGenerators[2] = (x, z) -> {
            double value = this.terrainNoises[5].noise2(x * 0.001 * div, z * 0.001 * div) + 1;
            value /= 2;
            value *= 8;
//            System.out.println(value);
//            return 0;


            value = 1 - Math.abs(this.terrainNoises[5].noise2(x * 0.01, z * 0.01));
            if (value * 255 >= 239)
//            value = 1 - Math.abs(this.terrainNoises[5].noise2(x * 0.007, z * 0.007));
//            if (value * 255 >= 248)
                return 0;
            return -1;
        };
    }

    private int getBiomeAt(double x, double z, int width) {
        double div = 768.0 / (double) width;
        double maxValue = Double.NEGATIVE_INFINITY;
        int biome = 0;
        boolean river = false;
        for (int i = 0; i < 2/* this.biomeNoiseGenerators.length*/; i++) {
            double noiseValue = 0;
            double freq = this.biomeNoiseFrequency;
            double amp = 1;
            for (int j = 0; j < N_OCTAVES; j++) {
                noiseValue += this.noises[i + j * this.biomeNoiseGenerators.length].
                        noise2(x * freq, z * freq) * amp;
                freq *= 2;
                amp *= 0.5;
            }
            if (i == 1) {
                noiseValue = Math.min(-.6, noiseValue);
            }
            if (i == 2) {
                double value = 1 - Math.abs(this.terrainNoises[5].noise2(x * 0.01, z * 0.01));
                if (value * 255 >= 239)
                    river = true;
                continue;
            }
            if (noiseValue > maxValue) {
                maxValue = noiseValue;
                biome = i;
            }
        }
        if (biome == 0 && river) {
            return 2;
        }

        return biome;
    }
}