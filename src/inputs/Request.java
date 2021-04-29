package inputs;

import inputs.callbacks.KeyCallback;

public class Request {

    private final KeyCallback       hecklingCallback;

    public Request(KeyCallback hecklingCallback) {
        this.hecklingCallback = hecklingCallback;
    }

    public KeyCallback getHecklingCallback() {
        return this.hecklingCallback;
    }

}