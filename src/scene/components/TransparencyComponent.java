package scene.components;

import util.math.Maths;

public class TransparencyComponent extends Component {

    private float alpha;

    public TransparencyComponent(float alpha) {
        this.alpha = alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = Maths.clamp(alpha, 0f, 1f);
    }

    public float getAlpha() {
        return this.alpha;
    }
}