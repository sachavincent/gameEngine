package inputs.callbacks;

import inputs.requests.Request;
import java.util.Queue;

@FunctionalInterface
public interface HandleRequestCallback {

    boolean onHandle(int action, int key, int scancode, Request request,
            Queue<Request> requests); // Returns true if handled
}
