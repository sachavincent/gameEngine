package inputs.callbacks;

import inputs.requests.Request;
import java.util.Queue;

public interface HandleRequestCallback {

    boolean onHandle(int action, int key, Request request, Queue<Request> requests); // Returns true if handled
}
