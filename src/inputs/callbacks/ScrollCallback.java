package inputs.callbacks;

@FunctionalInterface
public interface ScrollCallback {

    void onScroll(double xOffset, double yOffset);
}
