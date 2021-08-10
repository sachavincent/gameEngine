package util.feedback;

public interface Feedback<V, R> {

    Feedback<V, R> onSuccess(FeedbackReceivedCallback<V> feedback);

    Feedback<V, R> onFailure(FeedbackReceivedCallback<R> feedback);
}