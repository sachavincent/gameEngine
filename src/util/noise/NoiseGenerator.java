package util.noise;

import util.math.Maths;

@FunctionalInterface
public interface NoiseGenerator {

    double get(int x, int z);

    default double getNoise(int x, int z) {
        return Maths.clamp(get(x, z), 0.0, 255.0);
    }
}