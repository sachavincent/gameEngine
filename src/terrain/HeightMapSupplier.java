package terrain;

import util.feedback.Feedback;

@FunctionalInterface
public interface HeightMapSupplier<V, R> {

    Feedback<V, R> create(int width, int depth);
}
