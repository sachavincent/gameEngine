package inputs.callbacks;

@FunctionalInterface
public interface MousePressCallback {

    /**
     * @return true if handled
     */
    boolean onPress(int button);
}