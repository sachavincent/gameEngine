package util;

public class Mix {

    private final double firstValue;
    private final double secondValue;

    public Mix(double firstValue, double secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    /**
     * LINEAR Interpolation between attribute values
     *
     * @param factor must be between 0 and 1
     * @return interpolated value
     */
    public double interpolate(double factor) {
        if (factor < 0 || factor > 1)
            throw new IllegalArgumentException("Wrong factor for interpolation");
        return this.firstValue * factor + (1 - factor) * this.secondValue;
    }

    public double getFirstValue() {
        return this.firstValue;
    }

    public double getSecondValue() {
        return this.secondValue;
    }
}
