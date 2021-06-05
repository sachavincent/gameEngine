package inputs.requests;

import inputs.callbacks.HandleRequestCallback;

public abstract class Request {

    protected final HandleRequestCallback handleRequestCallback;
    protected final RequestType           requestType;

    public Request(RequestType requestType, HandleRequestCallback handleRequestCallback) {
        this.handleRequestCallback = handleRequestCallback;
        this.requestType = requestType;
    }

    public HandleRequestCallback getOnHandleRequest() {
        return this.handleRequestCallback;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public enum RequestType {
        CHAR,
        KEY
    }
}
