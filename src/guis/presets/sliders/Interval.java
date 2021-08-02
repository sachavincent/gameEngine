package guis.presets.sliders;

public class Interval {

    float min, max, step;
    float defaultValue;

    public Interval(float defaultValue, float min, float max, float step) {
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step / (this.max - this.min);
    }
}
