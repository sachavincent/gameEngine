package util.feedback;

@FunctionalInterface
public interface FeedbackReceivedCallback<X> {

    void onFeedbackReceived(X res);
}