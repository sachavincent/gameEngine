package inputs.requests;

import inputs.callbacks.HandleRequestCallback;

public abstract class Request {

    protected HandleRequestCallback handleRequestCallback;

    public Request(HandleRequestCallback handleRequestCallback) {
        this.handleRequestCallback = handleRequestCallback;
    }

    public HandleRequestCallback getOnHandleRequest() {
        return this.handleRequestCallback;
    }
}
