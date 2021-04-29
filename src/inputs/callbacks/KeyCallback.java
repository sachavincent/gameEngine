package inputs.callbacks;

public interface KeyCallback {

    boolean onKeyRequest(int action, int key); // Returns true if request is cancelled
}