package inputs.requests;

import inputs.callbacks.HandleRequestCallback;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Request {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    protected final HandleRequestCallback handleRequestCallback;
    protected final RequestType           requestType;
    protected final int                   id;

    public Request(RequestType requestType, HandleRequestCallback handleRequestCallback) {
        this.handleRequestCallback = handleRequestCallback;
        this.requestType = requestType;
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public HandleRequestCallback getOnHandleRequest() {
        return this.handleRequestCallback;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public int getId() {
        return this.id;
    }

    public enum RequestType {
        CHAR,
        KEY,
        MOUSE
    }
}
