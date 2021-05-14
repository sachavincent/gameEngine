package inputs.callbacks;

import inputs.KeyInput;

public interface KeyCallback {

    boolean onKeyRequest(int action, KeyInput keyInput); // Returns true if request is cancelled
}