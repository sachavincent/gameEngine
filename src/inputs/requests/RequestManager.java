package inputs.requests;

import inputs.requests.Request.RequestType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class RequestManager {

    private static RequestManager instance;

    private final Map<RequestType, Queue<Request>> requests = new HashMap<>();

    public static RequestManager getInstance() {
        return instance == null ? (instance = new RequestManager()) : instance;
    }

    private RequestManager() {
        this.requests.put(RequestType.KEY, new LinkedList<>());
        this.requests.put(RequestType.CHAR, new LinkedList<>());
        this.requests.put(RequestType.MOUSE, new LinkedList<>());
    }

    public void cancelLastRequest(Request request) {
        this.requests.get(request.getRequestType()).remove(request);
    }

    public void cancelLastRequest(RequestType requestType) {
        this.requests.get(requestType).poll();
    }

    public void cancelRequest(RequestType requestType, int id) {
        this.requests.get(requestType).removeIf(request -> request.getId() == id);
    }

    public int request(Request request) {
        this.requests.get(request.getRequestType()).add(request);

        return request.getId();
    }

    public boolean handleRequests(RequestType requestType, int action, char pressedKey, int scancode) {
        if (this.requests.isEmpty())
            return false;

        Queue<Request> requests = this.requests.get(requestType);
        if (requests.isEmpty())
            return false;

        Request request = requests.element();

        if (request != null)
            return request.getOnHandleRequest().onHandle(action, pressedKey, scancode, request, requests);

        return false;
    }
}
