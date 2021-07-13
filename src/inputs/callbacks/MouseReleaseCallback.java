package inputs.callbacks;

@FunctionalInterface
public interface MouseReleaseCallback {

    /**
     * @return true if handled
     */
    boolean onRelease(int button);
}