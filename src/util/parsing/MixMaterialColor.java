package util.parsing;

import java.util.Random;
import util.Mix;
import util.math.Vector3f;

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
        this.mixR = new Mix(firstColor.getX(), secondColor.getX());
        this.mixG = new Mix(firstColor.getY(), secondColor.getY());
        this.mixB = new Mix(firstColor.getZ(), secondColor.getZ());

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