package util.feedback;

public class Failure<V, R> extends FeedbackResponse<R> implements Feedback<V, R> {

    public Failure(R res) {
        super(res);
    }

    @Override
    public final Feedback<V, R> onSuccess(FeedbackReceivedCallback<V> feedback) {
        return this;
    }

    @Override
    public final Feedback<V, R> onFailure(FeedbackReceivedCallback<R> feedback) {
        feedback.onFeedbackReceived(this.res);
        return this;
    }
}
