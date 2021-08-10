package util.noise;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DemoScatteredBlend {
    private static final int DIVIDOR = 6;
    private static final int WIDTH = 768 / DIVIDOR;
    private static final int HEIGHT = 768 / DIVIDOR;
    private static final int CHUNK_WIDTH = 8;

    private static final int BLEND_RADIUS_PADDING = 24 / DIVIDOR;
    private static final double POINT_FREQUENCY = 0.04 * DIVIDOR;
    private static final double BIOME_NOISE_FREQUENCY = 0.002 * DIVIDOR;

    private static final int JITTER_SEED = 454515;
    private static final int NOISE_SEED = 545451; //-271070861
    private static final int BIOME_NOISE_SEED = 21879;

    private static final boolean ONLY_RENDER_WEIGHT_BORDERS = false;
    private static final boolean GENERATE_ACTUAL_TERRAIN = true;

    private static final Color[] BIOME_COLORS = {
//            new Color(8, 112, 32), new Color(133, 161, 90), new Color(104, 112, 112), new Color(242, 232, 52)
            new Color(8, 112, 32), new Color(104, 112, 112),
            Color.BLUE.brighter()
    };


    private static final OpenSimplex2S[] TERRAIN_NOISES = new OpenSimplex2S[10];

    static {
        for (int i = 0; i < TERRAIN_NOISES.length; i++) {
            TERRAIN_NOISES[i] = new OpenSimplex2S(BIOME_NOISE_SEED + i);
        }
    }

    private static NoiseGenerator[] BIOME_NOISE_GENERATORS = {
            // Forest
//            new NoiseGenerator() {
//                public double getNoise(int x, int z) {
//                    double value = TERRAIN_NOISES[0].noise2(x * 0.01, z * 0.01);
//                    value += TERRAIN_NOISES[1].noise2(x * 0.02, z * 0.02) * 0.5;
//                    value *= (2. / 3.) * 20;
//                    value += 49;
//                    return value;
//                }
//            },
            //Plains
            new NoiseGenerator() {
                public double get(int x, int z) {
                    double value = TERRAIN_NOISES[2].noise2(x * 0.015 * DIVIDOR, z * 0.015 * DIVIDOR);
//                    value += TERRAIN_NOISES[3].noise2(x * 0.03, z * 0.03) * 0.5;
                    value *= (2. / 3.) * 3;
                    value += 12;
                    return 12;
                }
            },
//            //Mountains
            new NoiseGenerator() {
                public double get(int x, int z) {
                    double value = 1 - Math.abs(TERRAIN_NOISES[4].noise2(x * 0.005 * DIVIDOR, z * 0.005 * DIVIDOR));
                    value += (1 - Math.abs(TERRAIN_NOISES[5].noise2(x * 0.01 * DIVIDOR, z * 0.01 * DIVIDOR))) * 0.5;
                    value += (1 - Math.abs(TERRAIN_NOISES[6].noise2(x * 0.02 * DIVIDOR, z * 0.02 * DIVIDOR))) * 0.25;
                    value += (1 - Math.abs(TERRAIN_NOISES[7].noise2(x * 0.04 * DIVIDOR, z * 0.04 * DIVIDOR))) * 0.0625;
                    value *= 122.71428571428571;
                    return value;
                }
            },
            //Rivers
            new NoiseGenerator() {
                public double get(int x, int z) {
                    double value = 1 - Math.abs(TERRAIN_NOISES[8].noise2(x * 0.001 * DIVIDOR, z * 0.001 * DIVIDOR));
                    value = Math.abs(value - 1);
                    value *= 60;
                    return value;
                }
            }
    };

    private static final int N_OCTAVES = 2;
    private static final OpenSimplex2S[] NOISES = new OpenSimplex2S[BIOME_COLORS.length * N_OCTAVES];

    static {
        for (int i = 0; i < NOISES.length; i++) {
            NOISES[i] = new OpenSimplex2S(NOISE_SEED + i);
        }
    }

    public static void main(String[] args)
            throws IOException {

        ScatteredBiomeBlender blender = new ScatteredBiomeBlender(POINT_FREQUENCY, BLEND_RADIUS_PADDING, CHUNK_WIDTH);

        // Image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int zc = 0; zc < HEIGHT; zc += CHUNK_WIDTH) {
            for (int xc = 0; xc < WIDTH; xc += CHUNK_WIDTH) {

                LinkedBiomeWeightMap firstBiomeWeightMap = blender.getBlendForChunk(JITTER_SEED, xc, zc, DemoScatteredBlend::getBiomeAt);

                for (int zi = 0; zi < CHUNK_WIDTH; zi++) {
                    for (int xi = 0; xi < CHUNK_WIDTH; xi++) {
                        int z = zc + zi;
                        int x = xc + xi;

                        double r, g, b;
                        r = g = b = 0;

                        if (GENERATE_ACTUAL_TERRAIN) {
                            double height = 0;
                            for (LinkedBiomeWeightMap entry = firstBiomeWeightMap; entry != null; entry = entry.getNext()) {
                                double weight = entry.getWeights() == null ? 1 : entry.getWeights()[zi * CHUNK_WIDTH + xi];
                                int biome = entry.getBiome();
//                                biome = 2;
                                double thisHeight = BIOME_NOISE_GENERATORS[biome].getNoise(x, z);
                                height += thisHeight * weight;
                            }
                            r = g = b = (int) height;

                        } else if (ONLY_RENDER_WEIGHT_BORDERS) {
                            double maxWeight = Double.NEGATIVE_INFINITY;
                            for (LinkedBiomeWeightMap entry = firstBiomeWeightMap; entry != null; entry = entry.getNext()) {
                                double weight = entry.getWeights() == null ? 1 : entry.getWeights()[zi * CHUNK_WIDTH + xi];
                                if (weight > maxWeight) {
                                    maxWeight = weight;
                                    int biome = entry.getBiome();
                                    Color color = BIOME_COLORS[biome];
                                    r = color.getRed();
                                    g = color.getGreen();
                                    b = color.getBlue();
                                }
                            }
                        } else {
                            for (LinkedBiomeWeightMap entry = firstBiomeWeightMap; entry != null; entry = entry.getNext()) {
                                double weight = entry.getWeights() == null ? 1 : entry.getWeights()[zi * CHUNK_WIDTH + xi];
                                int biome = entry.getBiome();
                                r += (BIOME_COLORS[biome].getRed() + 0.5) * weight;
                                g += (BIOME_COLORS[biome].getGreen() + 0.5) * weight;
                                b += (BIOME_COLORS[biome].getBlue() + 0.5) * weight;
                            }
                        }

                        // Render chunk borders
                        //if (xi == CHUNK_WIDTH - 1 || zi == CHUNK_WIDTH - 1) r = g = b = 191;
//                        int biomeAt = getBiomeAt(x, z);
//                        r = BIOME_COLORS[biomeAt].getRed();
//                        g = BIOME_COLORS[biomeAt].getGreen();
//                        b = BIOME_COLORS[biomeAt].getBlue();
                        int rgb = new Color((int) r, (int) g, (int) b).getRGB();
                        image.setRGB(x, z, rgb);
                    }
                }
            }
        }

        // Save it or show it
        if (args.length > 0 && args[0] != null) {
            ImageIO.write(image, "png", new File(args[0]));
            System.out.println("Saved image as " + args[0]);
        } else {
            JFrame frame = new JFrame();
            JLabel imageLabel = new JLabel();
            imageLabel.setIcon(new ImageIcon(image));
            frame.add(imageLabel);
            frame.pack();
            frame.setResizable(false);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

    }

    // Just pick N biomes based on the greatest value out of N noises
    static int getBiomeAt(double x, double z) {
        double maxValue = Double.NEGATIVE_INFINITY;
        int biome = 0;
        boolean river = false;
        for (int i = 0; i < BIOME_COLORS.length; i++) {
            double noiseValue = 0;
            double freq = BIOME_NOISE_FREQUENCY;
            double amp = 1;
            for (int j = 0; j < N_OCTAVES; j++) {
                noiseValue += NOISES[i + j * BIOME_COLORS.length].noise2(x * freq, z * freq) * amp;
                freq *= 2;
                amp *= 0.5;
            }
            if (i == 1) {
                noiseValue = Math.min(-.6, noiseValue);
            }
            if (i == 2) {
                double value = 1 - Math.abs(TERRAIN_NOISES[8].noise2(x * 0.001 * DIVIDOR, z * 0.001 * DIVIDOR));
                if (value > 0.86)
                    river = true;
                continue;
            }
            if (noiseValue > maxValue) {
                maxValue = noiseValue;
                biome = i;
            }
        }
        if (biome == 0 && river)
            return 2;

        return biome;
    }
}