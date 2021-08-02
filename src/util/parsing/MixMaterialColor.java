package util.parsing;

import util.Mix;
import util.math.Vector3f;

import java.util.Random;

public class MixMaterialColor extends MaterialColor {

    private final Random random;

    private final Mix mixR;
    private final Mix mixG;
    private final Mix mixB;

    public MixMaterialColor(Vector3f firstColor, Vector3f secondColor, double factor) {
        this(firstColor, secondColor, null);
        this.color = interpolate(factor);
    }

    public MixMaterialColor(Vector3f firstColor, Vector3f secondColor, Random random) {
        this.mixR = new Mix(firstColor.x, secondColor.x);
        this.mixG = new Mix(firstColor.y, secondColor.y);
        this.mixB = new Mix(firstColor.z, secondColor.z);

        this.random = random;
        this.color = interpolate(this.random.nextDouble());
    }

    private MixMaterialColor(MixMaterialColor copy) {
        this.mixR = copy.mixR;
        this.mixG = copy.mixG;
        this.mixB = copy.mixB;
        this.random = copy.random;
        this.color = copy.color;
    }

    @Override
    public MaterialColor copy() {
        MixMaterialColor copy = new MixMaterialColor(this);
        copy.color = interpolate(this.random.nextDouble());
        return copy;
    }

    private Vector3f interpolate(double factor) {
        double r = this.mixR.interpolate(factor);
        double g = this.mixG.interpolate(factor);
        double b = this.mixB.interpolate(factor);
        return new Vector3f(r, g, b);
    }
}